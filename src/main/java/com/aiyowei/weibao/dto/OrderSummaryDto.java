package com.aiyowei.weibao.dto;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDto {
    private Long id;
    private String status;
    private Double totalAmount;
    private Integer coinUsed;
    private Double payAmount;
    private OffsetDateTime createdAt;
}

