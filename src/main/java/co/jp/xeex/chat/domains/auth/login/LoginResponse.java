package co.jp.xeex.chat.domains.auth.login;

import co.jp.xeex.chat.domains.auth.dto.AppInfoDto;
import lombok.Data;

/**
 * Represents the response object for a login operation.
 * 
 * @author v_long, q_thinh
 */
@Data
public class LoginResponse {
    /**
     * The access token generated for the user.
     */
    private String accessToken;

    /**
     * The refresh token used to obtain a new access token.
     */
    private String refreshToken;

    /**
     * The ID of the user.
     */
    private String userId;

    /**
     * The username of the user.
     */
    private String userName;

    /**
     * The email address of the user.
     */
    private String email;

    /**
     * The full name of the user.
     */
    private String fullName;

    /**
     * The avatar of the user.
     */
    private String avatar;

    /**
     * The role code list associated with the user.
     */
    private String roleIdList;

    /**
     * The language preference of the user.
     */
    private String lang;

    /**
     * Represents the app configuration data for the login response.
     */
    private AppInfoDto appInfo;
}
