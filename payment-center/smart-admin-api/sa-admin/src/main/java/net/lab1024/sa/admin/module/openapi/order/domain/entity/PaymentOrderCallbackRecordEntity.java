package net.lab1024.sa.admin.module.openapi.order.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单状态回调记录表 实体类
 *
 * @Author bradydreamer
 * @Date 2025-01-09 14:35:52
 */

@Data
@TableName("t_payment_order_callbak_record")
public class PaymentOrderCallbackRecordEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单状态
     */
    private Integer orderStatus;

    /**
     * 回调时间
     */
    private String callbackTime;

    /**
     * 回调状态，0-失败；1-成功；
     */
    private Integer callbackStatus;

    /**
     * 回调结果
     */
    private String callbackResult;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}