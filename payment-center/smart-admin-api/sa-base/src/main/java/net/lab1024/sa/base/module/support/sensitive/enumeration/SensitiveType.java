package net.lab1024.sa.base.module.support.sensitive.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lab1024.sa.base.module.support.sensitive.util.SensitiveUtil;

import java.util.function.Function;


@AllArgsConstructor
@Getter
public enum SensitiveType {
    /**
     * 脱敏类型
     */
    USERNAME("username", "姓名", SensitiveUtil::username),
    PHONE("phone", "手机号", SensitiveUtil::phone),
    CUSTOM("custom", "自定义脱敏", null),
    ;

    private final String value;
    private final String desc;
    private final Function<String, String> function;
}
