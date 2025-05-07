package kr.money.book.helper;

import java.util.Collections;
import kr.money.book.utils.StringUtil;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class CustomWithMockUserSecurityContextFactory implements WithSecurityContextFactory<CustomWithMockUser> {

    private static final String DEFAULT_USER_KEY = "test-user";

    @Override
    public SecurityContext createSecurityContext(CustomWithMockUser annotation) {
        String userKey = determineUserKey(annotation.userKey());
        return createSecurityContextWithUserKey(userKey, annotation.role());
    }

    public static void setRandomUserKey(String userKey) {
        ThreadLocalRandomHolder.setRandomUserKey(userKey);
    }

    public static String getRandomUserKey() {
        return ThreadLocalRandomHolder.getRandomUserKey();
    }

    public static void clearRandomUserKey() {
        ThreadLocalRandomHolder.clear();
    }

    private String determineUserKey(String providedUserKey) {
        String storedUserKey = ThreadLocalRandomHolder.getRandomUserKey();

        // 저장된 userKey가 있으면 우선 사용
        if (isValidStoredUserKey(storedUserKey)) {
            return storedUserKey;
        }

        // SpEL 표현식 처리
        if (providedUserKey.startsWith("#{")) {
            return parseSpelExpression(providedUserKey);
        }

        // 기본값(test-user)이면 랜덤 값 생성
        if (providedUserKey.equals(DEFAULT_USER_KEY)) {
            return generateAndStoreRandomUserKey();
        }

        return providedUserKey;
    }

    private String parseSpelExpression(String spelExpression) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        String randomUserKey = StringUtil.generateRandomString(10);
        context.setVariable("randomUserKey", randomUserKey);

        try {
            String evaluatedKey = parser.parseExpression(spelExpression.substring(2, spelExpression.length() - 1))
                .getValue(context, String.class);
            ThreadLocalRandomHolder.setRandomUserKey(evaluatedKey);
            return evaluatedKey;
        } catch (Exception e) {
            ThreadLocalRandomHolder.setRandomUserKey(randomUserKey);
            return randomUserKey;
        }
    }

    private String generateAndStoreRandomUserKey() {
        String newUserKey = StringUtil.generateRandomString(10);
        ThreadLocalRandomHolder.setRandomUserKey(newUserKey);
        return newUserKey;
    }

    private boolean isValidStoredUserKey(String storedUserKey) {
        return storedUserKey != null && !storedUserKey.isEmpty();
    }

    private SecurityContext createSecurityContextWithUserKey(String userKey, String role) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
            userKey,
            null,
            Collections.singletonList(new SimpleGrantedAuthority(role))
        );
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        return securityContext;
    }

    private static class ThreadLocalRandomHolder {

        private static final ThreadLocal<String> RANDOM_USER_KEY = new ThreadLocal<>();

        static void setRandomUserKey(String userKey) {
            RANDOM_USER_KEY.set(userKey);
        }

        static String getRandomUserKey() {
            return RANDOM_USER_KEY.get();
        }

        static void clear() {
            RANDOM_USER_KEY.remove();
        }
    }
}
