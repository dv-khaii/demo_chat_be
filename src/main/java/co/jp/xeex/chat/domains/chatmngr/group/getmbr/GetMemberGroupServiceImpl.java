package co.jp.xeex.chat.domains.chatmngr.group.getmbr;

import co.jp.xeex.chat.base.ServiceBaseImpl;
import co.jp.xeex.chat.domains.chatmngr.group.dto.MemberDto;
import co.jp.xeex.chat.domains.chatmngr.service.UserService;
import co.jp.xeex.chat.exception.BusinessException;
import co.jp.xeex.chat.repository.UserRepository;
import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

/**
 * GetMemberGroupServiceImpl
 * 
 * @author q_thinh
 */
@Service
@AllArgsConstructor
public class GetMemberGroupServiceImpl extends ServiceBaseImpl<GetMemberGroupRequest, GetMemberGroupResponse>
        implements GetMemberGroupService {

    // repository uses
    private UserRepository userRepo;
    private UserService userService;

    @Override
    public GetMemberGroupResponse processRequest(GetMemberGroupRequest in) throws BusinessException {
        // Search group member
        List<MemberDto> memberDtos = userRepo.findByGroup(in.getGroupId());

        // Set url Avatar
        memberDtos = userService.setUrlAvatarListMember(memberDtos);

        // Response
        GetMemberGroupResponse response = new GetMemberGroupResponse();
        response.setGroupMembers(memberDtos);
        return response;
    }
}
