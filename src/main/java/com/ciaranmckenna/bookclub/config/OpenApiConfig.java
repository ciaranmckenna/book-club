package com.ciaranmckenna.bookclub.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI bookClubOpenAPI() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Book Club API")
                .description(
                    "REST API for Book Club application - manage reading lists, books, reviews, and user accounts")
                .version("1.0.0")
                .contact(new Contact().name("Book Club Team").email("support@bookclub.com")))
        .addSecurityItem(new SecurityRequirement().addList("session"))
        .components(
            new Components()
                .addSecuritySchemes(
                    "session",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .description("Session-based authentication")));
  }
}
