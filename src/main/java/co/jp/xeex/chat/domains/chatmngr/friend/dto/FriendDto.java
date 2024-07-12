package co.jp.xeex.chat.domains.chatmngr.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FriendDto
 * 
 * @author q_thinh
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendDto {
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
}
