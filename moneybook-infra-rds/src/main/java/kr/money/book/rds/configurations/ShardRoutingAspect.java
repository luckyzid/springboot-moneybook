package kr.money.book.rds.configurations;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ShardRoutingAspect {

    @Around("@annotation(useShard)")
    public Object routeToShard(ProceedingJoinPoint joinPoint, UseShard useShard) throws Throwable {

        DataSourceContextHolder.setDataSourceType("SHARD");
        Long shardKey = extractShardKey(joinPoint, useShard.shardKey());
        ShardContextHolder.setShardKey(shardKey);

        try {
            return joinPoint.proceed();
        } finally {
            ShardContextHolder.clear();
            DataSourceContextHolder.clear();
        }
    }

    private Long extractShardKey(ProceedingJoinPoint joinPoint, String shardKeyParam) {

        Object[] args = joinPoint.getArgs();
        Signature signature = joinPoint.getSignature();
        if (!(signature instanceof MethodSignature methodSignature)) {
            throw new IllegalArgumentException("Method signature not found");
        }

        String[] paramNames = methodSignature.getParameterNames();
        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].equals(shardKeyParam) && args[i] instanceof Long) {
                return (Long) args[i];
            }
        }

        throw new IllegalArgumentException("Shard key '" + shardKeyParam + "' not found or invalid");
    }
}
