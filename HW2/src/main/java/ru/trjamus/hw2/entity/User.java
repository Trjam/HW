package ru.trjamus.hw2.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "Users", schema = "\"user\"")
@NoArgsConstructor
@Getter
@Setter
@ToString

public class User {

    @Id
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;

    //коли нет подробностей, пофантазируем, что имя и мыло не может быть нуллом,
    //последнее еще и уникальное пусть будет

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private  Integer age;

    @CreationTimestamp
    @Column(name = "Created_at", nullable = false, updatable = false)
    private Date createdAt;

    public User(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

}
