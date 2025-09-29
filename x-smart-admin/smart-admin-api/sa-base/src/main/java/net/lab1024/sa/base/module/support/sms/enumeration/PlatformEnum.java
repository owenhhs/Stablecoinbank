package net.lab1024.sa.base.module.support.sms.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lab1024.sa.base.common.code.OpenErrorCode;
import net.lab1024.sa.base.common.enumeration.BaseEnum;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.module.support.sms.ISmsClient;
import net.lab1024.sa.base.module.support.sms.impl.QxcioudSmsClientImpl;

/**
 * @author 孙宇
 * @date 2024/11/10 23:46
 */
@Getter
@AllArgsConstructor
public enum PlatformEnum implements BaseEnum {

    qxcioud("qxcioud", "奇讯云", QxcioudSmsClientImpl.class),
    ;

    private final String value;

    private final String desc;

    private final Class<? extends ISmsClient> smsServiceClass;

    public static PlatformEnum getByValue(String value) {
        PlatformEnum[] values = PlatformEnum.values();
        for (PlatformEnum v : values) {
            if (v.getValue().equals(value)) {
                return v;
            }
        }
        throw new BusinessException(OpenErrorCode.PARAM_ERROR);
    }
}
