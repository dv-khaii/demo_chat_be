package co.jp.xeex.chat.domains.chat;

import org.apache.commons.lang3.StringUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import co.jp.xeex.chat.domains.chatmngr.msg.unread.ChatUnreadService;
import co.jp.xeex.chat.exception.BusinessException;
import lombok.AllArgsConstructor;

/**
 * implementation of ChatMessageSendService.<br>
 * v_long
 */
@AllArgsConstructor
@Service
public class ChatMessageBroadcastServiceImpl implements ChatMessageBroadcastService {
    private static final String SEND_MSG_ERR_MSG_EMPTY = "SEND_MSG_ERR_MSG_EMPTY";
    private static final String SEND_MSG_ERR_GROUP_ID_EMPTY = "SEND_MSG_ERR_GROUP_ID_EMPTY";
    private static final String SEND_MSG_ERR_MSG_ID_EMPTY = "SEND_MSG_ERR_MSG_ID_EMPTY";
    private static final String SEND_MSG_ERR_RECEIVER_NAME_EMPTY = "SEND_MSG_ERR_RECEIVER_NAME_EMPTY";
    // DI
    private SimpMessagingTemplate simpMessagingTemplate;
    private SystemMessageDtoFactoryService systemMessageDtoFactotyService;
    // v_long: 224-06-19: Added update of count of unread messages
    private ChatUnreadService chatUnreadService;

    @Override
    public ChatMessageDto broadcastMessageToPublicGroup(ChatMessageDto msgDto) throws BusinessException {
        if (msgDto == null) {
            throw new BusinessException(SEND_MSG_ERR_MSG_EMPTY);
        }
        // reset to default group id
        msgDto.groupId = StringUtils.EMPTY;
        systemMessageDtoFactotyService.setChatTime(msgDto);
        simpMessagingTemplate.convertAndSend(ChatEndPoints.CHAT_ALL_SUBSCRIBR, msgDto);

        return msgDto;
    }

    @Override
    public ChatMessageDto broadcastMessageToUser(ChatMessageDto msgDto, String receiverUser) throws BusinessException {

        if (msgDto == null) {// error to current user
            msgDto = systemMessageDtoFactotyService.createErrorMessage(StringUtils.EMPTY, "SYSTEM",
                    new BusinessException(SEND_MSG_ERR_MSG_EMPTY), "en");
            receiverUser = msgDto.requestBy;
        } else if (StringUtils.isEmpty(msgDto.groupId)) {// error to current user
            msgDto = systemMessageDtoFactotyService.createErrorMessage(StringUtils.EMPTY, msgDto.requestBy,
                    new BusinessException(SEND_MSG_ERR_GROUP_ID_EMPTY), msgDto.lang);
            receiverUser = msgDto.requestBy;
        } else if (StringUtils.isEmpty(msgDto.messageId)) {// error to current user
            msgDto = systemMessageDtoFactotyService.createErrorMessage(StringUtils.EMPTY, msgDto.requestBy,
                    new BusinessException(SEND_MSG_ERR_MSG_ID_EMPTY), msgDto.lang);
            receiverUser = msgDto.requestBy;
        } else if (StringUtils.isEmpty(receiverUser)) {// error
            msgDto = systemMessageDtoFactotyService.createErrorMessage(StringUtils.EMPTY, msgDto.requestBy,
                    new BusinessException(SEND_MSG_ERR_RECEIVER_NAME_EMPTY), msgDto.lang);
            receiverUser = msgDto.requestBy;
        }
        // send message to current user
        systemMessageDtoFactotyService.setChatTime(msgDto);
        simpMessagingTemplate.convertAndSendToUser(receiverUser, ChatEndPoints.CHAT_PRIVATE_MARK_SUBSCRIBR, msgDto);

        return msgDto;
    }

    @Override
    public ChatMessageDto broadcastMessageToGroup(ChatMessageDto msgDto) throws BusinessException {
        if (msgDto == null) {// error
            return null;
            //
        } else if (StringUtils.isEmpty(msgDto.groupId)) {// error
            msgDto = systemMessageDtoFactotyService.createErrorMessage(StringUtils.EMPTY, msgDto.requestBy,
                    new BusinessException(SEND_MSG_ERR_GROUP_ID_EMPTY), msgDto.lang);
            simpMessagingTemplate.convertAndSendToUser(msgDto.requestBy, ChatEndPoints.CHAT_PRIVATE_MARK_SUBSCRIBR,
                    msgDto);
            return msgDto;
        }
        Object[] params = new Object[] { ChatEndPoints.CHAT_GROUP_SUBSCRIBR, msgDto.groupId };
        String destination = String.format("%s/%s/private", params);

        systemMessageDtoFactotyService.setChatTime(msgDto);
        simpMessagingTemplate.convertAndSend(destination, msgDto);

        // v_long: increment unread count for all users in the group
        if (ChatAction.CHAT.equals(msgDto.action)) {
            chatUnreadService.increamentUnreadCountForChatGroup(msgDto, msgDto.requestBy);
        }

        return msgDto;
    }
}
