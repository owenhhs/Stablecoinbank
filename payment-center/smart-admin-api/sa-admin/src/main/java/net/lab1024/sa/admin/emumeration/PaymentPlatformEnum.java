package net.lab1024.sa.admin.emumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lab1024.sa.admin.module.openapi.order.service.IPaymentPlatformService;
import net.lab1024.sa.admin.module.openapi.order.service.impl.*;
import net.lab1024.sa.base.common.code.OpenErrorCode;
import net.lab1024.sa.base.common.enumeration.BaseEnum;
import net.lab1024.sa.base.common.exception.BusinessException;

/**
 * @Author sunyu
 * @Date 2024/07/25 21:47:13
 */

@Getter
@AllArgsConstructor
public enum PaymentPlatformEnum implements BaseEnum {

    /**
     * 非API接口方式的渠道接口实现类定义
     */
    fxmch("fxmch", "支付渠道管理平台", PaymentPlatformFxmchImpl.class),


    /**
     * 使用API接口方式的渠道接口实现类定义
     */
    suding("suding", "速鼎", PaymentPlatformSudingImpl.class),

    letonggou("letonggou", "聚合易支付", PaymentPlatformLetonggouIpml.class),

    styfwl("styfwl", "彩虹易支付", PaymentPlatformLetonggouIpml.class),

    payrock("payrock", "payrock", PaymentPlatformPayrockIpml.class),

    ltg("ltg", "LTG", PaymentPlatformLtgIpml.class),

    eyp("eyp", "EYP", PaymentPlatformEypIpml.class),

    lyp("lyp", "LYP", PaymentPlatformLypIpml.class),

    usePay("usepay", "USEPAY", PaymentPlatformUsepayIpml.class),

    np("np", "NP", PaymentPlatformNpIpml.class),

    ;
    private final String value;

    private final String desc;

    private final Class<? extends IPaymentPlatformService> paymentPlatformServiceClass;


    public static PaymentPlatformEnum getByValue(String value) {
        PaymentPlatformEnum[] values = PaymentPlatformEnum.values();
        for (PaymentPlatformEnum v : values) {
            if (v.getValue().equals(value)) {
                return v;
            }
        }
        throw new BusinessException(OpenErrorCode.UNKNOWN_PAYMENT_PLATFORM);
    }
}
