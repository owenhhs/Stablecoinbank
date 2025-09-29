package net.lab1024.sa.base.module.support.sms.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author 孙宇
 * @date 2024/11/11 00:03
 */
@Data
public class SmsQxcioudReportForm implements SmsReportForm {
    private String appkey;

    private String phone;

    @JsonProperty(value = "report_time")
    private String reportTime;

    private String status;

    private String desc;

    private String uid;

}
