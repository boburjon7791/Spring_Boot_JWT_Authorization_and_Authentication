package com.example.demo.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ToString
public class StoreDto implements Serializable {
    public Long id;
    @NotBlank
    public String name;
    public String description;
    @PositiveOrZero
    public Integer capacity;
    @Email
    public String email;
}
