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
@TableName("t_order_np_record")
public class OrderNpRecordEntity {

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
     * 订单编号
     */
    private String orderNo;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 支付人姓名
     */
    private String payUserName;

    /**
     * 支付人IP
     */
    private String orderIp;

    /**
     * 订单时间
     */
    private String orderTime;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 第三方订单号
     */
    private String thirdOrderNo;

    /**
     * 收银台url
     */
    private String sendUrl;

    /**
     * 订单状态
     */
    private String billStatus;

    /**
     * 订单USDT数量
     */
    private String amountUsdt;

    /**
     * 出金请求结果
     */
    private String withdrawData;

    /**
     * 订单完成时间
     */
    private String finishedTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}