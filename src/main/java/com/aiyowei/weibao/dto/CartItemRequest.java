package com.aiyowei.weibao.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemRequest {
    @NotNull
    private Long dishId;
    @Min(1)
    private Integer quantity;
    private String note;
}

