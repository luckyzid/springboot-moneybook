package kr.money.book.user.configure.auditing;


import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditorAwareMapping implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
            || !authentication.isAuthenticated()
            || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        return Optional.of((String) principal);
    }
}
