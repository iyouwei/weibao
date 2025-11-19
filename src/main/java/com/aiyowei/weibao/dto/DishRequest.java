package com.aiyowei.weibao.dto;

import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DishRequest {
    @NotNull
    private Long categoryId;
    @NotBlank
    private String name;
    private String cover;
    private String description;
    @NotNull
    @Min(0)
    private Double price;
    private Double rating;
    private List<String> tags;
    private Integer spicyLevel;
    private Integer status;
}


