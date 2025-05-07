package kr.money.book.web.configurations;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
    prefix = "swagger.ui",
    value = "enable",
    matchIfMissing = true
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Info info = new Info()
            .title("Moneybook API")
            .version("1.0.0")
            .description("Rest Api Docs")
            .termsOfService("http://urls")
            .contact(new Contact()
                .name("rest local")
                .email("dev@email.com")
                .url("http://urls"))
            .license(new License()
                .name("Lab License")
                .url("http://urls"));

        return new OpenAPI()
            .info(info)
            .components(new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    @Bean
    public OpenApiCustomizer globalResponsesCustomizer() {
        return openApi -> {
            openApi.getPaths().values().forEach(pathItem -> {
                pathItem.readOperations().forEach(operation -> {
                    ApiResponses apiResponses = operation.getResponses();
                    if (apiResponses == null) {
                        apiResponses = new ApiResponses();
                        operation.setResponses(apiResponses);
                    }
                    apiResponses.addApiResponse("401", new ApiResponse().description("인증되지 않은"));
                    apiResponses.addApiResponse("403", new ApiResponse().description("권한이 없는"));
                    apiResponses.addApiResponse("404", new ApiResponse().description("리소스를 찾을 수 없음"));
                    apiResponses.addApiResponse("500", new ApiResponse().description("서버 에러"));
                });
            });
        };
    }
}
