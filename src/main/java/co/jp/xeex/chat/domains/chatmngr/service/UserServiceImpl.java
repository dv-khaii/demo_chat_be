package co.jp.xeex.chat.domains.chatmngr.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import co.jp.xeex.chat.common.AppConstant;
import co.jp.xeex.chat.domains.chatmngr.group.dto.MemberDto;
import co.jp.xeex.chat.repository.UserRepository;
import co.jp.xeex.chat.util.EnvironmentUtil;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Implementation of the UserService interface.
 * Provides methods to retrieve and manipulate user data.
 * 
 * @author q_thinh
 */
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    // DI
    private UserRepository userRepo;
    private EnvironmentUtil environmentUtil;

    /**
     * Sets the URL avatar for each member in the provided list of MemberDto
     * objects.
     * 
     * @param memberDtos the list of MemberDto objects to update
     * @return a list of MemberDto objects with the URL avatar set
     */
    @Override
    public List<MemberDto> setUrlAvatarListMember(List<MemberDto> memberDtos) {
        // Setting image url
        for (MemberDto memberDto : memberDtos) {
            memberDto.setMemberImage(getUrlAvatarByAvatar(memberDto.getMemberImage()));
        }

        return memberDtos;
    }

    /**
     * Retrieves the URL of the avatar for a given employee code.
     *
     * @param empCd the employee code
     * @return the URL of the avatar, or null if no avatar is found
     */
    @Override
    public String getUrlAvatarByEmpCd(String empCd) {
        String avatar = userRepo.findAvatarByUser(empCd);
        return getUrlAvatarByAvatar(avatar);
    }

    /**
     * Returns the URL of the avatar image based on the provided avatar name.
     *
     * @param avatar The name of the avatar.
     * @return The URL of the avatar image.
     */
    @Override
    public String getUrlAvatarByAvatar(String avatar) {
        if (!StringUtils.isEmpty(avatar)) {
            return String.format(AppConstant.FILE_URL,
                    environmentUtil.fileHost, AppConstant.PATH_AVATAR_PREFIX, avatar);
        }

        return null;
    }
}
