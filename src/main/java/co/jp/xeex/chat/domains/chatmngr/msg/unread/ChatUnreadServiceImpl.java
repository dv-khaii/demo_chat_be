package co.jp.xeex.chat.domains.chatmngr.msg.unread;

import org.springframework.stereotype.Service;

import co.jp.xeex.chat.domains.chat.ChatMessageDto;
import co.jp.xeex.chat.entity.UnreadMessage;
import co.jp.xeex.chat.exception.BusinessException;
import co.jp.xeex.chat.repository.ChatGroupRepository;
import co.jp.xeex.chat.repository.UnreadMessageRepository;
import org.apache.commons.lang3.StringUtils;
import jakarta.transaction.Transactional;

import java.util.List;
import lombok.AllArgsConstructor;

/**
 * implements for chat unread service
 * 
 * @author v_long
 */
@Service
@AllArgsConstructor
public class ChatUnreadServiceImpl implements ChatUnreadService {
    private UnreadMessageRepository unreadMessageRepository;
    private ChatGroupRepository chatGroupRepository;

    @Override
    public GetChatUnreadReseponse getUnread(GetChatUnreadRequest request) throws BusinessException {
        UnreadMessage unreadMessage = unreadMessageRepository.getUnreadMessage(request.userId,
                request.chatGroupId, request.repplyMessageId);
        if (unreadMessage == null) {
            return null;
        }
        GetChatUnreadReseponse response = new GetChatUnreadReseponse();
        response.setUserId(unreadMessage.getUserId());
        response.setChatGroupId(unreadMessage.getChatGroupId());
        response.setRepplyMessageId(unreadMessage.getRepplyMessageId());
        response.setUnreadCount(unreadMessage.getUnreadCount());
        return response;
    }

    @Override
    public SetChatUnreadResponse setUnread(SetChatUnreadRequest request) {
        // Save unreadMessageRepply
        ChatUnreadDto chatUnreadRepplyDto = null;
        if (!StringUtils.isEmpty(request.repplyMessageId)) {
            UnreadMessage unreadMessageRepply = unreadMessageRepository.getUnreadMessage(request.userId,
                    request.chatGroupId, request.repplyMessageId);
            chatUnreadRepplyDto = setChatUnreadDto(request.userId, request.chatGroupId, request.repplyMessageId, 0);
            saveUnreadMessage(unreadMessageRepply, chatUnreadRepplyDto);
        }

        // Get unread message group
        UnreadMessage unreadMessage = unreadMessageRepository.getUnreadMessage(request.userId,
                request.chatGroupId, null);

        // Setting current unread count for group
        Integer unreadCount = unreadMessageRepository.getUnreadCountRepply(request.userId, request.chatGroupId);
        unreadCount = unreadCount == null ? 0 : unreadCount;

        // Save unreadMessageGroup
        ChatUnreadDto chatUnreadGroupDto = setChatUnreadDto(request.userId, request.chatGroupId,
                null, unreadCount);
        saveUnreadMessage(unreadMessage, chatUnreadGroupDto);

        // Commit
        unreadMessageRepository.flush();

        // create response
        SetChatUnreadResponse response = new SetChatUnreadResponse();
        response.setChatUnreadGroup(chatUnreadGroupDto);
        response.setChatUnreadRepply(chatUnreadRepplyDto);
        return response;
    }

    @Override
    @Transactional
    public void increamentUnreadCountForChatGroup(ChatMessageDto chatMessageDto, String empCd) {
        // 1. list all members from group
        List<String> members = chatGroupRepository.getUserByGroupId(chatMessageDto.groupId);

        // 2. increament unread count for each member
        for (String m : members) {
            // Check if the member is the sender, skip
            if (empCd.equals(m)) {
                continue;
            }

            // Save unreadMessageGroup
            UnreadMessage unreadMessageGroup = unreadMessageRepository.getUnreadMessage(m, chatMessageDto.groupId,
                    null);
            Integer unreadCount = unreadMessageGroup == null ? 1 : unreadMessageGroup.getUnreadCount() + 1;
            ChatUnreadDto chatUnreadGroupDto = setChatUnreadDto(m, chatMessageDto.groupId, null, unreadCount);
            saveUnreadMessage(unreadMessageGroup, chatUnreadGroupDto);

            // Save unreadMessageRepply
            if (!StringUtils.isEmpty(chatMessageDto.repplyMessageId)) {
                UnreadMessage unreadMessageRepply = unreadMessageRepository
                        .getUnreadMessage(m, chatMessageDto.groupId, chatMessageDto.repplyMessageId);
                unreadCount = unreadMessageRepply == null ? 1 : unreadMessageRepply.getUnreadCount() + 1;
                ChatUnreadDto chatUnreadRepplyDto = setChatUnreadDto(m, chatMessageDto.groupId,
                        chatMessageDto.repplyMessageId, unreadCount);
                saveUnreadMessage(unreadMessageRepply, chatUnreadRepplyDto);
            }
        }

        // Commit
        unreadMessageRepository.flush();
    }

    /**
     * Saves the unread message and updates the unread count.
     *
     * @param unreadMessage The unread message to be saved. If null, a new
     *                      UnreadMessage object will be created.
     * @param chatUnreadDto The chat unread DTO.
     */
    private void saveUnreadMessage(UnreadMessage unreadMessage, ChatUnreadDto chatUnreadDto) {
        if (unreadMessage == null) {
            unreadMessage = new UnreadMessage();
            unreadMessage.initDefault("system");
            unreadMessage.setChatGroupId(chatUnreadDto.getChatGroupId());
            unreadMessage.setUserId(chatUnreadDto.getUserId());
            unreadMessage.setRepplyMessageId(chatUnreadDto.getRepplyMessageId());
        }
        unreadMessage.setUnreadCount(chatUnreadDto.getUnreadCount());
        unreadMessageRepository.save(unreadMessage);
    }

    /**
     * Represents a data transfer object for chat unread information.
     * 
     * @param chatGroupId     The chat group ID.
     * @param userId          The user ID.
     * @param repplyMessageId The reply message ID.
     * @param unreadCount     The number of unread messages.
     * @return A ChatUnreadDto object.
     */
    private ChatUnreadDto setChatUnreadDto(String userId, String chatGroupId, String repplyMessageId,
            Integer unreadCount) {
        ChatUnreadDto chatUnreadDto = new ChatUnreadDto();
        chatUnreadDto.setUserId(userId);
        chatUnreadDto.setChatGroupId(chatGroupId);
        chatUnreadDto.setRepplyMessageId(repplyMessageId);
        chatUnreadDto.setUnreadCount(unreadCount);
        return chatUnreadDto;
    }
}
