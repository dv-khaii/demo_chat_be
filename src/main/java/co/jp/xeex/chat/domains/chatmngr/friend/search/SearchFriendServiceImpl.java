package co.jp.xeex.chat.domains.chatmngr.friend.search;

import co.jp.xeex.chat.base.ServiceBaseImpl;
import co.jp.xeex.chat.common.AppConstant;
import co.jp.xeex.chat.domains.chatmngr.friend.dto.FriendDetailDto;
import co.jp.xeex.chat.domains.chatmngr.friend.dto.FriendDto;
import co.jp.xeex.chat.domains.chatmngr.service.UserService;
import co.jp.xeex.chat.entity.UnreadMessage;
import co.jp.xeex.chat.exception.BusinessException;
import co.jp.xeex.chat.repository.ChatFriendRepository;
import co.jp.xeex.chat.repository.ChatMessageRepository;
import co.jp.xeex.chat.repository.UnreadMessageRepository;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * SearchFriendServiceImpl
 * 
 * @author q_thinh
 */
@Service
@AllArgsConstructor
public class SearchFriendServiceImpl extends ServiceBaseImpl<SearchFriendRequest, SearchFriendResponse>
        implements SearchFriendService {

    // repository uses
    private ChatFriendRepository friendsRepo;
    private ChatMessageRepository chatMessageRepo;
    private UnreadMessageRepository unreadMessageRepository;
    private UserService userService;

    @Override
    public SearchFriendResponse processRequest(SearchFriendRequest in) throws BusinessException {
        // Set searchValue
        in.setSearchValue((in.getSearchValue() == null || AppConstant.STAR_CHARACTER.equals(in.getSearchValue()))
                ? StringUtils.EMPTY
                : in.getSearchValue());

        // Get list friend detail
        List<FriendDetailDto> friendDetailDtos = getChatFriendDetails(in.requestBy, in.getSearchValue());

        // Response
        SearchFriendResponse response = new SearchFriendResponse();
        response.setFriends(friendDetailDtos);
        return response;
    }

    /**
     * getChatFriendDetails
     * 
     * @param empCd
     * @param searchValue
     * @return
     */
    private List<FriendDetailDto> getChatFriendDetails(String empCd, String searchValue) {
        List<FriendDetailDto> result = new ArrayList<>();

        // Get all chatfriend info
        List<FriendDto> friendDtos = friendsRepo.findByValue(empCd, searchValue);
        for (FriendDto friendDto : friendDtos) {
            FriendDetailDto friendDetailDto = new FriendDetailDto();
            friendDetailDto.setGroupId(friendDto.getGroupId());
            friendDetailDto.setGroupName(friendDto.getGroupName());
            friendDetailDto.setEmpCd(friendDto.getEmpCd());
            friendDetailDto.setStartMessageId(chatMessageRepo.getStartMessageIdByGroup(friendDto.getGroupId()));
            friendDetailDto.setAvatar(userService.getUrlAvatarByEmpCd(friendDto.getEmpCd()));

            // Get unread count
            UnreadMessage unreadMessage = unreadMessageRepository.getUnreadMessage(empCd, friendDto.getGroupId(), null);
            friendDetailDto.setUnreadCount(unreadMessage != null ? unreadMessage.getUnreadCount() : 0);

            result.add(friendDetailDto);
        }

        return result;
    }
}
