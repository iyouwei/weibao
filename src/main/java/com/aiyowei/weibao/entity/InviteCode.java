package com.aiyowei.weibao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("invite_code")
public class InviteCode {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long familyId;
    private String code;
    private String type;
    private Long creatorId;
    private LocalDateTime expireAt;
    private Integer status;
    private LocalDateTime createdAt;
}


