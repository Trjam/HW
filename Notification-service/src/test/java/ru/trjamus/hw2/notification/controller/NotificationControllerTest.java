package ru.trjamus.hw2.notification.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.trjamus.hw2.notification.service.EmailService;
import tools.jackson.databind.json.JsonMapper;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@DisplayName("NotificationController — тесты REST API")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    @MockitoBean
    private EmailService emailService;

    @Test
    @DisplayName("type=CREATED → 200 и вызов sendCreatedEmail")
    void whenCreated_thenSendCreatedEmail() throws Exception {
        var request = new NotificationController.SendRequest();
        request.setType("CREATED");
        request.setEmail("cvb@cvb.ru");

        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(emailService).sendCreatedEmail("cvb@cvb.ru");
        verify(emailService, never()).sendDeletedEmail(anyString());
    }

    @Test
    @DisplayName("type=DELETED → 200 и вызов sendDeletedEmail")
    void whenDeleted_thenSendDeletedEmail() throws Exception {
        var request = new NotificationController.SendRequest();
        request.setType("DELETED");
        request.setEmail("cvb@cvb2.ru");

        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(emailService).sendDeletedEmail("cvb@cvb2.ru");
        verify(emailService, never()).sendCreatedEmail(anyString());
    }

    @Test
    @DisplayName("Неизвестный type → 500, письмо не отправлено")
    void whenUnknownType_then500AndNoEmail() throws Exception {
        var request = new NotificationController.SendRequest();
        request.setType("abracadabra");
        request.setEmail("cvb@cvb3.ru");

        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());

        verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("Пустой type → 400")
    void whenBlankType_then400() throws Exception {
        var request = new NotificationController.SendRequest();
        request.setType("");
        request.setEmail("cvb@cvb4.ru");

        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("Невалидный email → 400")
    void whenInvalidEmail_then400() throws Exception {
        var request = new NotificationController.SendRequest();
        request.setType("CREATED");
        request.setEmail("abracadabra");

        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("Пустой email → 400")
    void whenBlankEmail_then400() throws Exception {
        var request = new NotificationController.SendRequest();
        request.setType("CREATED");
        request.setEmail("");

        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("Битый JSON → 400")
    void whenMalformedJson_then400() throws Exception {
        mockMvc.perform(post("/api/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{abra cadabra}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(emailService);
    }
}