package com.ecommerce.project.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private Long id;
    private ProductDto product;
    private Integer quantity;
    private double discount;
    private double orderedProductPrice;
}
