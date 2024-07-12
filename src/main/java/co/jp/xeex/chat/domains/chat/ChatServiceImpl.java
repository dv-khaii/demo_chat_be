package co.jp.xeex.chat.domains.chat;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import co.jp.xeex.chat.domains.chatmngr.group.dto.MemberDto;
import co.jp.xeex.chat.domains.chatmngr.msg.service.ChatMessageService;
import co.jp.xeex.chat.domains.chatmngr.service.UserService;
import co.jp.xeex.chat.entity.ChatGroup;
import co.jp.xeex.chat.entity.ChatMessage;
import co.jp.xeex.chat.entity.User;
import co.jp.xeex.chat.exception.BusinessException;
import co.jp.xeex.chat.repository.ChatGroupRepository;
import co.jp.xeex.chat.repository.ChatMessageRepository;
import co.jp.xeex.chat.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;

@AllArgsConstructor
@Service
@Log4j
public class ChatServiceImpl implements ChatService {
    /** Fiend not fount */
    private static final String CHAT_MSG_PROCCESS_ERR_GROUP_NOT_FOUND = "CHAT_MSG_PROCCESS_ERR_GROUP_NOT_FOUND";
    private ChatMessageRepository chatMessageRepository;
    private ChatGroupRepository chatGroupRepository;
    private ChatMessageService chatMessageService;
    private SystemMessageDtoFactoryService systemMessageDtoFactotyService;
    private UserRepository userRepository;
    private UserService userService;
    //
    // use to broadcast message to clients
    private ChatMessageBroadcastService broadcasrService;

    // PRIVATE METHODS

    private ChatMessage convertToMessage(ChatMessageDto msgDto) {

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setGroupId(msgDto.groupId);
        chatMessage.setId(msgDto.messageId);
        chatMessage.setId(msgDto.messageId);
        chatMessage.setChatContent(msgDto.chatContent == null ? StringUtils.EMPTY : msgDto.chatContent);
        chatMessage.setAction(msgDto.action);
        chatMessage.setCreateBy(msgDto.requestBy);
        chatMessage.setUpdateBy(msgDto.requestBy);
        chatMessage.setRepplyMessageId(msgDto.repplyMessageId);
        return chatMessage;
    }

    // IMPLEMENTED METHODS
    @Override
    public ChatMessageDto sendMessageToGroup(ChatMessageDto msgDto) throws BusinessException {
        /**
         * process steps:
         * - convert msgDto to ChatMessage to save to database
         * - save message to database to get the message id before sending to group
         * - send message to group. Client subscribes to /group/{groupiId}/private
         */
        // may need to check if groupID exists
        ChatGroup chatGroup = chatGroupRepository.findById(msgDto.groupId).orElse(null);
        if (chatGroup == null) {
            return this.feedBackError(msgDto,
                    new BusinessException(CHAT_MSG_PROCCESS_ERR_GROUP_NOT_FOUND, msgDto.lang));
        }
        // convert msgDto to ChatMessage to save to database
        ChatMessage chatMessage = this.convertToMessage(msgDto);
        chatMessage = chatMessageRepository.saveAndFlush(chatMessage);

        // Save chat files
        try {
            msgDto.messageId = chatMessage.getId();
            chatMessageService.saveChatFile(msgDto);
        } catch (BusinessException e) {
            return this.feedBackError(msgDto,
                    new BusinessException(e.getMessage()));
        }

        // Get avatar for sender
        msgDto.senderImage = userService.getUrlAvatarByEmpCd(msgDto.requestBy);

        // Set broadcast data
        msgDto.messageId = chatMessage.getId();
        if (msgDto.repplyMessageId != null) {
            msgDto.repplyMessage = chatMessageService.getRepplyMessageDetail(msgDto.repplyMessageId, msgDto.requestBy);
        }

        // Notify all other mention user
        if (!msgDto.mentionedUserNames.isEmpty()) {
            msgDto.action = ChatAction.MENTION;
            for (MemberDto memberDto : msgDto.mentionedUserNames) {
                if (!msgDto.requestBy.equals(memberDto.getMemberName())) {
                    broadcasrService.broadcastMessageToUser(msgDto, memberDto.getMemberName());
                }
            }
        }

        // broadcast message to group
        msgDto.action = ChatAction.CHAT;
        return broadcasrService.broadcastMessageToGroup(msgDto);
    }

    private ChatMessageDto feedBackError(ChatMessageDto msgDto, BusinessException e) throws BusinessException {
        ChatMessageDto message = systemMessageDtoFactotyService.createErrorMessage(StringUtils.EMPTY, msgDto.requestBy,
                e, msgDto.lang);

        log.error(message);
        return broadcasrService.broadcastMessageToUser(message, msgDto.requestBy);
    }

    @Override
    public void notifyLogin(String currentUserName, String lang) throws BusinessException {
        List<User> users = userRepository.findUsersOnRelationshipOf(currentUserName);
        for (User user : users) {
            ChatMessageDto msg = systemMessageDtoFactotyService.createLoginMessage(currentUserName, lang);
            broadcasrService.broadcastMessageToUser(msg, user.getEmpCd());
        }
    }

    @Override
    public void notifyError(String userName, BusinessException e) throws BusinessException {
        ChatMessageDto msg = systemMessageDtoFactotyService.createErrorMessage("-1", userName, e,
                e.getLang() == null ? "en" : e.getLang());
        broadcasrService.broadcastMessageToUser(msg, userName);
    }

    @Override
    public void notifyLogout(String currentUserName, String lang) throws BusinessException {
        List<User> users = userRepository.findUsersOnRelationshipOf(currentUserName);
        for (User user : users) {
            ChatMessageDto msg = systemMessageDtoFactotyService.createLogoutMessage(currentUserName, lang);
            broadcasrService.broadcastMessageToUser(msg, user.getEmpCd());
        }
    }

}
