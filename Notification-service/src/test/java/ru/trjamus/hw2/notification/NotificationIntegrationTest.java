package ru.trjamus.hw2.notification;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import ru.trjamus.hw2.notification.controller.NotificationController;
import ru.trjamus.hw2.notification.dto.UserEvent;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@DisplayName("интеграционные на отправку email")
class NotificationIntegrationTest {

    @Container
    static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("apache/kafka:3.8.0"));

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP);

    @Autowired
    private RestTestClient restTestClient; // <-- Внедряем готовый клиент

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.mail.host", () -> "localhost");
        registry.add("spring.mail.port", ServerSetupTest.SMTP::getPort);
    }

    @BeforeEach
    void setUp() {
        greenMail.reset();
    }

    @Test
    @DisplayName("Событие CREATED из Kafka → письмо «аккаунт создан»")
    void whenKafkaEventCreated_thenEmailIsSent() throws Exception {
        sendKafkaEvent(new UserEvent("CREATED", "qwe@qwe.ru"));

        assertThat(greenMail.waitForIncomingEmail(5_000, 1)).isTrue();
        MimeMessage message = greenMail.getReceivedMessages()[0];
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo("qwe@qwe.ru");
        assertThat(message.getSubject()).contains("Добро пожаловать");
    }

    @Test
    @DisplayName("Событие DELETED из Kafka → письмо «аккаунт удалён»")
    void whenKafkaEventDeleted_thenEmailIsSent() throws Exception {
        sendKafkaEvent(new UserEvent("DELETED", "qwe@qwe2.ru"));

        assertThat(greenMail.waitForIncomingEmail(5_000, 1)).isTrue();
        MimeMessage message = greenMail.getReceivedMessages()[0];
        assertThat(message.getAllRecipients()[0].toString()).isEqualTo("qwe@qwe2.ru");
        assertThat(message.getSubject()).contains("Аккаунт удалён");
    }

    @Test
    @DisplayName("REST CREATED → письмо «аккаунт создан»")
    void whenRestApiCreated_thenEmailIsSent() throws Exception {
        NotificationController.SendRequest request = new NotificationController.SendRequest();
        request.setType("CREATED");
        request.setEmail("qwe@qwe3.ru");

        // Fluent API RestTestClient
        restTestClient.post().uri("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isOk();

        assertThat(greenMail.waitForIncomingEmail(5_000, 1)).isTrue();
        assertThat(greenMail.getReceivedMessages()[0].getAllRecipients()[0].toString())
                .isEqualTo("qwe@qwe3.ru");
    }

    @Test
    @DisplayName("REST DELETED → письмо «аккаунт удалён»")
    void whenRestApiDeleted_thenEmailIsSent() throws Exception {
        NotificationController.SendRequest request = new NotificationController.SendRequest();
        request.setType("DELETED");
        request.setEmail("qwe@qwe4.ru");

        restTestClient.post().uri("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isOk();

        assertThat(greenMail.waitForIncomingEmail(5_000, 1)).isTrue();
        assertThat(greenMail.getReceivedMessages()[0].getAllRecipients()[0].toString())
                .isEqualTo("qwe@qwe4.ru");
    }

    @Test
    @DisplayName("REST с невалидным email → 400, письмо не отправлено")
    void whenRestApiInvalidEmail_then400AndNoEmail() {
        NotificationController.SendRequest request = new NotificationController.SendRequest();
        request.setType("CREATED");
        request.setEmail("abracadabra");

        restTestClient.post().uri("/api/notifications/send")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isBadRequest(); // <-- Проверка статуса

        assertThat(greenMail.getReceivedMessages()).isEmpty();
    }

    //хелпер для отправки всякого в кафку

    private void sendKafkaEvent(UserEvent event) {
        Map<String, Object> producerProps = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class
        );
        try (KafkaProducer<String, UserEvent> producer = new KafkaProducer<>(producerProps)) {
            producer.send(new ProducerRecord<>("user-events", event));
            producer.flush();
        }
    }
}