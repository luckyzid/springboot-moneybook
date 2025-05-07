package kr.money.book.analyze.web.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import kr.money.book.common.constants.AnalysisType;
import kr.money.book.analyze.web.domain.valueobject.AnalysisData;
import kr.money.book.analyze.web.domain.valueobject.AnalysisInfo;
import kr.money.book.analyze.web.domain.valueobject.BudgetAccountAnalysis;
import kr.money.book.analyze.web.domain.valueobject.BudgetAnalysis;
import kr.money.book.analyze.web.domain.valueobject.BudgetCategoryAnalysis;
import kr.money.book.analyze.web.exceptions.AnalysisException;
import kr.money.book.analyze.web.infra.AnalyzeAuthenticationService;
import kr.money.book.analyze.web.infra.AnalyzePersistenceAdapter;
import kr.money.book.common.constants.AccountType;
import kr.money.book.common.constants.BudgetType;
import kr.money.book.common.valueobject.CacheAccount;
import kr.money.book.common.valueobject.CacheCategory;
import kr.money.book.common.valueobject.CacheInform;
import kr.money.book.common.valueobject.CacheUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnalyzeServiceTest {

    @Mock
    private AnalyzePersistenceAdapter persistenceAdapter;

    @Mock
    private AnalyzeAuthenticationService authenticationService;

    @InjectMocks
    private AnalyzeService analyzeService;

    private String userKey;
    private CacheInform cacheInform;
    private Long accountIdx1 = 1l;
    private Long accountIdx2 = 2l;
    private Long categoryIdx1 = 1l;
    private Long categoryIdx2 = 2l;

    @BeforeEach
    void setUp() {
        userKey = "testUser";
        cacheInform = CacheInform.builder()
            .user(CacheUser.builder().userKey(userKey).build())
            .accounts(List.of(
                CacheAccount.builder().idx(accountIdx1).name("Bank").type(AccountType.BANK.name()).build(),
                CacheAccount.builder().idx(accountIdx2).name("Card").type(AccountType.CARD.name()).build()))
            .categories(List.of(
                CacheCategory.builder().idx(categoryIdx1).name("Income").build(),
                CacheCategory.builder().idx(categoryIdx2).name("Expense").build()))
            .build();
        when(authenticationService.getCacheInform(eq(userKey))).thenReturn(cacheInform);
    }

    @Test
    void 월별_분석_성공() {
        AnalysisInfo analysisInfo = AnalysisInfo.builder()
            .userKey(userKey)
            .analysisType(AnalysisType.MONTHLY)
            .startDate(LocalDateTime.of(2025, 3, 1, 0, 0))
            .build();

        LocalDateTime endDate = LocalDateTime.of(2025, 3, 31, 23, 59, 59, 999999999);
        LocalDate startLocalDate = analysisInfo.startDate().toLocalDate();
        LocalDate endLocalDate = endDate.toLocalDate();

        BudgetAnalysis budgetIncome = BudgetAnalysis.builder()
            .type(BudgetType.INCOME.name())
            .amount(BigDecimal.valueOf(1000))
            .transactionDate(LocalDateTime.of(2025, 3, 1, 0, 0))
            .build();
        BudgetAnalysis budgetExpense = BudgetAnalysis.builder()
            .type(BudgetType.EXPENSE.name())
            .amount(BigDecimal.valueOf(500))
            .transactionDate(LocalDateTime.of(2025, 3, 2, 0, 0))
            .build();

        BudgetAccountAnalysis accountIncome = BudgetAccountAnalysis.builder()
            .accountIdx(accountIdx1)
            .transactionDate(LocalDate.of(2025, 3, 1))
            .amount(BigDecimal.valueOf(1000))
            .income(BigDecimal.valueOf(1000))
            .expense(BigDecimal.ZERO)
            .build();
        BudgetAccountAnalysis accountExpense = BudgetAccountAnalysis.builder()
            .accountIdx(accountIdx2)
            .transactionDate(LocalDate.of(2025, 3, 2))
            .amount(BigDecimal.valueOf(-500))
            .income(BigDecimal.ZERO)
            .expense(BigDecimal.valueOf(500))
            .build();

        BudgetCategoryAnalysis categoryIncome = BudgetCategoryAnalysis.builder()
            .accountIdx(accountIdx1)
            .categoryIdx(categoryIdx1)
            .transactionDate(LocalDate.of(2025, 3, 1))
            .income(BigDecimal.valueOf(1000))
            .expense(BigDecimal.ZERO)
            .build();
        BudgetCategoryAnalysis categoryExpense = BudgetCategoryAnalysis.builder()
            .accountIdx(accountIdx1)
            .categoryIdx(categoryIdx2)
            .transactionDate(LocalDate.of(2025, 3, 2))
            .income(BigDecimal.ZERO)
            .expense(BigDecimal.valueOf(500))
            .build();

        when(persistenceAdapter.findBudgets(eq(userKey), eq(analysisInfo.startDate()), eq(endDate), isNull(), isNull()))
            .thenReturn(List.of(budgetIncome, budgetExpense));
        when(persistenceAdapter.findBudgetAccounts(eq(userKey), isNull(), eq(startLocalDate), eq(endLocalDate)))
            .thenReturn(List.of(accountIncome, accountExpense));
        when(persistenceAdapter.findBudgetCategories(eq(userKey), isNull(), isNull(), eq(startLocalDate), eq(endLocalDate)))
            .thenReturn(List.of(categoryIncome, categoryExpense));

        AnalysisData result = analyzeService.analyze(analysisInfo);

        assertThat(result.totalIncome()).isEqualByComparingTo("1000");
        assertThat(result.totalExpense()).isEqualByComparingTo("500");
        assertThat(result.budgets()).hasSize(2);
        assertThat(result.categoryAnalysis().get("1").totalIncome()).isEqualByComparingTo("1000");
        assertThat(result.categoryAnalysis().get("2").totalExpense()).isEqualByComparingTo("500");
        assertThat(result.accountAnalysis().get("1").totalIncome()).isEqualByComparingTo("1000");
        assertThat(result.accountAnalysis().get("2").totalExpense()).isEqualByComparingTo("500");
    }

    @Test
    void 필터와_함께_분석_성공() {
        AnalysisInfo analysisInfo = AnalysisInfo.builder()
            .userKey(userKey)
            .analysisType(AnalysisType.MONTHLY)
            .startDate(LocalDateTime.of(2025, 3, 1, 0, 0))
            .accountIdxList(List.of(accountIdx2))
            .categoryIdxList(List.of(categoryIdx2))
            .build();

        LocalDateTime endDate = LocalDateTime.of(2025, 3, 31, 23, 59, 59, 999999999);
        LocalDate startLocalDate = analysisInfo.startDate().toLocalDate();
        LocalDate endLocalDate = endDate.toLocalDate();

        BudgetAnalysis budgetExpense = BudgetAnalysis.builder()
            .type(BudgetType.EXPENSE.name())
            .amount(BigDecimal.valueOf(500))
            .transactionDate(LocalDateTime.of(2025, 3, 2, 0, 0))
            .build();

        BudgetAccountAnalysis accountExpense = BudgetAccountAnalysis.builder()
            .accountIdx(accountIdx2)
            .transactionDate(LocalDate.of(2025, 3, 2))
            .amount(BigDecimal.valueOf(-500))
            .income(BigDecimal.ZERO)
            .expense(BigDecimal.valueOf(500))
            .build();

        BudgetCategoryAnalysis categoryExpense = BudgetCategoryAnalysis.builder()
            .accountIdx(accountIdx2)
            .categoryIdx(categoryIdx2)
            .transactionDate(LocalDate.of(2025, 3, 2))
            .income(BigDecimal.ZERO)
            .expense(BigDecimal.valueOf(500))
            .build();

        when(persistenceAdapter.findBudgets(eq(userKey), eq(analysisInfo.startDate()), eq(endDate), eq(List.of(accountIdx2)), eq(List.of(categoryIdx2))))
            .thenReturn(List.of(budgetExpense));
        when(persistenceAdapter.findBudgetAccounts(eq(userKey), eq(List.of(accountIdx2)), eq(startLocalDate), eq(endLocalDate)))
            .thenReturn(List.of(accountExpense));
        when(persistenceAdapter.findBudgetCategories(eq(userKey), eq(List.of(accountIdx2)), eq(List.of(categoryIdx2)), eq(startLocalDate), eq(endLocalDate)))
            .thenReturn(List.of(categoryExpense));

        AnalysisData result = analyzeService.analyze(analysisInfo);

        assertThat(result.totalIncome()).isEqualByComparingTo("0");
        assertThat(result.totalExpense()).isEqualByComparingTo("500");
        assertThat(result.budgets()).hasSize(1);
        assertThat(result.categoryAnalysis().get("2").totalExpense()).isEqualByComparingTo("500");
        assertThat(result.accountAnalysis().get("2").totalExpense()).isEqualByComparingTo("500");
    }

    @Test
    void 빈_데이터_분석시_0_반환() {
        AnalysisInfo analysisInfo = AnalysisInfo.builder()
            .userKey(userKey)
            .analysisType(AnalysisType.MONTHLY)
            .startDate(LocalDateTime.of(2025, 3, 1, 0, 0))
            .build();

        LocalDateTime endDate = LocalDateTime.of(2025, 3, 31, 23, 59, 59, 999999999);
        LocalDate startLocalDate = analysisInfo.startDate().toLocalDate();
        LocalDate endLocalDate = endDate.toLocalDate();

        when(persistenceAdapter.findBudgets(eq(userKey), eq(analysisInfo.startDate()), eq(endDate), isNull(), isNull()))
            .thenReturn(Collections.emptyList());
        when(persistenceAdapter.findBudgetAccounts(eq(userKey), isNull(), eq(startLocalDate), eq(endLocalDate)))
            .thenReturn(Collections.emptyList());
        when(persistenceAdapter.findBudgetCategories(eq(userKey), isNull(), isNull(), eq(startLocalDate), eq(endLocalDate)))
            .thenReturn(Collections.emptyList());

        AnalysisData result = analyzeService.analyze(analysisInfo);

        assertThat(result.totalIncome()).isEqualByComparingTo("0");
        assertThat(result.totalExpense()).isEqualByComparingTo("0");
        assertThat(result.budgets()).isEmpty();
        assertThat(result.categoryAnalysis()).isEmpty();
        assertThat(result.accountAnalysis()).isEmpty();
    }

    @Test
    void 주별_분석_성공() {
        AnalysisInfo analysisInfo = AnalysisInfo.builder()
            .userKey(userKey)
            .analysisType(AnalysisType.WEEKLY)
            .startDate(LocalDateTime.of(2025, 3, 1, 0, 0))
            .build();

        LocalDateTime endDate = LocalDateTime.of(2025, 3, 7, 23, 59, 59, 999999999);
        LocalDate startLocalDate = analysisInfo.startDate().toLocalDate();
        LocalDate endLocalDate = endDate.toLocalDate();

        BudgetAnalysis budgetIncome = BudgetAnalysis.builder()
            .type(BudgetType.INCOME.name())
            .amount(BigDecimal.valueOf(1000))
            .transactionDate(LocalDateTime.of(2025, 3, 1, 0, 0))
            .build();

        BudgetAccountAnalysis accountIncome = BudgetAccountAnalysis.builder()
            .accountIdx(accountIdx1)
            .transactionDate(LocalDate.of(2025, 3, 1))
            .amount(BigDecimal.valueOf(1000))
            .income(BigDecimal.valueOf(1000))
            .expense(BigDecimal.ZERO)
            .build();

        BudgetCategoryAnalysis categoryIncome = BudgetCategoryAnalysis.builder()
            .accountIdx(accountIdx1)
            .categoryIdx(categoryIdx1)
            .transactionDate(LocalDate.of(2025, 3, 1))
            .income(BigDecimal.valueOf(1000))
            .expense(BigDecimal.ZERO)
            .build();

        when(persistenceAdapter.findBudgets(eq(userKey), eq(analysisInfo.startDate()), eq(endDate), isNull(), isNull()))
            .thenReturn(List.of(budgetIncome));
        when(persistenceAdapter.findBudgetAccounts(eq(userKey), isNull(), eq(startLocalDate), eq(endLocalDate)))
            .thenReturn(List.of(accountIncome));
        when(persistenceAdapter.findBudgetCategories(eq(userKey), isNull(), isNull(), eq(startLocalDate), eq(endLocalDate)))
            .thenReturn(List.of(categoryIncome));

        AnalysisData result = analyzeService.analyze(analysisInfo);

        assertThat(result.totalIncome()).isEqualByComparingTo("1000");
        assertThat(result.totalExpense()).isEqualByComparingTo("0");
        assertThat(result.budgets()).hasSize(1);
        assertThat(result.categoryAnalysis().get("1").totalIncome()).isEqualByComparingTo("1000");
        assertThat(result.accountAnalysis().get("1").totalIncome()).isEqualByComparingTo("1000");
    }

    @Test
    void 빈_필터로_분석시_전체_데이터_반환() {
        AnalysisInfo analysisInfo = AnalysisInfo.builder()
            .userKey(userKey)
            .analysisType(AnalysisType.MONTHLY)
            .startDate(LocalDateTime.of(2025, 3, 1, 0, 0))
            .accountIdxList(Collections.emptyList())
            .categoryIdxList(Collections.emptyList())
            .build();

        LocalDateTime endDate = LocalDateTime.of(2025, 3, 31, 23, 59, 59, 999999999);
        LocalDate startLocalDate = analysisInfo.startDate().toLocalDate();
        LocalDate endLocalDate = endDate.toLocalDate();

        BudgetAnalysis budgetIncome = BudgetAnalysis.builder()
            .type(BudgetType.INCOME.name())
            .amount(BigDecimal.valueOf(1000))
            .transactionDate(LocalDateTime.of(2025, 3, 1, 0, 0))
            .build();

        BudgetAccountAnalysis accountIncome = BudgetAccountAnalysis.builder()
            .accountIdx(accountIdx1)
            .transactionDate(LocalDate.of(2025, 3, 1))
            .amount(BigDecimal.valueOf(1000))
            .income(BigDecimal.valueOf(1000))
            .expense(BigDecimal.ZERO)
            .build();

        BudgetCategoryAnalysis categoryIncome = BudgetCategoryAnalysis.builder()
            .accountIdx(accountIdx1)
            .categoryIdx(categoryIdx1)
            .transactionDate(LocalDate.of(2025, 3, 1))
            .income(BigDecimal.valueOf(1000))
            .expense(BigDecimal.ZERO)
            .build();

        doReturn(List.of(budgetIncome))
            .when(persistenceAdapter)
            .findBudgets(userKey, analysisInfo.startDate(), endDate, Collections.emptyList(), Collections.emptyList());
        doReturn(List.of(accountIncome))
            .when(persistenceAdapter)
            .findBudgetAccounts(userKey, Collections.emptyList(), startLocalDate, endLocalDate);
        doReturn(List.of(categoryIncome))
            .when(persistenceAdapter)
            .findBudgetCategories(userKey, Collections.emptyList(), Collections.emptyList(), startLocalDate, endLocalDate);

        AnalysisData result = analyzeService.analyze(analysisInfo);

        assertThat(result.totalIncome()).isEqualByComparingTo("1000");
        assertThat(result.totalExpense()).isEqualByComparingTo("0");
        assertThat(result.budgets()).hasSize(1);
        assertThat(result.budgets().get(0).type()).isEqualTo(BudgetType.INCOME.name());
        assertThat(result.categoryAnalysis().get("1").totalIncome()).isEqualByComparingTo("1000");
        assertThat(result.accountAnalysis().get("1").totalIncome()).isEqualByComparingTo("1000");
    }

    @Test
    void 여러_계정과_카테고리_분석() {
        AnalysisInfo analysisInfo = AnalysisInfo.builder()
            .userKey(userKey)
            .analysisType(AnalysisType.MONTHLY)
            .startDate(LocalDateTime.of(2025, 3, 1, 0, 0))
            .accountIdxList(List.of(accountIdx1, accountIdx2))
            .categoryIdxList(List.of(categoryIdx1, categoryIdx2))
            .build();

        LocalDateTime endDate = LocalDateTime.of(2025, 3, 31, 23, 59, 59, 999999999);
        LocalDate startLocalDate = analysisInfo.startDate().toLocalDate();
        LocalDate endLocalDate = endDate.toLocalDate();

        BudgetAnalysis budgetIncome = BudgetAnalysis.builder()
            .type(BudgetType.INCOME.name())
            .amount(BigDecimal.valueOf(1000))
            .transactionDate(LocalDateTime.of(2025, 3, 1, 0, 0))
            .build();
        BudgetAnalysis budgetExpense = BudgetAnalysis.builder()
            .type(BudgetType.EXPENSE.name())
            .amount(BigDecimal.valueOf(500))
            .transactionDate(LocalDateTime.of(2025, 3, 2, 0, 0))
            .build();

        BudgetAccountAnalysis accountIncome = BudgetAccountAnalysis.builder()
            .accountIdx(accountIdx1)
            .transactionDate(LocalDate.of(2025, 3, 1))
            .amount(BigDecimal.valueOf(1000))
            .income(BigDecimal.valueOf(1000))
            .expense(BigDecimal.ZERO)
            .build();
        BudgetAccountAnalysis accountExpense = BudgetAccountAnalysis.builder()
            .accountIdx(accountIdx2)
            .transactionDate(LocalDate.of(2025, 3, 2))
            .amount(BigDecimal.valueOf(-500))
            .income(BigDecimal.ZERO)
            .expense(BigDecimal.valueOf(500))
            .build();

        BudgetCategoryAnalysis categoryIncome = BudgetCategoryAnalysis.builder()
            .accountIdx(accountIdx1)
            .categoryIdx(categoryIdx1)
            .transactionDate(LocalDate.of(2025, 3, 1))
            .income(BigDecimal.valueOf(1000))
            .expense(BigDecimal.ZERO)
            .build();
        BudgetCategoryAnalysis categoryExpense = BudgetCategoryAnalysis.builder()
            .accountIdx(accountIdx1)
            .categoryIdx(categoryIdx2)
            .transactionDate(LocalDate.of(2025, 3, 2))
            .income(BigDecimal.ZERO)
            .expense(BigDecimal.valueOf(500))
            .build();

        when(persistenceAdapter.findBudgets(eq(userKey), eq(analysisInfo.startDate()), eq(endDate), eq(List.of(accountIdx1, accountIdx2)), eq(List.of(categoryIdx1, categoryIdx2))))
            .thenReturn(List.of(budgetIncome, budgetExpense));
        when(persistenceAdapter.findBudgetAccounts(eq(userKey), eq(List.of(accountIdx1, accountIdx2)), eq(startLocalDate), eq(endLocalDate)))
            .thenReturn(List.of(accountIncome, accountExpense));
        when(persistenceAdapter.findBudgetCategories(eq(userKey), eq(List.of(accountIdx1, accountIdx2)), eq(List.of(categoryIdx1, categoryIdx2)), eq(startLocalDate), eq(endLocalDate)))
            .thenReturn(List.of(categoryIncome, categoryExpense));

        AnalysisData result = analyzeService.analyze(analysisInfo);

        assertThat(result.totalIncome()).isEqualByComparingTo("1000");
        assertThat(result.totalExpense()).isEqualByComparingTo("500");
        assertThat(result.budgets()).hasSize(2);
        assertThat(result.categoryAnalysis().get("1").totalIncome()).isEqualByComparingTo("1000");
        assertThat(result.categoryAnalysis().get("2").totalExpense()).isEqualByComparingTo("500");
        assertThat(result.accountAnalysis().get("1").totalIncome()).isEqualByComparingTo("1000");
        assertThat(result.accountAnalysis().get("2").totalExpense()).isEqualByComparingTo("500");
    }

    @Test
    void 잘못된_계정_분석시_예외_발생() {
        AnalysisInfo analysisInfo = AnalysisInfo.builder()
            .userKey(userKey)
            .analysisType(AnalysisType.MONTHLY)
            .startDate(LocalDateTime.of(2025, 3, 1, 0, 0))
            .accountIdxList(List.of(999L))
            .build();

        AnalysisException exception = assertThrows(AnalysisException.class, () -> analyzeService.analyze(analysisInfo));
        assertThat(exception.getErrorCode()).isEqualTo(AnalysisException.ErrorCode.UNAUTHORIZED_ACCESS);
    }

    @Test
    void 잘못된_카테고리_분석시_예외_발생() {
        AnalysisInfo analysisInfo = AnalysisInfo.builder()
            .userKey(userKey)
            .analysisType(AnalysisType.MONTHLY)
            .startDate(LocalDateTime.of(2025, 3, 1, 0, 0))
            .categoryIdxList(List.of(999L))
            .build();

        AnalysisException exception = assertThrows(AnalysisException.class, () -> analyzeService.analyze(analysisInfo));
        assertThat(exception.getErrorCode()).isEqualTo(AnalysisException.ErrorCode.UNAUTHORIZED_ACCESS);
    }

    @Test
    void 날짜_범위_밖_분석시_빈_결과_반환() {
        AnalysisInfo analysisInfo = AnalysisInfo.builder()
            .userKey(userKey)
            .analysisType(AnalysisType.MONTHLY)
            .startDate(LocalDateTime.of(2025, 4, 1, 0, 0))
            .build();

        LocalDateTime endDate = LocalDateTime.of(2025, 4, 30, 23, 59, 59, 999999999);
        LocalDate startLocalDate = analysisInfo.startDate().toLocalDate();
        LocalDate endLocalDate = endDate.toLocalDate();

        when(persistenceAdapter.findBudgets(eq(userKey), eq(analysisInfo.startDate()), eq(endDate), isNull(), isNull()))
            .thenReturn(Collections.emptyList());
        when(persistenceAdapter.findBudgetAccounts(eq(userKey), isNull(), eq(startLocalDate), eq(endLocalDate)))
            .thenReturn(Collections.emptyList());
        when(persistenceAdapter.findBudgetCategories(eq(userKey), isNull(), isNull(), eq(startLocalDate), eq(endLocalDate)))
            .thenReturn(Collections.emptyList());

        AnalysisData result = analyzeService.analyze(analysisInfo);

        assertThat(result.budgets()).isEmpty();
        assertThat(result.totalIncome()).isEqualByComparingTo("0");
        assertThat(result.totalExpense()).isEqualByComparingTo("0");
        assertThat(result.categoryAnalysis()).isEmpty();
        assertThat(result.accountAnalysis()).isEmpty();
    }

    @Test
    void 잘못된_계정_분석시_예외_발생2() {
        AnalysisInfo analysisInfo = AnalysisInfo.builder()
            .userKey(userKey)
            .analysisType(AnalysisType.MONTHLY)
            .startDate(LocalDateTime.of(2025, 3, 1, 0, 0))
            .accountIdxList(List.of(999l))
            .build();

        assertThrows(AnalysisException.class, () -> analyzeService.analyze(analysisInfo));
    }

    @Test
    void 잘못된_카테고리_분석시_예외_발생2() {
        AnalysisInfo analysisInfo = AnalysisInfo.builder()
            .userKey(userKey)
            .analysisType(AnalysisType.MONTHLY)
            .startDate(LocalDateTime.of(2025, 3, 1, 0, 0))
            .categoryIdxList(List.of(999l))
            .build();

        assertThrows(AnalysisException.class, () -> analyzeService.analyze(analysisInfo));
    }

    @Test
    void 음수_금액_분석시_예외_발생() {
        AnalysisInfo analysisInfo = AnalysisInfo.builder()
            .userKey(userKey)
            .analysisType(AnalysisType.MONTHLY)
            .startDate(LocalDateTime.of(2025, 3, 1, 0, 0))
            .build();

        LocalDateTime endDate = LocalDateTime.of(2025, 3, 31, 23, 59, 59, 999999999);
        LocalDate startLocalDate = analysisInfo.startDate().toLocalDate();
        LocalDate endLocalDate = endDate.toLocalDate();

        BudgetAnalysis budgetIncome = BudgetAnalysis.builder()
            .type(BudgetType.INCOME.name())
            .amount(BigDecimal.valueOf(-1000))
            .transactionDate(LocalDateTime.of(2025, 3, 1, 0, 0))
            .build();

        when(persistenceAdapter.findBudgets(eq(userKey), eq(analysisInfo.startDate()), eq(endDate), isNull(), isNull()))
            .thenReturn(List.of(budgetIncome));

        AnalysisException exception = assertThrows(AnalysisException.class, () -> analyzeService.analyze(analysisInfo));
        assertThat(exception.getErrorCode()).isEqualTo(AnalysisException.ErrorCode.INVALID_AMOUNT);
    }

    @Test
    void 복합_분석_성공() {
        AnalysisInfo analysisInfo = AnalysisInfo.builder()
            .userKey(userKey)
            .analysisType(AnalysisType.MONTHLY)
            .startDate(LocalDateTime.of(2025, 3, 1, 0, 0))
            .accountIdxList(List.of(accountIdx1, accountIdx2))
            .categoryIdxList(List.of(categoryIdx1, categoryIdx2))
            .build();

        LocalDateTime endDate = LocalDateTime.of(2025, 3, 31, 23, 59, 59, 999999999);
        LocalDate startLocalDate = analysisInfo.startDate().toLocalDate();
        LocalDate endLocalDate = endDate.toLocalDate();

        BudgetAnalysis budgetIncome1 = BudgetAnalysis.builder()
            .type(BudgetType.INCOME.name())
            .amount(BigDecimal.valueOf(1000))
            .transactionDate(LocalDateTime.of(2025, 3, 1, 0, 0))
            .build();
        BudgetAnalysis budgetIncome2 = BudgetAnalysis.builder()
            .type(BudgetType.INCOME.name())
            .amount(BigDecimal.valueOf(2000))
            .transactionDate(LocalDateTime.of(2025, 3, 2, 0, 0))
            .build();
        BudgetAnalysis budgetExpense1 = BudgetAnalysis.builder()
            .type(BudgetType.EXPENSE.name())
            .amount(BigDecimal.valueOf(500))
            .transactionDate(LocalDateTime.of(2025, 3, 3, 0, 0))
            .build();
        BudgetAnalysis budgetExpense2 = BudgetAnalysis.builder()
            .type(BudgetType.EXPENSE.name())
            .amount(BigDecimal.valueOf(1000))
            .transactionDate(LocalDateTime.of(2025, 3, 4, 0, 0))
            .build();

        BudgetAccountAnalysis accountIncome1 = BudgetAccountAnalysis.builder()
            .accountIdx(accountIdx1)
            .transactionDate(LocalDate.of(2025, 3, 1))
            .amount(BigDecimal.valueOf(1000))
            .income(BigDecimal.valueOf(1000))
            .expense(BigDecimal.ZERO)
            .build();
        BudgetAccountAnalysis accountIncome2 = BudgetAccountAnalysis.builder()
            .accountIdx(accountIdx2)
            .transactionDate(LocalDate.of(2025, 3, 2))
            .amount(BigDecimal.valueOf(2000))
            .income(BigDecimal.valueOf(2000))
            .expense(BigDecimal.ZERO)
            .build();
        BudgetAccountAnalysis accountExpense1 = BudgetAccountAnalysis.builder()
            .accountIdx(accountIdx1)
            .transactionDate(LocalDate.of(2025, 3, 3))
            .amount(BigDecimal.valueOf(-500))
            .income(BigDecimal.ZERO)
            .expense(BigDecimal.valueOf(500))
            .build();
        BudgetAccountAnalysis accountExpense2 = BudgetAccountAnalysis.builder()
            .accountIdx(accountIdx2)
            .transactionDate(LocalDate.of(2025, 3, 4))
            .amount(BigDecimal.valueOf(-1000))
            .income(BigDecimal.ZERO)
            .expense(BigDecimal.valueOf(1000))
            .build();

        BudgetCategoryAnalysis categoryIncome1 = BudgetCategoryAnalysis.builder()
            .accountIdx(accountIdx1)
            .categoryIdx(categoryIdx1)
            .transactionDate(LocalDate.of(2025, 3, 1))
            .income(BigDecimal.valueOf(1000))
            .expense(BigDecimal.ZERO)
            .build();
        BudgetCategoryAnalysis categoryIncome2 = BudgetCategoryAnalysis.builder()
            .accountIdx(accountIdx2)
            .categoryIdx(categoryIdx1)
            .transactionDate(LocalDate.of(2025, 3, 2))
            .income(BigDecimal.valueOf(2000))
            .expense(BigDecimal.ZERO)
            .build();
        BudgetCategoryAnalysis categoryExpense1 = BudgetCategoryAnalysis.builder()
            .accountIdx(accountIdx1)
            .categoryIdx(categoryIdx2)
            .transactionDate(LocalDate.of(2025, 3, 3))
            .income(BigDecimal.ZERO)
            .expense(BigDecimal.valueOf(500))
            .build();
        BudgetCategoryAnalysis categoryExpense2 = BudgetCategoryAnalysis.builder()
            .accountIdx(accountIdx2)
            .categoryIdx(categoryIdx2)
            .transactionDate(LocalDate.of(2025, 3, 4))
            .income(BigDecimal.ZERO)
            .expense(BigDecimal.valueOf(1000))
            .build();

        when(persistenceAdapter.findBudgets(eq(userKey), eq(analysisInfo.startDate()), eq(endDate), eq(List.of(accountIdx1, accountIdx2)), eq(List.of(categoryIdx1, categoryIdx2))))
            .thenReturn(List.of(budgetIncome1, budgetIncome2, budgetExpense1, budgetExpense2));
        when(persistenceAdapter.findBudgetAccounts(eq(userKey), eq(List.of(accountIdx1, accountIdx2)), eq(startLocalDate), eq(endLocalDate)))
            .thenReturn(List.of(accountIncome1, accountIncome2, accountExpense1, accountExpense2));
        when(persistenceAdapter.findBudgetCategories(eq(userKey), eq(List.of(accountIdx1, accountIdx2)), eq(List.of(categoryIdx1, categoryIdx2)), eq(startLocalDate), eq(endLocalDate)))
            .thenReturn(List.of(categoryIncome1, categoryIncome2, categoryExpense1, categoryExpense2));

        AnalysisData result = analyzeService.analyze(analysisInfo);

        assertThat(result.totalIncome()).isEqualByComparingTo("3000");
        assertThat(result.totalExpense()).isEqualByComparingTo("1500");
        assertThat(result.budgets()).hasSize(4);
        assertThat(result.categoryAnalysis().get("1").totalIncome()).isEqualByComparingTo("3000");
        assertThat(result.categoryAnalysis().get("2").totalExpense()).isEqualByComparingTo("1500");
        assertThat(result.accountAnalysis().get("1").totalIncome()).isEqualByComparingTo("1000");
        assertThat(result.accountAnalysis().get("1").totalExpense()).isEqualByComparingTo("500");
        assertThat(result.accountAnalysis().get("2").totalIncome()).isEqualByComparingTo("2000");
        assertThat(result.accountAnalysis().get("2").totalExpense()).isEqualByComparingTo("1000");
    }
}
