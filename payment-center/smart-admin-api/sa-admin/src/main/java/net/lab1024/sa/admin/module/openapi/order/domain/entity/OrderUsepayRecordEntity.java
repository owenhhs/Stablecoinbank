package net.lab1024.sa.admin.module.openapi.order.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * usepay订单流水记录表 实体类
 *
 * @Author bradydreamer
 * @Date 2025-01-09 14:35:52
 */

@Data
@TableName("t_order_usepay_record")
public class OrderUsepayRecordEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 渠道商户id
     */
    private Long channelId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 交易类型
     */
    private String tradeType;

    /**
     * 交易方式
     */
    private String paymentMethod;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 币种
     */
    private String currency;

    /**
     * 国家代码
     */
    private String country;

    /**
     * 电子邮箱
     */
    private String email;

    /**
     * 第三方流水
     */
    private String reference;

    /**
     * 支付链接
     */
    private String paymentUrl;

    /**
     * 订单状态
     */
    private String status;

    /**
     * 状态信息
     */
    private String statusMessage;

    /**
     * 订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 订单时间
     */
    private String orderTime;

    /**
     * 完成时间
     */
    private String completedTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}