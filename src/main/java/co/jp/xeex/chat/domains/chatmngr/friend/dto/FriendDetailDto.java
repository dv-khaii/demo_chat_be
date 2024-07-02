package co.jp.xeex.chat.domains.chatmngr.friend.dto;

import lombok.Data;

/**
 * FriendDetailDto
 * 
 * @author q_thinh
 */
@Data
public class FriendDetailDto {
    /**
     * Represents the unique identifier of a chat group.
     */
    private String groupId;
    /**
     * Represents the group name of a friend.
     */
    private String groupName;
    /**
     * The employee code associated with the friend.
     */
    private String empCd;
    /**
     * Represents the avatar of a friend.
     */
    private String avatar;
    /**
     * message start in group chat
     * client use to check lazy load message history
     */
    private String startMessageId;
    /**
     * unread message count
     * client use to detect unread message in group
     */
    private Integer unreadCount = 0;
}
