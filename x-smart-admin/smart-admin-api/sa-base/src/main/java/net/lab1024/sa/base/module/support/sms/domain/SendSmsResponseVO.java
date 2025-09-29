package net.lab1024.sa.base.module.support.sms.domain;

import lombok.Data;

/**
 * @author 孙宇
 * @date 2024/11/07 23:26
 */
@Data
public class SendSmsResponseVO {
    private Boolean success;
    private String code;
    private String message;
    private String bizId;
}
