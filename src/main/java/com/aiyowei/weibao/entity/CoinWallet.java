package com.aiyowei.weibao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("coin_wallet")
public class CoinWallet {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long familyId;
    private Integer balance;
    private Integer frozen;
    private Integer version;
    private LocalDateTime updatedAt;
}


