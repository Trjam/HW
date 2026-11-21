package ru.trjamus.hw2.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.trjamus.hw2.assembler.UserModelAssembler;
import ru.trjamus.hw2.dto.UserRequest;
import ru.trjamus.hw2.dto.UserResponse;
import ru.trjamus.hw2.dto.UserUpdateRequest;
import ru.trjamus.hw2.service.UserService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "CRUD-операции над пользователями")
public class UserController {

    private final UserService userService;

    private final UserModelAssembler assembler;

    @Operation(summary = "Просто пинг", description = "без комментариев")
    @ApiResponse(responseCode = "200", description = "Пингуется, ура")
    @GetMapping("/ping")
    public String ping() {
        return "работает";
    }

    @Operation(summary = "Запрос на получение всех пользователей", description = "Список всех пользователей")
    @ApiResponse(responseCode = "200", description = "Список получен")
    @GetMapping("/all")
    public CollectionModel<EntityModel<UserResponse>> getAll() {

        List<UserResponse> users = userService.findAll();

        return assembler
                .toCollectionModel(users)
                .add(linkTo(UserController.class)
                        .withSelfRel());
    }

    @Operation(summary = "Запрос на получение всех взрослых пользователей (18+)", description = "Список всех пользователей 18 лет и старше")
    @ApiResponse(responseCode = "200", description = "Список получен")
    @GetMapping("/adults")
    public CollectionModel<EntityModel<UserResponse>> getAllAdults() {

        List<UserResponse> users = userService.findAllAdults();
        return assembler
                .toCollectionModel(users)
                .add(linkTo(UserController.class)
                        .slash("adults")
                        .withSelfRel());
    }

    @Operation(summary = "Запрос данных о пользователе по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{id}")
    public EntityModel<UserResponse> getById(@PathVariable UUID id) {

        return assembler
                .toModel(userService.findById(id));
    }

    @Operation(summary = "Создать пользователя", description = "Создаёт пользователя и отправляет событие в Kafka (CREATED)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь создан"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные"),
            @ApiResponse(responseCode = "409", description = "Email уже существует")
    })
    @PostMapping
    public ResponseEntity<EntityModel<UserResponse>> create(@Valid @RequestBody UserRequest request) {

        UserResponse created = userService.create(request);
        EntityModel<UserResponse> model = assembler.toModel(created);
        return ResponseEntity
                .created(URI.create("/api/users/" + created.id()))
                .body(model);
    }

    @Operation(summary = "Обновить данные пользователя по ID", description = "Передаются только поля, которые нужно изменить")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь обновлён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "409", description = "Email уже существует")
    })
    @PutMapping("/{id}")
    public EntityModel<UserResponse> update(@PathVariable UUID id, @Valid @RequestBody UserUpdateRequest request) {
        return assembler.toModel(userService.update(id, request));
    }

    @Operation(summary = "Удалить пользователя по ID",description = "Удаляет пользователя и отправляет событие в Kafka (DELETED)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь удалён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}