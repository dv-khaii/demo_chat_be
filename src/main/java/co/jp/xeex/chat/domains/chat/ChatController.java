package co.jp.xeex.chat.domains.chat;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import co.jp.xeex.chat.base.ChatControllerBase;
import co.jp.xeex.chat.exception.BusinessException;
import lombok.AllArgsConstructor;

/**
 * This class is a controller class for people chat together.<br>
 * (public chat, private chat, group chat)<br>
 * <br>
 * 
 * @author v_long
 */
@Controller
@AllArgsConstructor
public class ChatController extends ChatControllerBase {
    private ChatService chatService;

    /**
     * Receive message from client and send to all users of a group.<br>
     * 
     * @param msgDto
     * @return
     * @throws Exception
     */
    @MessageMapping(ChatEndPoints.CHAT_GROUP_API)
    public final ChatMessageDto chatGroup(@Payload ChatMessageDto msgDto, SimpMessageHeaderAccessor headerAccessor) {
        ChatMessageDto msg = msgDto;
        try {
            // validate message
            this.preProcessChatMessage(msgDto, headerAccessor);
        } catch (BusinessException e) {
            // create an invalid message
            msg = super.createErrorChatMessage(msg, e);
            return msg;
        }
        try {
            // send message to group (msg may be invalid message)
            msg = chatService.sendMessageToGroup(msg);
        } catch (Exception e) {
            // system or bussiness exception error
            msg = super.createErrorChatMessage(msg, e);
        }
        // return error message
        return msg;
    }
}
