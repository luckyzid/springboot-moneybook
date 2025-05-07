package kr.money.book.budget.web.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kr.money.book.common.constants.AccountType;
import kr.money.book.common.constants.BudgetType;
import kr.money.book.budget.web.domain.valueobject.BudgetInfo;
import kr.money.book.budget.web.exceptions.BudgetException;
import kr.money.book.budget.web.infra.BudgetAuthenticationService;
import kr.money.book.budget.web.infra.BudgetPersistenceAdapter;
import kr.money.book.common.valueobject.CacheInform;
import kr.money.book.common.valueobject.CacheUser;
import kr.money.book.common.valueobject.CacheAccount;
import kr.money.book.common.valueobject.CacheCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BudgetServiceTest {
    @Mock
    private BudgetPersistenceAdapter budgetPersistenceAdapter;

    @Mock
    private BudgetAuthenticationService budgetAuthenticationService;

    @InjectMocks
    private BudgetService budgetService;

    private String randomUserKey;
    private Long accountIdx = 1l;
    private Long categoryIdx = 1l;

    @BeforeEach
    void setUp() {
        randomUserKey = "testUser";
    }

    @Test
    void 예산생성_성공() {
        BudgetInfo budgetInfo = BudgetInfo.builder()
            .userKey(randomUserKey)
            .type(BudgetType.INCOME)
            .amount(BigDecimal.valueOf(1000))
            .comment("Test Income")
            .transactionDate(LocalDateTime.now())
            .accountIdx(accountIdx)
            .categoryIdx(categoryIdx)
            .build();

        CacheInform cacheInform = CacheInform.builder()
            .user(CacheUser.builder().userKey(randomUserKey).build())
            .accounts(List.of(CacheAccount.builder().idx(accountIdx).name("TestAccount").type(AccountType.BANK.name()).build()))
            .categories(List.of(CacheCategory.builder().idx(categoryIdx).name("TestCategory").build()))
            .build();

        when(budgetAuthenticationService.getCacheInform(randomUserKey)).thenReturn(cacheInform);
        when(budgetPersistenceAdapter.saveBudget(any(BudgetInfo.class))).thenReturn(budgetInfo.toBuilder().idx(1l).build());

        BudgetInfo result = budgetService.createBudget(budgetInfo);

        assertNotNull(result.idx());
        assertEquals("Test Income", result.comment());
        verify(budgetPersistenceAdapter).saveBudget(any(BudgetInfo.class));
    }

    @Test
    void 예산업데이트_성공() {
        BudgetInfo budgetInfo = BudgetInfo.builder()
            .idx(1l)
            .userKey(randomUserKey)
            .type(BudgetType.EXPENSE)
            .amount(BigDecimal.valueOf(500))
            .comment("Updated Expense")
            .transactionDate(LocalDateTime.now())
            .accountIdx(accountIdx)
            .categoryIdx(categoryIdx)
            .build();

        CacheInform cacheInform = CacheInform.builder()
            .user(CacheUser.builder().userKey(randomUserKey).build())
            .accounts(List.of(CacheAccount.builder().idx(accountIdx).name("TestAccount").type(AccountType.BANK.name()).build()))
            .categories(List.of(CacheCategory.builder().idx(categoryIdx).name("TestCategory").build()))
            .build();

        when(budgetAuthenticationService.getCacheInform(randomUserKey)).thenReturn(cacheInform);
        when(budgetPersistenceAdapter.updateBudget(any(BudgetInfo.class))).thenReturn(budgetInfo);

        BudgetInfo result = budgetService.updateBudget(budgetInfo);

        assertEquals("Updated Expense", result.comment());
        verify(budgetPersistenceAdapter).updateBudget(any(BudgetInfo.class));
    }

    @Test
    void 예산삭제_성공() {
        BudgetInfo budgetInfo = BudgetInfo.builder()
            .idx(1l)
            .userKey(randomUserKey)
            .type(BudgetType.INCOME)
            .amount(BigDecimal.valueOf(1000))
            .transactionDate(LocalDateTime.now())
            .accountIdx(accountIdx)
            .categoryIdx(categoryIdx)
            .build();

        when(budgetPersistenceAdapter.findByUserKeyAndBudgetIdx(randomUserKey, 1l))
            .thenReturn(Optional.of(budgetInfo));
        doNothing().when(budgetPersistenceAdapter).deleteBudget(randomUserKey, 1l);

        budgetService.deleteBudget(randomUserKey, 1l);
        verify(budgetPersistenceAdapter).deleteBudget(randomUserKey, 1l);
    }

    @Test
    void 예산목록조회_성공() {
        BudgetInfo budgetInfo = BudgetInfo.builder()
            .idx(1l)
            .userKey(randomUserKey)
            .type(BudgetType.INCOME)
            .amount(BigDecimal.valueOf(1000))
            .transactionDate(LocalDateTime.now())
            .accountIdx(accountIdx)
            .categoryIdx(categoryIdx)
            .build();

        when(budgetPersistenceAdapter.findBudgetsByUserKey(randomUserKey)).thenReturn(List.of(budgetInfo));

        List<BudgetInfo> result = budgetService.getBudgetList(randomUserKey);
        assertEquals(1, result.size());
        assertEquals(BigDecimal.valueOf(1000), result.get(0).amount());
    }

    @Test
    void 예산조회_찾지못함_예외발생() {
        when(budgetPersistenceAdapter.findByUserKeyAndBudgetIdx(randomUserKey, 999l)).thenReturn(Optional.empty());

        assertThrows(BudgetException.class, () -> budgetService.getBudget(randomUserKey, 999l));
    }

    @Test
    void 예산업데이트_계좌와카테고리정확히업데이트() {
        BudgetInfo initialBudget = BudgetInfo.builder()
            .idx(1l)
            .userKey(randomUserKey)
            .type(BudgetType.EXPENSE)
            .amount(BigDecimal.valueOf(500))
            .comment("Initial Expense")
            .transactionDate(LocalDateTime.now())
            .accountIdx(accountIdx)
            .categoryIdx(categoryIdx)
            .build();

        BudgetInfo updatedBudget = BudgetInfo.builder()
            .idx(1l)
            .userKey(randomUserKey)
            .type(BudgetType.EXPENSE)
            .amount(BigDecimal.valueOf(600))
            .comment("Updated Expense")
            .transactionDate(LocalDateTime.now())
            .accountIdx(accountIdx)
            .categoryIdx(categoryIdx)
            .build();

        CacheInform cacheInform = CacheInform.builder()
            .user(CacheUser.builder().userKey(randomUserKey).build())
            .accounts(List.of(CacheAccount.builder().idx(accountIdx).name("TestAccount").type(AccountType.BANK.name()).build()))
            .categories(List.of(CacheCategory.builder().idx(categoryIdx).name("TestCategory").build()))
            .build();

        when(budgetAuthenticationService.getCacheInform(randomUserKey)).thenReturn(cacheInform);
        when(budgetPersistenceAdapter.updateBudget(any(BudgetInfo.class))).thenReturn(updatedBudget);

        BudgetInfo result = budgetService.updateBudget(updatedBudget);

        assertEquals(BigDecimal.valueOf(600), result.amount());
        assertEquals("Updated Expense", result.comment());
        verify(budgetAuthenticationService).getCacheInform(randomUserKey);
        verify(budgetPersistenceAdapter).updateBudget(any(BudgetInfo.class));
    }

    @Test
    void 예산생성_권한없는계좌_예외발생() {
        BudgetInfo budgetInfo = BudgetInfo.builder()
            .userKey(randomUserKey)
            .type(BudgetType.INCOME)
            .amount(BigDecimal.valueOf(1000))
            .comment("Test Income")
            .transactionDate(LocalDateTime.now())
            .accountIdx(999l)  // 존재하지 않는 계좌
            .categoryIdx(categoryIdx)
            .build();

        CacheInform cacheInform = CacheInform.builder()
            .user(CacheUser.builder().userKey(randomUserKey).build())
            .accounts(List.of(CacheAccount.builder().idx(accountIdx).name("TestAccount").type(AccountType.BANK.name()).build()))
            .categories(List.of(CacheCategory.builder().idx(categoryIdx).name("TestCategory").build()))
            .build();

        when(budgetAuthenticationService.getCacheInform(randomUserKey)).thenReturn(cacheInform);

        assertThrows(BudgetException.class, () -> budgetService.createBudget(budgetInfo));
    }

    @Test
    void 예산생성_권한없는카테고리_예외발생() {
        BudgetInfo budgetInfo = BudgetInfo.builder()
            .userKey(randomUserKey)
            .type(BudgetType.INCOME)
            .amount(BigDecimal.valueOf(1000))
            .comment("Test Income")
            .transactionDate(LocalDateTime.now())
            .accountIdx(accountIdx)
            .categoryIdx(999l)  // 존재하지 않는 카테고리
            .build();

        CacheInform cacheInform = CacheInform.builder()
            .user(CacheUser.builder().userKey(randomUserKey).build())
            .accounts(List.of(CacheAccount.builder().idx(accountIdx).name("TestAccount").type(AccountType.BANK.name()).build()))
            .categories(List.of(CacheCategory.builder().idx(categoryIdx).name("TestCategory").build()))
            .build();

        when(budgetAuthenticationService.getCacheInform(randomUserKey)).thenReturn(cacheInform);

        assertThrows(BudgetException.class, () -> budgetService.createBudget(budgetInfo));
    }

    @Test
    void 예산생성_잘못된금액_예외발생() {
        BudgetInfo budgetInfo = BudgetInfo.builder()
            .userKey(randomUserKey)
            .type(BudgetType.INCOME)
            .amount(BigDecimal.valueOf(-1000))  // 음수 금액
            .comment("Test Income")
            .transactionDate(LocalDateTime.now())
            .accountIdx(accountIdx)
            .categoryIdx(categoryIdx)
            .build();

        assertThrows(BudgetException.class, () -> budgetService.createBudget(budgetInfo));
    }

    @Test
    void 예산업데이트_권한없는계좌_예외발생() {
        BudgetInfo budgetInfo = BudgetInfo.builder()
            .idx(1l)
            .userKey(randomUserKey)
            .type(BudgetType.EXPENSE)
            .amount(BigDecimal.valueOf(500))
            .comment("Updated Expense")
            .transactionDate(LocalDateTime.now())
            .accountIdx(999l)  // 존재하지 않는 계좌
            .categoryIdx(categoryIdx)
            .build();

        CacheInform cacheInform = CacheInform.builder()
            .user(CacheUser.builder().userKey(randomUserKey).build())
            .accounts(List.of(CacheAccount.builder().idx(accountIdx).name("TestAccount").type(AccountType.BANK.name()).build()))
            .categories(List.of(CacheCategory.builder().idx(categoryIdx).name("TestCategory").build()))
            .build();

        when(budgetAuthenticationService.getCacheInform(randomUserKey)).thenReturn(cacheInform);

        assertThrows(BudgetException.class, () -> budgetService.updateBudget(budgetInfo));
    }

    @Test
    void 예산업데이트_권한없는카테고리_예외발생() {
        BudgetInfo budgetInfo = BudgetInfo.builder()
            .idx(1l)
            .userKey(randomUserKey)
            .type(BudgetType.EXPENSE)
            .amount(BigDecimal.valueOf(500))
            .comment("Updated Expense")
            .transactionDate(LocalDateTime.now())
            .accountIdx(accountIdx)
            .categoryIdx(999l)  // 존재하지 않는 카테고리
            .build();

        CacheInform cacheInform = CacheInform.builder()
            .user(CacheUser.builder().userKey(randomUserKey).build())
            .accounts(List.of(CacheAccount.builder().idx(accountIdx).name("TestAccount").type(AccountType.BANK.name()).build()))
            .categories(List.of(CacheCategory.builder().idx(categoryIdx).name("TestCategory").build()))
            .build();

        when(budgetAuthenticationService.getCacheInform(randomUserKey)).thenReturn(cacheInform);

        assertThrows(BudgetException.class, () -> budgetService.updateBudget(budgetInfo));
    }

    @Test
    void 예산삭제_존재하지않는예산_예외발생() {
        when(budgetPersistenceAdapter.findByUserKeyAndBudgetIdx(randomUserKey, 999l))
            .thenReturn(Optional.empty());

        assertThrows(BudgetException.class, () -> budgetService.deleteBudget(randomUserKey, 999l));
    }

    @Test
    void 사용자별예산전체삭제_성공() {
        doNothing().when(budgetPersistenceAdapter).deleteByUserKey(randomUserKey);

        budgetService.deleteAccount(randomUserKey);

        verify(budgetPersistenceAdapter).deleteByUserKey(randomUserKey);
    }

    @Test
    void 예산목록조회_빈목록() {
        when(budgetPersistenceAdapter.findBudgetsByUserKey(randomUserKey)).thenReturn(List.of());

        List<BudgetInfo> result = budgetService.getBudgetList(randomUserKey);
        assertEquals(0, result.size());
    }

    @Test
    void 예산목록조회_다중예산() {
        BudgetInfo budgetInfo1 = BudgetInfo.builder()
            .idx(1l)
            .userKey(randomUserKey)
            .type(BudgetType.INCOME)
            .amount(BigDecimal.valueOf(1000))
            .transactionDate(LocalDateTime.now())
            .accountIdx(accountIdx)
            .categoryIdx(categoryIdx)
            .build();

        BudgetInfo budgetInfo2 = BudgetInfo.builder()
            .idx(2l)
            .userKey(randomUserKey)
            .type(BudgetType.EXPENSE)
            .amount(BigDecimal.valueOf(500))
            .transactionDate(LocalDateTime.now())
            .accountIdx(accountIdx)
            .categoryIdx(categoryIdx)
            .build();

        when(budgetPersistenceAdapter.findBudgetsByUserKey(randomUserKey))
            .thenReturn(List.of(budgetInfo1, budgetInfo2));

        List<BudgetInfo> result = budgetService.getBudgetList(randomUserKey);
        assertEquals(2, result.size());
        assertEquals(BigDecimal.valueOf(1000), result.get(0).amount());
        assertEquals(BigDecimal.valueOf(500), result.get(1).amount());
    }
}
