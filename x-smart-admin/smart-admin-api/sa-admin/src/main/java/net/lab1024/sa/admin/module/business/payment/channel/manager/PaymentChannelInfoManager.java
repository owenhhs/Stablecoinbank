package net.lab1024.sa.admin.module.business.payment.channel.manager;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.constant.AdminRedisKeyConst;
import net.lab1024.sa.admin.module.business.orderinfo.dao.OrderInfoDao;
import net.lab1024.sa.admin.module.business.orderinfo.domain.entity.OrderInfoEntity;
import net.lab1024.sa.admin.module.business.payment.channel.dao.*;
import net.lab1024.sa.admin.module.business.payment.channel.domain.dto.PaymentChannelInfoRouteDTO;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.*;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelBusinessCfgEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelInfoEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelPayInfoBusinessCfg;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelPayInfoCfgEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.form.PaymentChannelInfoAddForm;
import net.lab1024.sa.admin.module.business.payment.channel.domain.form.PaymentChannelPayInfoQueryForm;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoRouteListVO;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoRouteVO;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelPayInfoCfgVO;
import net.lab1024.sa.admin.module.system.department.dao.DepartmentDao;
import net.lab1024.sa.base.common.code.OpenErrorCode;
import net.lab1024.sa.base.common.code.OrderErrorCode;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartDateFormatterEnum;
import net.lab1024.sa.base.common.util.SmartLocalDateUtil;
import net.lab1024.sa.base.module.support.redis.RedisService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PaymentChannelInfoManager extends ServiceImpl<PaymentChannelInfoDao, PaymentChannelInfoEntity> {

    @Resource
    private DepartmentDao departmentDao;

    @Resource
    private PaymentChannelParentDao paymentChannelParentDao;

    @Resource
    private PaymentChannelInfoDao paymentChannelInfoDao;

    @Resource
    private PaymentChannelBusinessCfgDao paymentChannelBusinessCfgDao;

    @Resource
    private PaymentChannelPayInfoCfgDao paymentChannelPayInfoCfgDao;

    @Resource
    private PaymentChannelPayInfoBusinessCfgDao paymentChannelPayInfoBusinessCfgDao;

    @Resource
    private ChannelRouteWhiteListDao channelRouteWhiteListDao;

    @Resource
    private OrderInfoDao orderInfoDao;

    @Resource
    private PaymentChannelPayInfoPollingListManager paymentChannelPayInfoPollingListManager;

    @Resource
    private RedisService redisService;
    /**
     * 订单付款时间，默认15分钟
     */
    @Value("${zfx.pay.time:900}")
    private Long orderPayTime;

    @Value(value = "${zfx.user.lock.departmentids:}")
    private List<Long> userLockDepartmentIds;


    public PaymentChannelParentEntity getParentInfoByMerCode(String merCode) {
        LambdaQueryWrapper<PaymentChannelParentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentChannelParentEntity::getMerCode, merCode);
        return paymentChannelParentDao.selectOne(queryWrapper);
    }


    public PaymentChannelInfoEntity getInfoByDepartmentId(Long departmentId) {
        QueryWrapper<PaymentChannelInfoEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department_id", departmentId);
        return this.getOne(queryWrapper);
    }

    public PaymentChannelInfoEntity getInfoByMerCode(String merCode) {
        LambdaQueryWrapper<PaymentChannelInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentChannelInfoEntity::getMerCode, merCode);
        return this.getOne(queryWrapper);
    }

    public List<PaymentChannelInfoEntity> getInfoByDepartmentIds(List<Long> departmentIds) {
        QueryWrapper<PaymentChannelInfoEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("department_id", departmentIds);
        return this.list(queryWrapper);
    }

    public List<PaymentChannelBusinessCfgEntity> getBusinessCfgByDepartmentId(Long departmentId) {
        QueryWrapper<PaymentChannelBusinessCfgEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department_id", departmentId);
        return paymentChannelBusinessCfgDao.selectList(queryWrapper);
    }


    public List<PaymentChannelBusinessCfgEntity> getBusinessCfgByDepartmentIds(List<Long> departmentIds) {
        QueryWrapper<PaymentChannelBusinessCfgEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("department_id", departmentIds);
        return paymentChannelBusinessCfgDao.selectList(queryWrapper);
    }

    public void saveBusinessCfg(PaymentChannelInfoAddForm addForm, Long departmentId) {
        PaymentChannelBusinessCfgEntity businessCfg = new PaymentChannelBusinessCfgEntity();
        businessCfg.setDepartmentId(departmentId);
        businessCfg.setAmountMin(addForm.getAmountMin());
        businessCfg.setAmountMax(addForm.getAmountMax());
        businessCfg.setPlatformAward(addForm.getPlatformAward());
        businessCfg.setPlatformBrokerage(addForm.getPlatformBrokerage());
        businessCfg.setMerAward(addForm.getMerAward());
        businessCfg.setMerBrokerage(addForm.getMerBrokerage());
        businessCfg.setCreateTime(LocalDateTime.now());
        businessCfg.setUpdateTime(LocalDateTime.now());
        paymentChannelBusinessCfgDao.insert(businessCfg);
    }

    public void updateBusinessCfg(PaymentChannelBusinessCfgEntity businessCfg) {
        paymentChannelBusinessCfgDao.updateById(businessCfg);
    }


    public List<PaymentChannelPayInfoCfgEntity> getPayInfoCfgByDepartmentId(Long departmentId) {
        QueryWrapper<PaymentChannelPayInfoCfgEntity> queryWrapper = new QueryWrapper<>();
        if (departmentId != null) {
            queryWrapper.eq("department_id", departmentId);
        }
        queryWrapper.orderByDesc("create_time");
        return paymentChannelPayInfoCfgDao.selectList(queryWrapper);
    }

    public List<PaymentChannelPayInfoCfgVO> getPayInfoCfgPage(Page<?> page, PaymentChannelPayInfoQueryForm queryForm) {
        return paymentChannelPayInfoCfgDao.queryPage(page, queryForm);
    }


    public List<PaymentChannelPayInfoBusinessCfg> getPayInfoBusinessCfgByPayInfoIds(List<Long> payInfoIds) {
        QueryWrapper<PaymentChannelPayInfoBusinessCfg> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("pay_info_id", payInfoIds);
        return paymentChannelPayInfoBusinessCfgDao.selectList(queryWrapper);
    }


    public void paymentChannelRouteReversed(Long payInfoId) {
        PaymentChannelPayInfoCfgEntity payInfoCfgEntity = paymentChannelPayInfoCfgDao.selectById(payInfoId);
        if (payInfoCfgEntity == null) return;
        PaymentChannelInfoEntity channelInfoEntity = getInfoByDepartmentId(payInfoCfgEntity.getDepartmentId());
        if (channelInfoEntity == null) return;
        if (channelInfoEntity.getRouteType() == 2) {
            paymentChannelPayInfoPollingListManager.chooseReversed(payInfoCfgEntity.getDepartmentId(), payInfoId);
        }
    }

    /**
     * 获取可用的路由通道
     * 两层路由：第一层：商户通道路由，第二层：配置的支付通道路由
     */
    public PaymentChannelInfoRouteDTO getPaymentChannelInfo(Long departmentId, BigDecimal amount, String depositType,
                                                            String paymentChannel, String orderNo, String username,
                                                            String currency, String country, Long userId, boolean isXj) {

        List<Long> departmentIds = departmentDao.getDepartmentIdListByParentId(departmentId);
        if (CollectionUtils.isEmpty(departmentIds)) {
            log.error("获取可用的路由通道，子级商户通道路由为空，departmentId:{}", departmentId);
            throw new BusinessException(OpenErrorCode.DATA_NOT_EXIST);
        }

        // 获取白名单列表
        List<Long> channelIds = checkWhiteList(username);

        // 用户锁定逻辑处理
        if (CollectionUtils.isEmpty(channelIds)) {
            List<Long> intersection = (List<Long>) org.apache.commons.collections4.CollectionUtils.intersection(departmentIds, userLockDepartmentIds);
            if (!CollectionUtils.isEmpty(intersection)) {
                try {
                    PaymentChannelInfoRouteDTO channelInfoRouteDTO = userLock(orderNo, depositType, amount, currency, country, userId, intersection.get(0));
                    if (channelInfoRouteDTO != null) {
                        return channelInfoRouteDTO;
                    }
                } catch (Exception e) {
                    log.error("用户锁定逻辑处理异常，回退到通用处理逻辑，error:{}", e.getMessage(), e);
                }
            }
        }

        //第一层路由
        //查询商户列表
        QueryWrapper<PaymentChannelInfoEntity> queryWrapper = new QueryWrapper<>();
        //payment_count大于当前订单数量
        //queryWrapper.gt("payment_count", orderCount + 1);
        //payment_limit大于当前支付限额
        //queryWrapper.gt("payment_limit", orderAmountTotal[0].add(amount));

        queryWrapper.in("department_id", departmentIds);
        // 命中白名单
        if (!CollectionUtils.isEmpty(channelIds)) {
            queryWrapper.in("id", channelIds);
        } else {
            //状态是有效的
            queryWrapper.eq("status", 1);
        }
        queryWrapper.eq("black_list", 2);
        //判断
        List<PaymentChannelInfoEntity> paymentChannelInfoEntityList = paymentChannelInfoDao.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(paymentChannelInfoEntityList)) {
            throw new BusinessException(OrderErrorCode.NO_ROUTE_ERROR);
        }


        List<PaymentChannelInfoRouteListVO> routeListPay = new ArrayList<>();
        paymentChannelInfoEntityList.forEach(item -> {
            PaymentChannelInfoRouteListVO temp = new PaymentChannelInfoRouteListVO();
            BeanUtil.copyProperties(item, temp);
            routeListPay.add(temp);
        });

        //2.过滤通道（剔除限额和数量不满足的通道）
        Iterator<PaymentChannelInfoRouteListVO> iteratorChannel = routeListPay.iterator();
        while (iteratorChannel.hasNext()) {
            PaymentChannelInfoRouteListVO itemChannel = iteratorChannel.next();
            // 查询订单数据以及支付限额满足的商户通道
            QueryWrapper<OrderInfoEntity> queryWrapperOrder = new QueryWrapper<>();
            queryWrapperOrder.ge("create_time", LocalDate.now().atStartOfDay())
                    .le("create_time", LocalDate.now().atTime(LocalTime.MAX));
            queryWrapperOrder.in("status", Arrays.asList(1, 2));
            queryWrapperOrder.eq("department_id", itemChannel.getDepartmentId());
            List<OrderInfoEntity> orderInfoEntityList = orderInfoDao.selectList(queryWrapperOrder);

            int orderCount = orderInfoEntityList.size();
            final BigDecimal[] orderAmountTotal = {new BigDecimal(0)};

            orderInfoEntityList.forEach(item -> {
                orderAmountTotal[0] = orderAmountTotal[0].add(item.getAmount());
            });

            BigDecimal totalAmount = orderAmountTotal[0];
            if ((itemChannel.getPaymentCount() < orderCount + 1)
                    || totalAmount.add(amount).compareTo(itemChannel.getPaymentLimit()) > 0) {
                log.info("渠道单日限额或订单数量超限，orderNo:{}, departmentId:{}, amount:{}, limitAmount:{}, totalAmount:{}, limitCount:{}, orderCount:{}",
                        orderNo, itemChannel.getDepartmentId(), amount, itemChannel.getPaymentLimit(), totalAmount, itemChannel.getPaymentCount(), orderCount);
                iteratorChannel.remove();
            }
        }

        //2.查询收款区间满足的商户可用通道
        Iterator<PaymentChannelInfoRouteListVO> iterator = routeListPay.iterator();
        while (iterator.hasNext()) {
            PaymentChannelInfoRouteListVO item = iterator.next();
            QueryWrapper<PaymentChannelBusinessCfgEntity> queryWrapperChannelBusiness = new QueryWrapper<>();
            //amount_min<=amount
            queryWrapperChannelBusiness.le("amount_min", amount);
            //amount_max>=mount
            queryWrapperChannelBusiness.ge("amount_max", amount);
            queryWrapperChannelBusiness.eq("department_id", item.getDepartmentId());
            List<PaymentChannelBusinessCfgEntity> paymentChannelBusinessCfgEntityList = paymentChannelBusinessCfgDao.selectList(queryWrapperChannelBusiness);
            if (CollectionUtils.isEmpty(paymentChannelBusinessCfgEntityList)) {
                log.info("渠道单笔限额超限，orderNo:{}, departmentId:{}, amount:{}", orderNo, item.getDepartmentId(), amount);
                iterator.remove();
            } else {
                item.setPaymentChannelBusinessId(paymentChannelBusinessCfgEntityList.get(0).getId());
            }
        }
        String redisKey = AdminRedisKeyConst.PAYMENT_CHANNEL_ROUTE_CACHE + orderNo;
        List<Long> payInfoIds = redisService.getList(redisKey, Long.class);
        if (CollectionUtils.isEmpty(payInfoIds)) {
            payInfoIds = new ArrayList<>();
        }
        //3.满足支付方式的通道(在通道路由检查，不在商户路由检查)
//        Iterator<PaymentChannelInfoRouteListVO> iteratorPay = routeListPay.iterator();
//        while (iteratorPay.hasNext()) {
//            PaymentChannelInfoRouteListVO item = iteratorPay.next();
//            QueryWrapper<PaymentChannelPayInfoCfgEntity> queryWrapperPayInfo = new QueryWrapper<>();
//            if ("bank".equals(depositType)) {
//                queryWrapperPayInfo.eq("type", depositType);
//            } else {
//                queryWrapperPayInfo.eq("type", paymentChannel);
//            }
//            queryWrapperPayInfo.eq("status", 1);
//            queryWrapperPayInfo.eq("black_list", 2);
//            queryWrapperPayInfo.eq("department_id", item.getDepartmentId());
//            if (!CollectionUtils.isEmpty(payInfoIds)) {
//                queryWrapperPayInfo.notIn("id", payInfoIds);
//            }
//            List<PaymentChannelPayInfoCfgEntity> paymentChannelPayInfoCfgEntities = paymentChannelPayInfoCfgDao.selectList(queryWrapperPayInfo);
//            if (CollectionUtils.isEmpty(paymentChannelPayInfoCfgEntities) && paymentChannelPayInfoCfgEntities.size() == 0) {
//                iteratorPay.remove();
//            }
//        }

        //4.商户通道路由比例
        List<PaymentChannelInfoRouteVO> routeMer = new ArrayList<>();
        routeListPay.forEach(item -> {
            PaymentChannelInfoRouteVO temp = new PaymentChannelInfoRouteVO();
            BeanUtil.copyProperties(item, temp);
            routeMer.add(temp);
        });

        String type;
        if ("bank".equalsIgnoreCase(depositType)) {
            type = depositType;
        } else {
            type = paymentChannel;
        }

        //循环路由商户通道，直到有满足条件的商户对应的支付通道
        PaymentChannelInfoRouteDTO routeDTOPay = new PaymentChannelInfoRouteDTO();
        int size = routeMer.size();
        for (int i = 0; i < size; i++) {

            //百分比路由
            PaymentChannelInfoRouteDTO routeDTOMer = this.getRepaymentRouteByPercentage(routeMer, 1, 1);

            //第二层路由
            // 1.查询满足的商户支付通道路由
            QueryWrapper<PaymentChannelPayInfoCfgEntity> queryWrapperPayInfo = new QueryWrapper<>();
            //payment_count大于当前订单数量
            //queryWrapperPayInfo.gt("payment_count", orderCount + 1);
            //payment_limit大于当前支付限额
            //queryWrapperPayInfo.gt("payment_limit", orderAmountTotal[0].add(amount));
            queryWrapperPayInfo.eq("department_id", routeDTOMer.getDepartmentId());
            queryWrapperPayInfo.eq("type", type);
            //状态是有效的
            queryWrapperPayInfo.eq("status", 1);
            if (!payInfoIds.isEmpty()) {
                queryWrapperPayInfo.notIn("id", payInfoIds);
            }
            queryWrapperPayInfo.eq("xinjiang", isXj ? 1 : 0);
            queryWrapperPayInfo.eq("currency", currency);
            queryWrapperPayInfo.eq("country", country);

            List<PaymentChannelPayInfoCfgEntity> paymentChannelPayInfoCfgEntityList = paymentChannelPayInfoCfgDao.selectList(queryWrapperPayInfo);
            List<PaymentChannelInfoRouteVO> routePay = new ArrayList<>();

            //2.查询收款区间满足的支付通道
            Iterator<PaymentChannelPayInfoCfgEntity> iteratorPayInfo = paymentChannelPayInfoCfgEntityList.iterator();
            // 获取当前时间
            long currentTime = System.currentTimeMillis();
            String currentDay = SmartLocalDateUtil.format(LocalDateTime.now(), SmartDateFormatterEnum.YMD);
            while (iteratorPayInfo.hasNext()) {
                PaymentChannelPayInfoCfgEntity item = iteratorPayInfo.next();
                // 判断是否在工作时间
                String[] workTime = item.getWorkTime().split("-");
                long startTime = SmartLocalDateUtil.dateToTimestamp(currentDay + " " + workTime[0], SmartDateFormatterEnum.YMD_HM);
                long endTime = SmartLocalDateUtil.dateToTimestamp(currentDay + " " + workTime[1], SmartDateFormatterEnum.YMD_HM);
                if (currentTime < startTime || currentTime > endTime) {
                    log.info("支付方式不在工作时间，orderNo:{}, departmentId:{}, payInfoId:{}, currentTime:{}, workTime:{}",
                            orderNo, item.getDepartmentId(), item.getId(), currentTime, item.getWorkTime());
                    iteratorPayInfo.remove();
                    continue;
                }

                // 判断业务范围
                QueryWrapper<PaymentChannelPayInfoBusinessCfg> queryWrapperChannelBusiness = new QueryWrapper<>();
                //amount_min<=amount
                queryWrapperChannelBusiness.le("amount_min", amount);
                //amount_max>=mount
                queryWrapperChannelBusiness.ge("amount_max", amount);
                queryWrapperChannelBusiness.eq("pay_info_id", item.getId());
                List<PaymentChannelPayInfoBusinessCfg> paymentChannelPayInfoBusinessCfgList = paymentChannelPayInfoBusinessCfgDao.selectList(queryWrapperChannelBusiness);
                if (CollectionUtils.isEmpty(paymentChannelPayInfoBusinessCfgList)) {
                    log.info("支付方式业务范围不匹配，orderNo:{}, departmentId:{}, payInfoId:{}, amount:{}",
                            orderNo, item.getDepartmentId(), item.getId(), amount);
                    iteratorPayInfo.remove();
                    continue;
                }

                //查询订单，剔除不满足限额和限单的数据
                QueryWrapper<OrderInfoEntity> queryWrapperOrder = new QueryWrapper<>();
                queryWrapperOrder.ge("create_time", LocalDate.now().atStartOfDay())
                        .le("create_time", LocalDate.now().atTime(LocalTime.MAX));
                queryWrapperOrder.in("status", Arrays.asList(1, 2));
                queryWrapperOrder.eq("payment_channel_pay_info_id", item.getId());
                queryWrapperOrder.eq("currency", currency);
                queryWrapperOrder.eq("country", country);
                List<OrderInfoEntity> orderInfoEntityList = orderInfoDao.selectList(queryWrapperOrder);

                int orderCount = orderInfoEntityList.size();
                final BigDecimal[] orderAmountTotal = {new BigDecimal(0)};

                orderInfoEntityList.forEach(itemOrder -> {
                    orderAmountTotal[0] = orderAmountTotal[0].add(itemOrder.getAmount());
                });

                BigDecimal totalAmount = orderAmountTotal[0];
                if ((item.getPaymentCount() < orderCount + 1)
                        || totalAmount.add(amount).compareTo(item.getPaymentLimit()) > 0) {
                    log.info("超过支付方式限额或订单数量限制，orderNo:{}, departmentId:{}, payInfoId:{}, amount:{}, limitAmount:{}, totalAmount:{}, limitCount:{}, orderCount:{}",
                            orderNo, item.getDepartmentId(), item.getId(), amount, item.getPaymentLimit(), totalAmount, item.getPaymentCount(), orderCount);
                    iteratorPayInfo.remove();
                    continue;
                }
                PaymentChannelInfoRouteVO temp = new PaymentChannelInfoRouteVO();
                BeanUtil.copyProperties(item, temp);
                temp.setPaymentChannelPayInfoId(item.getId());
                temp.setMerName(routeDTOMer.getMerName());
                routePay.add(temp);
            }

            if (CollectionUtils.isEmpty(routePay)) {
                log.info("此商户通道下没有可用的支付通道，amount：{},depositType:{},paymentChannel:{},orderNo:{}",
                        amount, depositType, paymentChannel, orderNo);
                //删除指定的迭代器记录
                Iterator<PaymentChannelInfoRouteVO> routeListVOIterator = routeMer.iterator();
                while (routeListVOIterator.hasNext()) {
                    PaymentChannelInfoRouteVO iteratorTemp = routeListVOIterator.next();
                    if (routeDTOMer.getDepartmentId().equals(iteratorTemp.getDepartmentId())) {
                        routeListVOIterator.remove();
                    }
                }
                continue;
            }
            //3.支付通道由比例
            routeDTOPay = this.getRepaymentRouteByPercentage(routePay, 2, routeDTOMer.getRouteType());
            routeDTOPay.setPaymentChannelBusinessId(routeDTOMer.getPaymentChannelBusinessId());
            if (routeDTOPay.getDepartmentId() != null) {
                break;
            }
        }

        if (routeDTOPay.getDepartmentId() == null) {
            log.info("最终没有可用的支付通道，amount：{}, depositType:{}, paymentChannel:{}, orderNo:{}, isXj:{}",
                    amount, depositType, paymentChannel, orderNo, isXj);
            throw new BusinessException(OrderErrorCode.NO_ROUTE_ERROR);
        }
        payInfoIds.add(routeDTOPay.getPaymentChannelPayInfoId());
        redisService.set(redisKey, payInfoIds, orderPayTime);

        return routeDTOPay;
    }

    private PaymentChannelInfoRouteDTO userLock(String orderNo, String depositType, BigDecimal amount, String currency, String country, Long userId, Long departmentId) {
        LambdaQueryWrapper<OrderInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfoEntity::getUserId, userId);
        queryWrapper.eq(OrderInfoEntity::getDepositType, depositType);
        queryWrapper.eq(OrderInfoEntity::getDepartmentId, departmentId);
        queryWrapper.eq(OrderInfoEntity::getStatus, 2);
        queryWrapper.orderByDesc(OrderInfoEntity::getFinishTime);
        queryWrapper.last("limit 1");
        OrderInfoEntity orderInfoEntity = orderInfoDao.selectOne(queryWrapper);
        if (orderInfoEntity == null) {
            return null;
        }
        PaymentChannelInfoVO channelInfoVO = paymentChannelInfoDao.queryByDepartmentId(departmentId);
        if (channelInfoVO == null) {
            return null;
        }
        PaymentChannelPayInfoCfgEntity payInfoCfgEntity = paymentChannelPayInfoCfgDao.selectById(orderInfoEntity.getPaymentChannelPayInfoId());
        if (payInfoCfgEntity != null && payInfoCfgEntity.getStatus().equals(1)) {
            PaymentChannelInfoRouteVO routePay = payInfoCheck(payInfoCfgEntity, orderNo, amount, currency, country, channelInfoVO.getMerName());
            if (routePay == null) {
                return null;
            }
            LambdaQueryWrapper<PaymentChannelBusinessCfgEntity> queryWrapperBusinessCfg = new LambdaQueryWrapper<>();
            queryWrapperBusinessCfg.eq(PaymentChannelBusinessCfgEntity::getDepartmentId, channelInfoVO.getDepartmentId());
            queryWrapperBusinessCfg.le(PaymentChannelBusinessCfgEntity::getAmountMin, amount);
            queryWrapperBusinessCfg.gt(PaymentChannelBusinessCfgEntity::getAmountMax, amount);
            PaymentChannelBusinessCfgEntity businessCfg = paymentChannelBusinessCfgDao.selectOne(queryWrapperBusinessCfg);
            if (businessCfg == null) {
                return null;
            }
            PaymentChannelInfoRouteDTO routeDTOPay = new PaymentChannelInfoRouteDTO();
            BeanUtil.copyProperties(routePay, routeDTOPay);
            routeDTOPay.setPaymentChannelBusinessId(businessCfg.getId());
            return routeDTOPay;
        }
        return null;
    }


    private PaymentChannelInfoRouteVO payInfoCheck(PaymentChannelPayInfoCfgEntity payInfoCfgEntity, String orderNo, BigDecimal amount, String currency, String country, String merName) {
        // 获取当前时间
        long currentTime = System.currentTimeMillis();
        String currentDay = SmartLocalDateUtil.format(LocalDateTime.now(), SmartDateFormatterEnum.YMD);
        // 判断是否在工作时间
        String[] workTime = payInfoCfgEntity.getWorkTime().split("-");
        long startTime = SmartLocalDateUtil.dateToTimestamp(currentDay + " " + workTime[0], SmartDateFormatterEnum.YMD_HM);
        long endTime = SmartLocalDateUtil.dateToTimestamp(currentDay + " " + workTime[1], SmartDateFormatterEnum.YMD_HM);
        if (currentTime < startTime || currentTime > endTime) {
            log.info("支付方式不在工作时间，orderNo:{}, departmentId:{}, payInfoId:{}, currentTime:{}, workTime:{}",
                    orderNo, payInfoCfgEntity.getDepartmentId(), payInfoCfgEntity.getId(), currentTime, payInfoCfgEntity.getWorkTime());
            return null;
        }

        // 判断业务范围
        QueryWrapper<PaymentChannelPayInfoBusinessCfg> queryWrapperChannelBusiness = new QueryWrapper<>();
        //amount_min<=amount
        queryWrapperChannelBusiness.le("amount_min", amount);
        //amount_max>=mount
        queryWrapperChannelBusiness.ge("amount_max", amount);
        queryWrapperChannelBusiness.eq("pay_info_id", payInfoCfgEntity.getId());
        List<PaymentChannelPayInfoBusinessCfg> paymentChannelPayInfoBusinessCfgList = paymentChannelPayInfoBusinessCfgDao.selectList(queryWrapperChannelBusiness);
        if (CollectionUtils.isEmpty(paymentChannelPayInfoBusinessCfgList)) {
            log.info("支付方式业务范围不匹配，orderNo:{}, departmentId:{}, payInfoId:{}, amount:{}",
                    orderNo, payInfoCfgEntity.getDepartmentId(), payInfoCfgEntity.getId(), amount);
            return null;
        }

        //查询订单，剔除不满足限额和限单的数据
        QueryWrapper<OrderInfoEntity> queryWrapperOrder = new QueryWrapper<>();
        queryWrapperOrder.ge("create_time", LocalDate.now().atStartOfDay())
                .le("create_time", LocalDate.now().atTime(LocalTime.MAX));
        queryWrapperOrder.in("status", Arrays.asList(1, 2));
        queryWrapperOrder.eq("payment_channel_pay_info_id", payInfoCfgEntity.getId());
        queryWrapperOrder.eq("currency", currency);
        queryWrapperOrder.eq("country", country);
        List<OrderInfoEntity> orderInfoEntityList = orderInfoDao.selectList(queryWrapperOrder);

        int orderCount = orderInfoEntityList.size();
        final BigDecimal[] orderAmountTotal = {new BigDecimal(0)};

        orderInfoEntityList.forEach(itemOrder -> {
            orderAmountTotal[0] = orderAmountTotal[0].add(itemOrder.getAmount());
        });

        BigDecimal totalAmount = orderAmountTotal[0];
        if ((payInfoCfgEntity.getPaymentCount() < orderCount + 1)
                || totalAmount.add(amount).compareTo(payInfoCfgEntity.getPaymentLimit()) > 0) {
            log.info("超过支付方式限额或订单数量限制，orderNo:{}, departmentId:{}, payInfoId:{}, amount:{}, limitAmount:{}, totalAmount:{}, limitCount:{}, orderCount:{}",
                    orderNo, payInfoCfgEntity.getDepartmentId(), payInfoCfgEntity.getId(), amount,
                    payInfoCfgEntity.getPaymentLimit(), totalAmount, payInfoCfgEntity.getPaymentCount(), orderCount);
            return null;
        }
        PaymentChannelInfoRouteVO temp = new PaymentChannelInfoRouteVO();
        BeanUtil.copyProperties(payInfoCfgEntity, temp);
        temp.setPaymentChannelPayInfoId(payInfoCfgEntity.getId());
        temp.setMerName(merName);
        return temp;
    }

    /**
     * 通过比例路由通道（商户+支付通道）
     * @param level 层级： 商户级--1  商户下支付通道--2
     * @param routeType 路由类型： 百分比路由--1  轮询方式路由--2
     * @return
     */
    private PaymentChannelInfoRouteDTO getRepaymentRouteByPercentage(List<PaymentChannelInfoRouteVO> paymentChannelInfoRouteVOList, int level, int routeType) {
        if (level != 1 && level != 2) {
            throw new BusinessException("不支持的路由层级 " + level);
        }
        if (routeType != 1 && routeType != 2) {
            throw new BusinessException("不支持的路由方式 " + routeType);
        }
        if (paymentChannelInfoRouteVOList.isEmpty()) {
            log.error("无路由通道paymentChannelInfoRouteVOList:{}", paymentChannelInfoRouteVOList);
            throw new BusinessException(OrderErrorCode.NO_ROUTE_ERROR);
        }
        PaymentChannelInfoRouteDTO result = new PaymentChannelInfoRouteDTO();

        //只有路由层级是2（商户下支付通道）切路由类型是2（轮询方式路由）的，才启用轮询路由
        int realRouteType = 1;
        if (level == 2 && routeType == 2) realRouteType = 2;

        //百分比路由方式
        if (realRouteType == 1) {
            //百分比的总基数
            Long randomTotalPercentage = 0L;
            //可用的通道百分比全部为0时 随机一个通道
            int percentageZoreChannel = 0;
            for (PaymentChannelInfoRouteVO paymentChannelInfoRouteVO : paymentChannelInfoRouteVOList) {
                if (paymentChannelInfoRouteVO.getPaymentScale() > 0) {
                    randomTotalPercentage += paymentChannelInfoRouteVO.getPaymentScale();
                } else {
                    percentageZoreChannel++;
                }
            }
            //可用的通道百分比全部为0
            if (percentageZoreChannel > 0 && percentageZoreChannel == paymentChannelInfoRouteVOList.size()) {
                Collections.shuffle(paymentChannelInfoRouteVOList);
                PaymentChannelInfoRouteVO routeUsableChannelDTO = paymentChannelInfoRouteVOList.get(0);
                BeanUtil.copyProperties(routeUsableChannelDTO, result);
                log.error("商户通道路由，可用的通道百分比全部为0，随机一个百分比为0的通道 routeUsableChannelDTO={},result={}", routeUsableChannelDTO, result);
                return result;
            }

            log.info("百分比随机代扣路由渠道 randomTotalPercentage={},size={},payRouteRepaymentUsableList={}", randomTotalPercentage, paymentChannelInfoRouteVOList.size(), JSON.toJSON(paymentChannelInfoRouteVOList));
            Collections.shuffle(paymentChannelInfoRouteVOList);
            int value = new Random().nextInt(randomTotalPercentage.intValue());
            int rate = 0;
            for (PaymentChannelInfoRouteVO paymentChannelInfoRouteVO : paymentChannelInfoRouteVOList) {
                if (paymentChannelInfoRouteVO.getPaymentScale() > 0) {
                    rate += paymentChannelInfoRouteVO.getPaymentScale();
                    if (rate > value) {
                        BeanUtil.copyProperties(paymentChannelInfoRouteVO, result);
                        break;
                    }
                }
            }
            if (StringUtils.isEmpty(result)) {
                log.error("代扣路由时 通过[百分比]未找到路由渠道,请检测路由配置 paymentChannelInfoEntityList={}", JSON.toJSON(paymentChannelInfoRouteVOList));
                throw new BusinessException("代扣路由时 通过百分比未找到路由渠道,请检测路由配置");
            }
            return result;
        }

        //轮询路由方式
        PaymentChannelInfoRouteVO paymentChannelInfoRouteVO = paymentChannelPayInfoPollingListManager.chooseOneByPolling(paymentChannelInfoRouteVOList);
        if (paymentChannelInfoRouteVO == null) {
            log.error("代扣路由时 通过[轮询方式]未找到路由渠道,请检测路由配置 paymentChannelInfoEntityList={}", JSON.toJSON(paymentChannelInfoRouteVOList));
            throw new BusinessException("代扣路由时 通过[轮询方式]未找到路由渠道,请检测路由配置");
        }
        BeanUtil.copyProperties(paymentChannelInfoRouteVO, result);
        return result;
    }


    public List<Long> checkWhiteList(String username) {
        LambdaQueryWrapper<ChannelRouteWhiteListEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChannelRouteWhiteListEntity::getUsername, username);
        queryWrapper.eq(ChannelRouteWhiteListEntity::getStatus, 1);
        List<ChannelRouteWhiteListEntity> whiteListEntityList = channelRouteWhiteListDao.selectList(queryWrapper);
        return whiteListEntityList.stream().map(ChannelRouteWhiteListEntity::getChannelId).collect(Collectors.toList());
    }

    public List<String> queryCurrencyOptions(Long departmentId, boolean isAdmin) {
        return paymentChannelPayInfoCfgDao.queryCurrencyOptions(departmentId, isAdmin ? 1 : 0);
    }
}

