package kr.money.book.user.web.rds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import javax.sql.DataSource;
import kr.money.book.helper.IntegrationTest;
import kr.money.book.rds.configurations.DataSourceContextHolder;
import kr.money.book.rds.configurations.DataSourceType;
import kr.money.book.rds.configurations.RoutingDataSource;
import kr.money.book.rds.configurations.ShardContextHolder;
import kr.money.book.rds.configurations.properties.DataSourceProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@IntegrationTest
@SpringBootTest(properties = {
    "spring.datasource.enable=true",
    "spring.datasource.mode=replication",
    "spring.datasource.sharding.enable=true",
    "spring.datasource.sharding.strategy=MODULAR",
    "spring.datasource.master.jdbc-url=jdbc:mysql://127.0.0.1:13306/db_local?maxAllowedPacket=10485760&zeroDateTimeBehavior=CONVERT_TO_NULL&verifyServerCertificate=false&useSSL=false&allowPublicKeyRetrieval=true",
    "spring.datasource.master.username=local_rdb_master",
    "spring.datasource.master.password=local_rdb",
    "spring.datasource.master.driver-class-name=com.mysql.cj.jdbc.Driver",
    "spring.datasource.slave.jdbc-url=jdbc:mysql://127.0.0.1:23306/db_local?maxAllowedPacket=10485760&zeroDateTimeBehavior=CONVERT_TO_NULL&verifyServerCertificate=false&useSSL=false&allowPublicKeyRetrieval=true",
    "spring.datasource.slave.username=local_rdb_master",
    "spring.datasource.slave.password=local_rdb",
    "spring.datasource.slave.driver-class-name=com.mysql.cj.jdbc.Driver",
    "spring.datasource.sharding.shards[0].master.jdbc-url=jdbc:mysql://127.0.0.1:33306/shard_0_db?maxAllowedPacket=10485760&zeroDateTimeBehavior=CONVERT_TO_NULL&verifyServerCertificate=false&useSSL=false&allowPublicKeyRetrieval=true",
    "spring.datasource.sharding.shards[0].master.username=local_rdb_master",
    "spring.datasource.sharding.shards[0].master.password=local_rdb",
    "spring.datasource.sharding.shards[0].master.driver-class-name=com.mysql.cj.jdbc.Driver",
    "spring.datasource.sharding.shards[0].slave.jdbc-url=jdbc:mysql://127.0.0.1:43306/shard_0_db?maxAllowedPacket=10485760&zeroDateTimeBehavior=CONVERT_TO_NULL&verifyServerCertificate=false&useSSL=false&allowPublicKeyRetrieval=true",
    "spring.datasource.sharding.shards[0].slave.username=local_rdb_master",
    "spring.datasource.sharding.shards[0].slave.password=local_rdb",
    "spring.datasource.sharding.shards[0].slave.driver-class-name=com.mysql.cj.jdbc.Driver",
    "spring.datasource.sharding.shards[1].master.jdbc-url=jdbc:mysql://127.0.0.1:53306/shard_1_db?maxAllowedPacket=10485760&zeroDateTimeBehavior=CONVERT_TO_NULL&verifyServerCertificate=false&useSSL=false&allowPublicKeyRetrieval=true",
    "spring.datasource.sharding.shards[1].master.username=local_rdb_master",
    "spring.datasource.sharding.shards[1].master.password=local_rdb",
    "spring.datasource.sharding.shards[1].master.driver-class-name=com.mysql.cj.jdbc.Driver",
    "spring.datasource.sharding.shards[1].slave.jdbc-url=jdbc:mysql://127.0.0.1:63306/shard_1_db?maxAllowedPacket=10485760&zeroDateTimeBehavior=CONVERT_TO_NULL&verifyServerCertificate=false&useSSL=false&allowPublicKeyRetrieval=true",
    "spring.datasource.sharding.shards[1].slave.username=local_rdb_master",
    "spring.datasource.sharding.shards[1].slave.password=local_rdb",
    "spring.datasource.sharding.shards[1].slave.driver-class-name=com.mysql.cj.jdbc.Driver",
    "spring.jpa.hibernate.ddl-auto=none"
})
@DirtiesContext
public class ReplicationShardingTrueTest {

    @Autowired
    @Qualifier("primaryDataSource")
    private DataSource primaryDataSource;

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @BeforeEach
    void setUp() {
        DataSourceContextHolder.clear();
        ShardContextHolder.clear();
        TransactionSynchronizationManager.initSynchronization();
    }

    @Test
    void 복제모드_샤딩활성화_테스트() {
        assertTrue(dataSourceProperties.isEnable());
        assertEquals("replication", dataSourceProperties.getMode());
        assertTrue(dataSourceProperties.getSharding().isEnable());

        RoutingDataSource routingDataSource = (RoutingDataSource) ((LazyConnectionDataSourceProxy) primaryDataSource).getTargetDataSource();
        Map<Object, DataSource> targetDataSources = routingDataSource.getTargetDataSources();

        assertTrue(targetDataSources.containsKey(DataSourceType.MASTER));
        assertTrue(targetDataSources.containsKey(DataSourceType.SLAVE));
        assertTrue(targetDataSources.containsKey("SHARD_0_MASTER"));
        assertTrue(targetDataSources.containsKey("SHARD_0_SLAVE"));
        assertTrue(targetDataSources.containsKey("SHARD_1_MASTER"));
        assertTrue(targetDataSources.containsKey("SHARD_1_SLAVE"));

        DataSourceContextHolder.setDataSourceType("SHARD");
        ShardContextHolder.setShardKey(0l);
        TransactionSynchronizationManager.setCurrentTransactionReadOnly(true);
        assertEquals("SHARD_0_SLAVE", routingDataSource.getCurrentLookupKey());

        TransactionSynchronizationManager.setCurrentTransactionReadOnly(false);
        assertEquals("SHARD_0_MASTER", routingDataSource.getCurrentLookupKey());

        ShardContextHolder.setShardKey(1l);
        TransactionSynchronizationManager.setCurrentTransactionReadOnly(true);
        assertEquals("SHARD_1_SLAVE", routingDataSource.getCurrentLookupKey());

        TransactionSynchronizationManager.clearSynchronization();
    }
}
