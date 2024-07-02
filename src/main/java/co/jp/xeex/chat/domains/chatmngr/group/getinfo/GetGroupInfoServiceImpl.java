package co.jp.xeex.chat.domains.chatmngr.group.getinfo;

import co.jp.xeex.chat.base.ServiceBaseImpl;
import co.jp.xeex.chat.domains.chatmngr.group.dto.ChatGroupDetailDto;
import co.jp.xeex.chat.domains.chatmngr.group.dto.MemberDto;
import co.jp.xeex.chat.domains.chatmngr.service.UserService;
import co.jp.xeex.chat.entity.ChatGroup;
import co.jp.xeex.chat.exception.BusinessException;
import co.jp.xeex.chat.repository.ChatGroupRepository;
import co.jp.xeex.chat.repository.UserRepository;
import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

/**
 * GetGroupInfoServiceImpl
 * 
 * @author q_thinh
 */
@Service
@AllArgsConstructor
public class GetGroupInfoServiceImpl extends ServiceBaseImpl<GetGroupInfoRequest, GetGroupInfoResponse>
        implements GetGroupInfoService {

    // Error keys
    private static final String GET_GROUP_INFO_ERR_GROUP_IS_NOT_EXISTED = "GET_GROUP_INFO_ERR_GROUP_IS_NOT_EXISTED";

    // DI
    private ChatGroupRepository chatGroupRepo;
    private UserRepository userRepo;
    private UserService userService;

    @Override
    public GetGroupInfoResponse processRequest(GetGroupInfoRequest in) throws BusinessException {
        // Get chatGroup
        ChatGroup chatGroup = chatGroupRepo.findById(in.groupId).orElse(null);
        if (chatGroup == null) {
            throw new BusinessException(GET_GROUP_INFO_ERR_GROUP_IS_NOT_EXISTED, in.lang);
        }

        // Get group members
        List<MemberDto> memberDtos = userRepo.findByGroup(in.groupId);

        // Set url Avatar
        memberDtos = userService.setUrlAvatarListMember(memberDtos);

        // Setting ChatGroup info
        ChatGroupDetailDto chatGroupDetailDto = new ChatGroupDetailDto();
        chatGroupDetailDto.setGroupId(chatGroup.getId());
        chatGroupDetailDto.setGroupName(chatGroup.getGroupName());
        chatGroupDetailDto.setMembers(memberDtos);

        // Response
        GetGroupInfoResponse response = new GetGroupInfoResponse();
        response.setGroupInfo(chatGroupDetailDto);
        return response;
    }
}
