package co.jp.xeex.chat.domains.chatmngr.msg.unread;

import lombok.Data;

@Data
public class SetChatUnreadResponse {
    /**
     * Represents the chat unread group.
     */
    private ChatUnreadDto chatUnreadGroup;
    /**
     * Represents the response object for setting chat unread status.
     * Contains information about the chat unread status.
     */
    private ChatUnreadDto chatUnreadRepply;
}
