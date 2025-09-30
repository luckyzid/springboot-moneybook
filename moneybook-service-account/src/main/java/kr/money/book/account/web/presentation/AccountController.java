package kr.money.book.account.web.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import kr.money.book.account.web.application.AccountService;
import kr.money.book.account.web.domain.command.AccountListAbTestCommand;
import kr.money.book.account.web.domain.datatransfer.AccountCreateRequest;
import kr.money.book.account.web.domain.datatransfer.AccountCreateResponse;
import kr.money.book.account.web.domain.datatransfer.AccountInfoListResponse;
import kr.money.book.account.web.domain.datatransfer.AccountInfoResponse;
import kr.money.book.account.web.domain.datatransfer.AccountUpdateRequest;
import kr.money.book.account.web.domain.datatransfer.AccountUpdateResponse;
import kr.money.book.account.web.domain.valueobject.AccountInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
@Tag(name = "Account", description = "계정 관리 (은행/카드) API")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    @Operation(summary = "계정 리스트", description = "사용자의 계정 목록 조회")
    public ResponseEntity<AccountInfoListResponse> getAccountList(
        Authentication authentication) {

        String userKey = authentication.getName();
        List<AccountInfo> accounts = accountService.getAccountList(AccountListAbTestCommand.of(userKey));

        return ResponseEntity.ok(AccountInfoListResponse.of(accounts));
    }

    @GetMapping("/{accountIdx}")
    @Operation(summary = "계정 조회", description = "계정 정보 조회")
    public ResponseEntity<AccountInfoResponse> getAccount(
        Authentication authentication,
        @PathVariable Long accountIdx) {

        String userKey = authentication.getName();
        AccountInfo accountInfo = accountService.getAccount(userKey, accountIdx);

        return ResponseEntity.ok(AccountInfoResponse.of(accountInfo));
    }

    @PostMapping
    @Operation(summary = "계정 추가", description = "새로운 계정을 추가")
    public ResponseEntity<AccountCreateResponse> createAccount(
        Authentication authentication,
        @Valid @RequestBody AccountCreateRequest request) {

        String userKey = authentication.getName();
        AccountInfo accountInfo = accountService.createAccount(request.toAccountInfo(userKey));

        return ResponseEntity.ok(AccountCreateResponse.of(accountInfo));
    }

    @PutMapping("/{accountIdx}")
    @Operation(summary = "계정 수정", description = "기존 계정 정보를 수정")
    public ResponseEntity<AccountUpdateResponse> updateAccount(
        Authentication authentication,
        @PathVariable Long accountIdx,
        @Valid @RequestBody AccountUpdateRequest request) {

        String userKey = authentication.getName();
        AccountInfo accountInfo = accountService.updateAccount(request.toAccountInfo(userKey, accountIdx));

        return ResponseEntity.ok(AccountUpdateResponse.of(accountInfo));
    }

    @DeleteMapping("/{accountIdx}")
    @Operation(summary = "계정 삭제", description = "계정 삭제")
    public ResponseEntity<Void> deleteAccount(
        Authentication authentication,
        @PathVariable Long accountIdx) {

        String userKey = authentication.getName();
        accountService.deleteAccount(userKey, accountIdx);

        return ResponseEntity.ok().build();
    }
}
