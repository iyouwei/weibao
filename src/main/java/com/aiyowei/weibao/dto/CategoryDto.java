package com.aiyowei.weibao.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    private String name;
    private String icon;
    private String description;
    private Integer sort;
    private Boolean visible;
}

