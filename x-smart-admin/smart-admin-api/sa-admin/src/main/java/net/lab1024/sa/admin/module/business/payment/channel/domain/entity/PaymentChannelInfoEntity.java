package net.lab1024.sa.admin.module.business.payment.channel.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 二级渠道基本信息表 实体类
 */

@Data
@TableName("t_payment_channel_info")
public class PaymentChannelInfoEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 部门id
     */
    private Long departmentId;

    /**
     * 商户名称
     */
    private String merName;

    /**
     * 商户昵称
     */
    private String merNickName;

    /**
     * 商户编码
     */
    private String merCode;

    /**
     * 商户持有人姓名
     */
    private String merUsername;

    /**
     * 商户绑定手机号
     */
    private String phone;

    /**
     * 商户证件号
     */
    private String idNumber;

    /**
     * 支付比例（百分比，路由使用）
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
     * 兑付比例
     */
    private Integer cashScale;

    /**
     * 兑付限额
     */
    private BigDecimal cashLimit;

    /**
     * 兑付笔数限制
     */
    private Integer cashCount;


    /**
     * 状态，0-禁用；1-启用；
     */
    private Integer status;

    /**
     * 实名认证状态，0:未认证，1:已认证
     */
    private Integer realNameAuth;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否黑名单（1：黑名单，2：非黑名单）
     */
    private Integer blackList;

    /**
     * 路由类型 1-百分比 2-轮询
     */
    private Integer routeType;

}