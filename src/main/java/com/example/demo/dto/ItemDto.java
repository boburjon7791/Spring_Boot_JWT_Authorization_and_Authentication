package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ToString
public class ItemDto implements Serializable {
    public Long id;
    @NotBlank
    public String name;
    public String description;
    @NotBlank
    public String storeName;
    @PositiveOrZero
    public Double price;
}
