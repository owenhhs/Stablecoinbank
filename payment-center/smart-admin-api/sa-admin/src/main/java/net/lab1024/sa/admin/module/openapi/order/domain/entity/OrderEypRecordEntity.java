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
@TableName("t_order_eyp_record")
public class OrderEypRecordEntity {

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
     * 平台订单号
     */
    private String platformOrderNo;

    /**
     * 订单状态
     */
    private String status;

    /**
     * 状态信息
     */
    private String statusMessage;

    /**
     * 订单实际金额
     */
    private BigDecimal realAmount;

    /**
     * 银行卡号
     */
    private String bankCard;

    /**
     * 银行账户
     */
    private String bankName;

    /**
     * 开户行
     */
    private String bankBranch;

    /**
     * 订单时间
     */
    private String createdAt;

    /**
     * 订单过期时间
     */
    private String expiredAt;

    /**
     * 完成时间
     */
    private String succeededAt;


    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}