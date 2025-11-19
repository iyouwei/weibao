package com.aiyowei.weibao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("dish")
public class Dish {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long categoryId;
    private String name;
    private String cover;
    private String description;
    private Double price;
    private Double rating;
    private String tags;
    private Integer spicyLevel;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


