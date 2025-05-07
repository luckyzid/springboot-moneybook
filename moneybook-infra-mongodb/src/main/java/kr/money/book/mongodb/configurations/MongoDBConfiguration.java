package kr.money.book.mongodb.configurations;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import java.util.List;
import java.util.concurrent.TimeUnit;
import kr.money.book.mongodb.configurations.properties.MongoDBProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
@EnableConfigurationProperties(MongoDBProperties.class)
public class MongoDBConfiguration {

    private final MongoDBProperties mongoDBProperties;

    public MongoDBConfiguration(MongoDBProperties mongoDBProperties) {
        this.mongoDBProperties = mongoDBProperties;
    }

    @Bean
    public MongoClient mongoClient() {

        if (!mongoDBProperties.isEnable()) {
            throw new IllegalStateException("MongoDB is disabled");
        }

        return mongoDBProperties.isReplicaSetMode()
            ? createReplicaSetMongoClient()
            : createStandaloneMongoClient();
    }

    @Bean
    public MongoTemplate mongoTemplate() {

        return new MongoTemplate(mongoClient(), mongoDBProperties.getDatabase());
    }

    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory mongoDatabaseFactory) {

        return new MongoTransactionManager(mongoDatabaseFactory);
    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory(MongoClient mongoClient) {

        return new SimpleMongoClientDatabaseFactory(mongoClient, mongoDBProperties.getDatabase());
    }

    private MongoClient createReplicaSetMongoClient() {

        StringBuilder connectionString = new StringBuilder("mongodb://");
        if (mongoDBProperties.getUsername() != null && mongoDBProperties.getPassword() != null) {
            connectionString
                .append(mongoDBProperties.getUsername()).append(":")
                .append(mongoDBProperties.getPassword()).append("@");
        }

        List<MongoDBProperties.ReplicaConfig> replicaSet = mongoDBProperties.getReplicaSet();
        for (int i = 0; i < replicaSet.size(); i++) {
            MongoDBProperties.ReplicaConfig node = replicaSet.get(i);
            connectionString.append(node.getHost()).append(":").append(node.getPort());
            if (i < replicaSet.size() - 1) {
                connectionString.append(",");
            }
        }

        connectionString.append("/")
            .append(mongoDBProperties.getDatabase())
            .append(mongoDBProperties.getOption());

        MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(connectionString.toString()))
            .applyToClusterSettings(builder -> builder
                .serverSelectionTimeout(10000, TimeUnit.MILLISECONDS)
                .localThreshold(15, TimeUnit.MILLISECONDS))
            .applyToSocketSettings(builder -> builder
                .connectTimeout(10000, TimeUnit.MILLISECONDS)
                .readTimeout(10000, TimeUnit.MILLISECONDS))
            .applyToConnectionPoolSettings(builder -> builder
                .maxWaitTime(2000, TimeUnit.MILLISECONDS)
                .maxConnectionIdleTime(60000, TimeUnit.MILLISECONDS))
            .retryWrites(true)
            .retryReads(true)
            .build();

        return MongoClients.create(settings);
    }

    private MongoClient createStandaloneMongoClient() {

        StringBuilder connectionString = new StringBuilder("mongodb://");
        if (mongoDBProperties.getUsername() != null && mongoDBProperties.getPassword() != null) {
            connectionString
                .append(mongoDBProperties.getUsername()).append(":")
                .append(mongoDBProperties.getPassword()).append("@");
        }

        connectionString
            .append(mongoDBProperties.getHost()).append(":").append(mongoDBProperties.getPort())
            .append("/").append(mongoDBProperties.getDatabase());

        MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(connectionString.toString()))
            .applyToSocketSettings(builder -> builder
                .connectTimeout(10000, TimeUnit.MILLISECONDS)
                .readTimeout(10000, TimeUnit.MILLISECONDS))
            .applyToConnectionPoolSettings(builder -> builder
                .maxWaitTime(2000, TimeUnit.MILLISECONDS)
                .maxConnectionIdleTime(60000, TimeUnit.MILLISECONDS))
            .retryWrites(true)
            .retryReads(true)
            .build();

        return MongoClients.create(settings);
    }
}
