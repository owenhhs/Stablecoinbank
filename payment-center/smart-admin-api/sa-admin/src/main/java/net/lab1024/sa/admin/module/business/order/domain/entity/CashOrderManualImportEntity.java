package net.lab1024.sa.admin.module.business.order.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 兑付订单手工导入记录表 实体类
 */

@Data
@TableName("t_cash_order_manual_import")
public class CashOrderManualImportEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 操作批次号
     */
    private String batchNo;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 出金類型，bank
     */
    private String withdrawType;

    /**
     * 限定出金渠道
     */
    private String withdrawChannelList;

    /**
     * 出金金额
     */
    private BigDecimal amount;

    /**
     * 出金金额（USDT）
     */
    private BigDecimal amountUsdt;

    /**
     * 币种
     */
    private String currency;

    /**
     * 国家
     */
    private String country;

    /**
     * 银行账户名称
     */
    private String accountHolder;

    /**
     * 银行卡号码
     */
    private String bankAccount;

    /**
     * 银行代码
     */
    private String bankCode;

    /**
     * 收款银行名称
     */
    private String bankName;

    /**
     * 收款银行分行名称
     */
    private String bankBranch;

    /**
     * 收款银行所在省份
     */
    private String bankProvince;

    /**
     * 收款银行所在城市
     */
    private String bankCity;

    /**
     * 出金备注
     */
    private String remark;

    /**
     * 订单确认通知回调地址
     */
    private String callback;

    /**
     * 用户编号
     */
    private String userId;

    /**
     * 用户IP地址
     */
    private String clientIp;

    /**
     * 用户设备类型
     */
    private String device;

    /**
     * 额外参数
     */
    private String ext;

    /**
     * 交易状态 1-待处理 2-处理中 3-已完成
     */
    private Integer status;

    /**
     * 申请时间
     */
    private Long applyTime;

    /**
     * 订单完成时间
     */
    private Long finishedTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}