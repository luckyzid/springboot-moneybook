package kr.money.book.budget.web.domain.mapper;

import kr.money.book.budget.web.domain.entity.Budget;
import kr.money.book.budget.web.domain.valueobject.BudgetInfo;
import kr.money.book.common.boilerplate.DomainMapper;
import org.springframework.stereotype.Component;

@Component
public class BudgetToBudgetInfoMapper implements DomainMapper<Budget, BudgetInfo> {

    @Override
    public BudgetInfo map(Budget budget) {
        if (budget == null) {
            return null;
        }
        return BudgetInfo.builder()
            .idx(budget.getIdx())
            .userKey(budget.getUserKey())
            .type(budget.getType())
            .amount(budget.getAmount())
            .comment(budget.getComment())
            .transactionDate(budget.getTransactionDate())
            .accountIdx(budget.getAccountIdx())
            .categoryIdx(budget.getCategoryIdx())
            .build();
    }
}
