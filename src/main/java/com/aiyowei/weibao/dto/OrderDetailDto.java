package com.aiyowei.weibao.dto;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDto {
    private Long id;
    private String status;
    private Double totalAmount;
    private Integer coinUsed;
    private Double payAmount;
    private String deliveryType;
    private OffsetDateTime createdAt;
    private List<CartItemDto> items;
}

