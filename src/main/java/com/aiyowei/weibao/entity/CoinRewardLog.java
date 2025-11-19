package com.aiyowei.weibao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("coin_reward_log")
public class CoinRewardLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long walletId;
    private String taskCode;
    private Integer amount;
    private LocalDateTime createdAt;
}


