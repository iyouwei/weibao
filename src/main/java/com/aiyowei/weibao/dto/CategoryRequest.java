package com.aiyowei.weibao.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {
    @NotBlank
    private String name;
    private String icon;
    private String description;
    private Integer sortOrder;
    private Boolean visible;
}


