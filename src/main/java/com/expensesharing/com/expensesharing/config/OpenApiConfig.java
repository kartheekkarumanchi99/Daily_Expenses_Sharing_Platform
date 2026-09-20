package com.expensesharing.com.expensesharing.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI expenseSharingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Daily Expenses Sharing Platform API")
                        .description("REST API for managing users, recording expenses, splitting them "
                                + "(EQUAL / EXACT / PERCENTAGE), and generating consolidated balance sheets. "
                                + "All /users and /expenses endpoints require a Bearer JWT obtained from /auth.")
                        .version("v1")
                        .contact(new Contact()
                                .name("Kartheek Karumanchi")
                                .email("kartheekkarumanchi99@gmail.com"))
                        .license(new License().name("GNU GPL v2")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                .components(new Components().addSecuritySchemes(BEARER_SCHEME,
                        new SecurityScheme()
                                .name(BEARER_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
