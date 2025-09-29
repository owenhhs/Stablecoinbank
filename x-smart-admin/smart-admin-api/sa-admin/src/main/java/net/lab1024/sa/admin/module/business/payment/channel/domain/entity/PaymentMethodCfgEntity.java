package net.lab1024.sa.admin.module.business.payment.channel.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 支付渠道支付方式配置表 实体类
 */

@Data
@TableName("t_payment_method_cfg")
public class PaymentMethodCfgEntity {

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
     * 支付方式类型，payment-支付方式；cash-兑付方式；
     */
    private String paymentType;

    /**
     * 支付方式，bank-银行卡；Alipay-支付宝;WeChat-微信
     */
    private String paymentMethod;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}