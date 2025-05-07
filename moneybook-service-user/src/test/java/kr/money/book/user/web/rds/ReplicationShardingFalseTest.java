package kr.money.book.user.web.rds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    "spring.datasource.sharding.enable=false",
    "spring.datasource.master.jdbc-url=jdbc:mysql://127.0.0.1:13306/db_local?maxAllowedPacket=10485760&zeroDateTimeBehavior=CONVERT_TO_NULL&verifyServerCertificate=false&useSSL=false&allowPublicKeyRetrieval=true",
    "spring.datasource.master.username=local_rdb_master",
    "spring.datasource.master.password=local_rdb",
    "spring.datasource.master.driver-class-name=com.mysql.cj.jdbc.Driver",
    "spring.datasource.slave.jdbc-url=jdbc:mysql://127.0.0.1:23306/db_local?maxAllowedPacket=10485760&zeroDateTimeBehavior=CONVERT_TO_NULL&verifyServerCertificate=false&useSSL=false&allowPublicKeyRetrieval=true",
    "spring.datasource.slave.username=local_rdb_master",
    "spring.datasource.slave.password=local_rdb",
    "spring.datasource.slave.driver-class-name=com.mysql.cj.jdbc.Driver",
    "spring.jpa.hibernate.ddl-auto=none"
})
@DirtiesContext
public class ReplicationShardingFalseTest {

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
    void 복제모드_샤딩비활성화_테스트() {
        assertTrue(dataSourceProperties.isEnable());
        assertEquals("replication", dataSourceProperties.getMode());
        assertFalse(dataSourceProperties.getSharding().isEnable());

        RoutingDataSource routingDataSource = (RoutingDataSource) ((LazyConnectionDataSourceProxy) primaryDataSource).getTargetDataSource();
        Map<Object, DataSource> targetDataSources = routingDataSource.getTargetDataSources();

        assertTrue(targetDataSources.containsKey(DataSourceType.MASTER));
        assertTrue(targetDataSources.containsKey(DataSourceType.SLAVE));
        assertEquals(2, targetDataSources.size());

        TransactionSynchronizationManager.setCurrentTransactionReadOnly(true);
        assertEquals(DataSourceType.SLAVE, routingDataSource.getCurrentLookupKey());

        TransactionSynchronizationManager.setCurrentTransactionReadOnly(false);
        assertEquals(DataSourceType.MASTER, routingDataSource.getCurrentLookupKey());

        DataSourceContextHolder.setDataSourceType("SHARD");
        ShardContextHolder.setShardKey(500l);
        assertEquals(DataSourceType.MASTER, routingDataSource.getCurrentLookupKey());

        TransactionSynchronizationManager.clearSynchronization();
    }
}
