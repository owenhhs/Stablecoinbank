package net.lab1024.sa.admin.module.business.payment.channel.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.admin.constant.AdminRedisKeyConst;
import net.lab1024.sa.admin.module.business.payment.channel.dao.PaymentChannelInfoDao;
import net.lab1024.sa.admin.module.business.payment.channel.dao.PaymentChannelPayInfoBusinessCfgDao;
import net.lab1024.sa.admin.module.business.payment.channel.dao.PaymentChannelPayInfoCfgDao;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.*;
import net.lab1024.sa.admin.module.business.payment.channel.domain.form.*;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.*;
import net.lab1024.sa.admin.module.business.payment.channel.manager.PaymentChannelInfoManager;
import net.lab1024.sa.admin.module.business.payment.channel.manager.PaymentChannelParentManager;
import net.lab1024.sa.admin.module.business.payment.channel.manager.PaymentChannelPayInfoPollingListManager;
import net.lab1024.sa.admin.module.system.datascope.constant.DataScopeViewTypeEnum;
import net.lab1024.sa.admin.module.system.department.dao.DepartmentDao;
import net.lab1024.sa.admin.module.system.department.domain.entity.DepartmentEntity;
import net.lab1024.sa.admin.module.system.department.manager.DepartmentCacheManager;
import net.lab1024.sa.admin.module.system.login.domain.RequestEmployee;
import net.lab1024.sa.admin.module.system.role.domain.entity.RoleDataScopeEntity;
import net.lab1024.sa.admin.module.system.role.manager.RoleEmployeeManager;
import net.lab1024.sa.admin.util.AdminRequestUtil;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.RedisLockUtil;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import net.lab1024.sa.base.module.support.file.service.FileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class PaymentChannelInfoService {
    @Resource
    private FileService fileService;
    @Resource
    private PaymentChannelParentManager paymentChannelParentManager;
    @Resource
    private PaymentChannelInfoDao paymentChannelInfoDao;
    @Resource
    private PaymentChannelInfoManager paymentChannelInfoManager;
    @Resource
    private PaymentChannelPayInfoCfgDao channelPayInfoCfgDao;
    @Resource
    private PaymentChannelPayInfoBusinessCfgDao channelPayInfoBusinessCfgDao;
    @Resource
    private PaymentChannelPayInfoPollingListManager paymentChannelPayInfoPollingListManager;
    @Resource
    private DepartmentDao departmentDao;
    @Resource
    private DepartmentCacheManager departmentCacheManager;
    @Resource
    private RoleEmployeeManager roleEmployeeManager;

    public List<PaymentChannelParentEntity> queryParentChannelList() {
        RequestEmployee requestUser = AdminRequestUtil.getRequestUser();
        List<RoleDataScopeEntity> roleDataScopeEntityList = roleEmployeeManager.getRoleDataScope(requestUser.getUserId());
        boolean dataScopeDepartment = roleDataScopeEntityList.stream().anyMatch(v -> DataScopeViewTypeEnum.DEPARTMENT.equalsValue(v.getViewType()));

        List<PaymentChannelParentEntity> paymentChannelInfoEntities = new ArrayList<>();
        if (requestUser.getAdministratorFlag() || !dataScopeDepartment) {
            paymentChannelInfoEntities = paymentChannelParentManager.list();
        } else {
            PaymentChannelParentEntity paymentChannelParentEntity = paymentChannelParentManager.getByDepartmentId(requestUser.getDepartmentId());
            paymentChannelInfoEntities.add(paymentChannelParentEntity);
        }
        if (CollectionUtils.isEmpty(paymentChannelInfoEntities)) {
            return new ArrayList<>(0);
        }
        return paymentChannelInfoEntities;
    }

    @Transactional(rollbackFor = Exception.class)
    public void addParentChannelInfo(PaymentChannelParentAddForm addForm) {
        RequestEmployee requestUser = AdminRequestUtil.getRequestUser();

        String redisKey = AdminRedisKeyConst.PAYMENT_CHANNEL_INFO_ADD_UPDATE_LOCK + addForm.getMerCode();
        boolean lock = RedisLockUtil.tryLock(redisKey);
        if (!lock) {
            throw new BusinessException(UserErrorCode.REPEAT_SUBMIT);
        }
        try {
            // 主商户只有管理员权限才可以添加
            if (!requestUser.getAdministratorFlag()) {
                throw new BusinessException(UserErrorCode.NO_PERMISSION);
            }

            DepartmentEntity departmentEntity = new DepartmentEntity();
            departmentEntity.setName(addForm.getMerName());
            departmentEntity.setManagerId(requestUser.getUserId());
            departmentEntity.setParentId(1L);
            departmentEntity.setSort(0);
            departmentDao.insert(departmentEntity);
            departmentCacheManager.clearCache();

            PaymentChannelParentEntity paymentChannelParentEntity = SmartBeanUtil.copy(addForm, PaymentChannelParentEntity.class);
            paymentChannelParentEntity.setDepartmentId(departmentEntity.getDepartmentId());
            paymentChannelParentManager.save(paymentChannelParentEntity);
        } finally {
            RedisLockUtil.unlock(redisKey);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void updateParentChannelInfo(PaymentChannelParentUpdateForm updateForm) {
        String redisKey = AdminRedisKeyConst.PAYMENT_CHANNEL_INFO_ADD_UPDATE_LOCK + updateForm.getId();
        boolean lock = RedisLockUtil.tryLock(redisKey);
        if (!lock) {
            throw new BusinessException(UserErrorCode.REPEAT_SUBMIT);
        }
        try {
            PaymentChannelParentEntity infoEntity = paymentChannelParentManager.getById(updateForm.getId());
            if (infoEntity == null) {
                throw new BusinessException(UserErrorCode.DATA_NOT_EXIST);
            }
            // 可以编辑所属部门商户信息
            RequestEmployee requestUser = AdminRequestUtil.getRequestUser();
            List<Long> departmentIds = departmentCacheManager.getDepartmentSelfAndChildren(requestUser.getDepartmentId());
            if (!requestUser.getAdministratorFlag() && !departmentIds.contains(infoEntity.getDepartmentId())) {
                throw new BusinessException(UserErrorCode.NO_PERMISSION);
            }
            infoEntity.setMerName(updateForm.getMerName());
            infoEntity.setStatus(updateForm.getStatus());
            infoEntity.setUpdateTime(LocalDateTime.now());
            paymentChannelParentManager.updateById(infoEntity);

            // 更新部门名称
            DepartmentEntity entity = departmentDao.selectById(infoEntity.getDepartmentId());
            entity.setName(updateForm.getMerName());
            departmentDao.updateById(entity);
            departmentCacheManager.clearCache();

        } finally {
            RedisLockUtil.unlock(redisKey);
        }
    }

    public List<PaymentChannelInfoVO> queryPaymentInfoList() {
        RequestEmployee requestUser = AdminRequestUtil.getRequestUser();
        List<RoleDataScopeEntity> roleDataScopeEntityList = roleEmployeeManager.getRoleDataScope(requestUser.getUserId());
        boolean dataScopeDepartment = roleDataScopeEntityList.stream().anyMatch(v -> DataScopeViewTypeEnum.DEPARTMENT.equalsValue(v.getViewType()));

        List<PaymentChannelInfoEntity> paymentChannelInfoEntities;
        if (requestUser.getAdministratorFlag() || !dataScopeDepartment) {
            paymentChannelInfoEntities = paymentChannelInfoManager.list();
        } else {
            List<Long> departmentIds = departmentCacheManager.getDepartmentSelfAndChildren(requestUser.getDepartmentId());
            paymentChannelInfoEntities = paymentChannelInfoManager.getInfoByDepartmentIds(departmentIds);
        }
        if (CollectionUtils.isEmpty(paymentChannelInfoEntities)) {
            return new ArrayList<>(0);
        }
        if (!requestUser.getAdministratorFlag()) {
            paymentChannelInfoEntities.forEach(v -> v.setPaymentScale(-1));
        }

        List<PaymentChannelInfoVO> paymentChannelInfoVOS = SmartBeanUtil.copyList(paymentChannelInfoEntities, PaymentChannelInfoVO.class);

        List<Long> departmentIds = paymentChannelInfoEntities.stream().map(PaymentChannelInfoEntity::getDepartmentId).collect(Collectors.toList());
        List<PaymentChannelBusinessCfgEntity> businessCfgEntities = paymentChannelInfoManager.getBusinessCfgByDepartmentIds(departmentIds);
        Map<Long, PaymentChannelBusinessCfgEntity> businessCfgEntityMap = businessCfgEntities.stream().collect(Collectors.toMap(PaymentChannelBusinessCfgEntity::getDepartmentId, v -> v));

        List<DepartmentEntity> departmentEntities = departmentCacheManager.getDepartmentListByIds(departmentIds);
        Map<Long, DepartmentEntity> departmentMap = departmentEntities.stream().collect(Collectors.toMap(DepartmentEntity::getDepartmentId, v -> v));

        paymentChannelInfoVOS.forEach(v -> {
            PaymentChannelBusinessCfgEntity businessCfg = businessCfgEntityMap.get(v.getDepartmentId());

            v.setParentDepartmentId(departmentMap.get(v.getDepartmentId()).getParentId());
            v.setAmountMin(businessCfg.getAmountMin());
            v.setAmountMax(businessCfg.getAmountMax());
            if (requestUser.getAdministratorFlag()) {
                v.setPlatformAward(businessCfg.getPlatformAward());
                v.setPlatformBrokerage(businessCfg.getPlatformBrokerage());
                v.setMerAward(businessCfg.getMerAward());
                v.setMerBrokerage(businessCfg.getMerBrokerage());
            } else {
                v.setPlatformAward(null);
                v.setPlatformBrokerage(null);
                v.setMerAward(null);
                v.setMerBrokerage(null);
            }
        });
        return paymentChannelInfoVOS;
    }

    @Transactional(rollbackFor = Exception.class)
    public void addChannelInfo(PaymentChannelInfoAddForm addForm) {
        RequestEmployee requestUser = AdminRequestUtil.getRequestUser();

        String redisKey = AdminRedisKeyConst.PAYMENT_CHANNEL_INFO_ADD_UPDATE_LOCK + addForm.getMerCode();
        boolean lock = RedisLockUtil.tryLock(redisKey);
        if (!lock) {
            throw new BusinessException(UserErrorCode.REPEAT_SUBMIT);
        }
        try {
            List<Long> departmentIds = departmentCacheManager.getDepartmentSelfAndChildren(requestUser.getDepartmentId());
            if (!requestUser.getAdministratorFlag() && !departmentIds.contains(addForm.getParentDepartmentId())) {
                throw new BusinessException(UserErrorCode.NO_PERMISSION);
            }

            DepartmentEntity departmentEntity = new DepartmentEntity();
            departmentEntity.setName(addForm.getMerName());
            departmentEntity.setManagerId(requestUser.getUserId());
            departmentEntity.setParentId(addForm.getParentDepartmentId());
            departmentEntity.setSort(0);
            departmentDao.insert(departmentEntity);
            departmentCacheManager.clearCache();

            PaymentChannelInfoEntity paymentChannelInfoEntity = SmartBeanUtil.copy(addForm, PaymentChannelInfoEntity.class);
            paymentChannelInfoEntity.setDepartmentId(departmentEntity.getDepartmentId());
            paymentChannelInfoManager.save(paymentChannelInfoEntity);

            paymentChannelInfoManager.saveBusinessCfg(addForm, departmentEntity.getDepartmentId());

        } finally {
            RedisLockUtil.unlock(redisKey);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateChannelInfo(PaymentChannelInfoUpdateForm updateForm) {
        RequestEmployee requestUser = AdminRequestUtil.getRequestUser();
        String redisKey = AdminRedisKeyConst.PAYMENT_CHANNEL_INFO_ADD_UPDATE_LOCK + updateForm.getId();
        boolean lock = RedisLockUtil.tryLock(redisKey);
        if (!lock) {
            throw new BusinessException(UserErrorCode.REPEAT_SUBMIT);
        }
        try {
            PaymentChannelInfoEntity infoEntity = paymentChannelInfoManager.getById(updateForm.getId());
            if (infoEntity == null) {
                throw new BusinessException(UserErrorCode.DATA_NOT_EXIST);
            }
            List<Long> departmentIds = departmentCacheManager.getDepartmentSelfAndChildren(requestUser.getDepartmentId());
            if (!requestUser.getAdministratorFlag() && !departmentIds.contains(infoEntity.getDepartmentId())) {
                throw new BusinessException(UserErrorCode.NO_PERMISSION);
            }

            infoEntity.setMerName(updateForm.getMerName());
            infoEntity.setPhone(updateForm.getPhone());
            // 只有超管可以修改此项
            if (requestUser.getAdministratorFlag()) {
                //判断routeType是否已经从【百分比】更换为【轮询】，或者从【轮询】更换为【百分比】
                if (infoEntity.getRouteType() != updateForm.getRouteType()) {
                    if (updateForm.getRouteType() == 1) {
                        // 从【轮询】更换为【百分比】
                        // 需要将t_payment_channel_pay_info_pollinglist所有记录调整为失效。
                        paymentChannelPayInfoPollingListManager.disablePollingListByDeparmentId(infoEntity.getDepartmentId());

                    } else if (updateForm.getRouteType() == 2) {
                        // 从【百分比】更换为【轮询】
                        // 需要将所有相关的t_payment_channel_pay_info_cfg记录插入t_payment_channel_pay_info_pollinglist
                        paymentChannelPayInfoPollingListManager.initDataByDepartmentId(infoEntity.getDepartmentId());

                    } else {
                        throw new BusinessException(UserErrorCode.PARAM_ERROR);
                    }
                    infoEntity.setRouteType(updateForm.getRouteType());
                }

                infoEntity.setPaymentScale(updateForm.getPaymentScale());
                infoEntity.setBlackList(updateForm.getBlackList());
            }
            infoEntity.setPaymentCount(updateForm.getPaymentCount());
            infoEntity.setPaymentLimit(updateForm.getPaymentLimit());
            infoEntity.setStatus(updateForm.getStatus());
            infoEntity.setUpdateTime(LocalDateTime.now());
            paymentChannelInfoManager.updateById(infoEntity);

            // 更新部门名称
            DepartmentEntity entity = departmentDao.selectById(infoEntity.getDepartmentId());
            entity.setName(updateForm.getMerName());
            departmentDao.updateById(entity);
            departmentCacheManager.clearCache();

            List<PaymentChannelBusinessCfgEntity> businessCfgEntities = paymentChannelInfoManager.getBusinessCfgByDepartmentId(infoEntity.getDepartmentId());
            if (!CollectionUtils.isEmpty(businessCfgEntities)) {
                PaymentChannelBusinessCfgEntity businessCfg = businessCfgEntities.get(0);
                businessCfg.setAmountMin(updateForm.getAmountMin());
                businessCfg.setAmountMax(updateForm.getAmountMax());
                if (requestUser.getAdministratorFlag()) {
                    businessCfg.setPlatformAward(updateForm.getPlatformAward());
                    businessCfg.setPlatformBrokerage(updateForm.getPlatformBrokerage());
                    businessCfg.setMerAward(updateForm.getMerAward());
                    businessCfg.setMerBrokerage(updateForm.getMerBrokerage());
                }
                businessCfg.setUpdateTime(LocalDateTime.now());
                paymentChannelInfoManager.updateBusinessCfg(businessCfg);
            }

        } finally {
            RedisLockUtil.unlock(redisKey);
        }
    }


    public PaymentChannelDetailVO getChannelDetail(PaymentChannelDetailForm channelDetailForm) {

        RequestEmployee requestUser = AdminRequestUtil.getRequestUser();
        List<RoleDataScopeEntity> roleDataScopeEntityList = roleEmployeeManager.getRoleDataScope(requestUser.getUserId());
        boolean dataScopeDepartment = roleDataScopeEntityList.stream().anyMatch(v -> DataScopeViewTypeEnum.DEPARTMENT.equalsValue(v.getViewType()));

        Long departmentId = requestUser.getDepartmentId();
        if (requestUser.getAdministratorFlag() || !dataScopeDepartment) {
            departmentId = channelDetailForm.getDepartmentId();
        }
        if (departmentId == null) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR);
        }
        List<Long> departmentIds = departmentCacheManager.getDepartmentSelfAndChildren(requestUser.getDepartmentId());
        if (!requestUser.getAdministratorFlag() && !departmentIds.contains(departmentId)) {
            throw new BusinessException(UserErrorCode.NO_PERMISSION);
        }
        PaymentChannelInfoEntity channelInfo = paymentChannelInfoManager.getInfoByDepartmentId(departmentId);
        if (channelInfo == null) {
            throw new BusinessException(UserErrorCode.DATA_NOT_EXIST);
        }
        PaymentChannelDetailVO detailVO = SmartBeanUtil.copy(channelInfo, PaymentChannelDetailVO.class);

        // 获取渠道业务范围
        List<PaymentChannelBusinessCfgEntity> businessCfgEntities = paymentChannelInfoManager.getBusinessCfgByDepartmentId(departmentId);
        List<PaymentChannelBusinessVO> businessList = SmartBeanUtil.copyList(businessCfgEntities, PaymentChannelBusinessVO.class);
        detailVO.setBusinessList(businessList);
        return detailVO;
    }

    public PageResult<PaymentChannelPayInfoVO> queryChannelPayinfoList(PaymentChannelPayInfoQueryForm queryForm) {
        RequestEmployee requestUser = AdminRequestUtil.getRequestUser();
        List<RoleDataScopeEntity> roleDataScopeEntityList = roleEmployeeManager.getRoleDataScope(requestUser.getUserId());
        boolean dataScopeDepartment = roleDataScopeEntityList.stream().anyMatch(v -> DataScopeViewTypeEnum.DEPARTMENT.equalsValue(v.getViewType()));

        queryForm.setAdministratorFlag(requestUser.getAdministratorFlag() || !dataScopeDepartment);

        if ((requestUser.getAdministratorFlag() && queryForm.getDepartmentId() != null) || !queryForm.getAdministratorFlag()) {
            long departmentId = queryForm.getDepartmentId() != null ? queryForm.getDepartmentId() : requestUser.getDepartmentId();
            List<Long> departmentIds = departmentCacheManager.getDepartmentSelfAndChildren(departmentId);
            if (!requestUser.getAdministratorFlag() && !departmentIds.contains(departmentId)) {
                throw new BusinessException(UserErrorCode.NO_PERMISSION);
            }
            queryForm.setDepartmentIds(departmentIds);
            queryForm.setAdministratorFlag(false);
        }

        // 获取支付方式列表
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<PaymentChannelPayInfoCfgVO> channelPayInfoCfgEntityList = paymentChannelInfoManager.getPayInfoCfgPage(page, queryForm);
        List<PaymentChannelPayInfoVO> payInfoVOList = SmartBeanUtil.copyList(channelPayInfoCfgEntityList, PaymentChannelPayInfoVO.class);
        if (CollectionUtils.isEmpty(payInfoVOList)) {
            return SmartPageUtil.convert2PageResult(page, payInfoVOList);
        }

        // 获取支付方式的业务范围
        List<Long> payInfoIdList = payInfoVOList.stream().map(PaymentChannelPayInfoVO::getId).collect(Collectors.toList());
        List<PaymentChannelPayInfoBusinessCfg> payInfoBusinessCfgList = paymentChannelInfoManager.getPayInfoBusinessCfgByPayInfoIds(payInfoIdList);
        Map<Long, List<PaymentChannelPayInfoBusinessCfg>> payInfoBusinessCfgMap = payInfoBusinessCfgList.stream().collect(Collectors.groupingBy(PaymentChannelPayInfoBusinessCfg::getPayInfoId));

        for (PaymentChannelPayInfoVO payInfoVO : payInfoVOList) {
            List<PaymentChannelPayInfoBusinessCfg> businessCfgs = payInfoBusinessCfgMap.get(payInfoVO.getId());
            payInfoVO.setAmountMin(businessCfgs.get(0).getAmountMin());
            payInfoVO.setAmountMax(businessCfgs.get(0).getAmountMax());

        }
        return SmartPageUtil.convert2PageResult(page, payInfoVOList);
    }



    public void addPayInfo(Long fileId, String type, String username, String bankInfo, String bankName, String bankNo,
                           Integer paymentScale, BigDecimal paymentLimit,Integer paymentCount, String workTime,
                           BigDecimal amountMin, BigDecimal amountMax, Integer xinjiang, Long departmentId,
                           String currency, String country) {

        RequestEmployee requestUser = AdminRequestUtil.getRequestUser();
        if (departmentId == null) {
            departmentId = requestUser.getDepartmentId();
        } else if (!requestUser.getAdministratorFlag() && !departmentId.equals(requestUser.getDepartmentId())) {
            throw new BusinessException(UserErrorCode.NO_PERMISSION);
        }
        List<Long> departmentIds = departmentCacheManager.getDepartmentSelfAndChildren(requestUser.getDepartmentId());
        if (!requestUser.getAdministratorFlag() && !departmentIds.contains(departmentId)) {
            throw new BusinessException(UserErrorCode.NO_PERMISSION);
        }

        if ("bank".equalsIgnoreCase(type)) {
            if (StringUtils.isAnyEmpty(bankInfo, bankName, bankNo)) {
                throw new BusinessException("类型为bank，请填写完整银行卡信息");
            }
        } else if (fileId == null) {
            throw new BusinessException("请上传收款码");
        }

        if (StringUtils.isEmpty(workTime)) {
            workTime = "00:00-24:00";
        } else {
            String regex = "\\b([01]?[0-9]|2[0-3]):[0-5][0-9]-([01]?[0-9]|2[0-4]):[0-5][0-9]\\b";
            if (!workTime.matches(regex)) {
                throw new BusinessException("工作时间格式错误");
            }
        }
        String payUrl = "";
        if (fileId != null) {
            ResponseDTO<String> responseDTO = fileService.getFileUrlById(fileId);
            if (!responseDTO.getOk()) {
                throw new BusinessException(responseDTO.getMsg());
            }
            payUrl = responseDTO.getData();
        }

        PaymentChannelInfoEntity channelInfo = paymentChannelInfoManager.getInfoByDepartmentId(departmentId);
        if (channelInfo == null) {
            throw new BusinessException(UserErrorCode.DATA_NOT_EXIST);
        }

        PaymentChannelPayInfoCfgEntity payInfoCfgEntity = new PaymentChannelPayInfoCfgEntity();
        payInfoCfgEntity.setDepartmentId(departmentId);
        payInfoCfgEntity.setType(type);
        payInfoCfgEntity.setUsername(username);
        payInfoCfgEntity.setBankInfo(bankInfo);
        payInfoCfgEntity.setBankName(bankName);
        payInfoCfgEntity.setBankNo(bankNo);
        payInfoCfgEntity.setPaymentScale(paymentScale);
        payInfoCfgEntity.setPaymentLimit(paymentLimit);
        payInfoCfgEntity.setPaymentCount(paymentCount);
        payInfoCfgEntity.setWorkTime(workTime);
        payInfoCfgEntity.setCurrency(currency);
        payInfoCfgEntity.setCountry(country);
        payInfoCfgEntity.setBlackList(2);
        payInfoCfgEntity.setXinjiang(xinjiang == null ? 0 : xinjiang);
        payInfoCfgEntity.setStatus(1);
        payInfoCfgEntity.setPayUrl(payUrl);
        payInfoCfgEntity.setCreateTime(LocalDateTime.now());
        payInfoCfgEntity.setUpdateTime(LocalDateTime.now());
        channelPayInfoCfgDao.insert(payInfoCfgEntity);

        PaymentChannelPayInfoBusinessCfg businessCfg = new PaymentChannelPayInfoBusinessCfg();
        businessCfg.setPayInfoId(payInfoCfgEntity.getId());
        businessCfg.setAmountMin(amountMin);
        businessCfg.setAmountMax(amountMax);
        businessCfg.setCreateTime(LocalDateTime.now());
        businessCfg.setUpdateTime(LocalDateTime.now());
        channelPayInfoBusinessCfgDao.insert(businessCfg);

        if (channelInfo.getRouteType() == 2) {
            // 【轮询】
            paymentChannelPayInfoPollingListManager.insertPollingRecord(payInfoCfgEntity);
        }
    }

    public void updatePayInfo(Long payInfoId, BigDecimal amountMin, BigDecimal amountMax, Integer status,
                              Long fileId, Integer paymentScale, BigDecimal paymentLimit, Integer paymentCount,
                              String workTime, String username, String bankInfo, String bankName, String bankNo,
                              Integer xinjiang, Long departmentId, String currency, String country) {
        if (amountMin == null && amountMax == null && status == null) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR);
        }
        RequestEmployee requestUser = AdminRequestUtil.getRequestUser();
        if (departmentId == null) {
            departmentId = requestUser.getDepartmentId();
        }
        PaymentChannelInfoEntity channelInfo = paymentChannelInfoManager.getInfoByDepartmentId(departmentId);
        if (channelInfo == null) {
            throw new BusinessException(UserErrorCode.DATA_NOT_EXIST);
        }
        List<Long> departmentIds = departmentCacheManager.getDepartmentSelfAndChildren(requestUser.getDepartmentId());
        if (!requestUser.getAdministratorFlag() && !departmentIds.contains(departmentId)) {
            throw new BusinessException(UserErrorCode.NO_PERMISSION);
        }
        PaymentChannelPayInfoCfgEntity payInfoCfgEntity = channelPayInfoCfgDao.selectById(payInfoId);
        if (payInfoCfgEntity == null) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR);
        }
        if (!requestUser.getAdministratorFlag() && !payInfoCfgEntity.getDepartmentId().equals(departmentId)) {
            throw new BusinessException(UserErrorCode.NO_PERMISSION);
        }
        if (status != null) {
            payInfoCfgEntity.setStatus(status);
        }
        if (paymentScale != null) {
            payInfoCfgEntity.setPaymentScale(paymentScale);
        }
        if (paymentLimit != null) {
            payInfoCfgEntity.setPaymentLimit(paymentLimit);
        }
        if (paymentCount != null) {
            payInfoCfgEntity.setPaymentCount(paymentCount);
        }
        if (workTime != null) {
            String regex = "\\b([01]?[0-9]|2[0-3]):[0-5][0-9]-([01]?[0-9]|2[0-4]):[0-5][0-9]\\b";
            if (!workTime.matches(regex)) {
                throw new BusinessException("工作时间格式错误");
            }
            payInfoCfgEntity.setWorkTime(workTime);
        }
        if (username != null) {
            payInfoCfgEntity.setUsername(username);
        }
        if (currency != null) {
            payInfoCfgEntity.setCurrency(currency);
        }
        if (country != null) {
            payInfoCfgEntity.setCountry(country);
        }
        if (bankInfo != null) {
            payInfoCfgEntity.setBankInfo(bankInfo);
        }
        if (bankName != null) {
            payInfoCfgEntity.setBankName(bankName);
        }
        if (bankNo != null) {
            payInfoCfgEntity.setBankNo(bankNo);
        }
        if (xinjiang != null) {
            payInfoCfgEntity.setXinjiang(xinjiang);
        }
        if (fileId != null) {
            ResponseDTO<String> responseDTO = fileService.getFileUrlById(fileId);
            if (!responseDTO.getOk()) {
                throw new BusinessException(responseDTO.getMsg());
            }
            payInfoCfgEntity.setPayUrl(responseDTO.getData());
        }
        payInfoCfgEntity.setUpdateTime(LocalDateTime.now());
        channelPayInfoCfgDao.updateById(payInfoCfgEntity);

        LambdaQueryWrapper<PaymentChannelPayInfoBusinessCfg> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentChannelPayInfoBusinessCfg::getPayInfoId, payInfoId);
        List<PaymentChannelPayInfoBusinessCfg> businessCfgs = channelPayInfoBusinessCfgDao.selectList(queryWrapper);
        if (!businessCfgs.isEmpty()) {
            PaymentChannelPayInfoBusinessCfg businessCfg = businessCfgs.get(0);
            businessCfg.setAmountMin(amountMin);
            businessCfg.setAmountMax(amountMax);
            businessCfg.setUpdateTime(LocalDateTime.now());
            channelPayInfoBusinessCfgDao.updateById(businessCfg);
        }

        if (channelInfo.getRouteType() == 2) {
            // 【轮询】
            paymentChannelPayInfoPollingListManager.updatePollingRecord(payInfoCfgEntity);
        }
    }

    public void restPollingRouteCount() {
        LambdaQueryWrapper<PaymentChannelInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentChannelInfoEntity::getStatus, 1);
        queryWrapper.eq(PaymentChannelInfoEntity::getRouteType, 2);
        List<PaymentChannelInfoEntity> list = paymentChannelInfoDao.selectList(queryWrapper);
        if (list == null || list.isEmpty()) return;
        list.forEach(item -> {
            paymentChannelPayInfoPollingListManager.restPollingRouteCountByDeparmentId(item.getDepartmentId());
        });
    }

    public List<PaymentChannelParentEntity> queryParentOptions() {

        RequestEmployee requestUser = AdminRequestUtil.getRequestUser();

        List<RoleDataScopeEntity> roleDataScopeEntityList = roleEmployeeManager.getRoleDataScope(requestUser.getUserId());
        boolean dataScopeDepartment = roleDataScopeEntityList.stream().anyMatch(v -> DataScopeViewTypeEnum.DEPARTMENT.equalsValue(v.getViewType()));

        DepartmentEntity departmentEntity = departmentCacheManager.getDepartmentById(requestUser.getDepartmentId());

        LambdaQueryWrapper<PaymentChannelParentEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (!requestUser.getAdministratorFlag() && dataScopeDepartment) {
            queryWrapper.in(PaymentChannelParentEntity::getDepartmentId, Arrays.asList(departmentEntity.getParentId(), requestUser.getDepartmentId()));
        }
        return paymentChannelParentManager.list(queryWrapper);
    }

    public List<PaymentChannelInfoVO> queryOptions() {
        RequestEmployee requestUser = AdminRequestUtil.getRequestUser();

        List<RoleDataScopeEntity> roleDataScopeEntityList = roleEmployeeManager.getRoleDataScope(requestUser.getUserId());
        boolean dataScopeDepartment = roleDataScopeEntityList.stream().anyMatch(v -> DataScopeViewTypeEnum.DEPARTMENT.equalsValue(v.getViewType()));

        List<Long> departmentIds = departmentCacheManager.getDepartmentSelfAndChildren(requestUser.getDepartmentId());
        Integer administratorFlag = requestUser.getAdministratorFlag() || !dataScopeDepartment ? 1 : 0;

        return paymentChannelInfoDao.queryOptions(administratorFlag, departmentIds);
    }

    public List<String> queryCurrencyOptions() {
        RequestEmployee requestEmployee = (RequestEmployee) SmartRequestUtil.getRequestUser();
        return paymentChannelInfoManager.queryCurrencyOptions(requestEmployee.getDepartmentId(), requestEmployee.getAdministratorFlag());
    }

}