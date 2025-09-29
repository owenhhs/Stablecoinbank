package net.lab1024.sa.admin.module.business.payment.channel.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 支付渠道业务范围配置表 实体类
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Data
@TableName("t_payment_channel_business_cfg")
public class PaymentChannelBusinessCfgEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 渠道商户Id
     */
    private Long channelId;

    /**
     * 支付方式类型，payment-支付方式；cash-兑付方式；
     */
    private String paymentType;

    /**
     * 支付限额最小值
     */
    private BigDecimal amountMin;

    /**
     * 支付限额最大值
     */
    private BigDecimal amountMax;

    /**
     * 币种
     */
    private String currency;

    /**
     * 国家
     */
    private String country;

    /**
     * 佣金比例，百分比
     */
    private BigDecimal brokerage;

    /**
     * 单笔奖励
     */
    private BigDecimal award;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}