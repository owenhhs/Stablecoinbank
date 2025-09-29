package net.lab1024.sa.admin.module.business.payment.channel.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import net.lab1024.sa.base.module.support.sensitive.mybatisplugins.annoation.EncryptedColumn;
import net.lab1024.sa.base.module.support.sensitive.mybatisplugins.annoation.EncryptedTable;

/**
 * 渠道基本信息表 列表VO
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Data
@EncryptedTable
public class PaymentChannelInfoVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "商户名称")
    private String merName;

    @Schema(description = "商户编码")
    private String merCode;

    @Schema(description = "商户号")
    private String merAk;

    @EncryptedColumn
    @Schema(description = "商户号")
    private String merSk;

    @Schema(description = "域名")
    private String domain;

    @Schema(description = "额外配置信息")
    private String ext;

    @Schema(description = "商户持有人姓名")
    private String merUsername;

    @Schema(description = "绑定手机号")
    private String phone;

    @Schema(description = "证件号")
    private String idNumber;

    @Schema(description = "支付标志")
    private Integer paymentFlag;

    @Schema(description = "支付比例")
    private Integer paymentScale;

    @Schema(description = "支付限额")
    private BigDecimal paymentLimit;

    @Schema(description = "支付笔数限制")
    private Integer paymentCount;

    @Schema(description = "兑付标志")
    private Integer cashFlag;

    @Schema(description = "兑付比例")
    private Integer cashScale;

    @Schema(description = "兑付限额")
    private BigDecimal cashLimit;

    @Schema(description = "兑付笔数限制")
    private Integer cashCount;

    @Schema(description = "工作时间")
    private String workTime;

    @Schema(description = "接口类型 1-API方式，2-非API方式（通过【渠道管理平台】支持）")
    private Integer interfaceType;

    @Schema(description = "实现类关联值")
    private String implCode;

    @Schema(description = "是否支持新疆用户 0-不支持 1-支持")
    private Integer xjFlag;

    @Schema(description = "状态，0-禁用；1-启用；")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "业务范围ID")
    private Long businessId;

}