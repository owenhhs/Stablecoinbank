package net.lab1024.sa.admin.module.openapi.order.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ltg订单流水记录表 实体类
 *
 * @Author Sunny
 * @Date 2024-10-09 14:35:52
 * @Copyright Sunny
 */

@Data
@TableName("t_order_ltg_record")
public class OrderLtgRecordEntity {

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
    private Long amount;

    /**
     * 币种
     */
    private String currency;

    /**
     * 国家代码
     */
    private String country;

    /**
     * 第三方流水
     */
    private String payOrderId;

    /**
     * 支付数据类型
     */
    private String payDataType;

    /**
     * 支付数据
     */
    private String payData;

    /**
     * 渠道号
     */
    private String appId;

    /**
     * 订单状态
     */
    private String orderState;

    /**
     * 订单金额
     */
    private Long orderAmount;

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