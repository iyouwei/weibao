package com.aiyowei.weibao.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoinTaskDto {
    private String code;
    private String title;
    private String description;
    private Integer reward;
    private String limitType;
    private Boolean completed;
}

