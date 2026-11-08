package ru.trjamus.hw2.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendCreatedEmail(String to) {
        sendEmail(to, "Добро пожаловать!",
                "Здравствуйте! Ваш аккаунт на сайте был успешно создан.");
    }

    public void sendDeletedEmail(String to) {
        sendEmail(to, "Аккаунт удалён",
                "Здравствуйте! Ваш аккаунт был удалён.");
    }

    private void sendEmail(String to, String subject, String text) {
        log.info("""
            Email SEND ->
            To: {}
            Subject: {}
            Body: {}
            """, to, subject, text);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);

        log.info("Email SEND OK -> {}", to);
    }
}