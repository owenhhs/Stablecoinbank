package net.lab1024.sa.base.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WithdrawTypeEnum implements BaseEnum {

    /**
     * 银行
     */
    BANK("bank", "银行"),

    ;
    private final String value;

    private final String desc;

}
