package net.lab1024.sa.admin.module.business.order.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.module.support.sensitive.mybatisplugins.annoation.EncryptedColumn;
import net.lab1024.sa.base.module.support.sensitive.mybatisplugins.annoation.EncryptedTable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付订单信息表 列表VO
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Data
@EncryptedTable
public class PaymentOrderInfoListVO {


    @ExcelIgnore
    @Schema(description = "主键")
    private Long id;

    @ExcelIgnore
    @Schema(description = "平台ID")
    private Long platformId;

    @ExcelProperty("平台名称")
    @Schema(description = "平台名称")
    private String platformName;

    @ExcelIgnore
    @Schema(description = "交易时间")
    private Long payTime;

    @ExcelIgnore
    @Schema(description = "过期时间")
    private Long expiredTime;

    @ExcelIgnore
    @Schema(description = "交易完成时间")
    private Long finishedTime;

    @ExcelProperty("交易完成时间")
    private LocalDateTime finishedTimeLocal;

    @ExcelProperty("交易时间")
    private LocalDateTime payTimeLocal;

    @ExcelProperty("订单号")
    @Schema(description = "订单编号")
    private String orderNo;

    @ExcelProperty("金额")
    @Schema(description = "存款金额")
    private BigDecimal amount;

    @ExcelProperty("商户")
    @Schema(description = "商户名称")
    private String merName;

    @ExcelIgnore
    @Schema(description = "商户编号")
    private String merNo;

    @ExcelProperty("币种")
    @Schema(description = "币种")
    private String currency;

    @ExcelProperty("国家")
    @Schema(description = "国家")
    private String country;

    @ExcelProperty("子商户")
    @Schema(description = "子商户名称")
    private String subMerName;

    @ExcelProperty("付款方式")
    @Schema(description = "付款方式")
    private String paymentMethod;

    @ExcelProperty("付款人")
    @Schema(description = "银行账户")
    @EncryptedColumn
    private String accountName;

    @ExcelProperty("付款账号")
    @Schema(description = "银行卡号码")
    @EncryptedColumn
    private String bankAccount;

    @ExcelIgnore
    @Schema(description = "支付状态 1 待付款 2 已付款 3 已取消 4 已过期")
    private Integer payStatus;

    @ExcelProperty("支付状态")
    @Schema(description = "支付状态 1 待付款 2 已付款 3 已取消 4 已过期")
    private String payStatusStr;

    @ExcelIgnore
    @Schema(description = "标示成功或失败 1 待處理 2 已确认 3 挂起")
    private Integer status;

    @ExcelProperty("确认状态")
    @Schema(description = "标示成功或失败 1 待處理 2 已确认 3 挂起")
    private String statusStr;

    @ExcelIgnore
    @Schema(description = "支付单据文件id")
    private Long billFileId;

    @ExcelIgnore
    @Schema(description = "支付单据文件链接")
    private String billFileUrl;
}