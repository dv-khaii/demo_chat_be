package co.jp.xeex.chat.domains.chatmngr.dept.getmbr;

import co.jp.xeex.chat.base.ServiceBaseImpl;
import co.jp.xeex.chat.domains.chatmngr.group.dto.MemberDto;
import co.jp.xeex.chat.domains.chatmngr.service.UserService;
import co.jp.xeex.chat.exception.BusinessException;
import co.jp.xeex.chat.repository.UserRepository;
import lombok.AllArgsConstructor;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * GetDeptMemberServiceImpl
 * 
 * @author q_thinh
 */
@Service
@AllArgsConstructor
public class GetDeptMemberServiceImpl extends ServiceBaseImpl<GetDeptMemberRequest, GetDeptMemberResponse>
        implements GetDeptMemberService {

    // DI
    private UserRepository userRepo;
    private UserService userService;

    @Override
    public GetDeptMemberResponse processRequest(GetDeptMemberRequest in) throws BusinessException {
        in.setDeptCd(StringUtils.EMPTY.equals(in.getDeptCd()) ? null : in.getDeptCd());

        // Search dept member
        List<MemberDto> memberDtos = userRepo.findByDept(in.getDeptCd());

        // Set url Avatar
        memberDtos = userService.setUrlAvatarListMember(memberDtos);

        // Response
        GetDeptMemberResponse response = new GetDeptMemberResponse();
        response.setMembers(memberDtos);
        return response;
    }
}
