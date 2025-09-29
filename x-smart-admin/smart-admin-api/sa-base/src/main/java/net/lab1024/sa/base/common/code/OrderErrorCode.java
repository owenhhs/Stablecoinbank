package net.lab1024.sa.base.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderErrorCode implements ErrorCode {

    /**
     * 错误码
     */
    NO_ROUTE_ERROR(40001, "no match route")
    ;


    private final int code;

    private final String msg;

    private final String level;

    OrderErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.level = LEVEL_USER;
    }
}
