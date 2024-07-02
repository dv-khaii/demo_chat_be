package co.jp.xeex.chat.domains.chatmngr.friend.getnone;

import co.jp.xeex.chat.base.ServiceBaseImpl;
import co.jp.xeex.chat.domains.chatmngr.group.dto.MemberDto;
import co.jp.xeex.chat.domains.chatmngr.service.UserService;
import co.jp.xeex.chat.exception.BusinessException;
import co.jp.xeex.chat.repository.UserRepository;
import lombok.AllArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

/**
 * GetNoneFriendMemberServiceImpl
 * 
 * @author q_thinh
 */
@Service
@AllArgsConstructor
public class GetNoneFriendMemberServiceImpl
        extends ServiceBaseImpl<GetNoneFriendMemberRequest, GetNoneFriendMemberResponse>
        implements GetNoneFriendMemberService {

    // DI
    private UserService userService;
    private UserRepository userRepo;

    @Override
    public GetNoneFriendMemberResponse processRequest(GetNoneFriendMemberRequest in) throws BusinessException {
        // Get list none friend
        List<MemberDto> memberDtos = userRepo.findNoneByFriend(in.requestBy);

        // Set url Avatar
        memberDtos = userService.setUrlAvatarListMember(memberDtos);

        // Response
        GetNoneFriendMemberResponse response = new GetNoneFriendMemberResponse();
        response.setMembers(memberDtos);
        return response;
    }
}
