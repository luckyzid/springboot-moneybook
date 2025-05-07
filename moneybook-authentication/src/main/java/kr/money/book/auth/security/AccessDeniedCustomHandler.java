package kr.money.book.auth.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import kr.money.book.web.exceptions.ErrorCode;
import kr.money.book.web.response.GlobalApiResponse;
import kr.money.book.web.response.GlobalErrorResponse;
import kr.money.book.utils.JsonUtil;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

public class AccessDeniedCustomHandler implements AccessDeniedHandler {

    @Override
    public void handle(
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        GlobalErrorResponse error = GlobalErrorResponse.of(ErrorCode.INVALID_FORBIDDEN, request.getRequestURI());
        GlobalApiResponse<Object> apiResponse = GlobalApiResponse.error(error);
        response.getWriter().write(JsonUtil.toJson(apiResponse).orElse(""));
    }
}
