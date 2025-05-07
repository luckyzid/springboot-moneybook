package kr.money.book.web.configurations;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
    value = "server.http2",
    havingValue = "enabled",
    matchIfMissing = true
)
public class HttpToHttpsRedirectConfig {

    private int httpPort;
    private int httpsPort;

    public HttpToHttpsRedirectConfig(
        @Value("${server.http.port}") int httpPort,
        @Value("${server.port}") int httpsPort) {
        this.httpPort = httpPort;
        this.httpsPort = httpsPort;
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> servletContainerCustomizer() {

        return factory -> {
            factory.setPort(httpsPort);

            Connector httpConnector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
            httpConnector.setScheme("http");
            httpConnector.setPort(httpPort);
            httpConnector.setSecure(false);
            httpConnector.setRedirectPort(httpsPort);

            factory.addAdditionalTomcatConnectors(httpConnector);
        };
    }
}
