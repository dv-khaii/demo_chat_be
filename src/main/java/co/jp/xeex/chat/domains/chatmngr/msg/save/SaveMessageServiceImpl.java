package co.jp.xeex.chat.domains.chatmngr.msg.save;

import co.jp.xeex.chat.base.ServiceBaseImpl;
import co.jp.xeex.chat.domains.chat.ChatAction;
import co.jp.xeex.chat.domains.chat.ChatMessageBroadcastService;
import co.jp.xeex.chat.domains.chat.ChatMessageDto;
import co.jp.xeex.chat.domains.chatmngr.msg.service.ChatMessageService;
import co.jp.xeex.chat.domains.chatmngr.repply.mapper.ChatMessageMapper;
import co.jp.xeex.chat.domains.chatmngr.service.UserService;
import co.jp.xeex.chat.entity.ChatMessage;
import co.jp.xeex.chat.entity.User;
import co.jp.xeex.chat.exception.BusinessException;
import co.jp.xeex.chat.repository.ChatMessageRepository;
import co.jp.xeex.chat.repository.MessageTaskRepository;
import co.jp.xeex.chat.repository.UserRepository;
import lombok.AllArgsConstructor;

import java.util.Optional;

import org.springframework.stereotype.Service;

/**
 * SaveMessageServiceImpl
 * 
 * @author q_thinh
 */
@Service
@AllArgsConstructor
public class SaveMessageServiceImpl extends ServiceBaseImpl<SaveMessageRequest, SaveMessageResponse>
        implements SaveMessageService {

    // Error key
    private static final String SAVE_MESSAGE_ERR_PERMISSION_DENIED = "SAVE_MESSAGE_ERR_PERMISSION_DENIED";

    // DI
    private ChatMessageRepository chatMessageRepo;
    private UserRepository userRepo;
    private MessageTaskRepository messageTaskRepo;
    private UserService userService;
    private ChatMessageMapper chatMessageMapper;
    private ChatMessageService chatMessageService;
    private ChatMessageBroadcastService chatMessageSendService;

    @Override
    public SaveMessageResponse processRequest(SaveMessageRequest in) throws BusinessException {
        ChatMessage chatMessage = new ChatMessage();
        Optional<ChatMessage> chatMessageOpt = chatMessageRepo.findById(in.messageId);
        if (chatMessageOpt.isPresent()) {
            chatMessage = chatMessageOpt.get();
            // Check permission user
            if (!in.requestBy.equals(chatMessage.getCreateBy())) {
                throw new BusinessException(SAVE_MESSAGE_ERR_PERMISSION_DENIED, in.lang);
            }

            // Update message
            chatMessage.setAction(ChatAction.EDIT_CHAT);
        } else {
            // Create chat message
            chatMessage.initDefault(in.requestBy);
            chatMessage.setGroupId(in.groupId);
            chatMessage.setRepplyMessageId(in.repplyMessageId);
            chatMessage.setAction(ChatAction.CHAT);
        }
        chatMessage.setChatContent(in.chatContent);
        chatMessageRepo.saveAndFlush(chatMessage);

        // Setting message DTO
        ChatMessageDto messageDto = chatMessageMapper.chatMessageToDto(chatMessage);

        // Setting task id
        messageDto.taskId = messageTaskRepo.getTaskIdByMessageId(chatMessage.getId());

        // Setting user avatar
        User user = userRepo.findByUserName(chatMessage.getCreateBy());
        messageDto.senderImage = userService.getUrlAvatarByAvatar(user.getAvatar());

        // Notify to all user in group/friend
        messageDto.fullName = user.getFullName();
        messageDto.chatFiles = chatMessageService.getChatFileDto(chatMessage.getId());
        messageDto.lang = in.lang;
        chatMessageSendService.broadcastMessageToGroup(messageDto);

        // Response
        SaveMessageResponse response = new SaveMessageResponse();
        response.setMessage(messageDto);
        return response;
    }
}
