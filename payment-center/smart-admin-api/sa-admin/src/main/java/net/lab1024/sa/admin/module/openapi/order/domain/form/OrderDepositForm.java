package net.lab1024.sa.admin.module.openapi.order.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.admin.emumeration.CurrencyEnum;
import net.lab1024.sa.base.common.code.OpenErrorCode;
import net.lab1024.sa.base.common.domain.BaseRequest;
import net.lab1024.sa.base.common.enumeration.DepositTypeEnum;
import net.lab1024.sa.base.common.enumeration.PaymentChannelEnum;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.swagger.SchemaEnum;
import net.lab1024.sa.base.common.util.SmartEnumUtil;
import org.apache.commons.lang3.StringUtils;

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

    @Schema(description = "存款人用户ID")
    private String userId;

    @Schema(description = "用户手机号")
    private String mobile;

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

    @Schema(description = "商品名称")
    @Size(max = 255, message = "商品名称长度不能超过255")
    private String name;

    @Schema(description = "用户IP地址")
    @Size(min = 1, max = 128, message = "用户IP地址长度不能超过15")
    private String clientIp;

    @Schema(description = "用户设备类型")
    @Size(min = 1, max = 32, message = "用户设备类型长度不能超过32")
    private String device;

    @Schema(description = "额外参数")
    @Size(min = 1, max = 512, message = "额外参数长度不能超过512")
    private String ext;

    @Schema(description = "电子邮箱")
    @Size(min = 1, max = 255, message = "电子邮箱长度不能超过255")
    private String email;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "国家")
    private String country;


    public void checkParams() {
        if (DepositTypeEnum.QRCODE.equalsValue(depositType) && StringUtils.isEmpty(paymentChannel)) {
            throw new BusinessException("二维码渠道不能为空");
        }
        if (StringUtils.isEmpty(currency)) {
            currency = "CNY";
        }
        if (StringUtils.isEmpty(country)) {
            country = "CN";
        }
        if (!SmartEnumUtil.checkEnum(currency, CurrencyEnum.class)) {
            throw new BusinessException(OpenErrorCode.PARAM_ERROR);
        }
        CurrencyEnum currencyEnum = SmartEnumUtil.getEnumByValue(currency, CurrencyEnum.class);
        if (!currencyEnum.getCountries().contains(country)) {
            throw new BusinessException(OpenErrorCode.PARAM_ERROR);
        }
    }

}
