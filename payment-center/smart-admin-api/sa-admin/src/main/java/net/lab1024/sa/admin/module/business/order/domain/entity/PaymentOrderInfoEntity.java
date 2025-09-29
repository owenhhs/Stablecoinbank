package net.lab1024.sa.admin.module.business.order.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import net.lab1024.sa.base.module.support.sensitive.mybatisplugins.annoation.EncryptedColumn;
import net.lab1024.sa.base.module.support.sensitive.mybatisplugins.annoation.EncryptedTable;

/**
 * 支付订单信息表 实体类
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */
@EncryptedTable
@Data
@TableName("t_payment_order_info")
public class PaymentOrderInfoEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商户ID
     */
    private Long platformId;

    /**
     * 渠道商户Id
     */
    private Long channelId;

    /**
     * 业务范围Id
     */
    private Long businessId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 銀行類型，bank/qrcode
     */
    private String depositType;

    /**
     * 二維碼渠道，deposit_type=qrcode时有值
     */
    private String paymentChannel;

    /**
     * 存款金额
     */
    private BigDecimal amount;

    /**
     * 银行账户
     */
    @EncryptedColumn
    private String accountName;


    private String accountNameHash;

    /**
     * 银行卡号码
     */
    @EncryptedColumn
    private String bankAccount;

    /**
     * 银行代码
     */
    private String bankCode;

    /**
     * 存款备注
     */
    private String depositRemark;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单确认通知回调地址
     */
    private String callback;

    /**
     * 前端回调地址
     */
    private String landingUrl;

    /**
     * 申请时间
     */
    private Long applyTime;

    /**
     * 订单过期时间
     */
    private Long expiredTime;

    /**
     * 订单完成时间
     */
    private Long finishedTime;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 用户IP地址
     */
    private String clientIp;

    /**
     * 用户设备类型
     */
    private String device;

    /**
     * 电子邮箱
     */
    @EncryptedColumn
    private String email;

    /**
     * 币种
     */
    private String currency;

    /**
     * 国家
     */
    private String country;

    /**
     * 额外参数
     */
    private String ext;

    /**
     * 二维码文件id
     */
    private Long qrocdeId;

    /**
     * 第三方订单编号
     */
    private String thirdpartyOrderNo;

    private String subMerName;

    /**
     * 支付状态 1 待付款 2 已付款 3 已取消 4 过期
     */
    private Integer paymentStatus;

    /**
     * 订单状态 1 待确认 2 已确认 3 挂起
     */
    private Integer status;

    /**
     * 支付单据文件id
     */
    private Long billFileId;

    /**
     * 查询订单确认状态重试次数
     */
    private Integer retry;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}