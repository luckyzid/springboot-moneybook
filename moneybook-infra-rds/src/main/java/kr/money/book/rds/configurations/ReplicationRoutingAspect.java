package kr.money.book.rds.configurations;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ReplicationRoutingAspect {

    @Before("@annotation(UseSlave)")
    public void routeToSlave() {

        DataSourceContextHolder.setDataSourceType("SLAVE");
    }

    @After("@annotation(UseSlave)")
    public void clearDataSourceType() {

        DataSourceContextHolder.clear();
    }
}
