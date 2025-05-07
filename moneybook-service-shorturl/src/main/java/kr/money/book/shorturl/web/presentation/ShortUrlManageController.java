package kr.money.book.shorturl.web.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import kr.money.book.shorturl.web.application.ShortUrlService;
import kr.money.book.shorturl.web.domain.datatransfer.ShortUrlCreateRequest;
import kr.money.book.shorturl.web.domain.datatransfer.ShortUrlCreateResponse;
import kr.money.book.shorturl.web.domain.datatransfer.ShortUrlInfoListResponse;
import kr.money.book.shorturl.web.domain.datatransfer.ShortUrlInfoResponse;
import kr.money.book.shorturl.web.domain.datatransfer.ShortUrlUpdateRequest;
import kr.money.book.shorturl.web.domain.datatransfer.ShortUrlUpdateResponse;
import kr.money.book.shorturl.web.domain.valueobject.ShortUrlInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shorturl/manager")
@Tag(name = "ShortUrl", description = "짧은URL 관리 API")
public class ShortUrlManageController {

    private final ShortUrlService shortUrlService;

    public ShortUrlManageController(ShortUrlService shortUrlService) {
        this.shortUrlService = shortUrlService;
    }

    @GetMapping
    @Operation(summary = "단축 URL 리스트 조회", description = "모든 단축 URL을 조회합니다.")
    public ResponseEntity<ShortUrlInfoListResponse> getShortUrlList() {

        List<ShortUrlInfo> shortUrlInfos = shortUrlService.getShortUrlList();

        return ResponseEntity.ok(ShortUrlInfoListResponse.of(shortUrlInfos));
    }

    @PostMapping
    @Operation(summary = "단축 URL 생성", description = "새로운 단축 URL을 생성합니다.")
    public ResponseEntity<ShortUrlCreateResponse> createShortUrl(
        @Valid @RequestBody ShortUrlCreateRequest request) {

        ShortUrlInfo shortUrlInfo = shortUrlService.createShortUrl(request.toShortUrlInfo());

        return ResponseEntity.ok(ShortUrlCreateResponse.of(shortUrlInfo));
    }

    @GetMapping("/{shortKey}")
    @Operation(summary = "단축 URL 조회", description = "단축 URL 정보를 조회합니다.")
    public ResponseEntity<ShortUrlInfoResponse> getShortUrl(
        @PathVariable String shortKey) {

        ShortUrlInfo shortUrlInfo = shortUrlService.findShortUrl(shortKey);

        return ResponseEntity.ok(ShortUrlInfoResponse.of(shortUrlInfo));
    }

    @PutMapping("/{shortKey}")
    @Operation(summary = "단축 URL 수정", description = "단축 URL 정보를 수정합니다.")
    public ResponseEntity<ShortUrlUpdateResponse> updateShortUrl(
        @PathVariable String shortKey,
        @Valid @RequestBody ShortUrlUpdateRequest request) {

        ShortUrlInfo shortUrlInfo = shortUrlService.updateShortUrl(request.toShortUrlInfo(shortKey));

        return ResponseEntity.ok(ShortUrlUpdateResponse.of(shortUrlInfo));
    }

    @DeleteMapping("/{shortKey}")
    @Operation(summary = "단축 URL 삭제", description = "단축 URL을 삭제합니다.")
    public ResponseEntity<Void> deleteShortUrl(
        @PathVariable String shortKey) {

        shortUrlService.deleteShortUrl(shortKey);

        return ResponseEntity.ok().build();
    }
}
