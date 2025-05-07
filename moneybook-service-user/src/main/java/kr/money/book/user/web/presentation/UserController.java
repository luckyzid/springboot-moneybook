package kr.money.book.user.web.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.money.book.user.web.application.TokenService;
import kr.money.book.user.web.application.UserService;
import kr.money.book.user.web.domain.datatransfer.EmailCreateRequest;
import kr.money.book.user.web.domain.datatransfer.EmailCreateResponse;
import kr.money.book.user.web.domain.datatransfer.EmailLoginRequest;
import kr.money.book.user.web.domain.datatransfer.EmailLoginResponse;
import kr.money.book.user.web.domain.datatransfer.NameUpdateRequest;
import kr.money.book.user.web.domain.datatransfer.NameUpdateResponse;
import kr.money.book.user.web.domain.datatransfer.PasswordUpdateRequest;
import kr.money.book.user.web.domain.datatransfer.PasswordUpdateResponse;
import kr.money.book.user.web.domain.datatransfer.TokenLoginRequest;
import kr.money.book.user.web.domain.datatransfer.TokenLoginResponse;
import kr.money.book.user.web.domain.datatransfer.TokenRefreshRequest;
import kr.money.book.user.web.domain.datatransfer.TokenRefreshResponse;
import kr.money.book.user.web.domain.datatransfer.UserInfoResponse;
import kr.money.book.user.web.domain.valueobject.AuthTokenInfo;
import kr.money.book.user.web.domain.valueobject.UserInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "사용자 관리 API")
public class UserController {

    private final UserService userService;
    private final TokenService tokenService;

    public UserController(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @GetMapping("/info")
    @Operation(summary = "사용자 정보 조회", description = "현재 사용자 유저 정보")
    public ResponseEntity<UserInfoResponse> getUserInfo(
        Authentication authentication) {

        UserInfo userInfo = userService.getUserInfo(authentication.getName());

        return ResponseEntity.ok(UserInfoResponse.of(userInfo));
    }

    @PostMapping("/register")
    @Operation(summary = "이메일 회원가입", description = "이메일로 회원가입 처리")
    public ResponseEntity<EmailCreateResponse> createWithEmail(
        @Valid @RequestBody EmailCreateRequest request) {

        UserInfo userInfo = userService.createWithEmail(request.toUserInfo());

        return ResponseEntity.ok(EmailCreateResponse.of(userInfo));
    }

    @PostMapping("/login/email")
    @Operation(summary = "이메일 로그인", description = "이메일과 비밀번호로 로그인 후 토큰 발급")
    public ResponseEntity<EmailLoginResponse> loginWithEmail(
        @Valid @RequestBody EmailLoginRequest request,
        @RequestHeader("User-Agent") String userAgent,
        HttpServletRequest httpReq) {

        String ipAddress = httpReq.getRemoteAddr();
        AuthTokenInfo authTokenInfo = userService.loginWithEmail(request.toUserInfo(ipAddress, userAgent));

        return ResponseEntity.ok(EmailLoginResponse.of(authTokenInfo));
    }

    @PostMapping("/login/token")
    @Operation(summary = "토큰 로그인", description = "자체 발급 userToken 기반 로그인")
    public ResponseEntity<TokenLoginResponse> loginWIthUserToken(
        @Valid @RequestBody TokenLoginRequest request,
        @RequestHeader("User-Agent") String userAgent,
        HttpServletRequest httpReq) {

        String ipAddress = httpReq.getRemoteAddr();
        AuthTokenInfo authTokenInfo = userService.loginWithUserToken(request.toUserInfo(ipAddress, userAgent));

        return ResponseEntity.ok(TokenLoginResponse.of(authTokenInfo));
    }

    @PostMapping("/token/refresh")
    @Operation(summary = "토큰 갱신", description = "현재 사용자 토큰 재발급(access refresh token)")
    public ResponseEntity<TokenRefreshResponse> renewToken(
        @Valid @RequestBody TokenRefreshRequest request) {

        AuthTokenInfo authTokenInfo = tokenService.renewToken(request.refreshToken());

        return ResponseEntity.ok(TokenRefreshResponse.of(authTokenInfo));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "현재 사용자 인증 해제")
    public ResponseEntity<Void> logout(
        Authentication authentication) {

        userService.logout(authentication.getName());

        return ResponseEntity.ok().build();
    }

    @PutMapping("/name")
    @Operation(summary = "이름 변경", description = "현재 사용자 이름 변경")
    public ResponseEntity<NameUpdateResponse> updateName(
        Authentication authentication,
        @Valid @RequestBody NameUpdateRequest request) {

        UserInfo userInfo = userService.updateName(authentication.getName(), request.name());

        return ResponseEntity.ok(NameUpdateResponse.of(userInfo));
    }

    @PutMapping("/password")
    @Operation(summary = "패스워드 변경", description = "현재 사용자 패스워드 변경")
    public ResponseEntity<PasswordUpdateResponse> updatePassword(
        Authentication authentication,
        @Valid @RequestBody PasswordUpdateRequest request) {

        UserInfo userInfo = userService.updatePassword(
            authentication.getName(),
            request.currentPassword(),
            request.newPassword()
        );

        return ResponseEntity.ok(PasswordUpdateResponse.of(userInfo));
    }

    @DeleteMapping
    @Operation(summary = "회원 탈퇴(삭제)", description = "현재 사용자 탈퇴")
    public ResponseEntity<Void> deleteUser(
        Authentication authentication) {

        userService.deleteUser(authentication.getName());

        return ResponseEntity.ok().build();
    }
}
