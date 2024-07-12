package co.jp.xeex.chat.domains.auth.login;

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

@RestController
@AllArgsConstructor
public class LoginController extends ControllerBase<LoginRequest> {
    private LoginService service;
    private HttpServletResponse httpServletResponse;
    private JwtTokenService jwtTokenService;

    @Override
    @PostMapping(RestApiEndPoints.LOGIN)
    public final ResponseEntity<?> restApi(@RequestBody LoginRequest request) throws BusinessException {
        super.preProcessRequest(request);
        LoginResponse out = service.execute(request);
        if (out != null) {
            // Setting cookie header
            String cookieVal = String.format(AppConstant.COOKIE_VALUE,
                    out.getAccessToken(), jwtTokenService.getExpirationDate(out.getAccessToken()));
            httpServletResponse.addHeader(AppConstant.COOKIE_NAME, cookieVal);
        }
        return out != null ? createSuccessResponse(out) : createNullFailResponse();
    }
}
