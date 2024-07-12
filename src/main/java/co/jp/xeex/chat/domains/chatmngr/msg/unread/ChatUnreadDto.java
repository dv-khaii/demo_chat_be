package co.jp.xeex.chat.domains.chatmngr.msg.unread;

import lombok.Data;

/**
 * get count of unread message of groupChat of current user
 * 
 * @author q_thinh
 */
@Data
public class ChatUnreadDto {
    /**
     * The ID of the user associated with the chat unread response.
     */
    private String userId;
    /**
     * the chat group id associated with the chat messages
     */
    private String chatGroupId;
    /**
     * The ID of the reply message.
     */
    private String repplyMessageId;
    /**
     * count of unread messages (response)
     */
    private Integer unreadCount;
}
