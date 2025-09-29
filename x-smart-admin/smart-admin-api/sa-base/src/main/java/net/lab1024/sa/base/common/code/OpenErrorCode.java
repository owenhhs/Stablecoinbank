package net.lab1024.sa.base.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户级别的错误码（用户引起的错误返回码，可以不用关注）
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2021/09/21 22:12:27
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Getter
@AllArgsConstructor
public enum OpenErrorCode implements ErrorCode {

    /**
     * 错误码
     */
    PARAM_ERROR(30001, "Parameter error"),

    DATA_NOT_EXIST(30002, "Not found"),

    FORM_REPEAT_SUBMIT(30003, "Do not repeat submission"),

    REPEAT_SUBMIT(30004, "Operate too frequently"),

    NO_PERMISSION(30005, "No permission"),

    VERIFY_SIGNATURE_FAILURE(30006, "Signature verification failure"),

    ORDER_PAYMENT_OVERDUE(30007, "Order not found or payment is overdue，please try again."),

    USER_BLACK_LIST(41001, "The user is added to the blacklist."),

    ;


    private final int code;

    private final String msg;

    private final String level;

    OpenErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.level = LEVEL_USER;
    }
}
