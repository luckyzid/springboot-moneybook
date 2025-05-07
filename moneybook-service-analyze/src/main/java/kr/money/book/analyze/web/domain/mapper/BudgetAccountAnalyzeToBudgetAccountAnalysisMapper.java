package kr.money.book.analyze.web.domain.mapper;

import kr.money.book.analyze.web.domain.entity.BudgetAccountAnalyze;
import kr.money.book.analyze.web.domain.valueobject.BudgetAccountAnalysis;
import kr.money.book.common.boilerplate.DomainMapper;
import org.springframework.stereotype.Component;

@Component
public class BudgetAccountAnalyzeToBudgetAccountAnalysisMapper implements
    DomainMapper<BudgetAccountAnalyze, BudgetAccountAnalysis> {

    @Override
    public BudgetAccountAnalysis map(BudgetAccountAnalyze budgetAccountAnalyze) {

        if (budgetAccountAnalyze == null) {
            return null;
        }

        return BudgetAccountAnalysis.builder()
            .accountIdx(budgetAccountAnalyze.getAccountIdx())
            .amount(budgetAccountAnalyze.getAmount())
            .income(budgetAccountAnalyze.getIncome())
            .expense(budgetAccountAnalyze.getExpense())
            .transactionDate(budgetAccountAnalyze.getTransactionDate())
            .build();
    }
}
