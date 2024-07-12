package co.jp.xeex.chat.domains.auth.refeshtoken;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import co.jp.xeex.chat.base.ControllerBase;
import co.jp.xeex.chat.common.AppConstant;
import co.jp.xeex.chat.common.RestApiEndPoints;
import co.jp.xeex.chat.exception.BusinessException;
import co.jp.xeex.chat.token.JwtTokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

/**
 * Refeshtoken controller
 * 
 * @author v_long
 */

@RestController
@AllArgsConstructor
public class RefreshTokenController extends ControllerBase<RefreshTokenRequest> {
    private RefressTokenService refreshTokenService;
    private HttpServletResponse httpServletResponse;
    private JwtTokenService jwtTokenService;

    /**
     * Allows use of refresh token in header
     */
    @Override
    protected boolean getAllowRefreshToken() {
        return true;
    }

    @Override
    @PostMapping(RestApiEndPoints.REFRESH_TOKEN)
    public ResponseEntity<?> restApi(@RequestBody RefreshTokenRequest request) {
        try {
            // set default language
            if (null != request && request.lang == null) {
                request.lang = getCurrentLang();
            }
            super.preProcessRequest(request);
            RefreshTokenResponse out = refreshTokenService.execute(request);

            // Setting cookie header
            String cookie = String.format(AppConstant.COOKIE_VALUE,
                    out.getAccessToken(), jwtTokenService.getExpirationDate(out.getAccessToken()));
            httpServletResponse.addHeader(AppConstant.COOKIE_NAME, cookie);

            return out != null ? createSuccessResponse(out) : createNullFailResponse();
        } catch (BusinessException e) {
            return super.createErrorResponse(e, HttpStatus.UNAUTHORIZED);
        }
    }
}
