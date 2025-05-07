package kr.money.book.budget.web.application;

import java.math.BigDecimal;
import java.util.List;
import kr.money.book.budget.web.domain.valueobject.BudgetInfo;
import kr.money.book.budget.web.exceptions.BudgetException;
import kr.money.book.budget.web.infra.BudgetAuthenticationService;
import kr.money.book.budget.web.infra.BudgetPersistenceAdapter;
import kr.money.book.common.valueobject.CacheInform;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BudgetService {

    private final BudgetPersistenceAdapter budgetPersistenceAdapter;
    private final BudgetAuthenticationService budgetAuthenticationService;

    public BudgetService(
        BudgetPersistenceAdapter budgetPersistenceAdapter,
        BudgetAuthenticationService budgetAuthenticationService) {

        this.budgetPersistenceAdapter = budgetPersistenceAdapter;
        this.budgetAuthenticationService = budgetAuthenticationService;
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public BudgetInfo createBudget(BudgetInfo budgetInfo) {
        // Validate amount
        if (budgetInfo.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BudgetException(BudgetException.ErrorCode.INVALID_AMOUNT);
        }

        validateOwnership(budgetInfo.userKey(), budgetInfo.accountIdx(), budgetInfo.categoryIdx());
        BudgetInfo savedBudget = budgetPersistenceAdapter.saveBudget(budgetInfo);

        return savedBudget;
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public BudgetInfo updateBudget(BudgetInfo budgetInfo) {

        validateOwnership(budgetInfo.userKey(), budgetInfo.accountIdx(), budgetInfo.categoryIdx());
        BudgetInfo updatedBudget = budgetPersistenceAdapter.updateBudget(budgetInfo);

        return updatedBudget;
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = true,
        rollbackFor = Exception.class
    )
    public List<BudgetInfo> getBudgetList(String userKey) {

        return budgetPersistenceAdapter.findBudgetsByUserKey(userKey);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = true,
        rollbackFor = Exception.class
    )
    public BudgetInfo getBudget(String userKey, Long budgetIdx) {

        return budgetPersistenceAdapter.findByUserKeyAndBudgetIdx(userKey, budgetIdx)
            .orElseThrow(() -> new BudgetException(BudgetException.ErrorCode.BUDGET_NOT_FOUND));
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public void deleteBudget(String userKey, Long budgetIdx) {
        // Check if budget exists
        BudgetInfo budget = budgetPersistenceAdapter.findByUserKeyAndBudgetIdx(userKey, budgetIdx)
            .orElseThrow(() -> new BudgetException(BudgetException.ErrorCode.BUDGET_NOT_FOUND));

        budgetPersistenceAdapter.deleteBudget(userKey, budgetIdx);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public void deleteAccount(String userKey) {

        budgetPersistenceAdapter.deleteByUserKey(userKey);
    }

    private void validateOwnership(String userKey, Long accountIdx, Long categoryIdx) {

        CacheInform cacheInform = budgetAuthenticationService.getCacheInform(userKey);
        if (!cacheInform.hasAccount(accountIdx)) {
            throw new BudgetException(BudgetException.ErrorCode.UNAUTHORIZED_ACCESS);
        }
        if (!cacheInform.hasCategory(categoryIdx)) {
            throw new BudgetException(BudgetException.ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }
}
