package kr.money.book.analyze.web.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.money.book.analyze.web.application.AnalyzeService;
import kr.money.book.analyze.web.domain.datatransfer.AnalysisRequest;
import kr.money.book.analyze.web.domain.datatransfer.AnalysisResponse;
import kr.money.book.analyze.web.domain.valueobject.AnalysisData;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analyze")
@Tag(name = "Analyze", description = "분석 관련 API")
public class AnalyzeController {

    private final AnalyzeService analyzeService;

    public AnalyzeController(AnalyzeService analyzeService) {
        this.analyzeService = analyzeService;
    }

    @PostMapping
    @Operation(summary = "분석 요청", description = "월간/주간 분석")
    public ResponseEntity<AnalysisResponse> analyze(
        Authentication authentication,
        @Valid @RequestBody AnalysisRequest request) {

        String userKey = authentication.getName();
        AnalysisData analysisResult = analyzeService.analyze(request.toAnalysisInfo(userKey));

        return ResponseEntity.ok(AnalysisResponse.of(analysisResult));
    }
}
