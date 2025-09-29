package net.lab1024.sa.admin.emumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lab1024.sa.base.common.enumeration.BaseEnum;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author 孙宇
 * @date 2024/09/04 22:11
 */
@Getter
@AllArgsConstructor
public enum CurrencyEnum implements BaseEnum {
    /**
     * 阿里收款码
     */
    CNY("CNY", "人民币", Collections.singletonList("CN")),
    THB("THB", "泰铢", Collections.singletonList("TH")),
    IDR("IDR", "印尼盾", Collections.singletonList("ID")),
    VND("VND", "越南盾", Collections.singletonList("VN")),

    ;
    private final String value;

    private final String desc;

    private final List<String> countries;
}
