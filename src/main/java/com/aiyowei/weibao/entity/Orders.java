package com.aiyowei.weibao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("orders")
public class Orders {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long familyId;
    private String orderNo;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private Integer coinUsed;
    private BigDecimal payAmount;
    private String status;
    private String deliveryType;
    private Long addressId;
    private String remark;
    private LocalDateTime createdAt;
}


