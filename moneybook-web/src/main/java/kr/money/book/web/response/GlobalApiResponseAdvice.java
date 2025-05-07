package kr.money.book.web.response;

import jakarta.servlet.http.HttpServletRequest;
import kr.money.book.utils.JsonUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class GlobalApiResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(
        MethodParameter returnType,
        Class<? extends HttpMessageConverter<?>> converterType) {

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String requestPath = request.getRequestURI();
            if (requestPath.startsWith("/swagger-")) {
                return false;
            }
        }

        return !GlobalApiResponse.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public Object beforeBodyWrite(
        Object body,
        MethodParameter returnType,
        MediaType selectedContentType,
        Class<? extends HttpMessageConverter<?>> selectedConverterType,
        ServerHttpRequest request,
        ServerHttpResponse response) {

        String path = request.getURI().getPath();
        if (path.startsWith("/swagger-") || path.startsWith("/v3/api-docs")) {
            return body;
        }

        if (body instanceof GlobalApiResponse<?>) {
            return body;
        }

        if (body instanceof String) {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return JsonUtil.toJson(GlobalApiResponse.success(body))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "응답 변환 중 에러 발생"));
        }

        return GlobalApiResponse.success(body);
    }
}
