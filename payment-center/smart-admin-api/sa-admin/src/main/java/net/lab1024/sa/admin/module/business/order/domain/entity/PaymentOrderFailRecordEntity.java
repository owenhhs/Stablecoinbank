package net.lab1024.sa.admin.module.business.order.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author 孙宇
 * @date 2025/04/10 22:05
 */
@Data
@TableName("t_payment_order_fail_record")
public class PaymentOrderFailRecordEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private String params;

    private String failMsg;

    private String requestId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
