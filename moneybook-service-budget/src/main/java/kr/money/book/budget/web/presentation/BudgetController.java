package kr.money.book.budget.web.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import kr.money.book.budget.web.application.BudgetService;
import kr.money.book.budget.web.domain.datatransfer.BudgetCreateRequest;
import kr.money.book.budget.web.domain.datatransfer.BudgetCreateResponse;
import kr.money.book.budget.web.domain.datatransfer.BudgetInfoListResponse;
import kr.money.book.budget.web.domain.datatransfer.BudgetInfoResponse;
import kr.money.book.budget.web.domain.datatransfer.BudgetUpdateRequest;
import kr.money.book.budget.web.domain.datatransfer.BudgetUpdateResponse;
import kr.money.book.budget.web.domain.valueobject.BudgetInfo;
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
@RequestMapping("/budget")
@Tag(name = "Budget", description = "예산 관리 API")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @GetMapping
    @Operation(summary = "예산 리스트", description = "사용자의 예산 목록 조회")
    public ResponseEntity<BudgetInfoListResponse> getBudgetList(
        Authentication authentication) {

        String userKey = authentication.getName();
        List<BudgetInfo> budgets = budgetService.getBudgetList(userKey);

        return ResponseEntity.ok(BudgetInfoListResponse.of(budgets));
    }

    @GetMapping("/{budgetIdx}")
    @Operation(summary = "예산 조회", description = "예산 정보 조희")
    public ResponseEntity<BudgetInfoResponse> getBudget(
        Authentication authentication,
        @PathVariable Long budgetIdx) {

        String userKey = authentication.getName();
        BudgetInfo budgetInfo = budgetService.getBudget(userKey, budgetIdx);

        return ResponseEntity.ok(BudgetInfoResponse.of(budgetInfo));
    }

    @PostMapping
    @Operation(summary = "예산 추가", description = "새로운 예산 추가")
    public ResponseEntity<BudgetCreateResponse> createBudget(
        Authentication authentication,
        @Valid @RequestBody BudgetCreateRequest request) {

        String userKey = authentication.getName();
        BudgetInfo budgetInfo = budgetService.createBudget(request.toBudgetInfo(userKey));

        return ResponseEntity.ok(BudgetCreateResponse.of(budgetInfo));
    }

    @PutMapping("/{budgetIdx}")
    @Operation(summary = "예산 수정", description = "기존 예산 정보 수정")
    public ResponseEntity<BudgetUpdateResponse> updateBudget(
        Authentication authentication,
        @PathVariable Long budgetIdx,
        @Valid @RequestBody BudgetUpdateRequest request) {

        String userKey = authentication.getName();
        BudgetInfo budgetInfo = budgetService.updateBudget(request.toBudgetInfo(userKey, budgetIdx));

        return ResponseEntity.ok(BudgetUpdateResponse.of(budgetInfo));
    }

    @DeleteMapping("/{budgetIdx}")
    @Operation(summary = "예산 삭제", description = "예산 삭제")
    public ResponseEntity<Void> deleteBudget(
        Authentication authentication,
        @PathVariable Long budgetIdx) {

        String userKey = authentication.getName();
        budgetService.deleteBudget(userKey, budgetIdx);

        return ResponseEntity.ok().build();
    }
}
