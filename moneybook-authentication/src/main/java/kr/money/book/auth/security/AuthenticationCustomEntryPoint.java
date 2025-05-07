package kr.money.book.auth.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import kr.money.book.web.exceptions.ErrorCode;
import kr.money.book.web.response.GlobalApiResponse;
import kr.money.book.web.response.GlobalErrorResponse;
import kr.money.book.utils.JsonUtil;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

public class AuthenticationCustomEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        GlobalErrorResponse error = GlobalErrorResponse.of(ErrorCode.INVALID_UNAUTHORIZED, request.getRequestURI());
        GlobalApiResponse<Object> apiResponse = GlobalApiResponse.error(error);
        response.getWriter().write(JsonUtil.toJson(apiResponse).orElse(""));
    }
}
