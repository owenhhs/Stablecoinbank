package net.lab1024.sa.base.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WithdrawChnlTypeEnum implements BaseEnum {

    /**
     * 自有对接渠道
     */
    CONNECTED_CHANNEL(1, "自有对接渠道"),

    /**
     * 外部渠道
     */
    EXTERNAL_CHANNEL (2, "外部渠道"),

    ;
    private final Integer value;

    private final String desc;

}
