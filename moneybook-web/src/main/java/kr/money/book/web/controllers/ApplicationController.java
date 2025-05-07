package kr.money.book.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = ApplicationController.ENDPOINT_PREFIX)
@RequestMapping(ApplicationController.ENDPOINT_PREFIX)
public class ApplicationController {

    public static final String ENDPOINT_PREFIX = "application";

    @Operation(
        summary = "health check",
        description = "상태 조회",
        responses = {
            @ApiResponse(responseCode = "200", description = "정상 처리"),
            @ApiResponse(responseCode = "400", description = "클라 에러"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
        }
    )
    @PostMapping("/health/liveness")
    public ResponseEntity<String> liveness() {
        return ResponseEntity.ok().body("OK");
    }

    @Operation(
        summary = "에러 메세지",
        description = "에러 강제 체크",
        responses = {
            @ApiResponse(responseCode = "200", description = "정상 처리"),
            @ApiResponse(responseCode = "400", description = "클라 에러"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
        }
    )
    @PostMapping("/generate/badRequest")
    public ResponseEntity<String> badRequest() {
        throw new RuntimeException("generate custom bad request exception");
    }
}
