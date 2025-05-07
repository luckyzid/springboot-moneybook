package kr.money.book.web.response;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

@RestController
public class CustomErrorController implements ErrorController {

    private final CustomErrorAttributes errorAttributes;

    public CustomErrorController(CustomErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping(value = "/error", produces = "application/json")
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        ServletWebRequest webRequest = new ServletWebRequest(request);
        Map<String, Object> errorDetails = errorAttributes.getErrorAttributes(
            webRequest,
            ErrorAttributeOptions.defaults()
        );

        HttpStatus status = HttpStatus.valueOf((int) errorDetails.getOrDefault("status", 500));
        return new ResponseEntity<>(errorDetails, status);
    }
}
