package ru.trjamus.hw2.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions.circuitBreaker;
import static org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions.rewritePath;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouterFunction<ServerResponse> userServiceRoute() {
        return route("user-service")
                .GET("/api/users/**", http())
                .POST("/api/users/**", http())
                .PUT("/api/users/**", http())
                .DELETE("/api/users/**", http())
                .filter(lb("user-service"))
                .filter(circuitBreaker("userServiceCircuitBreaker", URI.create("forward:/fallback/users")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> notificationServiceRoute() {
        return route("notification-service")
                .POST("/api/notifications/**", http())
                .filter(lb("notification-service"))
                .filter(circuitBreaker("notificationServiceCircuitBreaker", URI.create("forward:/fallback/notifications")))
                .build();
    }

    //  для мультисвагера

    @Bean
    public RouterFunction<ServerResponse> userServiceApiDocsRoute() {
        return route("user-service-api-docs")
                .GET("/v3/api-docs/user-service", http())
                // 👈 Используем .filter() и правильный метод
                .filter(rewritePath("/v3/api-docs/user-service", "/v3/api-docs"))
                .filter(lb("user-service"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> notificationServiceApiDocsRoute() {
        return route("notification-service-api-docs")
                .GET("/v3/api-docs/notification-service", http())
                // 👈 Используем .filter()
                .filter(rewritePath("/v3/api-docs/notification-service", "/v3/api-docs"))
                .filter(lb("notification-service"))
                .build();
    }
}