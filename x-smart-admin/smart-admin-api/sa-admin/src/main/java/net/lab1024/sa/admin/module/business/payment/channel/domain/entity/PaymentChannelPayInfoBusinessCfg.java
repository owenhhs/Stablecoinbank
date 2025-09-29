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
@TableName("t_payment_channel_pay_info_business_cfg")
public class PaymentChannelPayInfoBusinessCfg {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 部门Id
     */
    private Long payInfoId;

    /**
     * 支付限额最小值
     */
    private BigDecimal amountMin;

    /**
     * 支付限额最大值
     */
    private BigDecimal amountMax;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}