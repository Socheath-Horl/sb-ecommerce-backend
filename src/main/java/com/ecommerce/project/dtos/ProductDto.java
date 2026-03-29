package com.ecommerce.project.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    @NotBlank
    private String name;
    private String image;
    @NotBlank
    private String description;
    private Integer quantity;
    private double price;
    private double discount;
    private Double specialPrice;
}
