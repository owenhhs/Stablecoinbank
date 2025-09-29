package net.lab1024.sa.admin.module.openapi.order.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.code.OpenErrorCode;
import net.lab1024.sa.base.common.domain.BaseRequest;
import net.lab1024.sa.base.common.enumeration.DepositTypeEnum;
import net.lab1024.sa.base.common.enumeration.PaymentChannelEnum;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.swagger.SchemaEnum;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * @author 孙宇
 * @date 2024/07/25 22:49
 */
@Data
public class OrderDepositForm implements BaseRequest {

    @Schema(description = "商户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "商户编号 不能为空")
    private String merCode;

    @Schema(description = "订单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "订单号 不能为空")
    @Size(min = 1, max = 30, message = "订单号长度不能超过30")
    private String orderNo;

    @SchemaEnum(value = DepositTypeEnum.class, desc = "银行类型")
    @NotBlank(message = "银行类型 不能为空")
    private String depositType;

    @SchemaEnum(value = PaymentChannelEnum.class, desc = "二维码渠道", required = false)
    private String paymentChannel;

    @Schema(description = "存款金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "存款金额 不能为空")
    private BigDecimal amount;

    @Schema(description = "存款人")
    @NotBlank(message = "存款人 不能为空")
    @Size(min = 1, max = 50, message = "存款人长度不能超过50")
    private String depositHolder;

    @Schema(description = "存款人账号")
    @NotBlank(message = "存款人账号 不能为空")
    @Size(min = 1, max = 32, message = "存款人账号长度不能超过30")
    private String bankAccount;

    @Schema(description = "存款备注")
    @Size(min = 1, max = 255, message = "存款备注长度不能超过255")
    private String depositRemark;

    @Schema(description = "回调地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "回调地址 不能为空")
    @Size(min = 1, max = 255, message = "回调地址长度不能超过255")
    private String callback;

    @Schema(description = "落地页地址")
    @Size(min = 1, max = 255, message = "落地页地址长度不能超过255")
    private String landingUrl;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "国家")
    private String country;

    @Schema(description = "用户id")
    private String userId;


    public void checkParams() {
        if (DepositTypeEnum.QRCODE.equalsValue(depositType)) {
            throw new BusinessException(OpenErrorCode.PARAM_ERROR);
        }
    }

}
