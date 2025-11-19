package com.aiyowei.weibao.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatisticDto {
    private Integer totalOrders;
    private Double totalRevenue;
    private Integer coinIssued;
    private Integer activeFamilies;
}

