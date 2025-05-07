package kr.money.book.rds.configurations;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;
import kr.money.book.rds.configurations.properties.DataSourceProperties;
import kr.money.book.rds.configurations.properties.DataSourceProperties.Shard;
import kr.money.book.rds.configurations.properties.DataSourceProperties.Sharding;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
public class RoutingDataSource extends AbstractRoutingDataSource {

    private final DataSourceProperties dataSourceProperties;
    private final Map<Long, String> shardKeyCache = new ConcurrentHashMap<>();

    public RoutingDataSource(DataSourceProperties dataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
    }

    @Override
    protected Object determineCurrentLookupKey() {

        String dataSourceType = DataSourceContextHolder.getDataSourceType(); // SLAVE, SHARD
        boolean isShardingEnabled = dataSourceProperties.getSharding().isEnable();
        boolean isReplicationMode = "replication".equals(dataSourceProperties.getMode());

        if (isShardingEnabled && "SHARD".equals(dataSourceType)) {
            Long shardKey = ShardContextHolder.getShardKey();
            if (shardKey == null) {
                throw new IllegalStateException("Shard key is required when sharding is enabled");
            }
            String shard = shardKeyCache.computeIfAbsent(shardKey, this::determineShardKey);
            if (!isReplicationMode) {
                return shard;
            }

            return TransactionSynchronizationManager.isCurrentTransactionReadOnly()
                ? shard + "_SLAVE"
                : shard + "_MASTER";
        }

        if (isReplicationMode) {
            if (dataSourceType != null) {
                return "SLAVE".equals(dataSourceType)
                    ? DataSourceType.SLAVE
                    : DataSourceType.MASTER;
            }

            return (TransactionSynchronizationManager.isCurrentTransactionReadOnly())
                ? DataSourceType.SLAVE
                : DataSourceType.MASTER;
        }

        return "DEFAULT";
    }

    private String determineShardKey(Long shardKey) {
        Sharding sharding = dataSourceProperties.getSharding();
        int shardCount = sharding.getShards().size();
        ShardStrategy strategy = ShardStrategy.valueOf(sharding.getStrategy());

        return switch (strategy) {
            case RANGE -> determineRangeShard(shardKey);
            case MODULAR -> "SHARD_" + (shardKey % shardCount);
        };
    }

    private String determineRangeShard(Long shardKey) {
        List<Shard> shards = dataSourceProperties.getSharding().getShards();
        for (int i = 0; i < shards.size(); i++) {
            Shard shard = shards.get(i);
            if (shardKey >= shard.getRangeStart() && shardKey <= shard.getRangeEnd()) {
                return "SHARD_" + i;
            }
        }
        throw new IllegalArgumentException("Shard key " + shardKey + " is out of defined ranges");
    }

    public Map<Object, DataSource> getTargetDataSources() {
        return super.getResolvedDataSources();
    }

    public Object getCurrentLookupKey() {
        return determineCurrentLookupKey();
    }
}
