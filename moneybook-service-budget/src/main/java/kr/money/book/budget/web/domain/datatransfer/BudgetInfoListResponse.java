package kr.money.book.budget.web.domain.datatransfer;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import kr.money.book.budget.web.domain.valueobject.BudgetInfo;
import lombok.Builder;

@Schema(description = "예산 목록")
@Builder(toBuilder = true)
public record BudgetInfoListResponse(

    @Schema(description = "예산 정보 리스트")
    List<BudgetInfoList> lists

) {

    public static BudgetInfoListResponse of(List<BudgetInfo> budgetInfos) {

        return BudgetInfoListResponse.builder()
            .lists(budgetInfos.stream()
                .filter(Objects::nonNull)
                .map(BudgetInfoList::of)
                .collect(Collectors.toList())
            )
            .build();
    }
}
