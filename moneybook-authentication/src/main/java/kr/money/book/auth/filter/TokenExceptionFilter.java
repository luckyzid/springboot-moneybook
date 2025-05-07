package kr.money.book.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import kr.money.book.web.exceptions.BaseErrorCode;
import kr.money.book.web.exceptions.ErrorCode;
import kr.money.book.web.response.GlobalApiResponse;
import kr.money.book.web.response.GlobalErrorResponse;
import kr.money.book.auth.exceptions.AuthException;
import kr.money.book.utils.JsonUtil;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

public class TokenExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (AuthException e) {
            sendErrorResponse(response, request, e.getErrorCode());
        } catch (Exception e) {
            sendErrorResponse(response, request, ErrorCode.INVALID_UNAUTHORIZED);
        }
    }

    private void sendErrorResponse(
        HttpServletResponse response,
        HttpServletRequest request,
        BaseErrorCode errorCode) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        GlobalErrorResponse error = GlobalErrorResponse.of(errorCode, request.getRequestURI());
        GlobalApiResponse<Object> apiResponse = GlobalApiResponse.error(error);
        response.getWriter().write(JsonUtil.toJson(apiResponse).orElse(""));
    }
}
