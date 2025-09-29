package net.lab1024.sa.admin.module.business.payment.channel.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付渠道业务范围配置表 实体类
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
     * 部门Id
     */
    private Long departmentId;

    /**
     * 支付限额最小值
     */
    private BigDecimal amountMin;

    /**
     * 支付限额最大值
     */
    private BigDecimal amountMax;

    /**
     * 平台佣金比例，百分比
     */
    private BigDecimal platformBrokerage;

    /**
     * 商户佣金比例，百分比
     */
    private BigDecimal merBrokerage;

    /**
     * 平台奖励
     */
    private BigDecimal platformAward;

    /**
     * 商户奖励
     */
    private BigDecimal merAward;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}