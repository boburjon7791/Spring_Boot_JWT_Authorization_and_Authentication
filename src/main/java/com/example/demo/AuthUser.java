package com.example.demo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Entity(name = "auth_user")
public class AuthUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    @Size(min = 8)
    @NotBlank
    private String password;
    @Column(nullable = false)
    @NotBlank
    @Builder.Default
    private String role=Role.CLIENT.name();
    public enum Role {
        ADMIN,
        CLIENT
    }
}
