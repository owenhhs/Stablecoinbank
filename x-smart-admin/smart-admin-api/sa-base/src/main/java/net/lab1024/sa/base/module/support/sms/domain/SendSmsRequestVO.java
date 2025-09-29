package net.lab1024.sa.base.module.support.sms.domain;

import lombok.Data;

import java.util.Map;

/**
 * @author 孙宇
 * @date 2024/11/10 23:33
 */
@Data
public class SendSmsRequestVO {
    private String phoneNumber;
    private SmsTemplateEntity templateEntity;
    private Map<String, String> templateParam;
}
