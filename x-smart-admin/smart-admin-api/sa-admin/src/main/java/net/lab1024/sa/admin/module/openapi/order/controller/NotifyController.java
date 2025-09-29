package net.lab1024.sa.admin.module.openapi.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.lab1024.sa.base.module.support.sms.SmsService;
import net.lab1024.sa.base.module.support.sms.domain.SmsQxcioudReportForm;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author 孙宇
 * @date 2024/11/10 23:58
 */
@RestController
@Tag(name = "")
public class NotifyController {

    @Resource
    private SmsService smsService;

    @Operation(summary = "奇讯云短信回执 @author sunyu")
    @PostMapping("/openapi/notify/qxcioud/sms/report")
    public Map<String, String> aliyunSmsReport(@RequestBody List<SmsQxcioudReportForm> reportForms) {
        return smsService.qxcioudSmsReport(reportForms);
    }

}
