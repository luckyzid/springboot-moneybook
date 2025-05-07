package kr.money.book.budget.web.infra;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import kr.money.book.budget.web.domain.entity.Budget;
import kr.money.book.budget.web.domain.entity.BudgetAccount;
import kr.money.book.budget.web.domain.entity.BudgetCategory;
import kr.money.book.budget.web.domain.mapper.BudgetInfoToBudgetMapper;
import kr.money.book.budget.web.domain.mapper.BudgetToBudgetInfoMapper;
import kr.money.book.budget.web.domain.repository.BudgetAccountRepository;
import kr.money.book.budget.web.domain.repository.BudgetCategoryRepository;
import kr.money.book.budget.web.domain.repository.BudgetRepository;
import kr.money.book.budget.web.domain.valueobject.BudgetAmount;
import kr.money.book.budget.web.domain.valueobject.BudgetInfo;
import kr.money.book.budget.web.exceptions.BudgetException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class BudgetPersistenceAdapter {

    private final BudgetRepository budgetRepository;
    private final BudgetCategoryRepository budgetCategoryRepository;
    private final BudgetAccountRepository budgetAccountRepository;
    private final BudgetInfoToBudgetMapper budgetInfoToBudgetMapper;
    private final BudgetToBudgetInfoMapper budgetToBudgetInfoMapper;

    public BudgetPersistenceAdapter(
        BudgetRepository budgetRepository,
        BudgetCategoryRepository budgetCategoryRepository,
        BudgetAccountRepository budgetAccountRepository,
        BudgetInfoToBudgetMapper budgetInfoToBudgetMapper,
        BudgetToBudgetInfoMapper budgetToBudgetInfoMapper) {

        this.budgetRepository = budgetRepository;
        this.budgetCategoryRepository = budgetCategoryRepository;
        this.budgetAccountRepository = budgetAccountRepository;
        this.budgetInfoToBudgetMapper = budgetInfoToBudgetMapper;
        this.budgetToBudgetInfoMapper = budgetToBudgetInfoMapper;
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public BudgetInfo saveBudget(BudgetInfo budgetInfo) {

        Budget budget = budgetInfoToBudgetMapper.map(budgetInfo);
        applyAdjustments(budget, false);
        Budget savedBudget = budgetRepository.save(budget);

        return budgetToBudgetInfoMapper.map(savedBudget);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public BudgetInfo updateBudget(BudgetInfo budgetInfo) {

        Budget foundBudget = budgetRepository.findByIdx(budgetInfo.idx())
            .orElseThrow(() -> new BudgetException(BudgetException.ErrorCode.BUDGET_NOT_FOUND));

        applyAdjustments(foundBudget, true);

        foundBudget.updateBudget(
            budgetInfo.accountIdx(),
            budgetInfo.categoryIdx(),
            budgetInfo.type(),
            budgetInfo.amount(),
            budgetInfo.comment(),
            budgetInfo.transactionDate()
        );
        applyAdjustments(foundBudget, false);

        return budgetToBudgetInfoMapper.map(foundBudget);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public void deleteBudget(String userKey, Long budgetIdx) {

        Budget foundBudget = budgetRepository.findByIdx(budgetIdx)
            .filter(b -> b.getUserKey().equals(userKey))
            .orElseThrow(() -> new BudgetException(BudgetException.ErrorCode.BUDGET_NOT_FOUND));

        applyAdjustments(foundBudget, true);
        budgetRepository.delete(foundBudget);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = true,
        rollbackFor = Exception.class
    )
    public List<BudgetInfo> findBudgetsByUserKey(String userKey) {

        return budgetRepository.findByUserKey(userKey).stream()
            .map(budgetToBudgetInfoMapper::map)
            .collect(Collectors.toList());
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = true,
        rollbackFor = Exception.class
    )
    public Optional<BudgetInfo> findByUserKeyAndBudgetIdx(String userKey, Long budgetIdx) {

        return budgetRepository.findByIdx(budgetIdx)
            .filter(b -> b.getUserKey().equals(userKey))
            .map(budgetToBudgetInfoMapper::map);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.REPEATABLE_READ,
        readOnly = false,
        rollbackFor = Exception.class
    )
    public void deleteByUserKey(String userKey) {

        budgetRepository.deleteByUserKey(userKey);
        budgetCategoryRepository.deleteByUserKey(userKey);
        budgetAccountRepository.deleteByUserKey(userKey);
    }

    private void applyAdjustments(Budget budget, boolean isSubtract) {

        BudgetAmount adjustment = budget.toBudgetAmount(isSubtract);
        adjustAccount(adjustment);
        adjustCategory(adjustment);
    }

    private void adjustAccount(BudgetAmount adjustment) {

        BudgetAccount account = budgetAccountRepository.findByUserKeyAndAccountIdxAndTransactionDate(
            adjustment.userKey(),
            adjustment.accountIdx(),
            adjustment.transactionDate().toLocalDate()
        ).orElseGet(() -> adjustment.createBudgetAccountFromAmount());

        account.adjustAmount(adjustment.amount(), adjustment.income(), adjustment.expense());
        budgetAccountRepository.save(account);
    }

    private void adjustCategory(BudgetAmount adjustment) {

        BudgetCategory category = budgetCategoryRepository.findByUserKeyAndAccountIdxAndCategoryIdxAndTransactionDate(
            adjustment.userKey(),
            adjustment.accountIdx(),
            adjustment.categoryIdx(),
            adjustment.transactionDate().toLocalDate()
        ).orElseGet(() -> adjustment.createBudgetCategoryFromAmount());

        category.adjustAmount(adjustment.amount(), adjustment.income(), adjustment.expense());
        budgetCategoryRepository.save(category);
    }
}

