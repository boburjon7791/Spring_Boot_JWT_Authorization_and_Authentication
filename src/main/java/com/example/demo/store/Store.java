package com.example.demo.store;

import com.example.demo.item.Item;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@ToString
public class Store implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;
    @NotBlank
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    @NotBlank
    @Email
    private String email;
    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Integer capacity;
    private String description;
    @ToString.Exclude

    @JsonManagedReference(value = "store")
    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY,cascade = CascadeType.ALL/*, orphanRemoval = true*/)
    @Builder.Default
    private Set<Item> items = new LinkedHashSet<>();
}
