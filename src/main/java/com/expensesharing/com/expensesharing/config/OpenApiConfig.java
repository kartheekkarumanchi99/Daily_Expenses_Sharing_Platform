package com.expensesharing.com.expensesharing.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI expenseSharingOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("Daily Expenses Sharing Platform API")
                .description("REST API for managing users, recording expenses, splitting them "
                        + "(EQUAL / EXACT / PERCENTAGE), and generating consolidated balance sheets.")
                .version("v1")
                .contact(new Contact()
                        .name("Kartheek Karumanchi")
                        .email("kartheekkarumanchi99@gmail.com"))
                .license(new License().name("GNU GPL v2")));
    }
}
