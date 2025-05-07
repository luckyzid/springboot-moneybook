package kr.money.book.category.web.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import kr.money.book.category.web.application.CategoryService;
import kr.money.book.category.web.domain.datatransfer.CategoryCreateRequest;
import kr.money.book.category.web.domain.datatransfer.CategoryCreateResponse;
import kr.money.book.category.web.domain.datatransfer.CategoryInfoListResponse;
import kr.money.book.category.web.domain.datatransfer.CategoryInfoResponse;
import kr.money.book.category.web.domain.datatransfer.CategoryUpdateRequest;
import kr.money.book.category.web.domain.datatransfer.CategoryUpdateResponse;
import kr.money.book.category.web.domain.valueobject.CategoryInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category")
@Tag(name = "Category", description = "카테고리 관리 관련 API")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "카테고리 리스트", description = "사용자의 카테고리 목록 조회 (계층형 구조)")
    public ResponseEntity<CategoryInfoListResponse> getCategoryList(
        Authentication authentication) {

        String userKey = authentication.getName();
        List<CategoryInfo> categories = categoryService.getCategoryList(userKey);

        return ResponseEntity.ok(CategoryInfoListResponse.of(categories));
    }

    @GetMapping("/{categoryIdx}")
    @Operation(summary = "카테고리 조회", description = "카테고리 정보 조회")
    public ResponseEntity<CategoryInfoResponse> getCategory(
        Authentication authentication,
        @PathVariable Long categoryIdx) {

        String userKey = authentication.getName();
        CategoryInfo categoryInfo = categoryService.getCategory(userKey, categoryIdx);

        return ResponseEntity.ok(CategoryInfoResponse.of(categoryInfo));
    }

    @PostMapping
    @Operation(summary = "카테고리 추가", description = "새로운 카테고리를 추가 (계층형 지원)")
    public ResponseEntity<CategoryCreateResponse> createCategory(
        Authentication authentication,
        @Valid @RequestBody CategoryCreateRequest request) {

        String userKey = authentication.getName();
        CategoryInfo categoryInfo = categoryService.createCategory(request.toCategoryInfo(userKey));

        return ResponseEntity.ok(CategoryCreateResponse.of(categoryInfo));
    }

    @PutMapping("/{categoryIdx}")
    @Operation(summary = "카테고리 수정", description = "기존 카테고리 정보를 수정 (계층형 지원)")
    public ResponseEntity<CategoryUpdateResponse> updateCategory(
        Authentication authentication,
        @PathVariable Long categoryIdx,
        @Valid @RequestBody CategoryUpdateRequest request) {

        String userKey = authentication.getName();
        CategoryInfo categoryInfo = categoryService.updateCategory(request.toCategoryInfo(userKey, categoryIdx));

        return ResponseEntity.ok(CategoryUpdateResponse.of(categoryInfo));
    }

    @DeleteMapping("/{categoryIdx}")
    @Operation(summary = "카테고리 삭제", description = "카테고리 삭제 및 하위 항목 모두 삭제")
    public ResponseEntity<Void> deleteCategory(
        Authentication authentication,
        @PathVariable Long categoryIdx) {

        String userKey = authentication.getName();
        categoryService.deleteCategory(userKey, categoryIdx);

        return ResponseEntity.ok().build();
    }
}
