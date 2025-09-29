package net.lab1024.sa.admin.module.business.payment.channel.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import net.lab1024.sa.base.module.support.sensitive.mybatisplugins.annoation.EncryptedColumn;
import net.lab1024.sa.base.module.support.sensitive.mybatisplugins.annoation.EncryptedTable;

import javax.validation.constraints.NotNull;

/**
 * 渠道基本信息表 实体类
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Data
@EncryptedTable
@TableName("t_payment_channel_info")
public class PaymentChannelInfoEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商户名称
     */
    private String merName;

    /**
     * 商户编码
     */
    private String merCode;

    /**
     * 商户号
     */
    private String merAk;

    /**
     * 商户秘钥
     */
    @EncryptedColumn
    private String merSk;

    /**
     * 域名
     */
    private String domain;

    /**
     * 额外配置信息
     */
    private String ext;

    /**
     * 商户持有人姓名
     */
    private String merUsername;

    /**
     * 绑定手机号
     */
    private String phone;

    /**
     * 证件号
     */
    private String idNumber;

    /**
     * 支付标志
     */
    private Integer paymentFlag;

    /**
     * 支付比例
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
     * 兑付标志
     */
    private Integer cashFlag;

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
     * 工作时间
     */
    private String workTime;

    /**
     * 接口类型 1-API方式，2-非API方式（通过【渠道管理平台】支持）
     */
    private Integer interfaceType;

    /**
     * 实现类关联值
     */
    private String implCode;

    /**
     * 是否支持新疆用户 0-不支持 1-支持
     */
    private Integer xjFlag;

    /**
     * 状态，0-禁用；1-启用；
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