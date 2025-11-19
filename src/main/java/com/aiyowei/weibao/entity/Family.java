package com.aiyowei.weibao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("family")
public class Family {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String type;
    private Long ownerId;
    private Long walletId;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


