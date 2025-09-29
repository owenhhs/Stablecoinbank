package net.lab1024.sa.admin.module.business.payment.channel.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付渠道配置支付具体信息 实体类
 */

@Data
@TableName("t_payment_channel_pay_info_cfg")
public class PaymentChannelPayInfoCfgEntity {

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
     * 开户行：工商银行，张家口分行
     */
    private String bankName;

    /**
     * 银行卡号
     */
    private String bankNo;

    /**
     * 币种
     */
    private String currency;

    /**
     * 国家
     */
    private String country;

    /**
     * 是否黑名单（1：黑名单，2：非黑名单）
     */
    private Integer blackList;

    /**
     * 支付比例，百分比，路由时使用
     */
    private Integer paymentScale;

    /**
     * 支付限额
     */
    private BigDecimal paymentLimit;

    /**
     * 支付笔数限制
     */
    private Integer paymentCount;

    /**
     * 微信，支付宝，对应的二维码
     */
    private String payUrl;

    /**
     * 支付渠道工作时间
     */
    private String workTime;

    /**
     * 是否新疆专用，0-否；1-是；
     */
    private Integer xinjiang;

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