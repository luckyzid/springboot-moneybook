package kr.money.book.user.web.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.money.book.user.web.application.UserService;
import kr.money.book.user.web.domain.datatransfer.BlockRequest;
import kr.money.book.user.web.domain.datatransfer.UnBlockRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/manager")
@Tag(name = "User", description = "사용자 관리 API")
public class UserManageController {

    private final UserService userService;

    public UserManageController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/block")
    @Operation(summary = "사용자 차단", description = "지정된 사용자를 차단합니다.")
    public ResponseEntity<Void> blockUser(
        @Valid @RequestBody BlockRequest request) {

        userService.blockUser(request.userKey());

        return ResponseEntity.ok().build();
    }

    @PutMapping("/unblock")
    @Operation(summary = "사용자 차단 해제", description = "지정된 사용자의 차단을 해제합니다.")
    public ResponseEntity<Void> unblockUser(
        @Valid @RequestBody UnBlockRequest request) {

        userService.unblockUser(request.userKey());

        return ResponseEntity.ok().build();
    }
}
