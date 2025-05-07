package kr.money.book.helper;

import java.util.Arrays;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

public class IntegrationTestExecutionListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestClass(@NonNull TestContext testContext) {
        ApplicationContext applicationContext = testContext.getApplicationContext();
        resetAllMocks(applicationContext);
    }

    private void resetAllMocks(ApplicationContext applicationContext) {
        Arrays.stream(applicationContext.getBeanDefinitionNames())
            .map(applicationContext::getBean)
            .filter(bean -> Mockito.mockingDetails(bean).isMock())
            .forEach(Mockito::reset);
    }
}
