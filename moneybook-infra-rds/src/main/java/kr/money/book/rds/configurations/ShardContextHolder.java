package kr.money.book.rds.configurations;

public class ShardContextHolder {

    private static final ThreadLocal<Long> SHARD_KEY_HOLDER = new ThreadLocal<>();

    public static void setShardKey(Long shardKey) {

        SHARD_KEY_HOLDER.set(shardKey);
    }

    public static Long getShardKey() {

        return SHARD_KEY_HOLDER.get();
    }

    public static void clear() {

        SHARD_KEY_HOLDER.remove();
    }
}
