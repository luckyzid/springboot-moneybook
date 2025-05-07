package kr.money.book.rds.configurations.properties;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceProperties {

    private boolean enable = false;
    private String mode = "standalone"; // standalone, replication
    private Sharding sharding = new Sharding();
    private String url;
    private String username;
    private String password;
    private String driverClassName;
    private Master master = new Master();
    private Slave slave = new Slave();

    @Setter
    @Getter
    public static class Master {

        private String jdbcUrl;
        private String username;
        private String password;
        private String driverClassName;
        private boolean readOnly = false;
    }

    @Setter
    @Getter
    public static class Slave {

        private String jdbcUrl;
        private String username;
        private String password;
        private String driverClassName;
        private boolean readOnly = true;
    }

    @Setter
    @Getter
    public static class Sharding {

        private boolean enable = false;
        private String strategy = "RANGE";
        private List<Shard> shards = List.of();
    }

    @Setter
    @Getter
    public static class Shard {

        private String jdbcUrl;
        private String username;
        private String password;
        private String driverClassName;
        private Master master = new Master();
        private Slave slave = new Slave();
        private int rangeStart;
        private int rangeEnd;
    }
}
