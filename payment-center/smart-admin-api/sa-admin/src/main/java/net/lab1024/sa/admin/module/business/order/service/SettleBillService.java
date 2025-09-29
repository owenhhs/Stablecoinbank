package net.lab1024.sa.admin.module.business.order.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.constant.AdminRedisKeyConst;
import net.lab1024.sa.admin.module.business.order.dao.SettleBillDao;
import net.lab1024.sa.admin.module.business.order.domain.entity.PaymentOrderInfoEntity;
import net.lab1024.sa.admin.module.business.order.domain.entity.SettleBillEntity;
import net.lab1024.sa.admin.module.business.order.domain.entity.SettleCalcTaskEntity;
import net.lab1024.sa.admin.module.business.order.domain.entity.SettleOrderEntity;
import net.lab1024.sa.admin.module.business.order.domain.form.*;
import net.lab1024.sa.admin.module.business.order.domain.vo.SettleBillListVO;
import net.lab1024.sa.admin.module.business.order.domain.vo.SettleVO;
import net.lab1024.sa.admin.module.business.order.manager.PaymentOrderInfoManager;
import net.lab1024.sa.admin.module.business.order.manager.SettleBillManager;
import net.lab1024.sa.admin.module.business.order.manager.SettleCalcTaskManager;
import net.lab1024.sa.admin.module.business.order.manager.SettleOrderManager;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelBusinessCfgEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelInfoEntity;
import net.lab1024.sa.admin.module.business.payment.channel.manager.PaymentChannelBusinessCfgManager;
import net.lab1024.sa.admin.module.business.payment.channel.manager.PaymentChannelInfoManager;
import net.lab1024.sa.base.common.util.HttpUtil;
import net.lab1024.sa.base.common.code.OpenErrorCode;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 结算单表 Service
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */
@Slf4j
@Service
public class SettleBillService {

    @Resource
    private SettleBillDao settleBillDao;
    @Resource
    private PaymentChannelBusinessCfgManager channelBusinessCfgManager;

    @Resource
    private PaymentOrderInfoManager paymentOrderInfoManager;
    @Resource
    private SettleBillManager settleBillManager;
    @Resource
    private SettleOrderManager settleOrderManager;
    @Resource
    private SettleCalcTaskManager settleCalcTaskManager;

    @Value(value = "${zfx.pay.api.domain}")
    private String domain;

    @Resource
    private PaymentChannelInfoManager paymentChannelInfoManager;

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<SettleBillListVO> queryPage(SettleBillQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        page.addOrder(new OrderItem("tpoi.trade_time", false));
        List<SettleBillListVO> list = settleBillDao.queryPage(page, queryForm);
        PageResult<SettleBillListVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    public long countExcelExportData(SettleBillExportQueryForm queryForm) {
        return settleBillDao.countExcelExportData(queryForm);
    }

    public List<SettleBillListVO> getExcelExportData(SettleBillExportQueryForm queryForm) {
        Page<?> page = new Page<>(queryForm.getPageNum(), queryForm.getPageSize());
        List<SettleBillListVO> excelExportData = settleBillDao.getExcelExportData(page, queryForm);
        List<SettleBillListVO> result = excelExportData.stream().map(item->{
            //结算状态 结算状态，0 待结算  1 结算中 2 已结算
            if(0 == item.getSettleStatus()){
                item.setSettleStatusStr("待結算");
            }else if(1 == item.getSettleStatus()){
                item.setSettleStatusStr("結算中");
            }else if(2 == item.getSettleStatus()){
                item.setSettleStatusStr("已結算");
            }
            return item;
        }).collect(Collectors.toList());
        return result;
    }


    /**
     * 更新
     *
     * @param updateForm
     * @return
     */
    public ResponseDTO<String> update(SettleBillUpdateForm updateForm) {
        SettleBillEntity settleBillEntity = settleBillDao.selectById(updateForm.getId());
        if (!settleBillEntity.getSettleStatus().equals(0)) {
            return ResponseDTO.ok();
        }
        PaymentChannelBusinessCfgEntity businessCfg = channelBusinessCfgManager.getById(settleBillEntity.getBusinessId());

        BigDecimal usd = settleBillEntity.getTradeAmount().divide(updateForm.getExchangeRate(), 2, RoundingMode.HALF_UP);
        BigDecimal sumBrokerage = usd.multiply(businessCfg.getBrokerage());
        BigDecimal settleAmount = usd.subtract(sumBrokerage).subtract(settleBillEntity.getAward());

        BigDecimal sumAward = businessCfg.getAward().multiply(new BigDecimal(settleBillEntity.getOrderCount()));

        settleBillEntity.setBrokerage(sumBrokerage);
        settleBillEntity.setAward(sumAward);
        settleBillEntity.setExchangeRate(updateForm.getExchangeRate());
        settleBillEntity.setSettleAmount(settleAmount);
        settleBillEntity.setSettleStatus(1);
        settleBillDao.updateById(settleBillEntity);
        return ResponseDTO.ok();
    }

    /**
     * 结算完成
     * @param updateForm
     * @return
     */
    public ResponseDTO<String> finished(SettleBillFinishedForm updateForm) {
        SettleBillEntity settleBillEntity = SmartBeanUtil.copy(updateForm, SettleBillEntity.class);
        settleBillEntity.setSettleStatus(2);
        settleBillDao.updateById(settleBillEntity);
        return ResponseDTO.ok();
    }

    public void paymentCalc(SettleCalcTaskEntity taskEntity) {
        try {
            Long startTime = SmartLocalDateUtil.dateToTimestamp(taskEntity.getTradeTime() + " 00:00:00");
            Instant instant = Instant.ofEpochMilli(startTime);
            // 转换为系统默认时区的LocalDate
            LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
            // 添加一天
            LocalDate nextDay = localDate.plusDays(1);
            // 格式化日期
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedNextDay = nextDay.format(formatter);
            Long endTime = SmartLocalDateUtil.dateToTimestamp(formattedNextDay + " 00:00:00");

            List<PaymentChannelBusinessCfgEntity> channelBusinessCfgEntityList = channelBusinessCfgManager.findAll();
            for (PaymentChannelBusinessCfgEntity channelBusinessCfg : channelBusinessCfgEntityList) {

                // 判断该商户订单是否已结算完成
                boolean isCompleted = settleBillManager.checkSettleCompleted(taskEntity.getTradeTime(),
                        channelBusinessCfg.getChannelId(), channelBusinessCfg.getId(), taskEntity.getTradeType());
                if (isCompleted) {
                    continue;
                }

                List<PaymentOrderInfoEntity> orderInfoEntityList = paymentOrderInfoManager.queryOrderListByHistory(channelBusinessCfg.getChannelId(),
                        channelBusinessCfg.getId(), startTime, endTime);

                if (CollectionUtils.isEmpty(orderInfoEntityList)) {
                    continue;
                }

                BigDecimal sumAmount = orderInfoEntityList.stream().map(PaymentOrderInfoEntity::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

                SettleBillEntity settleBillEntity = new SettleBillEntity();
                settleBillEntity.setChannelId(channelBusinessCfg.getChannelId());
                settleBillEntity.setBusinessId(channelBusinessCfg.getId());
                settleBillEntity.setTradeTime(taskEntity.getTradeTime());
                settleBillEntity.setTradeType(taskEntity.getTradeType());
                settleBillEntity.setTradeLimit(channelBusinessCfg.getAmountMin() + "~" + channelBusinessCfg.getAmountMax());
                settleBillEntity.setTradeAmount(sumAmount);
                settleBillEntity.setOrderCount(orderInfoEntityList.size());
                settleBillEntity.setBrokerage(new BigDecimal(0));
                settleBillEntity.setAward(new BigDecimal(0));
                settleBillEntity.setCurrency(channelBusinessCfg.getCurrency());
                settleBillEntity.setCreateTime(LocalDateTime.now());
                settleBillEntity.setUpdateTime(LocalDateTime.now());
                settleBillManager.save(settleBillEntity);

                List<SettleOrderEntity> orderEntityList = orderInfoEntityList.stream().map(v -> {
                    SettleOrderEntity orderEntity = new SettleOrderEntity();
                    orderEntity.setChannelId(channelBusinessCfg.getChannelId());
                    orderEntity.setOrderType(taskEntity.getTradeType());
                    orderEntity.setSettleBillId(settleBillEntity.getId());
                    orderEntity.setOrderId(v.getId());
                    orderEntity.setCreateTime(LocalDateTime.now());
                    orderEntity.setUpdateTime(LocalDateTime.now());
                    return orderEntity;
                }).collect(Collectors.toList());
                settleOrderManager.saveBatch(orderEntityList);
            }

            taskEntity.setTaskStatus(2);
            taskEntity.setEndTime(System.currentTimeMillis());
            taskEntity.setUpdateTime(LocalDateTime.now());
            settleCalcTaskManager.updateById(taskEntity);
        } catch (Exception e) {
            if (taskEntity != null) {
                taskEntity.setTaskStatus(4);
                taskEntity.setErrorMsg(e.getMessage());
                taskEntity.setUpdateTime(LocalDateTime.now());
                settleCalcTaskManager.updateById(taskEntity);
            }
            throw e;
        }
    }


    public ResponseDTO<String> recalculation(SettleBillRecalcForm recalcForm) {
        String redisKey = AdminRedisKeyConst.SETTLE_CALC_TASK_PAYMENT_ROLLBACK_LOCK + recalcForm.getSettleDate();
        boolean lock = RedisLockUtil.tryLock(redisKey);
        if (!lock) {
            return ResponseDTO.error(UserErrorCode.REPEAT_SUBMIT);
        }
        try {
            LambdaQueryWrapper<SettleCalcTaskEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SettleCalcTaskEntity::getTradeTime, recalcForm.getSettleDate());
            SettleCalcTaskEntity taskEntity = settleCalcTaskManager.getOne(queryWrapper);
            if (taskEntity == null) {
                return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST);
            }
            taskEntity.setStartTime(System.currentTimeMillis());
            taskEntity.setTaskStatus(1);
            settleCalcTaskManager.updateById(taskEntity);

            // 清除历史数据
            List<SettleBillEntity> billEntities = settleBillManager.getListByTradeTime(taskEntity.getTradeTime());
            for (SettleBillEntity billEntity : billEntities) {
                settleOrderManager.deleteBySettleId(billEntity.getId());
                settleBillManager.removeById(billEntity.getId());
            }
            // 重算
            paymentCalc(taskEntity);
        } finally {
            RedisLockUtil.unlock(redisKey);
        }
        return ResponseDTO.ok();
    }

    public ResponseDTO<PageResult<SettleVO>> queryPageBySettle(SettleQueryForm queryForm) {

        LambdaQueryWrapper<PaymentChannelInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentChannelInfoEntity::getImplCode, "fxmch");
        queryWrapper.last("limit 1");
        PaymentChannelInfoEntity channelInfoVO = paymentChannelInfoManager.getOne(queryWrapper);

        String date =SmartLocalDateUtil.getGMTDate();
        String contentType = "application/json";
        String uri = "/openapi/settle/queryPageBySettle";
        String signature = SignatureUtil.genSignature("POST", uri, contentType, date, queryForm, channelInfoVO.getMerSk());
        String authorization = channelInfoVO.getMerAk() + ":" + signature;

        Map<String, String> headers = new HashMap<>(4);
        headers.put("Authorization", authorization);
        headers.put("Content-Type", contentType);
        headers.put("Date", date);
        headers.put(LogIdUtil.REQUEST_ID, LogIdUtil.getRequestId());

        String requestJson = JSONObject.toJSONString(queryForm);
        String resultStr = HttpUtil.apiPostJson(channelInfoVO.getDomain(), uri, requestJson, headers);

        log.info("fxmch request, mercode:{}, params:{}, result:{}", channelInfoVO.getMerCode(), requestJson, resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("fxmch apply result is empty, mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }
        return JSONObject.parseObject(resultStr, new TypeReference<ResponseDTO<PageResult<SettleVO>>>() {});
    }
}
