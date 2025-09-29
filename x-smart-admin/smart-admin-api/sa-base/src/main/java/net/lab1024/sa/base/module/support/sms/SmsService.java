package net.lab1024.sa.base.module.support.sms;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.base.common.util.SpringBootBeanUtil;
import net.lab1024.sa.base.module.support.sms.dao.SmsTemplateDao;
import net.lab1024.sa.base.module.support.sms.domain.SendSmsRequestVO;
import net.lab1024.sa.base.module.support.sms.domain.SendSmsResponseVO;
import net.lab1024.sa.base.module.support.sms.domain.SmsQxcioudReportForm;
import net.lab1024.sa.base.module.support.sms.domain.SmsTemplateEntity;
import net.lab1024.sa.base.module.support.sms.enumeration.PlatformEnum;
import net.lab1024.sa.base.module.support.sms.enumeration.SmsBusinessEnum;
import net.lab1024.sa.base.module.support.sms.enumeration.SmsTypeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 孙宇
 * @date 2024/11/07 23:13
 */
@Slf4j
@Service
public class SmsService {

    @Resource
    private SmsTemplateDao smsTemplateDao;

    public List<SmsTemplateEntity> getSmsTemplate(Integer smsType, String businessType) {
        LambdaQueryWrapper<SmsTemplateEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SmsTemplateEntity::getSmsType, smsType);
        queryWrapper.eq(SmsTemplateEntity::getBusinessType, businessType);
        return smsTemplateDao.selectList(queryWrapper);
    }

    public boolean sendSms(SendSmsRequestVO requestVO, Integer smsType, String businessType) {
        try {
            List<SmsTemplateEntity> templateEntityList = getSmsTemplate(smsType, businessType);
            if (CollectionUtils.isEmpty(templateEntityList)){
                return false;
            }
            boolean sendSuccess = false;
            for (SmsTemplateEntity templateEntity : templateEntityList) {
                requestVO.setTemplateEntity(templateEntity);
                PlatformEnum platformEnum = PlatformEnum.getByValue(requestVO.getTemplateEntity().getPlatform());
                ISmsClient smsClient = SpringBootBeanUtil.getBean(platformEnum.getSmsServiceClass());
                SendSmsResponseVO responseVO = smsClient.send(requestVO);
                if (responseVO.getSuccess()) {
                    sendSuccess = true;
                    break;
                }
            }
            return sendSuccess;
        } catch (Exception e) {
            log.error("send sms error", e);
            return false;
        }
    }

    public Map<String, String> qxcioudSmsReport(List<SmsQxcioudReportForm> reportForms) {
        log.info("qxcioudSmsReport reports:{}", JSONObject.toJSONString(reportForms));
        Map<String, String> result = new HashMap<>();
        try {
            ISmsClient smsClient = SpringBootBeanUtil.getBean(PlatformEnum.qxcioud.getSmsServiceClass());
            for (SmsQxcioudReportForm form : reportForms) {
                smsClient.report(form);
            }
            result.put("code", "00000");
            return result;
        } catch (Exception e) {
            log.error("qxcioudSmsReport error, reportForms:{}", reportForms, e);
            result.put("code", "99999");
            result.put("desc", "记录保存异常");
            return result;
        }
    }



}
