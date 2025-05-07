package kr.money.book.utils;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class HttpRequestUtil {

    public static Map<String, String> getHeadersMap(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }

        return headers;
    }

    public static String getCallbackJson(HttpServletRequest request) throws IOException {
        return new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    public static String getParameterStrings(HttpServletRequest request) {
        try {
            Enumeration<String> params = request.getParameterNames();
            StringJoiner joiner = new StringJoiner(",");

            while (params.hasMoreElements()) {
                String paramName = params.nextElement();
                joiner.add(paramName + "=" + request.getParameter(paramName));
            }

            return joiner.toString();
        } catch (Exception e) {
            return "";
        }
    }
}
