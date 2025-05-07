package kr.money.book.helper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import kr.money.book.common.constants.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = CustomWithMockUserSecurityContextFactory.class)
public @interface CustomWithMockUser {

    String userKey() default "test-user";

    String role() default "ROLE_USER";
}
