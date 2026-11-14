package ru.trjamus.hw2.notification.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;
import ru.trjamus.hw2.notification.dto.UserEvent;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.retry.max-attempts:3}")
    private long maxAttempts;

    @Value("${app.kafka.retry.interval-ms:2000}")
    private long intervalMs;

    @Value("${app.kafka.error-handler.commit-after-retries:true}")
    private boolean commitAfterRetries;

    @Bean
    public ConsumerFactory<String, UserEvent> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
        props.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");

        JacksonJsonDeserializer<UserEvent> deserializer = new JacksonJsonDeserializer<>();
        Map<String, Object> deserializerConfig = new HashMap<>();
        deserializerConfig.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE, UserEvent.class.getName());
        deserializerConfig.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        deserializer.configure(deserializerConfig, false);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                (record, ex) -> log.error("Не удалось обработать сообщение после {} попыток: {}",
                        maxAttempts, ex.getMessage()),
                new FixedBackOff(intervalMs, maxAttempts)
        );

        errorHandler.setAckAfterHandle(commitAfterRetries);

        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}