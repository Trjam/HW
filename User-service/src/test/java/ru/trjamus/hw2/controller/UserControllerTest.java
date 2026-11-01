package ru.trjamus.hw2.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.trjamus.hw2.dto.UserRequest;
import ru.trjamus.hw2.dto.UserUpdateRequest;
import ru.trjamus.hw2.dto.UserResponse;
import ru.trjamus.hw2.exception.ResourceNotFoundException;
import ru.trjamus.hw2.service.UserService;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void getAll_returnsList() throws Exception {
        UserResponse response = new UserResponse(UUID.randomUUID(), "Боб", "qwe@qwe.ru", 25, LocalDateTime.now());
        when(userService.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Боб"));
    }

    @Test
    void getById_returnsUser() throws Exception {
        UUID id = UUID.randomUUID();
        UserResponse response = new UserResponse(id, "Тот", "asd@asd.ru", 30, LocalDateTime.now());
        when(userService.findById(id)).thenReturn(response);

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Тот"))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void getById_returns404_whenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(userService.findById(id)).thenThrow(new ResourceNotFoundException("User", id));

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Ресурс не найден"));
    }

    @Test
    void create_returns201AndBody() throws Exception {
        UserRequest request = new UserRequest();
        request.setName("Биб");
        request.setEmail("zxc@zxc.ru");
        request.setAge(22);

        UserResponse response = new UserResponse(UUID.randomUUID(), "Биб", "zxc@zxc.ru", 22, LocalDateTime.now());
        when(userService.create(any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.name").value("Биб"));
    }

    @Test
    void create_returns400_whenValidationFails() throws Exception {
        UserRequest request = new UserRequest();
        request.setName("");
        request.setEmail("abracadabra");

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    void update_returnsUpdatedUser() throws Exception {
        UUID id = UUID.randomUUID();
        UserUpdateRequest request = new UserUpdateRequest();
        request.setName("Афоня");

        UserResponse response = new UserResponse(id, "Афоня", "zzzzz@zzzzz.ru", 20, LocalDateTime.now());
        when(userService.update(eq(id), any(UserUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Афоня"));
    }

    @Test
    void delete_returns204() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_returns404_whenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new ResourceNotFoundException("User", id)).when(userService).deleteById(id);

        mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}