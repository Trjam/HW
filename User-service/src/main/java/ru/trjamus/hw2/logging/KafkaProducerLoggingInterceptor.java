package ru.trjamus.hw2.logging;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

@Slf4j
public class KafkaProducerLoggingInterceptor implements ProducerInterceptor<String, Object> {

    @Override
    public ProducerRecord<String, Object> onSend(ProducerRecord<String, Object> record) {
        log.info("""
                Kafka PRODUCE ->
                Topic: {}
                Key: {}
                Value: {}
                """,
                record.topic(),
                record.key(),
                record.value()
        );
        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        if (exception != null) {
            log.error("Kafka PRODUCE FAILED: {}", exception.getMessage());
        } else if (metadata != null) {
            log.info("""
                    Kafka PRODUCE ACK <-
                    Topic: {} | Partition: {} | Offset: {} | Timestamp: {}
                    """,
                    metadata.topic(),
                    metadata.partition(),
                    metadata.offset(),
                    metadata.timestamp()
            );
        }
    }

    @Override
    public void close() {}

    @Override
    public void configure(Map<String, ?> configs) {}

}