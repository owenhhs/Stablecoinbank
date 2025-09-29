package net.lab1024.sa.base.module.support.sms.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author 孙宇
 * @date 2024/11/07 23:18
 */
@Data
@Configuration
public class QxcioudSmsConfig {

    @Value("${sms.qxcioud.appkey}")
    private String appkey;

    @Value("${sms.qxcioud.appsecret}")
    private String appsecret;

    @Value("${sms.qxcioud.appcode}")
    private String appcode;

}

