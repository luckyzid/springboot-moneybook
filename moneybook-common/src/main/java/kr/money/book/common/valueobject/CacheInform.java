package kr.money.book.common.valueobject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "로그인 유저 정보")
public class CacheInform {

    @Schema(description = "사용자 고유 키")
    private CacheUser user;

    @Schema(description = "은행 or 카드 리스트")
    private List<CacheAccount> accounts;

    @Schema(description = "카테고리 리스트")
    private List<CacheCategory> categories;

    private Set<Long> accountIds;
    private Set<Long> categoryIds;

    @Builder
    public CacheInform(CacheUser user, List<CacheAccount> accounts, List<CacheCategory> categories) {
        this.user = user;
        this.accounts = accounts;
        this.categories = categories;
        updateAccountIds();
        updateCategoryIds();
    }

    public void setAccounts(List<CacheAccount> accounts) {

        this.accounts = accounts;
        updateAccountIds();
    }

    public void setCategories(List<CacheCategory> categories) {

        this.categories = categories;
        updateCategoryIds();
    }

    public void setUser(CacheUser user) {

        this.user = user;
    }

    private void updateAccountIds() {

        this.accountIds = (accounts != null && !accounts.isEmpty())
            ? accounts.stream().map(CacheAccount::getIdx).collect(Collectors.toSet())
            : Collections.emptySet();
    }

    private void updateCategoryIds() {

        this.categoryIds = (categories != null && !categories.isEmpty())
            ? categories.stream().map(CacheCategory::getIdx).collect(Collectors.toSet())
            : Collections.emptySet();
    }

    public boolean hasAccount(Long accountIdx) {

        return accountIds.contains(accountIdx);
    }

    public boolean hasAllAccounts(List<Long> accountIdxList) {

        if (accountIdxList == null || accountIdxList.isEmpty()) {
            return true;
        }

        return accountIds.containsAll(accountIdxList);
    }

    public boolean hasCategory(Long categoryIdx) {

        return categoryIds.contains(categoryIdx);
    }

    public boolean hasAllCategories(List<Long> categoryIdxList) {

        if (categoryIdxList == null || categoryIdxList.isEmpty()) {
            return true;
        }

        return categoryIds.containsAll(categoryIdxList);
    }

    public String getUserKey() {

        return user != null ? user.getUserKey() : null;
    }
}
