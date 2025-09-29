package net.lab1024.sa.admin.module.business.order.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.order.dao.SettleBillDao;
import net.lab1024.sa.admin.module.business.order.domain.entity.SettleBillEntity;
import net.lab1024.sa.admin.module.business.order.domain.form.SettleNotifyForm;
import net.lab1024.sa.admin.module.business.payment.channel.dao.PaymentChannelInfoDao;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelInfoEntity;
import net.lab1024.sa.admin.module.openapi.order.service.IPaymentPlatformService;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SettleBillNotifyService {
    @Resource
    private SettleBillDao settleBillDao;
    @Resource
    private PaymentChannelInfoDao paymentChannelInfoDao;

    public ResponseDTO notifySettleOrderUrl(SettleNotifyForm form, String authorization, String date) {
        log.info("notifySettleOrderUrl form:{}", form);
        Map<String, Object> params = new HashMap<>(4);
        params.put("authorization", authorization);
        params.put("date", date);
        params.put("uri", "/settleInfo/notifySettle");
        params.put("method", "POST");

        LambdaQueryWrapper<PaymentChannelInfoEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentChannelInfoEntity::getMerCode, form.getMerCode());
        PaymentChannelInfoEntity channelInfoVO = paymentChannelInfoDao.selectOne(wrapper);
        if (channelInfoVO == null) {
            log.error("notifySettleOrderUrl form:{} 非法渠道号!", form);
            return ResponseDTO.userErrorParam("非法渠道号 " + form.getMerCode());
        }

        IPaymentPlatformService platformService = IPaymentPlatformService.get(channelInfoVO.getImplCode());
        boolean signVerify = platformService.signVerify(channelInfoVO, params);
        if (!signVerify) {
            log.error("notifySettleOrderUrl form:{} 签名验证失败!", form);
            return ResponseDTO.userErrorParam("签名验证失败");
        }


        QueryWrapper<SettleBillEntity> queryWrapper = new QueryWrapper<>();
        //状态是有效的
        queryWrapper.eq("trade_time", form.getTradeDate());
        queryWrapper.eq("channel_id", channelInfoVO.getId());
        //判断
        List<SettleBillEntity> selectList = settleBillDao.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(selectList) || selectList.size() > 1) {
            log.error("支付中心结算有问题 form:{}", form);
            throw new BusinessException("支付中心结算有问题");
        }

        SettleBillEntity settleBillEntity = selectList.get(0);
        String updatedUrl;
        String settleUrlDB = settleBillEntity.getSettleUrl();
        if (StringUtils.isEmpty(settleUrlDB)) {
            updatedUrl = String.join(";", form.getSettleUrls());
        } else {
            updatedUrl = settleUrlDB + ";" + String.join(";", form.getSettleUrls());
        }
        settleBillEntity.setSettleUrl(updatedUrl);
        settleBillEntity.setUpdateTime(LocalDateTime.now());
        settleBillDao.updateById(settleBillEntity);
        return ResponseDTO.ok();
    }


    public ResponseDTO notifySettleOrder(SettleNotifyForm form, String authorization, String date) {
        log.info("notifySettleOrder form:{}", form);
        Map<String, Object> params = new HashMap<>(4);
        params.put("authorization", authorization);
        params.put("date", date);
        params.put("uri", "/settleInfo/notifySettle");
        params.put("method", "POST");

        LambdaQueryWrapper<PaymentChannelInfoEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentChannelInfoEntity::getMerCode, form.getMerCode());
        PaymentChannelInfoEntity channelInfoVO = paymentChannelInfoDao.selectOne(wrapper);
        if (channelInfoVO == null) {
            log.error("notifySettleOrder form:{} 非法渠道号!", form);
            return ResponseDTO.userErrorParam("非法渠道号 " + form.getMerCode());
        }

        IPaymentPlatformService platformService = IPaymentPlatformService.get(channelInfoVO.getImplCode());
        boolean signVerify = platformService.signVerify(channelInfoVO, params);
        if (!signVerify) {
            log.error("notifySettleOrder form:{} 签名验证失败!", form);
            return ResponseDTO.userErrorParam("签名验证失败");
        }

        LambdaQueryWrapper<SettleBillEntity> queryWrapper = new LambdaQueryWrapper<>();
        //状态是有效的
        queryWrapper.eq(SettleBillEntity::getTradeTime, form.getTradeDate());
        queryWrapper.eq(SettleBillEntity::getTradeType, form.getTradeType());
        queryWrapper.eq(SettleBillEntity::getChannelId, channelInfoVO.getId());
        //判断
        List<SettleBillEntity> selectList = settleBillDao.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(selectList) || selectList.size() > 1) {
            log.error("支付中心结算有问题 form:{}", form);
            throw new BusinessException("支付中心结算有问题");
        }
        SettleBillEntity settleBillEntity = selectList.get(0);
        if (settleBillEntity.getSettleStatus().equals(2)) {
            log.info("支付中心结算单已结算 form:{}", form);
            return ResponseDTO.ok();
        }

        settleBillEntity.setSettleStatus(2);
        settleBillEntity.setTradeAmount(form.getTradeAmount());
        settleBillEntity.setExchangeRate(form.getExchangeRate());
        settleBillEntity.setOrderCount(form.getOrderCount());
        settleBillEntity.setBrokerage(form.getBrokerage());
        settleBillEntity.setAward(form.getAward());
        settleBillEntity.setSettleAmount(form.getSettleAmount());

        String updatedUrl;
        String settleUrlDB = settleBillEntity.getSettleUrl();
        if (StringUtils.isEmpty(settleUrlDB)) {
            updatedUrl = String.join(";", form.getSettleUrls());
        } else {
            updatedUrl = settleUrlDB + ";" + String.join(";", form.getSettleUrls());
        }
        settleBillEntity.setSettleUrl(updatedUrl);
        settleBillEntity.setUpdateTime(LocalDateTime.now());
        settleBillDao.updateById(settleBillEntity);
        return ResponseDTO.ok();
    }


}
