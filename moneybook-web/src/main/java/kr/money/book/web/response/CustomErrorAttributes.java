package kr.money.book.web.response;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> defaultAttributes = super.getErrorAttributes(webRequest, options);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", defaultAttributes.get("timestamp"));
        errorDetails.put("status", defaultAttributes.get("status"));
        errorDetails.put("code", "MESSAGE_CODE_9999");
        errorDetails.put("message", defaultAttributes.get("error"));
        errorDetails.put("path", defaultAttributes.get("path"));
        errorDetails.put("details", null);

//        Map<String, Object> customAttributes = new HashMap<>();
//        customAttributes.put("success", false);
//        customAttributes.put("data", null);
//        customAttributes.put("error", errorDetails);

        return errorDetails;
    }
}
