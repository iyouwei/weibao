package com.aiyowei.weibao.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DishDto {
    private Long id;
    private Long categoryId;
    private String name;
    private String cover;
    private Double price;
    private Double rating;
    private List<String> tags;
    private Integer spicyLevel;
}

