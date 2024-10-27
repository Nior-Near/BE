package nior_near.server.global.auth.jwt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import nior_near.server.domain.user.exception.handler.MemberExceptionHandler;
import nior_near.server.global.common.ResponseCode;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Optional;

@Component
@Slf4j
public class TokenParser {
    private final String[] skipUrls = {"/h2-console/**"};
    public String parseBearerToken(HttpServletRequest request) {
        if (isSkipUrl(request.getRequestURI())) {
            return null;
        }

        String authorization = request.getHeader("Authorization");

        boolean hasAuthorization = StringUtils.hasText(authorization);
        if(!hasAuthorization) throw new MemberExceptionHandler(ResponseCode.ACCESS_TOKEN_NOT_FOUND);

        boolean isBearer = authorization.startsWith("Bearer ");
        if(!isBearer) throw new MemberExceptionHandler(ResponseCode.BEARER_PREFIX_VALUE_EXCEPTION);

        String token = authorization.substring(7);
        return token;
    }

    private boolean isSkipUrl(String requestUri) {
        return Arrays.stream(skipUrls)
                .anyMatch(pattern -> new AntPathMatcher().match(pattern, requestUri));
    }
}
