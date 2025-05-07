package kr.money.book.analyze.web.domain.mapper;

import kr.money.book.analyze.web.domain.entity.BudgetAnalyze;
import kr.money.book.analyze.web.domain.valueobject.BudgetAnalysis;
import kr.money.book.common.boilerplate.DomainMapper;
import org.springframework.stereotype.Component;

@Component
public class BudgetAnalyzeToBudgetAnalysisMapper implements DomainMapper<BudgetAnalyze, BudgetAnalysis> {

    @Override
    public BudgetAnalysis map(BudgetAnalyze budgetAnalyze) {

        if (budgetAnalyze == null) {
            return null;
        }

        return BudgetAnalysis.builder()
            .accountIdx(budgetAnalyze.getAccountIdx())
            .categoryIdx(budgetAnalyze.getCategoryIdx())
            .type(budgetAnalyze.getType())
            .comment(budgetAnalyze.getComment())
            .amount(budgetAnalyze.getAmount())
            .transactionDate(budgetAnalyze.getTransactionDate())
            .build();
    }
}
