package com.codesolutions.pmt.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Metadonnees de la documentation OpenAPI (Swagger UI sur /swagger-ui.html). */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI pmtOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Project Management Tool API")
                .description("API REST de la plateforme PMT (gestion de projets, taches, membres).")
                .version("1.0.0"));
    }
}
