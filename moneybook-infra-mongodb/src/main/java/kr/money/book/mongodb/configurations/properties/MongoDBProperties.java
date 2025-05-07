package kr.money.book.mongodb.configurations.properties;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "spring.mongodb")
public class MongoDBProperties {

    private boolean enable = false;
    private boolean replicaSetMode = false;
    private String host = "127.0.0.1";
    private Integer port = 27017;
    private String database = "moneybook";
    private String username;
    private String password;
    private String option;
    private List<ReplicaConfig> replicaSet;

    @Getter
    @Setter
    public static class ReplicaConfig {

        private String host = "127.0.0.1";
        private Integer port = 27017;
    }
}
