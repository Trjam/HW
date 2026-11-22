package ru.trjamus.hw2.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.client.simple.instances.user-service[0].uri=http://localhost:1",
        "spring.cloud.discovery.client.simple.instances.notification-service[0].uri=http://localhost:1"
})
class ApiGatewayApplicationTests {

    @LocalServerPort
    int port;

    @Test
    void fallback_returnsServiceUnavailable_whenDownstreamDown() {
        var client = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        var response = client.get()
                .uri("/api/users")
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(503);
        assertThat(response.getBody()).contains("user-service");
    }

    @Test
    void correlationId_isGeneratedAndReturned() {
        var client = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        var response = client.get()
                .uri("/api/users")
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getHeaders().getFirst("X-Correlation-Id")).isNotBlank();
    }

    @Test
    void traceId_isReturnedInResponse() {
        var client = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        var response = client.get()
                .uri("/api/users")
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getHeaders().getFirst("X-Trace-Id")).isNotBlank();
    }
}