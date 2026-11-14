package ru.trjamus.hw2.notification.kafka;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.trjamus.hw2.notification.dto.UserEvent;
import ru.trjamus.hw2.notification.service.EmailService;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final EmailService emailService;

    @KafkaListener(topics = "user-events", groupId = "notification-group")
    public void handleUserEvent(UserEvent event) {
        log.info("Получено событие: {}", event);

        switch (event.type()) {
            case "CREATED" -> emailService.sendCreatedEmail(event.email());
            case "DELETED" -> emailService.sendDeletedEmail(event.email());
            default -> log.warn("Неизвестный тип события: {}", event.type());
        }
    }

}