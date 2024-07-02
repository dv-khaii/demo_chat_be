package co.jp.xeex.chat.domains.chatmngr.service;

import org.springframework.stereotype.Service;

import co.jp.xeex.chat.domains.chatmngr.group.dto.MemberDto;
import java.util.List;

/**
 * FileUtil
 * 
 * @author q_thinh
 */
@Service
public interface UserService {
    /**
     * Sets the URL avatar for each member in the provided list of MemberDto
     * objects.
     *
     * @param memberDtos the list of MemberDto objects to update
     * @return a list of MemberDto objects with the URL avatar set
     */
    public List<MemberDto> setUrlAvatarListMember(List<MemberDto> memberDtos);

    /**
     * Retrieves the URL of the avatar for a given employee code.
     *
     * @param empCd the employee code for which to retrieve the avatar URL
     * @return the URL of the avatar as a String
     */
    public String getUrlAvatarByEmpCd(String empCd);

    /**
     * Retrieves the URL of the avatar based on the given avatar name.
     *
     * @param avatar the name of the avatar
     * @return the URL of the avatar
     */
    public String getUrlAvatarByAvatar(String avatar);
}
