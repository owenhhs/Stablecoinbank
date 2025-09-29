package net.lab1024.sa.base.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author sunyu
 * @Date 2024/07/25 21:47:13
 */

@Getter
@AllArgsConstructor
public enum DepositTypeEnum implements BaseEnum {

    /**
     * 银行
     */
    BANK("bank", "银行"),

    /**
     * 二维码
     */
    QRCODE("qrcode", "二维码"),
    ;
    private final String value;

    private final String desc;

}
