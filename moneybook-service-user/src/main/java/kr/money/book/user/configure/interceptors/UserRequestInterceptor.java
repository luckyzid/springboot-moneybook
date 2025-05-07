package kr.money.book.user.configure.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import kr.money.book.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class UserRequestInterceptor implements HandlerInterceptor {

    @Override
    public void afterCompletion(
        HttpServletRequest request,
        HttpServletResponse response,
        Object handler,
        Exception exception) {

        if (StringUtils.equals(request.getMethod(), HttpMethod.OPTIONS.name())) {
            return;
        }
        String headers = JsonUtil.toJson(getHeaders(request)).orElse("");
        String params = JsonUtil.toJson(getParameters(request)).orElse("");
        log.debug("Request Headers: {} | Parameters: {}", headers, params);
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {

        return Collections.list(request.getHeaderNames())
            .stream()
            .collect(Collectors.toMap(name -> name, request::getHeader));
    }

    private Map<String, String> getParameters(HttpServletRequest request) {

        return Collections.list(request.getParameterNames())
            .stream()
            .collect(Collectors.toMap(name -> name, request::getParameter));
    }
}
