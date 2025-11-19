package com.aiyowei.weibao.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private Long dishId;
    private String dishName;
    private Integer quantity;
    private String note;
    private Double price;
}

