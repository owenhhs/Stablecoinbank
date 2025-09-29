package net.lab1024.sa.admin.module.business.payment.channel.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商户下支付方式轮询记录表 实体类
 */

@Data
@TableName("t_payment_channel_pay_info_pollinglist")
public class PaymentChannelPayInfoPollingListEntity {

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
     * 支付方式表id
     */
    private Long payInfoId;

    /**
     * 支付方式类型
     */
    private String type;

    /**
     * 开户行人名称
     */
    private String username;

    /**
     * 银行：如工商银行
     */
    private String bankInfo;

    /**
     * 银行卡号
     */
    private String bankNo;

    /**
     * 轮询计数
     */
    private Long count;

    /**
     * 状态：0：禁用，1：启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}