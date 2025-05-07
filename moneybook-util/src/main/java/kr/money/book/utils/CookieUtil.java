package kr.money.book.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    public static String get(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static void add(HttpServletResponse response, String cookieName, String cookieValue, long expireSecond) {
        String cookieHeaderValue =
            cookieName + "=" + cookieValue + "; Path=/; Max-Age=" + expireSecond + "; Secure; HttpOnly; SameSite=None";
        response.addHeader("Set-Cookie", cookieHeaderValue);
    }

    public static void remove(HttpServletResponse response, String cookieName) {
        String cookieHeaderValue = cookieName + "=" + null + "; Path=/; Max-Age=0; Secure; HttpOnly; SameSite=None";
        response.addHeader("Set-Cookie", cookieHeaderValue);
    }
}
