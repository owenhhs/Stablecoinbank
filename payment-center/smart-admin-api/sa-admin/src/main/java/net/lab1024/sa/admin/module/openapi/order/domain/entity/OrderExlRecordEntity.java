package net.lab1024.sa.admin.module.openapi.order.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * eyp订单流水记录表 实体类
 *
 * @Author Sunny
 * @Date 2024-10-09 14:35:52
 * @Copyright Sunny
 */

@Data
@TableName("t_order_exl_record")
public class OrderExlRecordEntity {

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
     * 交易类型
     */
    private String tradeType;

    /**
     * 支付渠道
     */
    private String paymentMethod;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 用户id
     */
    private String uniqueCode;

    /**
     * 支付人姓名
     */
    private String payerName;

    private Integer code;

    private String msg;

    /**
     * 收银台url
     */
    private String payUrl;

    /**
     * 订单状态
     */
    private String tradeStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}