package co.jp.xeex.chat.token;

import java.io.IOException;
import java.util.Set;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.gson.Gson;

import co.jp.xeex.chat.base.ResponseBody;
import co.jp.xeex.chat.common.RestApiEndPoints;
import co.jp.xeex.chat.exception.BusinessException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j;

/**
 * This class is responsible for filtering incoming requests and validating JWT
 * tokens. It extends
 * the OncePerRequestFilter class from Spring Security.
 * 
 * @author v_long
 */
@Log4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenService jwtTokenService;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    // list of urls that are excluded from the filter
    private static final Set<String> EXCLUDED_URLS = Set.of(
            "**/login/**",
            "**/ws/**",
            "**/refresh/token**"
    // "**/file/stream/**", // test media
    // "http://localhost:8080/favicon.ico"// test media
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String path = request.getRequestURL().toString();
            String clientIP = request.getRemoteAddr();
            log.info("Request path: " + path + ", Client IP: " + clientIP);
            // check if the request url is in the excluded list
            if (EXCLUDED_URLS.stream()
                    .anyMatch(url -> new AntPathMatcher().match(url.toLowerCase(), path.toLowerCase()))) {
                // next chain
                filterChain.doFilter(request, response);// skip
                return;
            }

            // check token (from Authorization header or cookie)
            boolean isLogout = path.contains(RestApiEndPoints.LOGOUT);
            String token = request.getHeader(TokenConsts.AUTH_HEADER) == null ? getTokenCookie(request.getCookies())
                    : request.getHeader(TokenConsts.AUTH_HEADER);

            if (jwtTokenService.validateToken(token, isLogout)) {
                UserDetails userDetails = jwtTokenService.getUserDetailsFromToken(token);
                // get and store claim data into authentication
                TokenClaimData claimData = jwtTokenService.getClaimData(token);
                if (userDetails != null) {
                    // Create an authentication object
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, claimData, userDetails.getAuthorities());
                    // Set the authentication object in the SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    filterChain.doFilter(request, response);
                    return; // passed token
                }
            }
            // error token
            log.error("Invalid token");
            throw new IOException("Invalid token: " + token);
        } catch (Exception e) {
            log.error("Error checking token: " + e.getMessage());
            // build error response
            ResponseBody dto = new ResponseBody();
            dto.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            if (e instanceof BusinessException) {
                BusinessException be = (BusinessException) e;
                dto.setMessage(be.getMessage());
                dto.setMessageId(be.getMessageId());
                dto.setData(null);
                dto.setHasError(true);
                dto.setErrors(be.getMessage());
            } else {
                dto.setMessage(e.getMessage());
                dto.setMessageId(e.getMessage());
                dto.setData(null);
                dto.setHasError(true);
                dto.setErrors(e.getMessage());
            }
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(new Gson().toJson(dto));
            response.flushBuffer();
        }
    }

    /**
     * Retrieves the token from the provided cookies.
     *
     * @param cookies The array of cookies to search for the token.
     * @return The token value if found, or null if not found.
     */
    private String getTokenCookie(Cookie[] cookies) {
        // Get token from cookie
        if (cookies != null && cookies.length != 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("Token")) {
                    return "Bearer " + cookie.getValue();
                }
            }
        }

        return null;
    }

}
