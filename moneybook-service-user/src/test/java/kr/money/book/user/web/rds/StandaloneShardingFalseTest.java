package kr.money.book.user.web.rds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import javax.sql.DataSource;
import kr.money.book.helper.IntegrationTest;
import kr.money.book.rds.configurations.DataSourceContextHolder;
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

@IntegrationTest
@SpringBootTest(properties = {
    "spring.datasource.enable=true",
    "spring.datasource.mode=standalone",
    "spring.datasource.sharding.enable=false",
    "spring.datasource.url=jdbc:mysql://127.0.0.1:13306/db_local?maxAllowedPacket=10485760&zeroDateTimeBehavior=CONVERT_TO_NULL&verifyServerCertificate=false&useSSL=false&allowPublicKeyRetrieval=true",
    "spring.datasource.username=local_rdb_master",
    "spring.datasource.password=local_rdb",
    "spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver",
    "spring.jpa.hibernate.ddl-auto=none"
})
@DirtiesContext
public class StandaloneShardingFalseTest {

    @Autowired
    @Qualifier("primaryDataSource")
    private DataSource primaryDataSource;

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @BeforeEach
    void setUp() {
        DataSourceContextHolder.clear();
        ShardContextHolder.clear();
    }

    @Test
    void 독립모드_샤딩비활성화_테스트() {
        assertTrue(dataSourceProperties.isEnable());
        assertEquals("standalone", dataSourceProperties.getMode());
        assertFalse(dataSourceProperties.getSharding().isEnable());

        RoutingDataSource routingDataSource = (RoutingDataSource) ((LazyConnectionDataSourceProxy) primaryDataSource).getTargetDataSource();
        Map<Object, DataSource> targetDataSources = routingDataSource.getTargetDataSources();

        assertTrue(targetDataSources.containsKey("DEFAULT"));
        assertEquals(1, targetDataSources.size());

        DataSourceContextHolder.setDataSourceType("SHARD");
        ShardContextHolder.setShardKey(500l);
        assertEquals("DEFAULT", routingDataSource.getCurrentLookupKey());
    }
}
