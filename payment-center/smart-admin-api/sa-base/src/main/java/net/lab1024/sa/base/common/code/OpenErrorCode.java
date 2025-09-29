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

    ORDER_PAYMENT_OVERDUE(40007, "Order not found or payment is overdue，please try again."),

    NO_MATCH_CHANNEL(40008, "There are no eligible payment channels."),

    UNKNOWN_PAYMENT_PLATFORM(40009, "Unknown payment platform."),

    SYSTEM_ERROR(40010, "system error"),

    FILE_UPLOAD_FAIL(40011, "文件上传失败"),

    NO_MATCH_CHANNEL_WEB(40012, "未匹配到可用支付渠道"),
    ORDER_PAYMENT_OVERDUE_WEB(40013, "未查询到订单或订单付款时间已过期"),
    SYSTEM_ERROR_WEB(40014, "系统服务故障，请重试"),
    ORDER_PAYMENT_SWITCH_COUNT(40015, "付款码仅允许刷新一次"),
    ORDER_PAYMENT_SWITCH_NOT_ALLOW(40016, "订单状态不允许切换二维码"),

    ORDER_WITHDRAW_NOTFOUND_OR_WRONG_STATUS(40016, "Order not found or in unmatched status，please try again."),

    NP_ORDER_UNSUPPORTED(41010, "np order unsupported search"),

    /**
     * LTG相关错误码
     */
    LTG_CHANNEL_ERROR(41020, "ltg channel config not found"),
    LTG_ORDER_UNSUPPORTED(41021, "order unsupported search"),


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
