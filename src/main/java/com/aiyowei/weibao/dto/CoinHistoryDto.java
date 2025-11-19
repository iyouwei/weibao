package com.aiyowei.weibao.dto;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoinHistoryDto {
    private String type;
    private Integer amount;
    private String remark;
    private OffsetDateTime createdAt;
}

