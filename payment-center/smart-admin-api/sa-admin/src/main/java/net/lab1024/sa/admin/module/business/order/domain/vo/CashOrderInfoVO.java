package net.lab1024.sa.admin.module.business.order.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 兑付订单信息表 列表VO
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Data
public class CashOrderInfoVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "出金類型，bank")
    private String withdrawType;

    @Schema(description = "限定出金渠道")
    private String withdrawChannelList;

    @Schema(description = "存款金额")
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

    @Schema(description = "出金备注")
    private String remark;

    @Schema(description = "订单确认通知回调地址")
    private String callback;

    @Schema(description = "申请时间")
    private Long applyTime;

    @Schema(description = "订单完成时间")
    private Long finishedTime;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "用户编号")
    private String userId;

    @Schema(description = "用户IP地址")
    private String clientIp;

    @Schema(description = "用户设备类型")
    private String device;

    @Schema(description = "额外参数")
    private String ext;

    @Schema(description = "交易状态 1-待处理 2-处理中 3-已完成")
    private Integer status;

    @Schema(description = "当前批次号")
    private Integer currBatchNo;

    @Schema(description = "人工处理标志：0-自动处理 1-人工处理")
    private Integer manualFlag;

    @Schema(description = "人工处理原因")
    private String manualReason;

    @Schema(description = "人工拆单标志 0-未拆单 1-拆单")
    private Integer manualSplitFlag;

    @Schema(description = "人工处理备注")
    private String manualRemark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}