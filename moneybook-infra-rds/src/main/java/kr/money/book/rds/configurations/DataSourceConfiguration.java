package kr.money.book.rds.configurations;

import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import kr.money.book.rds.configurations.properties.DataSourceProperties;
import kr.money.book.rds.configurations.properties.JpaProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableConfigurationProperties({DataSourceProperties.class, JpaProperties.class})
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = DataSourceConfiguration.BASE_PACKAGE,
    entityManagerFactoryRef = "primaryEntityManagerFactory",
    transactionManagerRef = "primaryTransactionManager"
)
public class DataSourceConfiguration {

    final static String BASE_PACKAGE = "kr.money.book";

    private final DataSourceProperties dataSourceProperties;
    private final JpaProperties jpaProperties;

    public DataSourceConfiguration(DataSourceProperties dataSourceProperties, JpaProperties jpaProperties) {
        this.dataSourceProperties = dataSourceProperties;
        this.jpaProperties = jpaProperties;
    }

    @Bean("masterDataSource")
    public DataSource masterDataSource() {

        DataSourceProperties.Master master = dataSourceProperties.getMaster();

        return buildDataSource(
            master.getJdbcUrl(),
            master.getUsername(),
            master.getPassword(),
            master.getDriverClassName()
        );
    }

    @Bean("slaveDataSource")
    public DataSource slaveDataSource() {

        DataSourceProperties.Slave slave = dataSourceProperties.getSlave();

        return buildDataSource(
            slave.getJdbcUrl(),
            slave.getUsername(),
            slave.getPassword(),
            slave.getDriverClassName()
        );
    }

    @Bean("shardDataSources")
    public Map<String, Object> shardDataSources() {

        Map<String, Object> shardMap = new HashMap<>();
        if (!dataSourceProperties.getSharding().isEnable()) {
            return shardMap;
        }

        if (dataSourceProperties.getSharding().getShards().isEmpty()) {
            throw new IllegalStateException("Sharding is enabled but no shards are configured");
        }

        boolean isReplication = "replication".equals(dataSourceProperties.getMode());
        for (int i = 0; i < dataSourceProperties.getSharding().getShards().size(); i++) {
            DataSourceProperties.Shard shard = dataSourceProperties.getSharding().getShards().get(i);
            if (isReplication) {
                shardMap.put("SHARD_" + i + "_MASTER", buildDataSource(
                    shard.getMaster().getJdbcUrl(),
                    shard.getMaster().getUsername(),
                    shard.getMaster().getPassword(),
                    shard.getMaster().getDriverClassName()
                ));
                shardMap.put("SHARD_" + i + "_SLAVE", buildDataSource(
                    shard.getSlave().getJdbcUrl(),
                    shard.getSlave().getUsername(),
                    shard.getSlave().getPassword(),
                    shard.getSlave().getDriverClassName()
                ));
            } else {
                shardMap.put("SHARD_" + i, buildDataSource(
                    shard.getJdbcUrl(),
                    shard.getUsername(),
                    shard.getPassword(),
                    shard.getDriverClassName()
                ));
            }
        }
        return shardMap;
    }

    @Primary
    @Bean("primaryDataSource")
    public DataSource primaryDataSource(
        @Qualifier("masterDataSource") DataSource masterDataSource,
        @Qualifier("slaveDataSource") DataSource slaveDataSource,
        @Qualifier("shardDataSources") Map<String, Object> shardDataSources) {

        if (!dataSourceProperties.isEnable()) {
            throw new IllegalStateException("RDS is disabled");
        }

        return "replication".equals(dataSourceProperties.getMode())
            ? createReplicationDataSource(masterDataSource, slaveDataSource, shardDataSources)
            : createStandaloneDataSource(shardDataSources);
    }

    private DataSource createReplicationDataSource(
        DataSource masterDataSource,
        DataSource slaveDataSource,
        Map<String, Object> shardDataSources) {

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(DataSourceType.MASTER, masterDataSource);
        dataSourceMap.put(DataSourceType.SLAVE, slaveDataSource);

        RoutingDataSource routingDataSource = new RoutingDataSource(dataSourceProperties);

        if (dataSourceProperties.getSharding().isEnable()) {
            if (shardDataSources.isEmpty()) {
                throw new IllegalStateException("No shard data sources configured");
            }
            dataSourceMap.putAll(shardDataSources);
        }

        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(masterDataSource);
        routingDataSource.afterPropertiesSet();

        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

    private DataSource createStandaloneDataSource(Map<String, Object> shardDataSources) {

        RoutingDataSource routingDataSource = new RoutingDataSource(dataSourceProperties);
        Map<Object, Object> dataSourceMap = new HashMap<>();

        DataSource standaloneDataSource = buildDataSource(
            dataSourceProperties.getUrl(),
            dataSourceProperties.getUsername(),
            dataSourceProperties.getPassword(),
            dataSourceProperties.getDriverClassName()
        );
        dataSourceMap.put("DEFAULT", standaloneDataSource);

        if (dataSourceProperties.getSharding().isEnable()) {
            if (shardDataSources.isEmpty()) {
                throw new IllegalStateException("No shard data sources configured");
            }
            dataSourceMap.putAll(shardDataSources);
        }

        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(standaloneDataSource);
        routingDataSource.afterPropertiesSet();

        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

    private DataSource buildDataSource(String url, String username, String password, String driverClassName) {

        if (url == null) {
            throw new IllegalArgumentException("JDBC URL is required");
        }

        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password are required");
        }

        return DataSourceBuilder.create()
            .type(HikariDataSource.class)
            .url(url)
            .username(username)
            .password(password)
            .driverClassName(driverClassName)
            .build();
    }

    @Bean("primaryEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
        @Qualifier("primaryDataSource") DataSource dataSource) {

        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan(BASE_PACKAGE);
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        emf.setPersistenceUnitName("entityManager");
        ;

        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", jpaProperties.getHibernate().getDdlAuto());
        properties.setProperty("hibernate.show_sql", String.valueOf(jpaProperties.getHibernate().isShowSql()));
        properties.setProperty("hibernate.format_sql", String.valueOf(jpaProperties.getHibernate().isFormatSql()));
        properties.setProperty("hibernate.use_sql_comments",
            String.valueOf(jpaProperties.getHibernate().isUseSqlComments()));
        properties.setProperty("hibernate.globally_quoted_identifiers",
            String.valueOf(jpaProperties.getHibernate().isGloballyQuotedIdentifiers()));
        properties.setProperty("hibernate.globally_quoted_identifiers_skip_column_definitions",
            String.valueOf(jpaProperties.getHibernate().isGloballyQuotedIdentifiersSkipColumnDefinitions()));
        properties.setProperty("hibernate.dialect", jpaProperties.getHibernate().getDialect());

        emf.setJpaProperties(properties);

        return emf;
    }

    @Bean("primaryTransactionManager")
    public PlatformTransactionManager platformTransactionManager(
        @Qualifier("primaryEntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {

        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(entityManagerFactory.getObject());

        return tm;
    }
}
