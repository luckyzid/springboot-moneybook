package kr.money.book.rds.configurations.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "spring.jpa")
public class JpaProperties {

    private boolean showSql = false;
    private boolean generateDdl = false;
    private HibernateProperties hibernate = new HibernateProperties();

    @Setter
    @Getter
    public static class HibernateProperties {

        private String ddlAuto = "none";
        private boolean showSql = false;
        private boolean formatSql = false;
        private boolean useSqlComments = false;
        private boolean globallyQuotedIdentifiers = false;
        private boolean globallyQuotedIdentifiersSkipColumnDefinitions = false;
        private String dialect = "org.hibernate.dialect.H2Dialect";
    }
}
