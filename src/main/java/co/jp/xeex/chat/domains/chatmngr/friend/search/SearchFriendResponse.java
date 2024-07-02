package co.jp.xeex.chat.domains.chatmngr.friend.search;

import java.util.List;

import co.jp.xeex.chat.domains.chatmngr.friend.dto.FriendDetailDto;
import lombok.Data;

/**
 * SearchFriendResponse
 * 
 * @author q_thinh
 */
@Data
public class SearchFriendResponse {
    private List<FriendDetailDto> friends;
}
