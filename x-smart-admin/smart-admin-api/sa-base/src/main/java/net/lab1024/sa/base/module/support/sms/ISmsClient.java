package net.lab1024.sa.base.module.support.sms;

import net.lab1024.sa.base.module.support.sms.domain.SendSmsRequestVO;
import net.lab1024.sa.base.module.support.sms.domain.SendSmsResponseVO;
import net.lab1024.sa.base.module.support.sms.domain.SmsReportForm;

import java.util.List;

/**
 * @author 孙宇
 * @date 2024/11/10 23:30
 */
public interface ISmsClient {

    SendSmsResponseVO send(SendSmsRequestVO requestVO) throws Exception;


    void report(SmsReportForm reportForm);

    void report(List<SmsReportForm> reportFormList);

}
