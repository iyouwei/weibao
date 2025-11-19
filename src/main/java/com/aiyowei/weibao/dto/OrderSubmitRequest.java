package com.aiyowei.weibao.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderSubmitRequest extends OrderPreviewRequest {
    private Integer coinUse;
    private String remark;
}

