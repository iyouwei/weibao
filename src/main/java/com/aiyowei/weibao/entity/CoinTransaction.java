package com.aiyowei.weibao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("coin_transaction")
public class CoinTransaction {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long walletId;
    private String type;
    private Integer amount;
    private Long orderId;
    private String taskCode;
    private String remark;
    private LocalDateTime createdAt;
}


