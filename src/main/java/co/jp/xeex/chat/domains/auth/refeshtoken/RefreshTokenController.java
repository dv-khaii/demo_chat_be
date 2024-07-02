package co.jp.xeex.chat.domains.auth.refeshtoken;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import co.jp.xeex.chat.base.ControllerBase;
import co.jp.xeex.chat.common.RestApiEndPoints;
import co.jp.xeex.chat.exception.BusinessException;

/**
 * Refeshtoken controller
 * 
 * @author v_long
 */


@RestController
public class RefreshTokenController extends ControllerBase<RefreshTokenRequest> {
    private final RefressTokenService refreshTokenService;

    public RefreshTokenController(RefressTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    /**
     * Allows use of refresh token in header
     */
    @Override
    protected boolean getAllowRefreshToken() {
        return true;
    }

    @Override
    public ResponseEntity<?> restApi(@RequestBody RefreshTokenRequest request) throws BusinessException {
        return null;
    }

    @PostMapping(RestApiEndPoints.REFRESH_TOKEN)
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request)   {
        try {
            // set default language
            if (null != request && request.lang == null) {
                request.lang = getCurrentLang();
            }
            super.preProcessRequest(request);
            RefreshTokenResponse out = refreshTokenService.execute(request);
            return out != null ? createSuccessResponse(out) : createNullFailResponse();
        } catch (BusinessException e) {
            return super.createErrorResponse(e, HttpStatus.UNAUTHORIZED);
        }
    }
}
