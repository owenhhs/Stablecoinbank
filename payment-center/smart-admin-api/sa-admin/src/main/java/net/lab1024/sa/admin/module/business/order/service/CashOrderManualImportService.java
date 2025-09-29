package net.lab1024.sa.admin.module.business.order.service;

import cn.hutool.core.net.NetUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.order.dao.CashOrderManualImportDao;
import net.lab1024.sa.admin.module.business.order.domain.entity.CashOrderManualImportEntity;
import net.lab1024.sa.admin.module.business.order.domain.form.CashOrderManualImportForm;
import net.lab1024.sa.admin.module.business.order.domain.form.CashOrderManualImportQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.vo.CashOrderManualImportVO;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderWithdrawForm;
import net.lab1024.sa.admin.module.openapi.order.domain.vo.OrderWithdrawBatchImportVO;
import net.lab1024.sa.admin.module.openapi.order.domain.vo.OrderWithdrawVO;
import net.lab1024.sa.admin.module.openapi.order.service.OrderService;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.RequestMerchant;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.enumeration.WithdrawOrderStatusEnum;
import net.lab1024.sa.base.common.util.*;
import net.lab1024.sa.base.constant.RedisKeyConst;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 兑付订单手工导入表 Service
 */
@Slf4j
@Service
public class CashOrderManualImportService {

    @Resource
    private CashOrderManualImportDao cashOrderManualImportDao;

    @Resource
    private OrderService orderService;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${server.port}")
    private String port;

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<CashOrderManualImportVO> queryPage(CashOrderManualImportQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<CashOrderManualImportVO> list = cashOrderManualImportDao.queryPage(page, queryForm);
        PageResult<CashOrderManualImportVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }


    @Transactional(rollbackFor = Throwable.class)
    public ResponseDTO<OrderWithdrawBatchImportVO> batchImport(List<CashOrderManualImportForm> formList) {
        if (formList == null || formList.isEmpty()) return ResponseDTO.error(UserErrorCode.PARAM_ERROR);

        String batchNo = SmartLocalDateUtil.format(LocalDateTime.now(), SmartDateFormatterEnum.YMDHMS);
        String localhostIp = NetUtil.getLocalhostStr();

        OrderWithdrawBatchImportVO respVO = new OrderWithdrawBatchImportVO();
        for (CashOrderManualImportForm form : formList) {
            CashOrderManualImportEntity orderInfoEntity = new CashOrderManualImportEntity();
            orderInfoEntity.setBatchNo(batchNo);
            orderInfoEntity.setOrderNo(form.getOrderNo());
            orderInfoEntity.setWithdrawType(form.getWithdrawType());
            orderInfoEntity.setWithdrawChannelList(form.getWithdrawChannelList());
            orderInfoEntity.setAmount(form.getAmount());
            orderInfoEntity.setAmountUsdt(form.getAmoutUSDT());
            orderInfoEntity.setCurrency(form.getCurrency());
            orderInfoEntity.setCountry(form.getCountry());
            orderInfoEntity.setAccountHolder(form.getAccountHolder());
            orderInfoEntity.setBankAccount(form.getBankAccount());
//        orderInfoEntity.setBankCode(form.getBankCode());
            orderInfoEntity.setBankName(form.getBankName());
            orderInfoEntity.setBankBranch(form.getBankBranch());
            orderInfoEntity.setBankProvince(form.getBankProvince());
            orderInfoEntity.setBankCity(form.getBankCity());
            orderInfoEntity.setRemark(form.getRemark());
            orderInfoEntity.setCallback(generateCallbackUrl());
            orderInfoEntity.setApplyTime(System.currentTimeMillis());
//        orderInfoEntity.setName(form.getName());
            orderInfoEntity.setClientIp(localhostIp);
            orderInfoEntity.setUserId(form.getUseId());
//        orderInfoEntity.setDevice(form.getDevice());
//        orderInfoEntity.setEmail(form.getEmail());
            orderInfoEntity.setExt(form.getExt());

            orderInfoEntity.setStatus(1);

            orderInfoEntity.setCreateTime(LocalDateTime.now());
            orderInfoEntity.setUpdateTime(LocalDateTime.now());

            cashOrderManualImportDao.insert(orderInfoEntity);

        }

        return ResponseDTO.ok(respVO);
    }

    private String generateCallbackUrl() {
        String callback = "/notify/manual/withdraw/order/result";

        String localhostIp = NetUtil.getLocalhostStr();
        String finalContextPath = contextPath.startsWith("/") ? contextPath : "/" + contextPath;
        if (finalContextPath.endsWith("/")) {
            finalContextPath = finalContextPath.substring(0, finalContextPath.length() - 1);
        }
        String callbackUrl = "http://" + localhostIp + ":" + port + finalContextPath + callback;
        return callbackUrl;
    }


    /**
     * 定时任务：针对手工导入表中状态为1-待处理的清单，逐笔向接口发送请求
     */
    @Scheduled(initialDelay = 5000, fixedDelay = 5 * 60000)
    public void manualImportWithdrawOrderApplyTask() {

        // 获取订单状态
        String redisKey = RedisKeyConst.Order.WITHDRAW_ORDER_STATUS_TASK_LOCK + "manual-import-order-proc";
        boolean lock = RedisLockUtil.tryLock(redisKey);
        if (!lock) {
            return;
        }
        try {
            LambdaQueryWrapper<CashOrderManualImportEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CashOrderManualImportEntity::getStatus, WithdrawOrderStatusEnum.Pending.getValue());
            List<CashOrderManualImportEntity> orderList = cashOrderManualImportDao.selectList(queryWrapper);

            RequestMerchant requestMerchant = new RequestMerchant() {
                @Override
                public Long getId() {
                    return null;
                }

                @Override
                public String getMerNo() {
                    return "Manul0001";
                }

                @Override
                public String getMerName() {
                    return null;
                }

                @Override
                public String getSecretKey() {
                    return null;
                }

                @Override
                public Integer getStatus() {
                    return null;
                }

                @Override
                public LocalDateTime getCreateTime() {
                    return null;
                }

                @Override
                public LocalDateTime getUpdateTime() {
                    return null;
                }
            };
            for (CashOrderManualImportEntity entity : orderList) {
                try {
                    OrderWithdrawForm form = SmartBeanUtil.copy(entity, OrderWithdrawForm.class);
                    List<String> chnls = JSON.parseArray(entity.getWithdrawChannelList(), String.class);
                    form.setWithdrawChannelList(chnls);
                    form.setAmountUSDT(entity.getAmountUsdt());
                    ResponseDTO<String> responseDTO = orderService.orderWithdraw(requestMerchant, form);
                    log.error("manual import order (orderNo:{}) call ret ==> {}", entity.getOrderNo(), responseDTO);
                    if (responseDTO.getCode() == 0) {
                        entity.setStatus(WithdrawOrderStatusEnum.Processing.getValue());
                        cashOrderManualImportDao.updateById(entity);
                    }
                } catch (Exception e) {
                    log.error("manual import order (orderNo:{}) call error! {}", entity.getOrderNo(), e.getMessage());
                }
            }
        } finally {
            RedisLockUtil.unlock(redisKey);
        }
    }

    /**
     * 结果通知处理【仿交易所侧】
     * @param params
     * @return
     */
    public Map<String, Object> notifyOrderResult(Map<String, Object> params) {
        log.info("notifyOrderResult params ==> {}", JSON.toJSONString(params));

        String orderNo = params.get("orderNo").toString();
        Map<String, Object> map = new HashMap<>(1);

        String redisLock = RedisKeyConst.Order.WITHDRAW_ORDER_STATUS_TASK_LOCK + orderNo;
        boolean lock = RedisLockUtil.tryLock(redisLock);
        if (!lock) {
            map.put("code", 1);
            map.put("msg", "Concurrency limitation");
            return map;
        }
        try {
            LambdaQueryWrapper<CashOrderManualImportEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CashOrderManualImportEntity::getOrderNo, orderNo);
            CashOrderManualImportEntity orderInfoEntity = cashOrderManualImportDao.selectOne(queryWrapper);
            if (orderInfoEntity == null) {
                map.put("code", 1);
                map.put("msg", "not found");
                return map;
            }

            orderInfoEntity.setStatus(WithdrawOrderStatusEnum.Successed.getValue());
            orderInfoEntity.setFinishedTime(Long.valueOf(params.get("endTime").toString()));
            orderInfoEntity.setUpdateTime(LocalDateTime.now());
            cashOrderManualImportDao.updateById(orderInfoEntity);

            map.put("code", 0);
            map.put("msg", "success");
            return map;
        } finally {
            RedisLockUtil.unlock(redisLock);
        }
    }
}
