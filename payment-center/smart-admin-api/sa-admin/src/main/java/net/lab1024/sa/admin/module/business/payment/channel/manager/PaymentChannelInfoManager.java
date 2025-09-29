package net.lab1024.sa.admin.module.business.payment.channel.manager;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.emumeration.PaymentPlatformEnum;
import net.lab1024.sa.admin.module.business.order.dao.CashOrderInfoDao;
import net.lab1024.sa.admin.module.business.order.dao.PaymentOrderInfoDao;
import net.lab1024.sa.admin.module.business.order.domain.entity.PaymentOrderInfoEntity;
import net.lab1024.sa.admin.module.business.order.domain.vo.OrderCountVO;
import net.lab1024.sa.admin.module.business.payment.channel.dao.ChannelRouteWhiteListDao;
import net.lab1024.sa.admin.module.business.payment.channel.dao.PaymentChannelBusinessCfgDao;
import net.lab1024.sa.admin.module.business.payment.channel.dao.PaymentChannelInfoDao;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.ChannelRouteWhiteListEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelBusinessCfgEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelInfoEntity;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderDepositForm;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderWithdrawForm;
import net.lab1024.sa.base.common.enumeration.DepositTypeEnum;
import net.lab1024.sa.base.common.enumeration.PaymentTypeEnum;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartBigDecimalUtil;
import net.lab1024.sa.base.common.util.SmartDateFormatterEnum;
import net.lab1024.sa.base.common.util.SmartLocalDateUtil;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 渠道基本信息表  Manager
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */
@Slf4j
@Service
public class PaymentChannelInfoManager extends ServiceImpl<PaymentChannelInfoDao, PaymentChannelInfoEntity> {

    @Resource
    private PaymentOrderInfoDao paymentOrderInfoDao;
    @Resource
    private CashOrderInfoDao cashOrderInfoDao;
    @Resource
    private ChannelRouteWhiteListDao channelRouteWhiteListDao;
    @Value(value = "${zfx.user.lock.channelid:0}")
    private Long userLockChannelId;
    @Value("${zfx.pay.refuse.xinjiang.channelid}")
    private Long refuseXjDepartmentId;
    @Resource
    private PaymentChannelBusinessCfgDao paymentChannelBusinessCfgDao;


    public PaymentChannelInfoVO getPaymentChannelInfo(String tag, PaymentTypeEnum paymentType, Set<Long> channelIdList, OrderDepositForm depositForm, boolean isXj) {
        return getPaymentChannelInfo(tag, paymentType, channelIdList, depositForm.getDepositHolder(),
                depositForm.getDepositType(), depositForm.getPaymentChannel(),
                depositForm.getAmount(), depositForm.getCurrency(), depositForm.getCountry(), depositForm.getUserId(), isXj);
    }
    public PaymentChannelInfoVO getWithdrawChannelInfo(String tag, PaymentTypeEnum paymentType, Set<Long> channelIdList, OrderWithdrawForm form) {
        return getWithdrawChannelInfo(tag, paymentType, channelIdList, form.getWithdrawType(), form.getAccountHolder(),
                form.getAmount(), form.getCurrency(), form.getCountry(), form.getUserId());
    }
    public PaymentChannelInfoVO getWithdrawChannelInfo(String tag, PaymentTypeEnum paymentType, Set<Long> channelIdList,
                                                       String withdrawType, String accountHolder,
                                                       BigDecimal amount, String currency, String country, String userId) {
        return getPaymentChannelInfo(tag, paymentType, channelIdList, accountHolder, withdrawType, withdrawType, amount, currency, country, userId, false);
    }
    public PaymentChannelInfoVO getPaymentChannelInfo(String tag, PaymentTypeEnum paymentType, Set<Long> channelIdList,
                                                      String accountHolder,
                                                      String chnlType, String chnl,
                                                      BigDecimal amount, String currency, String country, String userId, boolean isXj) {
        // 临时变量
        Set<Long> tempChannelIdList = new HashSet<>(channelIdList);
        // 先检查白名单
        List<PaymentChannelInfoVO> paymentChannelList = checkWhiteList(paymentType, accountHolder, chnlType, chnl, amount, currency, country, isXj ? 1 : 0);
        if (CollectionUtils.isEmpty(paymentChannelList)) {
            // 检查用户在渠道管理平台最近两单情况
            // 如果用户当天最近连续两单在渠道管理平台未完成支付，则该用户不再路由到渠道管理平台
            boolean filterFxmch = false;
            if (StringUtils.isNotEmpty(userId) && DepositTypeEnum.QRCODE.equalsValue(chnlType)) {
                List<Long> fxmchChannelIdList = getFxmchChannelIdList();
                filterFxmch = checkLastOrder(Long.valueOf(userId), fxmchChannelIdList);
            }
            log.info("getPaymentChannelInfo orderNo:{}, userId:{}, filterFxmch={}, channelIdList:{}", tag, userId, filterFxmch, channelIdList);
            // 老用户逻辑不支持新疆用户
            if (!filterFxmch && !userLockChannelId.equals(0L) && !channelIdList.contains(userLockChannelId) && accountHolder.length() <= 4) {
                // 查询7天内订单完成的最新订单
                LambdaQueryWrapper<PaymentOrderInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(PaymentOrderInfoEntity::getUserId, Long.parseLong(userId));
                queryWrapper.eq(PaymentOrderInfoEntity::getDepositType, chnlType);
                queryWrapper.eq(PaymentOrderInfoEntity::getChannelId, userLockChannelId);
                queryWrapper.eq(PaymentOrderInfoEntity::getStatus, 2);
                queryWrapper.gt(PaymentOrderInfoEntity::getApplyTime, get7Timestamp());
                queryWrapper.orderByDesc(PaymentOrderInfoEntity::getFinishedTime);
                queryWrapper.last("limit 1");
                PaymentOrderInfoEntity paymentOrderInfoEntity = paymentOrderInfoDao.selectOne(queryWrapper);
                if (paymentOrderInfoEntity != null) {
                    PaymentChannelInfoVO paymentChannelInfoVO = this.baseMapper.queryById(paymentOrderInfoEntity.getChannelId());
                    if (paymentChannelInfoVO != null) {
                        LambdaQueryWrapper<PaymentChannelBusinessCfgEntity> businessQueryWrapper = new LambdaQueryWrapper<>();
                        businessQueryWrapper.eq(PaymentChannelBusinessCfgEntity::getChannelId, paymentOrderInfoEntity.getChannelId());
                        businessQueryWrapper.eq(PaymentChannelBusinessCfgEntity::getPaymentType, paymentType.getValue());
                        businessQueryWrapper.le(PaymentChannelBusinessCfgEntity::getAmountMin, amount);
                        businessQueryWrapper.gt(PaymentChannelBusinessCfgEntity::getAmountMax, amount);
                        businessQueryWrapper.eq(PaymentChannelBusinessCfgEntity::getCurrency, currency);
                        PaymentChannelBusinessCfgEntity businessCfg = paymentChannelBusinessCfgDao.selectOne(businessQueryWrapper);
                        if (businessCfg != null) {
                            channelIdList.add(paymentChannelInfoVO.getId());
                            paymentChannelInfoVO.setBusinessId(businessCfg.getId());
                            paymentChannelList.add(paymentChannelInfoVO);
                        }
                    }
                }
            }
            if (CollectionUtils.isEmpty(paymentChannelList)) {
                paymentChannelList = this.baseMapper.queryIdListByAmount(paymentType.getValue(), chnlType, chnl, amount, currency, country, isXj ? 1 : 0, filterFxmch);
            }
        }
        // 指定渠道，不支持新疆用户付款
        if (accountHolder.length() > 4) {
            paymentChannelList = paymentChannelList.stream().filter(v -> !v.getId().equals(refuseXjDepartmentId)).collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(paymentChannelList)) {
            log.warn("获取渠道列表为空, orderNo:{}", tag);
            return null;
        }
        Set<Long> paymentChannelIdList = paymentChannelList.stream().map(PaymentChannelInfoVO::getId).collect(Collectors.toSet());

        List<OrderCountVO> orderCountVOList;
        if (PaymentTypeEnum.payment.equals(paymentType)) {
            orderCountVOList = paymentOrderInfoDao.queryTodayOrderCount(new ArrayList<>(paymentChannelIdList));
        } else {
            orderCountVOList = cashOrderInfoDao.queryTodayOrderCount(new ArrayList<>(paymentChannelIdList));
        }
        if (CollectionUtils.isEmpty(orderCountVOList)) {
            log.warn("获取渠道当日订单数为空，orderNo:{}, paymentType:{}, paymentChannelIdList:{} ", tag, paymentType.getValue(), paymentChannelIdList);
            return null;
        }
        Map<Long, OrderCountVO> orderCountMap = orderCountVOList.stream().collect(Collectors.toMap(OrderCountVO::getChannelId, v->v));
        List<PaymentChannelInfoVO> matchChannels = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        String currentDay = SmartLocalDateUtil.format(LocalDateTime.now(), SmartDateFormatterEnum.YMD);
        for (PaymentChannelInfoVO channelInfoVO : paymentChannelList) {
            if (!checkTimePeriod(channelInfoVO.getWorkTime(), currentDay, currentTime)) {
                log.warn("匹配的渠道不在当前时间段，orderNo:{}, paymentType:{}, channelId:{}", tag, paymentType.getValue(), channelInfoVO.getId());
                continue;
            }
            // 判断渠道支付金额限额，订单量限额
            OrderCountVO orderCountVO = orderCountMap.get(channelInfoVO.getId());
            BigDecimal sumAmount = SmartBigDecimalUtil.add(orderCountVO.getTotalAmount(), amount, 2);
            if (PaymentTypeEnum.payment.equals(paymentType)) {
                if (SmartBigDecimalUtil.isGreaterThan(sumAmount, channelInfoVO.getPaymentLimit())) {
                    log.warn("匹配的渠道当日订单金额已达上限，orderNo:{}, paymentType:{}, channelId:{}", tag, paymentType.getValue(), channelInfoVO.getId());
                    continue;
                }
                if (orderCountVO.getOrderCount() >= channelInfoVO.getPaymentCount()) {
                    log.warn("匹配的渠道当日订单数已达上限，orderNo:{}, paymentType:{}, channelId:{}", tag, paymentType.getValue(), channelInfoVO.getId());
                    continue;
                }
            } else {
                if (SmartBigDecimalUtil.isGreaterThan(sumAmount, channelInfoVO.getCashLimit())) {
                    log.warn("匹配的渠道当日订单金额已达上限，orderNo:{}, paymentType:{}, channelId:{}", tag, paymentType.getValue(), channelInfoVO.getId());
                    continue;
                }
                if (orderCountVO.getOrderCount() >= channelInfoVO.getCashCount()) {
                    log.warn("匹配的渠道当日订单数已达上限，orderNo:{}, paymentType:{}, channelId:{}", tag, paymentType.getValue(), channelInfoVO.getId());
                    continue;
                }
            }
            matchChannels.add(channelInfoVO);
        }
        matchChannels = matchChannels.stream().filter(v -> !tempChannelIdList.contains(v.getId())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(matchChannels)) {
            log.warn("匹配的渠道为空，orderNo:{}, paymentType:{}, channelIdList:{}", tag, paymentType.getValue(), channelIdList);
            return null;
        }
        return getRepaymentRouteByPercentage(tag, matchChannels);
    }

    private List<Long> getFxmchChannelIdList() {
        LambdaQueryWrapper<PaymentChannelInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentChannelInfoEntity::getImplCode, PaymentPlatformEnum.fxmch.getValue());
        List<PaymentChannelInfoEntity> channelInfoEntities = this.baseMapper.selectList(queryWrapper);
        return channelInfoEntities.stream().map(PaymentChannelInfoEntity::getId).collect(Collectors.toList());
    }

    /**
     * 检查当天最后两笔订单状态是否等于2
     * @param userId
     * @param channelIdList
     * @return
     */
    private boolean checkLastOrder(Long userId, List<Long> channelIdList) {
        if (CollectionUtils.isEmpty(channelIdList)) {
            return false;
        }
        LambdaQueryWrapper<PaymentOrderInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentOrderInfoEntity::getUserId, userId);
        queryWrapper.in(PaymentOrderInfoEntity::getChannelId, channelIdList);
        queryWrapper.ge(PaymentOrderInfoEntity::getCreateTime, SmartLocalDateUtil.getTodayStart());
        queryWrapper.orderByDesc(PaymentOrderInfoEntity::getId);
        queryWrapper.last("limit 2");
        List<PaymentOrderInfoEntity> orderInfoEntities = paymentOrderInfoDao.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(orderInfoEntities) || orderInfoEntities.size() < 2) {
            return false;
        }
        return orderInfoEntities.stream().allMatch(v -> v.getStatus() != 2);
    }

    private long get7Timestamp() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysAgo = now.minusDays(7);
        ZonedDateTime zdt = sevenDaysAgo.atZone(ZoneId.systemDefault());
        return zdt.toInstant().toEpochMilli();
    }

    /**
     * 检查时间段
     * @param workTime
     * @param currentDay
     * @param currentTime
     * @return
     */
    private boolean checkTimePeriod(String workTime, String currentDay, long currentTime) {
        String[] parts = workTime.split(";");
        return Arrays.stream(parts).anyMatch(v -> checkTime(v, currentDay, currentTime));
    }

    /**
     * 检查时间
     * @param time
     * @param currentDay
     * @param currentTime
     * @return
     */
    private boolean checkTime(String time, String currentDay, long currentTime) {
        String[] se = time.split("-");
        // 判断渠道工作时间
        long startTime = SmartLocalDateUtil.dateToTimestamp(currentDay + " " + se[0], SmartDateFormatterEnum.YMD_HM);
        long endTime = SmartLocalDateUtil.dateToTimestamp(currentDay + " " + se[1], SmartDateFormatterEnum.YMD_HM);
        return currentTime > startTime && currentTime < endTime;
    }

    /**
     * 通过比例路由通道（商户+支付通道）
     *
     * @return
     */
    private PaymentChannelInfoVO getRepaymentRouteByPercentage(String tag, List<PaymentChannelInfoVO> channelInfoVOList) {
        if (channelInfoVOList.isEmpty()) {
            log.error("[{}] 无路由通道paymentChannelInfoRouteVOList:{}", tag, channelInfoVOList);
            return null;
        }
        PaymentChannelInfoVO result = null;
        //百分比的总基数
        int randomTotalPercentage = 0;
        //可用的通道百分比全部为0时 随机一个通道
        int percentageZoreChannel = 0;
        for (PaymentChannelInfoVO paymentChannelInfoRouteVO : channelInfoVOList) {
            if (paymentChannelInfoRouteVO.getPaymentScale() > 0) {
                randomTotalPercentage += paymentChannelInfoRouteVO.getPaymentScale();
            } else {
                percentageZoreChannel++;
            }
        }
        //可用的通道百分比全部为0
        if (percentageZoreChannel > 0 && percentageZoreChannel == channelInfoVOList.size()) {
            Collections.shuffle(channelInfoVOList);
            PaymentChannelInfoVO routeUsableChannelDTO = channelInfoVOList.get(0);
            result = SmartBeanUtil.copy(routeUsableChannelDTO, PaymentChannelInfoVO.class);
            log.error("[{}] 商户通道路由，可用的通道百分比全部为0，随机一个百分比为0的通道 routeUsableChannelDTO={},result={}", tag, routeUsableChannelDTO, result);
            return result;
        }

        log.info("[{}] 百分比随机代扣路由渠道 randomTotalPercentage={},size={},payRouteRepaymentUsableList={}", tag, randomTotalPercentage, channelInfoVOList.size(), JSON.toJSON(channelInfoVOList));
        Collections.shuffle(channelInfoVOList);
        int value = RandomUtils.nextInt(0, randomTotalPercentage);
        int rate = 0;
        for (PaymentChannelInfoVO paymentChannelInfoRouteVO : channelInfoVOList) {
            if (paymentChannelInfoRouteVO.getPaymentScale() > 0) {
                rate += paymentChannelInfoRouteVO.getPaymentScale();
                if (rate > value) {
                    result = SmartBeanUtil.copy(paymentChannelInfoRouteVO, PaymentChannelInfoVO.class);
                    break;
                }
            }
        }
        if (result == null) {
            log.error("[{}] 代扣路由时 通过百分比未找到路由渠道,请检测路由配置 paymentChannelInfoEntityList={}", tag, JSON.toJSON(channelInfoVOList));
            return null;
        }
        return result;
    }


    public List<PaymentChannelInfoVO> checkWhiteList(PaymentTypeEnum paymentType, OrderDepositForm depositForm, int xjFlag) {
        return checkWhiteList(paymentType, depositForm.getDepositHolder(),
                depositForm.getDepositType(), depositForm.getPaymentChannel(),
                depositForm.getAmount(), depositForm.getCurrency(), depositForm.getCountry(), xjFlag);
    }
    public List<PaymentChannelInfoVO> checkWhiteList(PaymentTypeEnum paymentType, String accountHolder,
                                                     String chnlType, String chnl,
                                                     BigDecimal amount, String currency, String country, int xjFlag) {
        LambdaQueryWrapper<ChannelRouteWhiteListEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChannelRouteWhiteListEntity::getUsername, accountHolder);
        queryWrapper.eq(ChannelRouteWhiteListEntity::getStatus, 1);
        List<ChannelRouteWhiteListEntity> whiteListEntityList = channelRouteWhiteListDao.selectList(queryWrapper);
        List<Long> channelIdList = whiteListEntityList.stream().map(ChannelRouteWhiteListEntity::getChannelId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(channelIdList)) {
            return new ArrayList<>();
        }
        return this.baseMapper.queryListByIds(channelIdList, paymentType.getValue(),
                chnlType, chnl,
                amount, currency, country, xjFlag);
    }

}
