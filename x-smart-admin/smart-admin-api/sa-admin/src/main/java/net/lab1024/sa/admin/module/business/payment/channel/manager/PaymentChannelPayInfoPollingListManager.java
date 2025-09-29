package net.lab1024.sa.admin.module.business.payment.channel.manager;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.payment.channel.dao.*;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.*;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoRouteVO;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.RedisLockUtil;
import net.lab1024.sa.base.constant.RedisKeyConst;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class PaymentChannelPayInfoPollingListManager extends ServiceImpl<PaymentChannelPayInfoPollingListDao, PaymentChannelPayInfoPollingListEntity> {

    @Resource
    private PaymentChannelPayInfoPollingListDao paymentChannelPayInfoPollingListDao;

    @Resource
    private PaymentChannelPayInfoCfgDao paymentChannelPayInfoCfgDao;

    /**
     * 从传递过来的支付渠道中按轮询方式获取一个count最小的支付渠道
     * @param paymentChannelInfoRouteVOList
     * @return
     */
    public PaymentChannelInfoRouteVO chooseOneByPolling(List<PaymentChannelInfoRouteVO> paymentChannelInfoRouteVOList) {
        log.error("chooseOneByPolling:: params = {}", JSON.toJSONString(paymentChannelInfoRouteVOList));

        if (paymentChannelInfoRouteVOList == null || paymentChannelInfoRouteVOList.isEmpty()) {
            return null;
        }
        //使用departmentId加锁
        Long deparmentId = paymentChannelInfoRouteVOList.get(0).getDepartmentId();
        String redisLock = RedisKeyConst.Order.ORDER_CHANNEL_ROUTE_TOKEN + deparmentId;
        boolean lock = RedisLockUtil.tryLock(redisLock);
        if (!lock) {
            throw new BusinessException("获取不到锁");
        }
        try {
            QueryWrapper<PaymentChannelPayInfoPollingListEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("department_id", deparmentId);
            //状态是有效的
            queryWrapper.eq("status", 1);

            List<Long> payInfoIds = new ArrayList<>();
            paymentChannelInfoRouteVOList.forEach(item -> {
                payInfoIds.add(item.getId());
            });
            queryWrapper.in("pay_info_id", payInfoIds);

            queryWrapper.orderByAsc("count");
            queryWrapper.last("limit 1");

            PaymentChannelPayInfoPollingListEntity entity = paymentChannelPayInfoPollingListDao.selectOne(queryWrapper);
            if (entity == null) {
                log.error("chooseOneByPolling::.selectOne failed !!! queryWrapper = {}", queryWrapper.getSqlSelect());
                return null;
            }

            //更新数据表
            entity.setCount(entity.getCount() + 1);
            entity.setUpdateTime(LocalDateTime.now());
            paymentChannelPayInfoPollingListDao.updateById(entity);

            for (int i = 0; i < paymentChannelInfoRouteVOList.size(); i++) {
                PaymentChannelInfoRouteVO routeVO = paymentChannelInfoRouteVOList.get(i);
//                System.out.println("entity.getPayInfoId() " + entity.getPayInfoId() + " " + routeVO.getId());
                if (Objects.equals(entity.getPayInfoId(), routeVO.getId())) {
                    return routeVO;
                }
            }

            return null;
        } finally {
            RedisLockUtil.unlock(redisLock);
        }
    }

    public void chooseReversed(Long deparmentId, Long payInfoId) {
        //使用departmentId加锁
        String redisLock = RedisKeyConst.Order.ORDER_CHANNEL_ROUTE_TOKEN + deparmentId;
        boolean lock = RedisLockUtil.tryLock(redisLock);
        if (!lock) {
            throw new BusinessException("获取不到锁");
        }

        try {
            QueryWrapper<PaymentChannelPayInfoPollingListEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("department_id", deparmentId);
            queryWrapper.eq("status", 1);
            queryWrapper.eq("pay_info_id", payInfoId);
            PaymentChannelPayInfoPollingListEntity entity = paymentChannelPayInfoPollingListDao.selectOne(queryWrapper);
            if (entity == null) {
                log.error("chooseReversed::.selectOne failed !!! queryWrapper = {}", queryWrapper.getSqlSelect());
                return;
            }

            //更新数据表
            long count = entity.getCount() - 1;
            if (count < 0) {
                return;
            }
            entity.setCount(count);
            entity.setUpdateTime(LocalDateTime.now());
            paymentChannelPayInfoPollingListDao.updateById(entity);

        } finally {
            RedisLockUtil.unlock(redisLock);
        }
    }

    public void initDataByDepartmentId(Long departmentId) {
        //使用departmentId加锁
        String redisLock = RedisKeyConst.Order.ORDER_CHANNEL_ROUTE_TOKEN + departmentId;
        boolean lock = RedisLockUtil.tryLock(redisLock);
        if (!lock) {
            throw new BusinessException("获取不到锁");
        }

        try {
            QueryWrapper<PaymentChannelPayInfoCfgEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("department_id", departmentId);
//            queryWrapper.eq("status", 1);
            List<PaymentChannelPayInfoCfgEntity> payInfos = paymentChannelPayInfoCfgDao.selectList(queryWrapper);

            for (PaymentChannelPayInfoCfgEntity entity : payInfos) {

                PaymentChannelPayInfoPollingListEntity pollingEntity = new PaymentChannelPayInfoPollingListEntity();
                pollingEntity.setDepartmentId(entity.getDepartmentId());
                pollingEntity.setPayInfoId(entity.getId());
                pollingEntity.setType(entity.getType());
                pollingEntity.setUsername(entity.getUsername());
                pollingEntity.setBankInfo(entity.getBankInfo());
                pollingEntity.setBankNo(entity.getBankNo());
                pollingEntity.setCount(0L);
                pollingEntity.setStatus(entity.getStatus());
                pollingEntity.setCreateTime(LocalDateTime.now());
                pollingEntity.setUpdateTime(LocalDateTime.now());

                paymentChannelPayInfoPollingListDao.insert(pollingEntity);
            }

        } finally {
            RedisLockUtil.unlock(redisLock);
        }
    }

    public void insertPollingRecord(PaymentChannelPayInfoCfgEntity entity) {
        //使用departmentId加锁
        String redisLock = RedisKeyConst.Order.ORDER_CHANNEL_ROUTE_TOKEN + entity.getDepartmentId();
        boolean lock = RedisLockUtil.tryLock(redisLock);
        if (!lock) {
            throw new BusinessException("获取不到锁");
        }

        try {
            PaymentChannelPayInfoPollingListEntity pollingEntity = new PaymentChannelPayInfoPollingListEntity();
            pollingEntity.setDepartmentId(entity.getDepartmentId());
            pollingEntity.setPayInfoId(entity.getId());
            pollingEntity.setType(entity.getType());
            pollingEntity.setUsername(entity.getUsername());
            pollingEntity.setBankInfo(entity.getBankInfo());
            pollingEntity.setBankNo(entity.getBankNo());
            pollingEntity.setCount(0L);
            pollingEntity.setStatus(1);
            pollingEntity.setCreateTime(LocalDateTime.now());
            pollingEntity.setUpdateTime(LocalDateTime.now());

            paymentChannelPayInfoPollingListDao.insert(pollingEntity);

            //为了避免出现新增后，新增的卡短期有大量交易，故将该departmentId下所有支付方式的count重置为0
            UpdateWrapper<PaymentChannelPayInfoPollingListEntity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("department_id", entity.getDepartmentId());
            updateWrapper.set("count", 0);
            paymentChannelPayInfoPollingListDao.update(null, updateWrapper);

        } finally {
            RedisLockUtil.unlock(redisLock);
        }
    }

    public void updatePollingRecord(PaymentChannelPayInfoCfgEntity entity) {
        //使用departmentId加锁
        String redisLock = RedisKeyConst.Order.ORDER_CHANNEL_ROUTE_TOKEN + entity.getDepartmentId();
        boolean lock = RedisLockUtil.tryLock(redisLock);
        if (!lock) {
            throw new BusinessException("获取不到锁");
        }

        try {
            QueryWrapper<PaymentChannelPayInfoPollingListEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("department_id", entity.getDepartmentId());
            queryWrapper.eq("pay_info_id", entity.getId());
            queryWrapper.orderByDesc("update_time");
            queryWrapper.last("limit 1");
            PaymentChannelPayInfoPollingListEntity pollingEntity = paymentChannelPayInfoPollingListDao.selectOne(queryWrapper);
            if (pollingEntity == null) {
                log.error("chooseReversed::.selectOne failed !!! queryWrapper = {}", queryWrapper.getSqlSelect());
                return;
            }

            pollingEntity.setType(entity.getType());
            pollingEntity.setUsername(entity.getUsername());
            pollingEntity.setBankInfo(entity.getBankInfo());
            pollingEntity.setBankNo(entity.getBankNo());

            pollingEntity.setStatus(entity.getStatus());
            pollingEntity.setUpdateTime(LocalDateTime.now());

            paymentChannelPayInfoPollingListDao.updateById(pollingEntity);

            if (entity.getStatus() == 1) {
                //为了避免出现新增后，新增的卡短期有大量交易，故将该departmentId下所有支付方式的count重置为0
                UpdateWrapper<PaymentChannelPayInfoPollingListEntity> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("department_id", entity.getDepartmentId());
                updateWrapper.set("count", 0);
                paymentChannelPayInfoPollingListDao.update(null, updateWrapper);
            }
        } finally {
            RedisLockUtil.unlock(redisLock);
        }
    }

    public void disablePollingListByDeparmentId(Long departmentId) {
        //使用departmentId加锁
        String redisLock = RedisKeyConst.Order.ORDER_CHANNEL_ROUTE_TOKEN + departmentId;
        boolean lock = RedisLockUtil.tryLock(redisLock);
        if (!lock) {
            throw new BusinessException("获取不到锁");
        }

        try {
//            UpdateWrapper<PaymentChannelPayInfoPollingListEntity> updateWrapper = new UpdateWrapper<>();
//            updateWrapper.eq("department_id", departmentId);
//            updateWrapper.set("status", 0);
//            paymentChannelPayInfoPollingListDao.update(null, updateWrapper);

            QueryWrapper<PaymentChannelPayInfoPollingListEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("department_id", departmentId);
            paymentChannelPayInfoPollingListDao.delete(queryWrapper);

        } finally {
            RedisLockUtil.unlock(redisLock);
        }
    }

    public void restPollingRouteCountByDeparmentId(Long departmentId) {
        //使用departmentId加锁
        String redisLock = RedisKeyConst.Order.ORDER_CHANNEL_ROUTE_TOKEN + departmentId;
        boolean lock = RedisLockUtil.tryLock(redisLock);
        if (!lock) {
            throw new BusinessException("获取不到锁");
        }

        try {
            UpdateWrapper<PaymentChannelPayInfoPollingListEntity> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("department_id", departmentId);
            updateWrapper.set("count", 0);
            paymentChannelPayInfoPollingListDao.update(null, updateWrapper);
        } finally {
            RedisLockUtil.unlock(redisLock);
        }
    }

}