package net.lab1024.sa.base.module.support.sms.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.base.common.util.HttpUtil;
import net.lab1024.sa.base.common.util.SmartDateFormatterEnum;
import net.lab1024.sa.base.common.util.SmartLocalDateUtil;
import net.lab1024.sa.base.module.support.sms.ISmsClient;
import net.lab1024.sa.base.module.support.sms.config.QxcioudSmsConfig;
import net.lab1024.sa.base.module.support.sms.dao.SmsQxcioudRecordDao;
import net.lab1024.sa.base.module.support.sms.domain.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 孙宇
 * @date 2024/11/23 16:12
 */
@Slf4j
@Service
public class QxcioudSmsClientImpl implements ISmsClient {

    private static final String QXCIOUD_SMS_DOMAIN = "http://sms.qxyai.com:9090";

    private static final String QXCIOUD_SMS_URI = "/sms/batch/v1";

    private static final String QXCIOUD_CODE_SUCCESS = "00000";

    @Resource
    private QxcioudSmsConfig qxcioudSmsConfig;

    @Resource
    private SmsQxcioudRecordDao smsQxcioudRecordDao;


    @Override
    public SendSmsResponseVO send(SendSmsRequestVO requestVO) throws Exception {
        long timestamp = System.currentTimeMillis();
        String sign = DigestUtils.md5Hex(qxcioudSmsConfig.getAppkey() + qxcioudSmsConfig.getAppsecret() + timestamp);
        String uid = getUid();

        String msg = requestVO.getTemplateEntity().getTemplateContent();
        if (requestVO.getTemplateParam() != null && !requestVO.getTemplateParam().isEmpty()) {
            for (String k : requestVO.getTemplateParam().keySet()) {
                msg = StringUtils.replace(msg, k, requestVO.getTemplateParam().get(k));
            }
        }
        Map<String, Object> params = new HashMap<>();
        params.put("appkey", qxcioudSmsConfig.getAppkey());
        params.put("appcode", qxcioudSmsConfig.getAppcode());
        params.put("timestamp", timestamp);
        params.put("sign", sign);
        params.put("uid", uid);
        params.put("phone", requestVO.getPhoneNumber());
        params.put("msg", msg);

        String result = HttpUtil.apiPost(QXCIOUD_SMS_DOMAIN, QXCIOUD_SMS_URI, params, null);
        log.debug("send sms params:{}, result:{}", params, result);
        SendSmsResponseVO sendSmsResponseVO = new SendSmsResponseVO();
        sendSmsResponseVO.setBizId(uid);

        QxcioudSendMsgResponseVO responseVO = JSONObject.parseObject(result, QxcioudSendMsgResponseVO.class);
        if (responseVO == null) {
            log.error("send sms fail params:{}, result:{}", params, result);
            sendSmsResponseVO.setSuccess(false);
            saveQxcioudSmsSendRecord(requestVO.getPhoneNumber(), requestVO, uid, msg, null);
            return sendSmsResponseVO;
        }
        saveQxcioudSmsSendRecord(requestVO.getPhoneNumber(), requestVO, uid, msg, responseVO);
        sendSmsResponseVO.setCode(responseVO.getCode());
        sendSmsResponseVO.setMessage(responseVO.getDesc());
        if (!QXCIOUD_CODE_SUCCESS.equals(responseVO.getCode())) {
            sendSmsResponseVO.setSuccess(false);
            return sendSmsResponseVO;
        }
        sendSmsResponseVO.setSuccess(true);
        return sendSmsResponseVO;
    }

    private String getUid() {
        int retry = 0;
        String uid;
        long recordCount;
        do {
            retry++;
            uid = RandomStringUtils.randomAlphabetic(32);
            LambdaQueryWrapper<SmsQxcioudRecordEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SmsQxcioudRecordEntity::getUid, uid);
            recordCount = smsQxcioudRecordDao.selectCount(wrapper);
        } while (recordCount > 0 && retry < 3);
        return uid;
    }

    private void saveQxcioudSmsSendRecord(String phone, SendSmsRequestVO requestVO, String uid, String msg, QxcioudSendMsgResponseVO responseVO) {
        String[] phoneList = StringUtils.split(phone, ",");
        for (String p : phoneList) {
            SmsQxcioudRecordEntity entity = new SmsQxcioudRecordEntity();
            entity.setTemplateCode(requestVO.getTemplateEntity().getTemplateCode());
            entity.setSmsType(requestVO.getTemplateEntity().getSmsType());
            entity.setBusinessType(requestVO.getTemplateEntity().getBusinessType());
            entity.setSendMessage(msg);
            entity.setSendTime(SmartLocalDateUtil.getCurrentTimeStr(SmartDateFormatterEnum.YMD_HMS));
            entity.setSendPhone(p);
            entity.setUid(uid);
            if (responseVO != null) {
                entity.setSendCode(responseVO.getResult().get(0).getStatus());
                entity.setSendDesc(responseVO.getResult().get(0).getDesc());
            }
            entity.setCreateTime(LocalDateTime.now());
            entity.setUpdateTime(LocalDateTime.now());
            smsQxcioudRecordDao.insert(entity);
        }
    }

    @Override
    public void report(SmsReportForm reportForm) {
        SmsQxcioudReportForm qxcioudReportForm = (SmsQxcioudReportForm) reportForm;

        LambdaQueryWrapper<SmsQxcioudRecordEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SmsQxcioudRecordEntity::getUid, qxcioudReportForm.getUid());
        wrapper.eq(SmsQxcioudRecordEntity::getSendPhone, qxcioudReportForm.getPhone());
        List<SmsQxcioudRecordEntity> recordEntityList = smsQxcioudRecordDao.selectList(wrapper);
        if (CollectionUtils.isEmpty(recordEntityList)) {
            return;
        }
        SmsQxcioudRecordEntity record = recordEntityList.get(0);
        record.setReportTime(qxcioudReportForm.getReportTime());
        record.setReportStatus(qxcioudReportForm.getStatus());
        record.setReportDesc(qxcioudReportForm.getDesc());
        smsQxcioudRecordDao.updateById(record);
    }

    @Override
    public void report(List<SmsReportForm> reportFormList) {
        List<String> uidList = reportFormList.stream().map(v -> ((SmsQxcioudReportForm) v).getUid()).collect(Collectors.toList());

        LambdaQueryWrapper<SmsQxcioudRecordEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SmsQxcioudRecordEntity::getUid, uidList);
        List<SmsQxcioudRecordEntity> recordEntityList = smsQxcioudRecordDao.selectList(wrapper);
        Map<String, SmsQxcioudRecordEntity> recordMap = recordEntityList.stream().collect(Collectors.toMap(SmsQxcioudRecordEntity::getUid, v -> v));

        for (SmsReportForm smsReportForm : reportFormList) {
            SmsQxcioudReportForm qxcioudReportForm = (SmsQxcioudReportForm) smsReportForm;

            SmsQxcioudRecordEntity record = recordMap.get(qxcioudReportForm.getUid());
            record.setReportTime(qxcioudReportForm.getReportTime());
            record.setReportStatus(qxcioudReportForm.getStatus());
            record.setReportDesc(qxcioudReportForm.getDesc());
            smsQxcioudRecordDao.updateById(record);
        }
    }
}
