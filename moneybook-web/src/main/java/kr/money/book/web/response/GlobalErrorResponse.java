package kr.money.book.web.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import java.util.Map;
import kr.money.book.web.exceptions.BaseErrorCode;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GlobalErrorResponse {

    private int status;
    private String code;
    private String message;
    private String path;
    private Map<String, Object> details;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    @Builder
    public GlobalErrorResponse(
        LocalDateTime timestamp,
        int status,
        String code,
        String message,
        String path,
        Map<String,
        Object> details) {

        this.timestamp = timestamp;
        this.status = status;
        this.code = code;
        this.message = message;
        this.path = path;
        this.details = details;
    }

    public static GlobalErrorResponse of(BaseErrorCode errorCode, String path) {

        return GlobalErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(errorCode.getStatus().value())
            .code(errorCode.getCode())
            .message(errorCode.getMessage())
            .path(path)
            .build();
    }

    public static GlobalErrorResponse of(BaseErrorCode errorCode, String path, Map<String, Object> details) {

        return GlobalErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(errorCode.getStatus().value())
            .code(errorCode.getCode())
            .message(errorCode.getMessage())
            .path(path)
            .details(details)
            .build();
    }
}
