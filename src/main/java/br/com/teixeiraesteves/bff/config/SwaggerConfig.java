package br.com.teixeiraesteves.bff.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "BFF KnowledgeBase API Gateway",
        version = "1.0.0",
        description = "BFF responsável por rotear requisições para o SRV (Autenticação, 2FA, Arquivos e Atividades)"
))
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("BFF KnowledgeBase API Gateway")
                        .version("1.0.0")
                        .description("BFF responsável por rotear requisições para o SRV (Autenticação, 2FA, Arquivos e Atividades)")
                );
    }
}
