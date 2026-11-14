package ru.trjamus.hw2.assembler;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import ru.trjamus.hw2.controller.UserController;
import ru.trjamus.hw2.dto.UserResponse;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler
        implements RepresentationModelAssembler<UserResponse, EntityModel<UserResponse>> {

    @Override
    public EntityModel<UserResponse> toModel(UserResponse user) {
        System.out.println(">>> toModel links: Зашли ");
        return EntityModel.of(user,
                linkTo(methodOn(UserController.class).getById(user.id())).withSelfRel(),
                linkTo(methodOn(UserController.class).update(user.id(), null)).withRel("update"),
                linkTo(methodOn(UserController.class).delete(user.id())).withRel("delete"),
                linkTo(methodOn(UserController.class).getAll()).withRel("all"),
                linkTo(methodOn(UserController.class).getAllAdults()).withRel("adults")
        );
    }

    @Override
    public CollectionModel<EntityModel<UserResponse>> toCollectionModel(
            Iterable<? extends UserResponse> entities) {
        List<EntityModel<UserResponse>> models = StreamSupport
                .stream(entities.spliterator(), false)
                .map(this::toModel)
                .toList();

        return CollectionModel.of(models,
                linkTo(methodOn(UserController.class).getAll()).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllAdults()).withRel("adults"),
                linkTo(methodOn(UserController.class).create(null)).withRel("create")
        );
    }
}