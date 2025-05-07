package kr.money.book.analyze.web.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import kr.money.book.analyze.web.domain.valueobject.AnalysisAmount;
import kr.money.book.analyze.web.domain.valueobject.AnalysisData;
import kr.money.book.analyze.web.domain.valueobject.AnalysisInfo;
import kr.money.book.analyze.web.domain.valueobject.BudgetAccountAnalysis;
import kr.money.book.analyze.web.domain.valueobject.BudgetAnalysis;
import kr.money.book.analyze.web.domain.valueobject.BudgetCategoryAnalysis;
import kr.money.book.analyze.web.exceptions.AnalysisException;
import kr.money.book.analyze.web.infra.AnalyzeAuthenticationService;
import kr.money.book.analyze.web.infra.AnalyzePersistenceAdapter;
import kr.money.book.common.constants.BudgetType;
import kr.money.book.common.valueobject.CacheInform;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnalyzeService {

    private final AnalyzePersistenceAdapter persistenceAdapter;
    private final AnalyzeAuthenticationService authenticationService;

    public AnalyzeService(AnalyzePersistenceAdapter persistenceAdapter,
        AnalyzeAuthenticationService authenticationService) {
        this.persistenceAdapter = persistenceAdapter;
        this.authenticationService = authenticationService;
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        readOnly = true,
        rollbackFor = Exception.class
    )
    public AnalysisData analyze(AnalysisInfo analysisInfo) {
        validateAnalysisInfo(analysisInfo);

        String userKey = analysisInfo.userKey();
        List<Long> accountIdxList = analysisInfo.accountIdxList();
        List<Long> categoryIdxList = analysisInfo.categoryIdxList();
        LocalDateTime startDate = analysisInfo.startDate();
        LocalDateTime endDate = analysisInfo.endDate();

        validateOwnership(userKey, accountIdxList, categoryIdxList);

        List<BudgetAnalysis> budgets = persistenceAdapter.findBudgets(
            userKey,
            startDate,
            endDate,
            accountIdxList,
            categoryIdxList
        );
        List<BudgetAccountAnalysis> accounts = persistenceAdapter.findBudgetAccounts(
            userKey,
            accountIdxList,
            startDate.toLocalDate(),
            endDate.toLocalDate()
        );
        List<BudgetCategoryAnalysis> categories = persistenceAdapter.findBudgetCategories(
            userKey,
            accountIdxList,
            categoryIdxList,
            startDate.toLocalDate(),
            endDate.toLocalDate()
        );

        validateData(budgets, accounts, categories);

        Map<String, AnalysisAmount> accountAnalysis = accounts.stream()
            .filter(a -> isEmptyOrContains(accountIdxList, a.accountIdx()))
            .collect(Collectors.toMap(
                a -> String.valueOf(a.accountIdx()),
                a -> AnalysisAmount.from(a.amount(), a.income(), a.expense()),
                AnalysisAmount::merge
            ));

        Map<String, AnalysisAmount> categoryAnalysis = categories.stream()
            .filter(c -> isEmptyOrContains(accountIdxList, c.accountIdx()))
            .filter(c -> isEmptyOrContains(categoryIdxList, c.categoryIdx()))
            .collect(Collectors.toMap(
                c -> String.valueOf(c.categoryIdx()),
                c -> AnalysisAmount.from(c.amount(), c.income(), c.expense()),
                AnalysisAmount::merge
            ));

        BigDecimal totalIncome = calculateTotal(budgets, BudgetType.INCOME::equals);
        BigDecimal totalExpense = calculateTotal(budgets, BudgetType.EXPENSE::equals);

        validateResults(totalIncome, totalExpense, accountAnalysis, categoryAnalysis);

        return AnalysisData.builder()
            .startDate(analysisInfo.startDate())
            .endDate(analysisInfo.endDate())
            .accountIdxList(analysisInfo.accountIdxList())
            .categoryIdxList(analysisInfo.categoryIdxList())
            .analysisType(analysisInfo.analysisType())
            .totalIncome(totalIncome)
            .totalExpense(totalExpense)
            .categoryAnalysis(categoryAnalysis)
            .accountAnalysis(accountAnalysis)
            .budgets(budgets)
            .build();
    }

    private void validateAnalysisInfo(AnalysisInfo analysisInfo) {
        if (analysisInfo.analysisType() == null) {
            throw new AnalysisException(AnalysisException.ErrorCode.INVALID_ANALYSIS_TYPE);
        }

        if (analysisInfo.startDate() == null || analysisInfo.endDate() == null) {
            throw new AnalysisException(AnalysisException.ErrorCode.INVALID_DATE_RANGE);
        }

        if (analysisInfo.startDate().isAfter(analysisInfo.endDate())) {
            throw new AnalysisException(AnalysisException.ErrorCode.INVALID_DATE_RANGE);
        }
    }

    private void validateData(List<BudgetAnalysis> budgets, List<BudgetAccountAnalysis> accounts, List<BudgetCategoryAnalysis> categories) {
        budgets.forEach(budget -> {
            if (budget.amount().compareTo(BigDecimal.ZERO) < 0) {
                throw new AnalysisException(AnalysisException.ErrorCode.INVALID_AMOUNT);
            }
        });

        accounts.forEach(account -> {
            if (account.accountIdx() == null || account.accountIdx() <= 0) {
                throw new AnalysisException(AnalysisException.ErrorCode.INVALID_ACCOUNT);
            }
        });

        categories.forEach(category -> {
            if (category.categoryIdx() == null || category.categoryIdx() <= 0) {
                throw new AnalysisException(AnalysisException.ErrorCode.INVALID_CATEGORY);
            }
        });
    }

    private void validateResults(BigDecimal totalIncome, BigDecimal totalExpense, 
                               Map<String, AnalysisAmount> accountAnalysis, 
                               Map<String, AnalysisAmount> categoryAnalysis) {
        if (totalIncome.compareTo(BigDecimal.ZERO) < 0 || totalExpense.compareTo(BigDecimal.ZERO) < 0) {
            throw new AnalysisException(AnalysisException.ErrorCode.INVALID_RESULT);
        }

        accountAnalysis.forEach((key, value) -> {
            if (value.totalIncome().compareTo(BigDecimal.ZERO) < 0 || value.totalExpense().compareTo(BigDecimal.ZERO) < 0) {
                throw new AnalysisException(AnalysisException.ErrorCode.INVALID_RESULT);
            }
        });

        categoryAnalysis.forEach((key, value) -> {
            if (value.totalIncome().compareTo(BigDecimal.ZERO) < 0 || value.totalExpense().compareTo(BigDecimal.ZERO) < 0) {
                throw new AnalysisException(AnalysisException.ErrorCode.INVALID_RESULT);
            }
        });
    }

    private boolean isEmptyOrContains(List<Long> list, Long value) {

        return list == null || list.isEmpty() || list.contains(value);
    }

    private BigDecimal calculateTotal(List<BudgetAnalysis> budgets, Predicate<String> typeFilter) {

        return budgets.stream()
            .filter(b -> typeFilter.test(b.type()))
            .map(BudgetAnalysis::amount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void validateOwnership(String userKey, List<Long> accountIdxList, List<Long> categoryIdxList) {

        CacheInform cacheInform = authenticationService.getCacheInform(userKey);
        if (!cacheInform.hasAllAccounts(accountIdxList)) {
            throw new AnalysisException(AnalysisException.ErrorCode.UNAUTHORIZED_ACCESS);
        }
        if (!cacheInform.hasAllCategories(categoryIdxList)) {
            throw new AnalysisException(AnalysisException.ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }
}
