package bank.management.configurations;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфиг Swagger-а.
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Bank card management system API",
                version = "1.0",
                description = "API предоставляющее тот или иной функционал взаимодействия с банковскими картами/пользователями в зависимости от роли пользователя."
        )
)
@Configuration
public class SwaggerConfiguration {

    // Убираю из схемы swagger-а все что связано с объектами для пагинации и GrantedAuthority.
    @Bean
    public OpenApiCustomizer swaggerCustomizer() {
        return openApi -> {
            openApi
                    .getComponents()
                    .getSchemas()
                    .keySet()
                    .removeIf(s ->
                            s.contains("Page") ||
                            s.contains("Sort") ||
                            s.contains("GrantedAuthority")
                    );
        };
    }
}
