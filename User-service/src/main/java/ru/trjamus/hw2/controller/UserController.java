package ru.trjamus.hw2.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.trjamus.hw2.dto.UserRequest;
import ru.trjamus.hw2.dto.UserResponse;
import ru.trjamus.hw2.dto.UserUpdateRequest;
import ru.trjamus.hw2.service.UserService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Просто пинг")
    @GetMapping("/ping")
    public String ping() {
        return "работает";
    }

    @Operation(summary = "Запрос на получение всех пользователей")
    @GetMapping("/all")
    public List<UserResponse> getAll() {
        return userService.findAll();
    }

    @Operation(summary = "Запрос на получение всех взрослых пользователей (18+)")
    @GetMapping("/adults")
    public List<UserResponse> getAllAdults() {
        return userService.findAllAdults();
    }

    @Operation(summary = "Запрос данных о пользователе по ID")
    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable UUID id) {
        return userService.findById(id);
    }

    @Operation(summary = "Создать пользователя")
    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request) {
        UserResponse created = userService.create(request);
        return ResponseEntity
                .created(URI.create("/api/users/" + created.id()))
                .body(created);
    }

    @Operation(summary = "Обновить данные пользователя по ID")
    @PutMapping("/{id}")
    public UserResponse update(@PathVariable UUID id, @Valid @RequestBody UserUpdateRequest request) {
        return userService.update(id, request);
    }

    @Operation(summary = "Удалить пользователя по ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}