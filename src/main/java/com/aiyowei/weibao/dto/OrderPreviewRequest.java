package com.aiyowei.weibao.dto;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class OrderPreviewRequest {
    @NotEmpty
    private List<CartItemRequest> cartItems;
    private String deliveryType;
    private Long addressId;
}

