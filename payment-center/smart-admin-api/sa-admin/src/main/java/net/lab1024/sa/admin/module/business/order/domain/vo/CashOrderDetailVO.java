package net.lab1024.sa.admin.module.business.order.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 兑付订单信息表 列表VO
 *
 * @Author bradydreamer
 * @Date 2025-01-22 22:12:31
 * @Copyright 2025
 */

@Data
public class CashOrderDetailVO {


    @Schema(description = "主键")
    private Long id;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "批次号")
    private Integer batchNo;

    @Schema(description = "申请时间")
    private Long applyTime;

    @Schema(description = "拆分序号")
    private Integer seqNo;

    @Schema(description = "出金子订单号")
    private String subOrderNo;

    @Schema(description = "渠道类型")
    private Integer chnlType;

    @Schema(description = "渠道商户Id")
    private Long channelId;

    @Schema(description = "出金金额")
    private BigDecimal amount;

    @Schema(description = "出金金额（USDT）")
    private BigDecimal amountUsdt;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "国家")
    private String country;

    @Schema(description = "银行账户名称")
    private String accountHolder;

    @Schema(description = "银行卡号码")
    private String bankAccount;

    @Schema(description = "银行代码")
    private String bankCode;

    @Schema(description = "收款银行名称")
    private String bankName;

    @Schema(description = "收款银行分行名称")
    private String bankBranch;

    @Schema(description = "收款银行所在省份")
    private String bankProvince;

    @Schema(description = "收款银行所在城市")
    private String bankCity;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "订单确认通知回调地址")
    private String callback;

    @Schema(description = "交易所用户编号")
    private String userId;

    @Schema(description = "用户IP地址")
    private String clientIp;

    @Schema(description = "用户设备类型")
    private String device;

    @Schema(description = "额外参数")
    private String ext;

    @Schema(description = "交易状态")
    private Integer status;

    @Schema(description = "错误信息")
    private String errmsg;

    @Schema(description = "查询订单确认状态重试次数")
    private Integer retry;

    @Schema(description = "第三方订单编号")
    private String thirdpartyOrderNo;

    @Schema(description = "出金单据文件id")
    private Long billFileId;

    @Schema(description = "订单完成时间")
    private Long finishedTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}