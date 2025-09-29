package net.lab1024.sa.admin.module.business.order.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 兑付订单拆分明细表 实体类
 */

@Data
@TableName("t_cash_order_detail")
public class CashOrderDetailEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 平台ID
     */
    private Long platformId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 处理批次
     */
    private Integer batchNo;

    /**
     * 批次申请时间
     */
    private Long applyTime;

    /**
     * 拆分序号
     */
    private Integer seqNo;

    /**
     * 出金子订单号：orderNo+batchNo+seqNo
     */
    private String subOrderNo;

    /**
     * 渠道类型 1-自有对接渠道、2-外部渠道
     */
    private Integer chnlType;

    /**
     * 渠道商户Id
     */
    private Long channelId;

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
     * 银行账户
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
     * 额外参数
     */
    private String ext;

    /**
     * 交易状态 1-处理中 2-处理成功 3-处理失败
     */
    private Integer status;

    private String errmsg;

    /**
     * 查询订单确认状态重试次数
     */
    private Integer retry;

    /**
     * 第三方订单编号
     */
    private String thirdpartyOrderNo;

    /**
     * 出金单据文件id
     */
    private Long billFileId;

    /**
     * 完成时间
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