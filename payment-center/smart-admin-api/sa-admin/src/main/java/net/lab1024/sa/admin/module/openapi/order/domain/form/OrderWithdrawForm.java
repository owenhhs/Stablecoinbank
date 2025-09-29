package net.lab1024.sa.admin.module.openapi.order.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.admin.emumeration.CurrencyEnum;
import net.lab1024.sa.base.common.code.OpenErrorCode;
import net.lab1024.sa.base.common.domain.BaseRequest;
import net.lab1024.sa.base.common.enumeration.WithdrawTypeEnum;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.swagger.SchemaEnum;
import net.lab1024.sa.base.common.util.SmartEnumUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class OrderWithdrawForm implements BaseRequest {
    @Schema(description = "订单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "订单号 不能为空")
    @Size(min = 1, max = 30, message = "订单号长度不能超过30")
    private String orderNo;

    @Schema(description = "用户IP地址")
    @Size(min = 1, max = 128, message = "用户IP地址长度不能超过128")
    @NotBlank(message = "客户IP 不能为空")
    private String clientIp;

    @SchemaEnum(value = WithdrawTypeEnum.class, desc = "出金类型")
    @NotBlank(message = "出金类型 不能为空")
    private String withdrawType;

    @Schema(description = "限定出金渠道", required = false)
    private List<String> withdrawChannelList;

    @Schema(description = "出金金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "出金金额 不能为空")
    private BigDecimal amount;

    @Schema(description = "出金金额(USDT)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "出金金额(USDT) 不能为空")
    private BigDecimal amountUSDT;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "国家")
    private String country;

    @Schema(description = "用户编号")
//    @NotBlank(message = "用户编号 不能为空")
    private String userId;

    @Schema(description = "账户持有人")
    @NotBlank(message = "账户持有人 不能为空")
    @Size(min = 1, max = 50, message = "账户持有人长度不能超过50")
    private String accountHolder;

    @Schema(description = "银行账号")
    @NotBlank(message = "银行账号 不能为空")
    @Size(min = 1, max = 32, message = "银行账号长度不能超过30")
    private String bankAccount;

    @Schema(description = "收款银行名称")
//    @NotBlank(message = "银行名称 不能为空")
    private String bankName;

    @Schema(description = "收款银行分行名称")
//    @NotBlank(message = "银行名称 不能为空")
    private String bankBranch;

    @Schema(description = "收款银行所在省份")
//    @NotBlank(message = "银行名称 不能为空")
    private String bankProvince;

    @Schema(description = "收款银行所在城市")
//    @NotBlank(message = "银行名称 不能为空")
    private String bankCity;

    @Schema(description = "出金备注")
    @Size(min = 1, max = 255, message = "备注长度不能超过255")
    private String remark;

    @Schema(description = "回调地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "回调地址 不能为空")
    @Size(min = 1, max = 255, message = "回调地址长度不能超过255")
    private String callback;

    @Schema(description = "额外参数")
    @Size(min = 1, max = 512, message = "额外参数长度不能超过512")
    private String ext;

    @Schema(description = "电子邮箱")
    @Size(min = 1, max = 255, message = "电子邮箱长度不能超过255")
    private String email;


    public void checkParams() {
        if (!SmartEnumUtil.checkEnum(withdrawType, WithdrawTypeEnum.class)) {
            throw new BusinessException("不支持的出金类型" + withdrawType);
        }
        if (withdrawChannelList == null) {
            withdrawChannelList = new ArrayList<>();
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
        if (!CurrencyEnum.CNY.equalsValue(currency)) {
            throw new BusinessException("不支持的出金币种" + currency);
        }
    }

}
