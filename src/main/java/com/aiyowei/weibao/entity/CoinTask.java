package com.aiyowei.weibao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("coin_task")
public class CoinTask {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private String title;
    private String description;
    private Integer reward;
    private String limitType;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


