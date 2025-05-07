package kr.money.book.budget.web.domain.mapper;

import kr.money.book.budget.web.domain.entity.Budget;
import kr.money.book.budget.web.domain.valueobject.BudgetInfo;
import kr.money.book.common.boilerplate.DomainMapper;
import org.springframework.stereotype.Component;

@Component
public class BudgetInfoToBudgetMapper implements DomainMapper<BudgetInfo, Budget> {

    @Override
    public Budget map(BudgetInfo budgetInfo) {
        if (budgetInfo == null) {
            return null;
        }
        return Budget.builder()
            .userKey(budgetInfo.userKey())
            .type(budgetInfo.type())
            .amount(budgetInfo.amount())
            .comment(budgetInfo.comment())
            .transactionDate(budgetInfo.transactionDate())
            .accountIdx(budgetInfo.accountIdx())
            .categoryIdx(budgetInfo.categoryIdx())
            .build();
    }
}
