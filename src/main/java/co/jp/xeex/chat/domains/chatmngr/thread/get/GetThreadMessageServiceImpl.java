package co.jp.xeex.chat.domains.chatmngr.thread.get;

import co.jp.xeex.chat.base.ServiceBaseImpl;
import co.jp.xeex.chat.domains.chat.ChatMessageDto;
import co.jp.xeex.chat.domains.chatmngr.msg.dto.ChatMessageDetailDto;
import co.jp.xeex.chat.domains.chatmngr.msg.dto.RepplyMessageDetailDto;
import co.jp.xeex.chat.domains.chatmngr.msg.service.ChatMessageService;
import co.jp.xeex.chat.domains.chatmngr.repply.mapper.ChatMessageMapper;
import co.jp.xeex.chat.domains.chatmngr.service.UserService;
import co.jp.xeex.chat.domains.chatmngr.thread.dto.ThreadGroupDto;
import co.jp.xeex.chat.domains.chatmngr.thread.dto.ThreadMessageDto;
import co.jp.xeex.chat.exception.BusinessException;
import co.jp.xeex.chat.repository.ChatMessageRepository;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * GetThreadMessageServiceImpl
 * 
 * @author q_thinh
 */
@Service
@AllArgsConstructor
public class GetThreadMessageServiceImpl
        extends ServiceBaseImpl<GetThreadMessageRequest, GetThreadMessageResponse>
        implements GetThreadMessageService {

    // DI
    private ChatMessageRepository chatMessageRepo;
    private ChatMessageMapper chatMessageMapper;
    private ChatMessageService chatMessageService;
    private UserService userService;

    @Override
    public GetThreadMessageResponse processRequest(GetThreadMessageRequest in) throws BusinessException {
        // Find all thread
        List<ThreadMessageDto> threadMessages = getThreadMessages(in.requestBy, in.getLimitMessage());

        // Response
        GetThreadMessageResponse response = new GetThreadMessageResponse();
        response.setThreadMessage(threadMessages);
        return response;
    }

    /**
     * getThreadMessages
     * 
     * @param requestBy
     * @param limitMessage
     * @return
     */
    private List<ThreadMessageDto> getThreadMessages(String requestBy, Integer limitMessage) {
        List<ThreadMessageDto> result = new ArrayList<>();

        // Get all thread
        List<ThreadGroupDto> threadGroups = chatMessageRepo.findThreadByUser(requestBy);
        for (ThreadGroupDto threadGroup : threadGroups) {
            ChatMessageDetailDto mainRepplyMessageDetail = chatMessageRepo
                    .findMessageDetailById(threadGroup.getRepplyMessageId());
            if (mainRepplyMessageDetail != null) {
                ThreadMessageDto threadMessageDto = new ThreadMessageDto();
                threadMessageDto.setGroupId(threadGroup.getGroupId());
                threadMessageDto.setGroupName(threadGroup.getGroupName());
                threadMessageDto.setRepplyMessageId(threadGroup.getRepplyMessageId());
                threadMessageDto.setMessage(getChatMessage(mainRepplyMessageDetail, limitMessage, requestBy));
                result.add(threadMessageDto);
            }
        }

        return result;
    }

    /**
     * getChatMessage
     * 
     * @param mainRepplyMessageDetail
     * @param limitMessage
     * @param requestBy
     * @return
     */
    private ChatMessageDto getChatMessage(ChatMessageDetailDto mainRepplyMessageDetail, Integer limitMessage,
            String requestBy) {
        // Add main repply chat message
        ChatMessageDto result = chatMessageMapper.chatMessageDetailToDto(mainRepplyMessageDetail);
        result.senderImage = userService.getUrlAvatarByAvatar(mainRepplyMessageDetail.getSenderImage());
        result.repplyMessage = getRepplyMessageDetail(mainRepplyMessageDetail.getMessageId(), limitMessage, requestBy);
        result.chatFiles = chatMessageService.getChatFileDto(mainRepplyMessageDetail.getMessageId());
        return result;
    }

    /**
     * getRepplyMessageDetail
     * 
     * @param repplyMessageId
     * @param limitMessage
     * @param requestBy
     * @return RepplyMsgInfoDto
     */
    private RepplyMessageDetailDto getRepplyMessageDetail(String repplyMessageId, Integer limitMessage,
            String requestBy) {
        // Get info repply message
        RepplyMessageDetailDto result = chatMessageService.getRepplyMessageDetail(repplyMessageId, requestBy);

        // Get repply message detail
        List<ChatMessageDetailDto> repplyChatMessageDetails = chatMessageRepo.findRepplyMessageById(repplyMessageId,
                limitMessage);

        // Reverse messages (ASC)
        Collections.reverse(repplyChatMessageDetails);

        // Add repply message
        List<ChatMessageDto> messages = new ArrayList<>();
        for (ChatMessageDetailDto repplyChatMessageDetail : repplyChatMessageDetails) {
            ChatMessageDto chatMessageDto = chatMessageMapper.chatMessageDetailToDto(repplyChatMessageDetail);
            chatMessageDto.senderImage = userService.getUrlAvatarByAvatar(repplyChatMessageDetail.getSenderImage());
            chatMessageDto.chatFiles = chatMessageService.getChatFileDto(repplyChatMessageDetail.getMessageId());
            messages.add(chatMessageDto);
        }

        result.setMessage(messages);
        return result;
    }
}
