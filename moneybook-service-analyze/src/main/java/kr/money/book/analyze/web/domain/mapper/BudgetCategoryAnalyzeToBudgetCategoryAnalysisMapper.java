package kr.money.book.analyze.web.domain.mapper;

import kr.money.book.analyze.web.domain.entity.BudgetCategoryAnalyze;
import kr.money.book.analyze.web.domain.valueobject.BudgetCategoryAnalysis;
import kr.money.book.common.boilerplate.DomainMapper;
import org.springframework.stereotype.Component;

@Component
public class BudgetCategoryAnalyzeToBudgetCategoryAnalysisMapper implements
    DomainMapper<BudgetCategoryAnalyze, BudgetCategoryAnalysis> {

    @Override
    public BudgetCategoryAnalysis map(BudgetCategoryAnalyze budgetCategoryAnalyze) {

        if (budgetCategoryAnalyze == null) {
            return null;
        }

        return BudgetCategoryAnalysis.builder()
            .accountIdx(budgetCategoryAnalyze.getAccountIdx())
            .categoryIdx(budgetCategoryAnalyze.getCategoryIdx())
            .amount(budgetCategoryAnalyze.getAmount())
            .income(budgetCategoryAnalyze.getIncome())
            .expense(budgetCategoryAnalyze.getExpense())
            .transactionDate(budgetCategoryAnalyze.getTransactionDate())
            .build();
    }
}
