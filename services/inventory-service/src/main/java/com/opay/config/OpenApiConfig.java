package com.opay.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI inventoryServiceOpenApi() {
        return new OpenAPI().info(new Info()
                .title("OrbitPay -- Inventory Service API")
                .description(
                        "Stock reservation for Opay's embedded payments & order "
                                + "orchestration platform. Reserves/releases stock in response to "
                                + "Kafka events -- see docs/PROJECT-PLAN.md at the repo root. This "
                                + "controller is a thin demo/ops surface, not the primary interface."
                )
                .version("v0.1 (Phase 3 -- Distributed Architecture)"));
    }
}
