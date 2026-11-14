package ru.trjamus.hw2.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import ru.trjamus.hw2.dto.UserEvent;
import ru.trjamus.hw2.dto.UserRequest;
import ru.trjamus.hw2.service.UserService;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = "user-events")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserEventPublisherIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Test
    void createUser_shouldPublishCreatedEvent() {

        UserRequest request = new UserRequest();
        request.setName("Test");
        request.setEmail("zxc@zxc.ru");
        request.setAge(22);
        userService.create(request);

        try (Consumer<String, UserEvent> consumer = createConsumer("test-create-group")) {
            consumer.subscribe(List.of("user-events"));

            ConsumerRecord<String, UserEvent> record = KafkaTestUtils.getSingleRecord(
                    consumer, "user-events", Duration.ofSeconds(10));

            assertThat(record).isNotNull();
            assertThat(record.value().type()).isEqualTo("CREATED");
            assertThat(record.value().email()).isEqualTo("zxc@zxc.ru");
        }
    }

    @Test
    void deleteUser_shouldPublishDeletedEvent() {

        UserRequest request = new UserRequest();
        request.setName("Delete");
        request.setEmail("zxc@zxc2.ru");
        request.setAge(33);
        var created = userService.create(request);

        userService.deleteById(created.id());

        try (Consumer<String, UserEvent> consumer = createConsumer("test-delete-group")) {
            consumer.subscribe(List.of("user-events"));

            ConsumerRecords<String, UserEvent> records = KafkaTestUtils.getRecords(
                    consumer, Duration.ofSeconds(10));

            assertThat(records.count()).isGreaterThanOrEqualTo(2);

            ConsumerRecord<String, UserEvent> last = null;
            for (ConsumerRecord<String, UserEvent> r : records) {
                last = r;
            }

            assertThat(last).isNotNull();
            assertThat(last.value().type()).isEqualTo("DELETED");
            assertThat(last.value().email()).isEqualTo("zxc@zxc2.ru");
        }
    }

    private Consumer<String, UserEvent> createConsumer(String groupId) {
        Map<String, Object> props = KafkaTestUtils.consumerProps(embeddedKafka, groupId, true);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
        props.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE, UserEvent.class.getName());
        props.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<String, UserEvent>(props).createConsumer();
    }
}