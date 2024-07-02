package co.jp.xeex.chat.domains.chatmngr.groupfriend.getall;

import co.jp.xeex.chat.base.ServiceBaseImpl;
import co.jp.xeex.chat.domains.chatmngr.friend.dto.FriendDto;
import co.jp.xeex.chat.domains.chatmngr.friend.dto.FriendDetailDto;
import co.jp.xeex.chat.domains.chatmngr.group.dto.ChatGroupDto;
import co.jp.xeex.chat.domains.chatmngr.service.UserService;
import co.jp.xeex.chat.entity.UnreadMessage;
import co.jp.xeex.chat.exception.BusinessException;
import co.jp.xeex.chat.domains.chatmngr.group.dto.ChatGroupDetailDto;
import co.jp.xeex.chat.repository.ChatFriendRepository;
import co.jp.xeex.chat.repository.ChatGroupRepository;
import co.jp.xeex.chat.repository.ChatMessageRepository;
import co.jp.xeex.chat.repository.UnreadMessageRepository;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * GetAllGroupFriendServiceImpl
 * 
 * @author q_thinh
 */
@Service
@AllArgsConstructor
public class GetAllGroupFriendServiceImpl extends ServiceBaseImpl<GetAllGroupFriendRequest, GetAllGroupFriendResponse>
        implements GetAllGroupFriendService {

    // DI
    private ChatGroupRepository chatGroupRepo;
    private ChatMessageRepository chatMessageRepo;
    private ChatFriendRepository chatFriendRepo;
    private UnreadMessageRepository unreadMessageRepository;
    private UserService userService;

    @Override
    public GetAllGroupFriendResponse processRequest(GetAllGroupFriendRequest in) throws BusinessException {
        // Response
        GetAllGroupFriendResponse response = new GetAllGroupFriendResponse();

        // Get all group info
        List<ChatGroupDetailDto> chatGroupDetailDtos = getChatGroupDetails(in.requestBy, StringUtils.EMPTY);

        // Get list friend
        List<FriendDetailDto> friendDetailDtos = getFriendDetails(in.requestBy, StringUtils.EMPTY);

        // Response
        response.setGroups(chatGroupDetailDtos);
        response.setFriends(friendDetailDtos);
        return response;
    }

    /**
     * getChatGroupDetails
     * 
     * @param empCd
     * @param searchValue
     * @return
     */
    private List<ChatGroupDetailDto> getChatGroupDetails(String empCd, String searchValue) {
        List<ChatGroupDetailDto> result = new ArrayList<>();

        // Get all chatgroup info
        List<ChatGroupDto> chatGroupDtos = chatGroupRepo.findGroupByValue(empCd, searchValue);
        for (ChatGroupDto chatGroupDto : chatGroupDtos) {
            ChatGroupDetailDto chatGroupDetailDto = new ChatGroupDetailDto();
            chatGroupDetailDto.setGroupId(chatGroupDto.getGroupId());
            chatGroupDetailDto.setGroupName(chatGroupDto.getGroupName());
            chatGroupDetailDto.setStartMessageId(chatMessageRepo.getStartMessageIdByGroup(chatGroupDto.getGroupId()));

            // Get unread count
            UnreadMessage unreadMessage = unreadMessageRepository.getUnreadMessage(empCd, chatGroupDto.getGroupId(),
                    null);
            chatGroupDetailDto.setUnreadCount(unreadMessage != null ? unreadMessage.getUnreadCount() : 0);
            result.add(chatGroupDetailDto);
        }

        return result;
    }

    /**
     * getFriendDetails
     * 
     * @param empCd
     * @param searchValue
     * @return
     */
    private List<FriendDetailDto> getFriendDetails(String empCd, String searchValue) {
        List<FriendDetailDto> result = new ArrayList<>();

        // Get all chatfriend info
        List<FriendDto> friendDtos = chatFriendRepo.findByValue(empCd, searchValue);
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
