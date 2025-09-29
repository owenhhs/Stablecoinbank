package net.lab1024.sa.base.common.exception;

import net.lab1024.sa.base.common.code.ErrorCode;
import net.lab1024.sa.base.common.code.SystemErrorCode;

/**
 * 业务逻辑异常,全局异常拦截后统一返回ResponseCodeConst.SYSTEM_ERROR
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2020/8/25 21:57
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
public class BusinessException extends RuntimeException {

    private ErrorCode errorCode;

    public BusinessException() {
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(String message) {
        super(message);
        this.errorCode = SystemErrorCode.SYSTEM_ERROR;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = SystemErrorCode.SYSTEM_ERROR;
    }

    public BusinessException(Throwable cause) {
        super(cause);
        this.errorCode = SystemErrorCode.SYSTEM_ERROR;
    }

    public BusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorCode = SystemErrorCode.SYSTEM_ERROR;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
