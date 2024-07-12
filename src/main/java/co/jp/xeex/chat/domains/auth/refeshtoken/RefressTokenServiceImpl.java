package co.jp.xeex.chat.domains.auth.refeshtoken;

import org.springframework.stereotype.Service;

import co.jp.xeex.chat.entity.User;
import co.jp.xeex.chat.exception.BusinessException;
import co.jp.xeex.chat.repository.UserRepository;
import co.jp.xeex.chat.token.JwtTokenService;
import co.jp.xeex.chat.token.TokenClaimData;
import co.jp.xeex.chat.token.TokenType;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RefressTokenServiceImpl implements RefressTokenService {
    private static final String LOGIN_ERR_USER_NOT_EXITED = "LOGIN_ERR_USER_NOT_EXITED";
    private static final String REFRESH_TOKEN_ERR_REFRESH_TOKEN_INVALID = "REFRESH_TOKEN_ERR_REFRESH_TOKEN_INVALID";

    private JwtTokenService jwtTokenService;
    private UserRepository userRepository;

    @Override
    public RefreshTokenResponse execute(RefreshTokenRequest request) throws BusinessException {
        if (jwtTokenService.validateToken(request.refreshToken, false)) {
            TokenClaimData claim = jwtTokenService.getClaimData(request.refreshToken);// throw new bus exception here
            User user = userRepository.findByUserName(claim.getUserName());
            if (user == null) {
                throw new BusinessException(LOGIN_ERR_USER_NOT_EXITED, request.lang);
            }
            if (!TokenType.REFRESH.equals(claim.getTokenType())) {
                throw new BusinessException(REFRESH_TOKEN_ERR_REFRESH_TOKEN_INVALID, request.lang);
            }
            RefreshTokenResponse dto = new RefreshTokenResponse();

            // create new refresh token from the refresh token
            String newRefreshToken = createToken(user, TokenType.REFRESH, request.lang);
            dto.setRefreshToken(newRefreshToken);

            // create new access token from the refresh token
            String newAccessToken = createToken(user, TokenType.ACCESS, request.lang);
            dto.setAccessToken(newAccessToken);
            return dto;
        } else {
            throw new BusinessException(REFRESH_TOKEN_ERR_REFRESH_TOKEN_INVALID, "en");
        }
    }

    /**
     * Creates a token for the specified user with the given token type and
     * language.
     *
     * @param user      The user for whom the token is being created.
     * @param tokenType The type of the token.
     * @param lang      The language for the token.
     * @return The created token.
     */
    private String createToken(User user, TokenType tokenType, String lang) {
        return jwtTokenService.createToken(new TokenClaimData() {
            @Override
            public String getLang() {
                return lang;
            }

            @Override
            public String getUserName() {
                return user.getEmpCd();
            }

            @Override
            public String getEmail() {
                return user.getEmail();
            }

            @Override
            public String getFullName() {
                return user.getFullName();
            }

            @Override
            public String getUserId() {
                return user.getId();
            }

            @Override
            public TokenType getTokenType() {
                return tokenType;
            }

            @Override
            public String[] getRoles() {
                // Go to Db get roles list to create token
                return new String[0];
            }
        });
    }
}
