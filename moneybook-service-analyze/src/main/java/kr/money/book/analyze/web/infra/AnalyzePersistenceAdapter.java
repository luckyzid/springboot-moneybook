package kr.money.book.analyze.web.infra;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import kr.money.book.analyze.web.domain.entity.BudgetAccountAnalyze;
import kr.money.book.analyze.web.domain.entity.BudgetAnalyze;
import kr.money.book.analyze.web.domain.entity.BudgetCategoryAnalyze;
import kr.money.book.analyze.web.domain.mapper.BudgetAccountAnalyzeToBudgetAccountAnalysisMapper;
import kr.money.book.analyze.web.domain.mapper.BudgetAnalyzeToBudgetAnalysisMapper;
import kr.money.book.analyze.web.domain.mapper.BudgetCategoryAnalyzeToBudgetCategoryAnalysisMapper;
import kr.money.book.analyze.web.domain.repository.BudgetAccountAnalyzeRepository;
import kr.money.book.analyze.web.domain.repository.BudgetAnalyzeRepository;
import kr.money.book.analyze.web.domain.repository.BudgetCategoryAnalyzeRepository;
import kr.money.book.analyze.web.domain.valueobject.BudgetAccountAnalysis;
import kr.money.book.analyze.web.domain.valueobject.BudgetAnalysis;
import kr.money.book.analyze.web.domain.valueobject.BudgetCategoryAnalysis;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AnalyzePersistenceAdapter {

    private final BudgetAnalyzeRepository budgetRepository;
    private final BudgetAccountAnalyzeRepository accountRepository;
    private final BudgetCategoryAnalyzeRepository categoryRepository;
    private final BudgetAnalyzeToBudgetAnalysisMapper budgetAnalyzeMapper;
    private final BudgetAccountAnalyzeToBudgetAccountAnalysisMapper budgetAccountAnalyzeMapper;
    private final BudgetCategoryAnalyzeToBudgetCategoryAnalysisMapper budgetCategoryAnalyzeMapper;

    public AnalyzePersistenceAdapter(
        BudgetAnalyzeRepository budgetRepository,
        BudgetAccountAnalyzeRepository accountRepository,
        BudgetCategoryAnalyzeRepository categoryRepository,
        BudgetAnalyzeToBudgetAnalysisMapper budgetAnalyzeMapper,
        BudgetAccountAnalyzeToBudgetAccountAnalysisMapper budgetAccountAnalyzeMapper,
        BudgetCategoryAnalyzeToBudgetCategoryAnalysisMapper budgetCategoryAnalyzeMapper) {

        this.budgetRepository = budgetRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.budgetAnalyzeMapper = budgetAnalyzeMapper;
        this.budgetAccountAnalyzeMapper = budgetAccountAnalyzeMapper;
        this.budgetCategoryAnalyzeMapper = budgetCategoryAnalyzeMapper;
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = true,
        rollbackFor = Exception.class
    )
    public List<BudgetAnalysis> findBudgets(
        String userKey,
        LocalDateTime startDate,
        LocalDateTime endDate,
        List<Long> accountIdxList,
        List<Long> categoryIdxList) {

        List<Long> effectiveAccountIdxList = normalizeList(accountIdxList);
        List<Long> effectiveCategoryIdxList = normalizeList(categoryIdxList);

        List<BudgetAnalyze> budgetAnalyzes = budgetRepository.findByUserKeyAndTransactionDateBetweenAndAccountIdxIn(
            userKey,
            startDate,
            endDate,
            effectiveAccountIdxList,
            effectiveCategoryIdxList
        );

        return budgetAnalyzes.stream()
            .map(budgetAnalyzeMapper::map)
            .collect(Collectors.toList());
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = true,
        rollbackFor = Exception.class
    )
    public List<BudgetAccountAnalysis> findBudgetAccounts(
        String userKey,
        List<Long> accountIdxList,
        LocalDate startDate,
        LocalDate endDate) {

        List<Long> effectiveAccountIdxList = normalizeList(accountIdxList);

        List<BudgetAccountAnalyze> budgetAccountAnalyzes = accountRepository.findByUserKeyAndAccountIdxInAndTransactionDateBetween(
            userKey,
            effectiveAccountIdxList,
            startDate,
            endDate
        );

        return budgetAccountAnalyzes.stream()
            .map(budgetAccountAnalyzeMapper::map)
            .collect(Collectors.toList());
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = true,
        rollbackFor = Exception.class
    )
    public List<BudgetCategoryAnalysis> findBudgetCategories(
        String userKey,
        List<Long> accountIdxList,
        List<Long> categoryIdxList,
        LocalDate startDate,
        LocalDate endDate) {

        List<Long> effectiveAccountIdxList = normalizeList(accountIdxList);
        List<Long> effectiveCategoryIdxList = normalizeList(categoryIdxList);

        List<BudgetCategoryAnalyze> budgetCategoryAnalyzes = categoryRepository.findByUserKeyAndAccountIdxAndCategoryIdxInAndTransactionDateBetween(
            userKey,
            effectiveAccountIdxList,
            effectiveCategoryIdxList,
            startDate,
            endDate
        );

        return budgetCategoryAnalyzes.stream()
            .map(budgetCategoryAnalyzeMapper::map)
            .collect(Collectors.toList());
    }

    private List<Long> normalizeList(List<Long> list) {
        return (list != null && list.isEmpty()) ? null : list;
    }
}
