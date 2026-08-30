package com.opay.orderservices.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI orderServiceOpenApi(){
        return  new OpenAPI().info(new Info()
                .title("Opay -- Order Service API")
                .description(
                        "Order lifecycle management for Opay's embedded payments & order "
                                + "orchestration platform. This is one service in a larger planned "
                                + "microservices system"
                )
                .version("v0.1 (Phase 2 -- Production Backend)"));
    }

}
