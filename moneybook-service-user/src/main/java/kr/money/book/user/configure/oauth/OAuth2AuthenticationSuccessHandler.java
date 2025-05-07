package kr.money.book.user.configure.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import kr.money.book.web.response.GlobalApiResponse;
import kr.money.book.user.web.application.TokenService;
import kr.money.book.user.web.domain.valueobject.AuthTokenInfo;
import kr.money.book.user.web.domain.valueobject.PrincipalDetails;
import kr.money.book.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenService tokenService;

    public OAuth2AuthenticationSuccessHandler(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication) throws IOException {

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AuthTokenInfo authTokenInfo = getTokenPair(request, principal);

//      @Value("${spring.security.oauth2.client.login.sucess.url:/login/oauth2/success}")
//		String redirectUrl = UriComponentsBuilder.fromUriString(oauthLoginSuccessEndPoint)
//				.queryParam("accessToken", tokenPair.accessToken())
//				.queryParam("refreshToken", tokenPair.refreshToken())
//				.queryParam("userToken", tokenPair.userToken())
//				.build()
//				.toUriString();
//
//		response.sendRedirect(redirectUrl);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(
            JsonUtil.toJson(GlobalApiResponse.success(authTokenInfo)).orElse("")
        );
    }

    private AuthTokenInfo getTokenPair(HttpServletRequest request, PrincipalDetails principal) {
        String state = request.getParameter("state");
        String deviceInfo = "Unknown";
        String ipAddress = request.getRemoteAddr();

        // state=device:iphone|ip:127.0.0.1
        if (state != null && state.contains("|")) {
            String[] parts = state.split("\\|");
            for (String part : parts) {
                if (part.startsWith("device:")) {
                    deviceInfo = part.substring("device:".length());
                } else if (part.startsWith("ip:")) {
                    ipAddress = part.substring("ip:".length());
                }
            }
        }

        if ("Unknown".equals(deviceInfo)) {
            throw new IllegalArgumentException("Device 정보가 누락되었습니다.");
        }
        if (ipAddress == null || "0:0:0:0:0:0:0:1".equals(ipAddress) || "127.0.0.1".equals(
            ipAddress)) {
            throw new IllegalArgumentException("유효한 IP 주소가 필요합니다.");
        }

        return tokenService.generateTokens(principal.getUser(), deviceInfo, ipAddress);
    }
}
