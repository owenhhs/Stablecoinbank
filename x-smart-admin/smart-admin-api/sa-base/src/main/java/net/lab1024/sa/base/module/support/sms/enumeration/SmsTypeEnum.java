package net.lab1024.sa.base.module.support.sms.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lab1024.sa.base.common.enumeration.BaseEnum;

/**
 * @author 孙宇
 * @date 2024/11/08 00:19
 */
@Getter
@AllArgsConstructor
public enum SmsTypeEnum implements BaseEnum {

    AUTH_CODE(0, "验证码短信"),
    NOTIFY(1, "通知短信"),
    PROMOTION(2, "推广短信"),
    ;



    private final Integer value;
    private final String desc;

}
