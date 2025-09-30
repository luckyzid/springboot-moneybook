package kr.money.book.abtest.condition;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import kr.money.book.abtest.context.AbTestUserContext;
import org.springframework.stereotype.Component;

/**
 * Allows users whose "activationDate" attribute is on or after the configured threshold.
 * Expects a {@link java.time.LocalDate} or {@link java.util.Date} attribute.
 */
@Component
public class ActiveSinceDateAbTestCondition implements AbTestCondition {

    private static final String ATTRIBUTE_KEY = "activationDate";
    private static final LocalDate THRESHOLD = LocalDate.of(2024, 1, 1);

    @Override
    public boolean matches(AbTestUserContext userContext) {

        Object attribute = userContext.attribute(ATTRIBUTE_KEY);
        if (attribute == null) {
            return false;
        }

        LocalDate activationDate = resolveDate(attribute);
        return activationDate != null && !activationDate.isBefore(THRESHOLD);
    }

    private LocalDate resolveDate(Object attribute) {

        if (attribute instanceof LocalDate localDate) {
            return localDate;
        }

        if (attribute instanceof Date legacyDate) {
            return legacyDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        return null;
    }
}
