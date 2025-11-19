package com.aiyowei.weibao.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderPreviewResponse {
    private Double totalAmount;
    private Double discountAmount;
    private Integer coinAvailable;
    private Integer coinRecommended;
    private Double payableAmount;
    private List<CartItemDto> items;
}

