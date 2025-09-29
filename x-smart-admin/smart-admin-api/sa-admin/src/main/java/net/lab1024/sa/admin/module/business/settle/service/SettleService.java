package net.lab1024.sa.admin.module.business.settle.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.merchantmanage.service.MerchantApiService;
import net.lab1024.sa.admin.module.business.orderinfo.dao.OrderInfoDao;
import net.lab1024.sa.admin.module.business.orderinfo.domain.entity.OrderInfoEntity;
import net.lab1024.sa.admin.module.business.payment.channel.dao.PaymentChannelBusinessCfgDao;
import net.lab1024.sa.admin.module.business.payment.channel.dao.PaymentChannelInfoDao;
import net.lab1024.sa.admin.module.business.payment.channel.dao.PaymentChannelParentDao;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelBusinessCfgEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelInfoEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelParentEntity;
import net.lab1024.sa.admin.module.business.settle.dao.PaymentSettleDao;
import net.lab1024.sa.admin.module.business.settle.dao.PaymentSettleDetailDao;
import net.lab1024.sa.admin.module.business.settle.dao.PaymentSettleParentDao;
import net.lab1024.sa.admin.module.business.settle.domain.entity.PaymentSettleDetailEntity;
import net.lab1024.sa.admin.module.business.settle.domain.entity.PaymentSettleEntity;
import net.lab1024.sa.admin.module.business.settle.domain.entity.PaymentSettleParentEntity;
import net.lab1024.sa.admin.module.business.settle.domain.form.SettleDetailQueryForm;
import net.lab1024.sa.admin.module.business.settle.domain.form.SettleExportQueryForm;
import net.lab1024.sa.admin.module.business.settle.domain.form.SettleQueryForm;
import net.lab1024.sa.admin.module.business.settle.domain.form.SettleSetExchangeRateForm;
import net.lab1024.sa.admin.module.business.settle.domain.vo.SettleDetailVO;
import net.lab1024.sa.admin.module.business.settle.domain.vo.SettleVO;
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
import net.lab1024.sa.base.common.util.SmartLocalDateUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.util.SmartStringUtil;
import net.lab1024.sa.base.module.support.file.service.FileService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 结算表 Service
 */
@Slf4j
@Service
public class SettleService {

    @Resource
    private PaymentSettleDao paymentSettleDao;

    @Resource
    private PaymentChannelInfoDao paymentChannelInfoDao;

    @Resource
    private PaymentSettleDetailDao paymentSettleDetailDao;

    @Resource
    private OrderInfoDao orderInfoDao;

    @Resource
    private PaymentChannelBusinessCfgDao paymentChannelBusinessCfgDao;

    @Resource
    private FileService fileService;
    @Resource
    private RoleEmployeeManager roleEmployeeManager;

    @Resource
    private MerchantApiService merchantApiService;

    @Resource
    private DepartmentDao departmentDao;
    @Resource
    private PaymentChannelParentDao paymentChannelParentDao;
    @Resource
    private PaymentSettleParentDao paymentSettleParentDao;
    @Resource
    private DepartmentCacheManager departmentCacheManager;

    /**
     * 分页查询-结算
     *
     * @param queryForm
     * @return
     */
    public PageResult<SettleVO> queryPageBySettle(SettleQueryForm queryForm) {

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

        log.info("queryPageBySettle:{}", JSON.toJSON(queryForm));

        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        page.addOrder(new OrderItem("trade_date", false));
        List<SettleVO> list = paymentSettleDao.queryPage(page, queryForm);
        PageResult<SettleVO> pageResult = SmartPageUtil.convert2PageResult(page, list);

        if (!CollectionUtils.isEmpty(pageResult.getList())) {
            List<SettleVO> collect = pageResult.getList().stream().map(item -> {
                if (!SmartStringUtil.isEmpty(item.getSettleUrl())) {
                    String[] fileIds = item.getSettleUrl().split(",");
                    List<String> settleUrls = new ArrayList<>();
                    for (String fileId : fileIds) {
                        ResponseDTO<String> responseDTO = fileService.getFileUrlById(Long.valueOf(fileId));
                        if (!responseDTO.getOk()) {
                            throw new BusinessException(responseDTO.getMsg());
                        }
                        settleUrls.add(responseDTO.getData());
                    }
                    item.setSettleUrl(String.join(";", settleUrls));
                }
                return item;
            }).collect(Collectors.toList());
            pageResult.setList(collect);
        }
        return pageResult;
    }

    public List<SettleVO> getExcelExportData(SettleExportQueryForm queryForm) {
        RequestEmployee requestUser = AdminRequestUtil.getRequestUser();
        List<RoleDataScopeEntity> roleDataScopeEntityList = roleEmployeeManager.getRoleDataScope(requestUser.getUserId());
        boolean dataScopeDepartment = roleDataScopeEntityList.stream().anyMatch(v -> DataScopeViewTypeEnum.DEPARTMENT.equalsValue(v.getViewType()));

        queryForm.setAdministratorFlag(requestUser.getAdministratorFlag() || !dataScopeDepartment);
        if (!queryForm.getAdministratorFlag()) {
            queryForm.setDepartmentId(requestUser.getDepartmentId());
        }
        log.info("getExcelExportData:{}", JSON.toJSON(queryForm));

        List<SettleVO> excelExportData = paymentSettleDao.getExcelExportData(queryForm);
        List<SettleVO> result = excelExportData.stream().peek(item -> {
            if (1 == item.getTradeType()) {
                item.setTradeTypeStr("支付单");
            } else {
                item.setTradeTypeStr("兑付单");
            }
            //1 结算状态 1:待结算，2:结算中，3:待确认，4:已结算
            if (1 == item.getSettleStatus()) {
                item.setSettleStatusStr("待结算");
            } else if (2 == item.getSettleStatus()) {
                item.setSettleStatusStr("结算中");
            } else if (4 == item.getSettleStatus()) {
                item.setSettleStatusStr("已结算");
            }
        }).collect(Collectors.toList());
        return result;
    }

    /**
     * 分页查询-结算明细
     *
     * @param queryForm
     * @return
     */
    public PageResult<SettleDetailVO> queryPageBySettleDetail(SettleDetailQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<SettleDetailVO> list = paymentSettleDetailDao.queryPage(page, queryForm);
        PageResult<SettleDetailVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }


    /**
     * 设置结算状态为已结算
     *
     * @param settleId
     * @return
     */
    public ResponseDTO<String> confirmPayment(Long settleId, List<String> fileIds) {
        PaymentSettleEntity settleEntity = paymentSettleDao.selectById(settleId);
        settleEntity.setSettleStatus(4);
        settleEntity.setSettleUrl(String.join(",", fileIds));
        paymentSettleDao.updateById(settleEntity);

        LambdaQueryWrapper<PaymentChannelInfoEntity> channelWrapper = new LambdaQueryWrapper<>();
        channelWrapper.eq(PaymentChannelInfoEntity::getDepartmentId, settleEntity.getDepartmentId());
        PaymentChannelInfoEntity paymentChannelInfo =  paymentChannelInfoDao.selectOne(channelWrapper);

        DepartmentEntity departmentEntity = departmentDao.selectById(paymentChannelInfo.getDepartmentId());

        // 汇总结算单
        collectBill(settleEntity, departmentEntity.getParentId());

//        LambdaQueryWrapper<PaymentChannelParentEntity> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(PaymentChannelParentEntity::getDepartmentId, departmentEntity.getParentId());
//        PaymentChannelParentEntity parentEntity = paymentChannelParentDao.selectOne(wrapper);
//        // 如果是非自营商户，则调用子商户的接口
//        if (parentEntity.getIsOwned().equals(0)) {
//            ResponseDTO<String> stringResponseDTO = merchantApiService.orderSettleUrl(settleEntity.getId(), fileIds, settleEntity.getTradeDate(), parentEntity.getMerCode());
//            if (stringResponseDTO.getOk()) {
//                paymentSettleDao.updateById(settleEntity);
//            } else {
//                return ResponseDTO.error(UserErrorCode.ORDER_SETTLE_CONFIRM_ERROR);
//            }
//        }
        return ResponseDTO.ok();
    }

    private void collectBill(PaymentSettleEntity settleEntity, Long departmentParentId) {
        List<Long> departmentIds = departmentCacheManager.getDepartmentSelfAndChildren(departmentParentId);

        LambdaQueryWrapper<PaymentSettleEntity> paymentSettleEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
        paymentSettleEntityLambdaQueryWrapper.ne(PaymentSettleEntity::getSettleStatus, 4);
        paymentSettleEntityLambdaQueryWrapper.eq(PaymentSettleEntity::getTradeDate, settleEntity.getTradeDate());
        paymentSettleEntityLambdaQueryWrapper.eq(PaymentSettleEntity::getTradeType, settleEntity.getTradeType());
        paymentSettleEntityLambdaQueryWrapper.in(PaymentSettleEntity::getDepartmentId, departmentIds);
        long count = paymentSettleDao.selectCount(paymentSettleEntityLambdaQueryWrapper);
        if (count == 0) {
            LambdaQueryWrapper<PaymentChannelParentEntity> parentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            parentLambdaQueryWrapper.eq(PaymentChannelParentEntity::getDepartmentId, departmentParentId);
            PaymentChannelParentEntity parentEntity = paymentChannelParentDao.selectOne(parentLambdaQueryWrapper);

            // 子商户结算单汇总到主商户
            LambdaQueryWrapper<PaymentSettleEntity> settleWrapper = new LambdaQueryWrapper<>();
            settleWrapper.eq(PaymentSettleEntity::getSettleStatus, 4);
            settleWrapper.eq(PaymentSettleEntity::getTradeDate, settleEntity.getTradeDate());
            settleWrapper.eq(PaymentSettleEntity::getTradeType, settleEntity.getTradeType());
            settleWrapper.in(PaymentSettleEntity::getDepartmentId, departmentIds);
            List<PaymentSettleEntity> paymentSettleEntities = paymentSettleDao.selectList(settleWrapper);


            LambdaQueryWrapper<PaymentSettleParentEntity> parentEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
            parentEntityLambdaQueryWrapper.eq(PaymentSettleParentEntity::getDepartmentId, parentEntity.getDepartmentId());
            parentEntityLambdaQueryWrapper.eq(PaymentSettleParentEntity::getTradeDate, settleEntity.getTradeDate());
            parentEntityLambdaQueryWrapper.eq(PaymentSettleParentEntity::getTradeType, settleEntity.getTradeType());
            PaymentSettleParentEntity parentSettleEntity = paymentSettleParentDao.selectOne(parentEntityLambdaQueryWrapper);
            if (parentSettleEntity == null) {
                parentSettleEntity = new PaymentSettleParentEntity();
                parentSettleEntity.setDepartmentId(parentEntity.getDepartmentId());
                parentSettleEntity.setMerCode(parentEntity.getMerCode());
                parentSettleEntity.setMerName(parentEntity.getMerName());
                parentSettleEntity.setTradeDate(settleEntity.getTradeDate());
                parentSettleEntity.setTradeType(settleEntity.getTradeType());
                parentSettleEntity.setCurrency(settleEntity.getCurrency());
                parentSettleEntity.setCreateTime(LocalDateTime.now());
            }

            parentSettleEntity.setSettleStatus(4);
            parentSettleEntity.setExchangeRate(settleEntity.getExchangeRate());
            parentSettleEntity.setAmount(paymentSettleEntities.stream().map(PaymentSettleEntity::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            parentSettleEntity.setCountAmount(paymentSettleEntities.stream().map(PaymentSettleEntity::getCountAmount).reduce(0, Integer::sum));
            parentSettleEntity.setPlatformAward(paymentSettleEntities.stream().map(PaymentSettleEntity::getPlatformAward).reduce(BigDecimal.ZERO, BigDecimal::add));
            parentSettleEntity.setPlatformShouldSettled(paymentSettleEntities.stream().map(PaymentSettleEntity::getPlatformShouldSettled).reduce(BigDecimal.ZERO, BigDecimal::add));
            parentSettleEntity.setPlatformBrokerage(paymentSettleEntities.stream().map(PaymentSettleEntity::getPlatformBrokerage).reduce(BigDecimal.ZERO, BigDecimal::add));
            parentSettleEntity.setMerAward(paymentSettleEntities.stream().map(PaymentSettleEntity::getMerAward).reduce(BigDecimal.ZERO, BigDecimal::add));
            parentSettleEntity.setMerShouldSettled(paymentSettleEntities.stream().map(PaymentSettleEntity::getMerShouldSettled).reduce(BigDecimal.ZERO, BigDecimal::add));
            parentSettleEntity.setMerBrokerage(paymentSettleEntities.stream().map(PaymentSettleEntity::getMerBrokerage).reduce(BigDecimal.ZERO, BigDecimal::add));

            String fileIds = paymentSettleEntities.stream().map(PaymentSettleEntity::getSettleUrl).collect(Collectors.joining(","));
            parentSettleEntity.setSettleUrl(fileIds);
            parentSettleEntity.setUpdateTime(LocalDateTime.now());
            if (parentSettleEntity.getId() == null) {
                paymentSettleParentDao.insert(parentSettleEntity);
            } else {
                paymentSettleParentDao.updateById(parentSettleEntity);
            }
            // 报告给支付中心
            merchantApiService.orderSettlePush(parentSettleEntity);
        }
    }




    /**
     * 设置汇率，把结算状态改为结算中
     *
     * @param settleSetExchangeRateForm
     * @return
     */
    @Transactional
    public ResponseDTO<String> setExchangeRate(SettleSetExchangeRateForm settleSetExchangeRateForm) {
        BigDecimal exchangeRate = settleSetExchangeRateForm.getExchangeRate();

        PaymentSettleEntity settleEntity = paymentSettleDao.selectById(settleSetExchangeRateForm.getSettleId());
        settleEntity.setExchangeRate(exchangeRate);
        settleEntity.setSettleStatus(2);


        AtomicReference<PaymentChannelBusinessCfgEntity> businessCfg = new AtomicReference<>(new PaymentChannelBusinessCfgEntity());
        //1.设置结算明细表数据
        QueryWrapper<PaymentSettleDetailEntity> queryWrapperSettleDetail = new QueryWrapper<>();
        queryWrapperSettleDetail.eq("settle_id", settleSetExchangeRateForm.getSettleId());
        List<PaymentSettleDetailEntity> paymentSettleDetailEntities = paymentSettleDetailDao.selectList(queryWrapperSettleDetail);
        final BigDecimal[] merShouldSettled = {new BigDecimal(0)};
        final BigDecimal[] platformShouldSettled = {new BigDecimal(0)};
        final BigDecimal[] merBrokerage = {new BigDecimal(0)};
        final BigDecimal[] platformBrokerage = {new BigDecimal(0)};
        final BigDecimal[] merAward = {new BigDecimal(0)};
        final BigDecimal[] platformAward = {new BigDecimal(0)};
        paymentSettleDetailEntities.stream().forEach(item -> {

//            QueryWrapper<PaymentChannelBusinessCfgEntity> queryWrapperChannelBusiness = new QueryWrapper<>();
//            queryWrapperChannelBusiness.eq("department_id", settleEntity.getDepartmentId());
//            List<PaymentChannelBusinessCfgEntity> paymentChannelBusinessCfgEntities = paymentChannelBusinessCfgDao.selectList(queryWrapperChannelBusiness);
//            paymentChannelBusinessCfgEntities.forEach(temp -> {
//                int min = item.getAmount().compareTo(temp.getAmountMin());
//                int max = item.getAmount().compareTo(temp.getAmountMax());
//                if (min >= 0 && max <= 0) {
//                    businessCfg.set(temp);
//                }
//            });

            PaymentChannelBusinessCfgEntity businessCfgDB = paymentChannelBusinessCfgDao.selectById(item.getPaymentChannelBusinessId());
            businessCfg.set(businessCfgDB);

            //通过汇率计算相应值
            BigDecimal divideDetail = item.getAmount().divide(exchangeRate, 2, RoundingMode.HALF_UP);
            //商户应结=交易额/汇率-（交易额/汇率*平台佣金+平台奖励）
            BigDecimal merShouldSettledDetail = divideDetail.subtract(divideDetail.multiply(businessCfg.get().getMerBrokerage()).add(businessCfg.get().getMerAward()));
            item.setMerShouldSettled(merShouldSettledDetail);
            BigDecimal platformShouldSettledDetail = divideDetail.subtract(divideDetail.multiply(businessCfg.get().getPlatformBrokerage()).add(businessCfg.get().getPlatformAward()));
            item.setPlatformShouldSettled(platformShouldSettledDetail);
            item.setMerAward(businessCfg.get().getMerAward());
            item.setPlatformAward(businessCfg.get().getPlatformAward());
            //佣金=交易额/汇率*佣金比例
            BigDecimal merBrokerageDetail = divideDetail.multiply(businessCfg.get().getMerBrokerage());
            item.setMerBrokerage(merBrokerageDetail);
            BigDecimal platformBrokerageDetail = divideDetail.multiply(businessCfg.get().getPlatformBrokerage());
            item.setPlatformBrokerage(platformBrokerageDetail);
            merShouldSettled[0] = merShouldSettled[0].add(merShouldSettledDetail);
            platformShouldSettled[0] = platformShouldSettled[0].add(platformShouldSettledDetail);
            merBrokerage[0] = merBrokerage[0].add(merBrokerageDetail);
            platformBrokerage[0] = platformBrokerage[0].add(platformBrokerageDetail);
            merAward[0] = merAward[0].add(businessCfg.get().getMerAward());
            platformAward[0] = platformAward[0].add(businessCfg.get().getPlatformAward());
            paymentSettleDetailDao.updateById(item);
        });

        //2.设置结算表数据
        //通过汇率计算相应值
        //商户应结=交易额/汇率-（交易额/汇率*平台佣金+平台奖励）
        settleEntity.setMerShouldSettled(merShouldSettled[0]);
        //平台应结=交易额/汇率-（交易额/汇率*平台佣金+商户奖励）
        settleEntity.setPlatformShouldSettled(platformShouldSettled[0]);
        //佣金=交易额/汇率*佣金比例
        settleEntity.setMerBrokerage(merBrokerage[0]);
        settleEntity.setPlatformBrokerage(platformBrokerage[0]);
        //奖励从商户配置中取固定值
        settleEntity.setMerAward(merAward[0]);
        settleEntity.setPlatformAward(platformAward[0]);
        paymentSettleDao.updateById(settleEntity);
        return ResponseDTO.ok();
    }


    @Transactional
    public void deleteSettleItem(String date) {
        QueryWrapper<PaymentSettleEntity> settleWrapper = new QueryWrapper<>();
        settleWrapper.eq("trade_date", date);
        settleWrapper.ne("settle_status", 4);
        List<PaymentSettleEntity> paymentSettleEntities = paymentSettleDao.selectList(settleWrapper);
        if (!CollectionUtils.isEmpty(paymentSettleEntities)) {
            QueryWrapper<PaymentSettleEntity> settleDeleteWrapper = new QueryWrapper<>();
            settleDeleteWrapper.eq("trade_date", date);
            settleDeleteWrapper.ne("settle_status", 4);
            int delete = paymentSettleDao.delete(settleDeleteWrapper);
            if (delete > 0) {
                paymentSettleEntities.forEach(item -> {
                    QueryWrapper<PaymentSettleDetailEntity> settleDetailDeleteWrapper = new QueryWrapper<>();
                    settleDetailDeleteWrapper.eq("settle_id", item.getId());
                    paymentSettleDetailDao.delete(settleDetailDeleteWrapper);
                });
            }
        }

        QueryWrapper<PaymentSettleParentEntity> parentSettleWrapper = new QueryWrapper<>();
        parentSettleWrapper.eq("trade_date", date);
        parentSettleWrapper.ne("settle_status", 4);
        List<PaymentSettleParentEntity> parentSettleEntities = paymentSettleParentDao.selectList(parentSettleWrapper);
        if (!CollectionUtils.isEmpty(parentSettleEntities)) {
            QueryWrapper<PaymentSettleParentEntity> parentSettleDeleteWrapper = new QueryWrapper<>();
            parentSettleDeleteWrapper.eq("trade_date", date);
            parentSettleDeleteWrapper.ne("settle_status", 4);
            paymentSettleParentDao.delete(parentSettleDeleteWrapper);
        }

        this.settleTask(SmartLocalDateUtil.getNowStart(date), SmartLocalDateUtil.getNowEnd(date), date);
//        this.parentPaymentSettle(date);
    }

    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void settleTask() {
        log.info("settleTask----------------");
        settleTask(SmartLocalDateUtil.getPreviousStart(),SmartLocalDateUtil.getPreviousEnd(),null);
    }

    @Transactional
    public void settleTask(LocalDateTime startTime, LocalDateTime endTime, String settleDate) {
        Map<Long, Long> matchMap = new HashMap<>();
        QueryWrapper<OrderInfoEntity> queryWrapperOrder = new QueryWrapper<>();
        queryWrapperOrder.ge("finish_time", startTime)
                .le("finish_time", endTime)
                .eq("status", 2);
        List<OrderInfoEntity> orderInfoEntityList = orderInfoDao.selectList(queryWrapperOrder);


        // 按照 departmentId 进行分组，并对每组的金额进行求和
        Map<Long, Map<String, BigDecimal>> departmentCurrencySum = orderInfoEntityList.stream().
                collect(Collectors.groupingBy(OrderInfoEntity::getDepartmentId, Collectors.toMap(
                        OrderInfoEntity::getCurrency,
                        OrderInfoEntity::getAmount,
                        BigDecimal::add
                )));

        // 计算每组求和是由几条记录累加得到
        Map<Long, Map<String, Long>> departmentCurrencyCount = orderInfoEntityList.stream().
                collect(Collectors.groupingBy(OrderInfoEntity::getDepartmentId,
                        Collectors.groupingBy(OrderInfoEntity::getCurrency, Collectors.counting())));

        Map<Long, Map<String, Long>> map = new HashMap<>();
        //保存结算表
        departmentCurrencySum.forEach((departmentId, currencyMap) -> {
            Map<String, Long> cMap = new HashMap<>();

            Map<String, Long> countRecordsByCurrency = departmentCurrencyCount.get(departmentId);
            currencyMap.forEach((currency, totalAmount) -> {
                PaymentSettleEntity settleEntity = new PaymentSettleEntity();
                if (settleDate != null) {
                    settleEntity.setTradeDate(settleDate);
                    //过滤已经结算成功的，如果结算已经成功的商户就不需要进行现次插入了
                    QueryWrapper<PaymentSettleEntity> settleWrapper = new QueryWrapper<>();
                    settleWrapper.eq("department_id", departmentId);
                    settleWrapper.eq("trade_date", settleDate);
                    List<PaymentSettleEntity> paymentSettleEntities = paymentSettleDao.selectList(settleWrapper);
                    if (!CollectionUtils.isEmpty(paymentSettleEntities)) {
                        matchMap.put(departmentId, departmentId);
                        return;
                    }
                } else {
                    settleEntity.setTradeDate(SmartLocalDateUtil.getPreviousDate());
                }

                settleEntity.setCurrency(currency);
                settleEntity.setAmount(totalAmount);
                settleEntity.setDepartmentId(departmentId);
                settleEntity.setCountAmount(countRecordsByCurrency.get(currency).intValue());
                settleEntity.setSettleStatus(1);

                QueryWrapper<PaymentChannelInfoEntity> queryWrapperChannel = new QueryWrapper<>();
                queryWrapperChannel.eq("department_id", departmentId);
                List<PaymentChannelInfoEntity> channelList = paymentChannelInfoDao.selectList(queryWrapperChannel);
                PaymentChannelInfoEntity paymentChannelInfoEntity = channelList.get(0);

                settleEntity.setMerName(paymentChannelInfoEntity.getMerName());
                settleEntity.setMerCode(paymentChannelInfoEntity.getMerCode());
                //交易类型是支付单
                settleEntity.setTradeType(1);
                //交易汇率在其它地方设置
                //settleEntity.setExchangeRate();
                //这些值通过商户中的配置进行计算，但是前提是，需要把汇率录入以后才可以进行计算
                //settleEntity.setMerShouldSettled();
                //settleEntity.setPlatformShouldSettled();
                //settleEntity.setMerBrokerage();
                //settleEntity.setPlatformBrokerage();
                //settleEntity.setMerAward();
                //settleEntity.setPlatformAward();

                //在其它地方设置
                //settleEntity.setSettleUrl();
                paymentSettleDao.insert(settleEntity);
                cMap.put(currency, settleEntity.getId());
            });
            map.put(departmentId, cMap);
        });


        //保存结算明细表
        orderInfoEntityList.forEach(item -> {
            if (settleDate != null) {
                if (matchMap.containsKey(item.getDepartmentId())) {
                    return;
                }
            }
            QueryWrapper<PaymentChannelInfoEntity> queryWrapperChannel = new QueryWrapper<>();
            queryWrapperChannel.eq("department_id", item.getDepartmentId());
            List<PaymentChannelInfoEntity> channelList = paymentChannelInfoDao.selectList(queryWrapperChannel);
            PaymentChannelInfoEntity paymentChannelInfoEntity = channelList.get(0);
            PaymentSettleDetailEntity paymentSettleDetailEntity = new PaymentSettleDetailEntity();
            paymentSettleDetailEntity.setPaymentChannelBusinessId(item.getPaymentChannelBusinessId());
            paymentSettleDetailEntity.setAmount(item.getAmount());
            paymentSettleDetailEntity.setMerCode(paymentChannelInfoEntity.getMerCode());
            paymentSettleDetailEntity.setMerName(paymentChannelInfoEntity.getMerName());
            paymentSettleDetailEntity.setAmount(item.getAmount());
            if ("bank".equals(item.getDepositType())) {
                paymentSettleDetailEntity.setPaymentMethod("bank");
            } else {
                paymentSettleDetailEntity.setPaymentMethod(item.getPaymentChannel());
            }

            paymentSettleDetailEntity.setDepositHolder(item.getDepositHolder());
            paymentSettleDetailEntity.setBankAccount(item.getBankAccount());
            paymentSettleDetailEntity.setCollectionHolder(item.getCollectionHolder());
            paymentSettleDetailEntity.setCollectionAccount(item.getCollectionCardNo());
            paymentSettleDetailEntity.setSettleStatus(1);
            paymentSettleDetailEntity.setStatus(item.getStatus());
            paymentSettleDetailEntity.setSettleId(map.get(item.getDepartmentId()).get(item.getCurrency()));
            paymentSettleDetailEntity.setTradeDate(item.getCreateTime());
            paymentSettleDetailEntity.setOrderNo(item.getOrderNo());

            QueryWrapper<PaymentChannelBusinessCfgEntity> queryWrapperChannelBusiness = new QueryWrapper<>();
            queryWrapperChannelBusiness.eq("department_id", item.getDepartmentId());
            List<PaymentChannelBusinessCfgEntity> paymentChannelBusinessCfgEntities = paymentChannelBusinessCfgDao.selectList(queryWrapperChannelBusiness);
            paymentChannelBusinessCfgEntities.forEach(temp -> {
                int min = item.getAmount().compareTo(temp.getAmountMin());
                int max = item.getAmount().compareTo(temp.getAmountMax());
                if (min >= 0 && max <= 0) {
                    paymentSettleDetailEntity.setBusinessScope(temp.getAmountMin().toString() + "-" + temp.getAmountMax());
                }
            });

            //在输入完汇率后再进行设置
//            paymentSettleDetailEntity.setMerShouldSettled();
//            paymentSettleDetailEntity.setPlatformShouldSettled();
//            paymentSettleDetailEntity.setMerAward();
//            paymentSettleDetailEntity.setPlatformAward();
//            paymentSettleDetailEntity.setMerBrokerage();
//            paymentSettleDetailEntity.setPlatformBrokerage();
            paymentSettleDetailDao.insert(paymentSettleDetailEntity);
        });

//        parentPaymentSettle(StringUtils.isEmpty(settleDate) ? SmartLocalDateUtil.getPreviousDate() : settleDate);
    }

    @Transactional
    public void parentPaymentSettle(String settleDate) {
        LambdaQueryWrapper<PaymentSettleEntity> settleWrapper = new LambdaQueryWrapper<>();
        settleWrapper.eq(PaymentSettleEntity::getTradeDate, settleDate);
        settleWrapper.eq(PaymentSettleEntity::getTradeType, 1);
        List<PaymentSettleEntity> paymentSettleEntities = paymentSettleDao.selectList(settleWrapper);
        Map<Long, PaymentSettleEntity> settleEntityMap = paymentSettleEntities.stream().collect(Collectors.toMap(PaymentSettleEntity::getDepartmentId, v -> v));

        List<Long> departmentIds = paymentSettleEntities.stream().map(PaymentSettleEntity::getDepartmentId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(departmentIds)) {
            return;
        }
        LambdaQueryWrapper<DepartmentEntity> departmentWrapper = new LambdaQueryWrapper<>();
        departmentWrapper.in(DepartmentEntity::getDepartmentId, departmentIds);
        List<DepartmentEntity> departmentEntities = departmentDao.selectList(departmentWrapper);

        Map<Long, List<DepartmentEntity>> departmentMap = departmentEntities.stream().collect(Collectors.groupingBy(DepartmentEntity::getParentId));

        for (Map.Entry<Long, List<DepartmentEntity>> entry : departmentMap.entrySet()) {
            Long parentId = entry.getKey();
            List<DepartmentEntity> departmentList = entry.getValue();

            List<PaymentSettleEntity> parentSettleList = new ArrayList<>();
            departmentList.forEach(department -> {
                PaymentSettleEntity settleEntity = settleEntityMap.get(department.getDepartmentId());
                parentSettleList.add(settleEntity);
            });

            LambdaQueryWrapper<PaymentChannelParentEntity> parentWrapper = new LambdaQueryWrapper<>();
            parentWrapper.eq(PaymentChannelParentEntity::getDepartmentId, parentId);
            PaymentChannelParentEntity channelParent = paymentChannelParentDao.selectOne(parentWrapper);

            LambdaQueryWrapper<PaymentSettleParentEntity> parentSettleWrapper = new LambdaQueryWrapper<>();
            parentSettleWrapper.eq(PaymentSettleParentEntity::getDepartmentId, parentId);
            parentSettleWrapper.eq(PaymentSettleParentEntity::getTradeDate, settleDate);
            long count = paymentSettleParentDao.selectCount(parentSettleWrapper);
            if (count > 0) {
                continue;
            }

            PaymentSettleParentEntity parentSettleEntity = new PaymentSettleParentEntity();
            parentSettleEntity.setDepartmentId(parentId);
            parentSettleEntity.setMerCode(channelParent.getMerCode());
            parentSettleEntity.setMerName(channelParent.getMerName());
            parentSettleEntity.setSettleStatus(1);
            parentSettleEntity.setTradeDate(settleDate);
            parentSettleEntity.setTradeType(1);
            parentSettleEntity.setAmount(parentSettleList.stream().map(PaymentSettleEntity::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            parentSettleEntity.setCurrency(parentSettleList.get(0).getCurrency());
            parentSettleEntity.setExchangeRate(parentSettleList.get(0).getExchangeRate());
            parentSettleEntity.setCountAmount(parentSettleList.stream().map(PaymentSettleEntity::getCountAmount).reduce(0, Integer::sum));
            parentSettleEntity.setCreateTime(LocalDateTime.now());
            parentSettleEntity.setUpdateTime(LocalDateTime.now());
            paymentSettleParentDao.insert(parentSettleEntity);
        }
    }





    /**
     * 设置汇率，把结算状态改为结算中
     *
     * @param settleSetExchangeRateForm
     * @return
     */
    @Transactional
    public ResponseDTO<String> setParentExchangeRate(SettleSetExchangeRateForm settleSetExchangeRateForm) {
        BigDecimal exchangeRate = settleSetExchangeRateForm.getExchangeRate();

        PaymentSettleParentEntity settleEntity = paymentSettleParentDao.selectById(settleSetExchangeRateForm.getSettleId());
        settleEntity.setExchangeRate(exchangeRate);
        settleEntity.setSettleStatus(2);

        List<Long> departmentIds = departmentCacheManager.getDepartmentSelfAndChildren(settleEntity.getDepartmentId());

        LambdaQueryWrapper<PaymentSettleEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(PaymentSettleEntity::getDepartmentId, departmentIds);
        wrapper.eq(PaymentSettleEntity::getTradeDate, settleEntity.getTradeDate());
        wrapper.eq(PaymentSettleEntity::getTradeType, settleEntity.getTradeType());
        List<PaymentSettleEntity> settleEntities = paymentSettleDao.selectList(wrapper);

        List<Long> settleIds = settleEntities.stream().map(PaymentSettleEntity::getId).collect(Collectors.toList());


        AtomicReference<PaymentChannelBusinessCfgEntity> businessCfg = new AtomicReference<>(new PaymentChannelBusinessCfgEntity());
        //1.设置结算明细表数据
        QueryWrapper<PaymentSettleDetailEntity> queryWrapperSettleDetail = new QueryWrapper<>();
        queryWrapperSettleDetail.in("settle_id", settleIds);
        List<PaymentSettleDetailEntity> paymentSettleDetailEntities = paymentSettleDetailDao.selectList(queryWrapperSettleDetail);

        final BigDecimal[] merShouldSettled = {new BigDecimal(0)};
        final BigDecimal[] platformShouldSettled = {new BigDecimal(0)};
        final BigDecimal[] merBrokerage = {new BigDecimal(0)};
        final BigDecimal[] platformBrokerage = {new BigDecimal(0)};
        final BigDecimal[] merAward = {new BigDecimal(0)};
        final BigDecimal[] platformAward = {new BigDecimal(0)};

        paymentSettleDetailEntities.forEach(item -> {

            PaymentChannelBusinessCfgEntity businessCfgDB = paymentChannelBusinessCfgDao.selectById(item.getPaymentChannelBusinessId());
            businessCfg.set(businessCfgDB);

            //通过汇率计算相应值
            BigDecimal divideDetail = item.getAmount().divide(exchangeRate, 2, RoundingMode.HALF_UP);
            //商户应结=交易额/汇率-（交易额/汇率*平台佣金+平台奖励）
            BigDecimal merShouldSettledDetail = divideDetail.subtract(divideDetail.multiply(businessCfg.get().getMerBrokerage()).add(businessCfg.get().getMerAward()));
            item.setMerShouldSettled(merShouldSettledDetail);
            BigDecimal platformShouldSettledDetail = divideDetail.subtract(divideDetail.multiply(businessCfg.get().getPlatformBrokerage()).add(businessCfg.get().getPlatformAward()));
            item.setPlatformShouldSettled(platformShouldSettledDetail);
            item.setMerAward(businessCfg.get().getMerAward());
            item.setPlatformAward(businessCfg.get().getPlatformAward());
            //佣金=交易额/汇率*佣金比例
            BigDecimal merBrokerageDetail = divideDetail.multiply(businessCfg.get().getMerBrokerage());
            item.setMerBrokerage(merBrokerageDetail);
            BigDecimal platformBrokerageDetail = divideDetail.multiply(businessCfg.get().getPlatformBrokerage());
            item.setPlatformBrokerage(platformBrokerageDetail);
            merShouldSettled[0] = merShouldSettled[0].add(merShouldSettledDetail);
            platformShouldSettled[0] = platformShouldSettled[0].add(platformShouldSettledDetail);
            merBrokerage[0] = merBrokerage[0].add(merBrokerageDetail);
            platformBrokerage[0] = platformBrokerage[0].add(platformBrokerageDetail);
            merAward[0] = merAward[0].add(businessCfg.get().getMerAward());
            platformAward[0] = platformAward[0].add(businessCfg.get().getPlatformAward());
            paymentSettleDetailDao.updateById(item);
        });

        //2.设置结算表数据
        //通过汇率计算相应值
        //商户应结=交易额/汇率-（交易额/汇率*平台佣金+平台奖励）
        settleEntity.setMerShouldSettled(merShouldSettled[0]);
        //平台应结=交易额/汇率-（交易额/汇率*平台佣金+商户奖励）
        settleEntity.setPlatformShouldSettled(platformShouldSettled[0]);
        //佣金=交易额/汇率*佣金比例
        settleEntity.setMerBrokerage(merBrokerage[0]);
        settleEntity.setPlatformBrokerage(platformBrokerage[0]);
        //奖励从商户配置中取固定值
        settleEntity.setMerAward(merAward[0]);
        settleEntity.setPlatformAward(platformAward[0]);
        paymentSettleParentDao.updateById(settleEntity);
        return ResponseDTO.ok();
    }


    /**
     * 设置结算状态为已结算
     *
     * @param settleId
     * @return
     */
    public ResponseDTO<String> confirmParentPayment(Long settleId, List<String> fileIds) {
        PaymentSettleParentEntity settleEntity = paymentSettleParentDao.selectById(settleId);
        settleEntity.setSettleStatus(4);
        settleEntity.setSettleUrl(String.join(",", fileIds));

        LambdaQueryWrapper<PaymentChannelParentEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentChannelParentEntity::getDepartmentId, settleEntity.getDepartmentId());
        PaymentChannelParentEntity parentEntity = paymentChannelParentDao.selectOne(wrapper);
        // 如果是自营商户，调用商户接口
        if (parentEntity.getIsOwned().equals(1)) {
            ResponseDTO<String> stringResponseDTO = merchantApiService.orderSettleUrl(settleEntity.getId(), fileIds, settleEntity.getTradeDate(), settleEntity.getMerCode());
            if (stringResponseDTO.getOk()) {
                paymentSettleParentDao.updateById(settleEntity);
            } else {
                return ResponseDTO.error(UserErrorCode.ORDER_SETTLE_CONFIRM_ERROR);
            }
        } else {
            paymentSettleParentDao.updateById(settleEntity);
        }
        return ResponseDTO.ok();
    }


    /**
     * 分页查询-结算
     *
     * @param queryForm
     * @return
     */
    public PageResult<SettleVO> queryPageByParentSettle(SettleQueryForm queryForm) {

        RequestEmployee requestUser = AdminRequestUtil.getRequestUser();
        List<RoleDataScopeEntity> roleDataScopeEntityList = roleEmployeeManager.getRoleDataScope(requestUser.getUserId());
        boolean dataScopeDepartment = roleDataScopeEntityList.stream().anyMatch(v -> DataScopeViewTypeEnum.DEPARTMENT.equalsValue(v.getViewType()));

        queryForm.setAdministratorFlag(requestUser.getAdministratorFlag() || !dataScopeDepartment);
        if (!queryForm.getAdministratorFlag()) {
            queryForm.setDepartmentId(requestUser.getDepartmentId());
        }
        log.info("queryPageBySettle:{}", JSON.toJSON(queryForm));

        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        page.addOrder(new OrderItem("trade_date", false));
        List<SettleVO> list = paymentSettleParentDao.queryPage(page, queryForm);
        PageResult<SettleVO> pageResult = SmartPageUtil.convert2PageResult(page, list);

        if (!CollectionUtils.isEmpty(pageResult.getList())) {
            List<SettleVO> collect = pageResult.getList().stream().map(item -> {
                if (!SmartStringUtil.isEmpty(item.getSettleUrl())) {
                    List<String> settleUrls = new ArrayList<>();
                    String[] fileIds = item.getSettleUrl().split(",");
                    for (String fileId : fileIds) {
                        ResponseDTO<String> responseDTO = fileService.getFileUrlById(Long.valueOf(fileId));
                        if (!responseDTO.getOk()) {
                            throw new BusinessException(responseDTO.getMsg());
                        }
                        settleUrls.add(responseDTO.getData());
                    }
                    item.setSettleUrl(String.join(";", settleUrls));
                }
                return item;
            }).collect(Collectors.toList());
            pageResult.setList(collect);
        }
        return pageResult;
    }

    public List<SettleVO> getParentExcelExportData(SettleExportQueryForm queryForm) {
        RequestEmployee requestUser = AdminRequestUtil.getRequestUser();
        List<RoleDataScopeEntity> roleDataScopeEntityList = roleEmployeeManager.getRoleDataScope(requestUser.getUserId());
        boolean dataScopeDepartment = roleDataScopeEntityList.stream().anyMatch(v -> DataScopeViewTypeEnum.DEPARTMENT.equalsValue(v.getViewType()));

        queryForm.setAdministratorFlag(requestUser.getAdministratorFlag() || !dataScopeDepartment);
        if (!queryForm.getAdministratorFlag()) {
            queryForm.setDepartmentId(requestUser.getDepartmentId());
        }
        log.info("getExcelExportData:{}", JSON.toJSON(queryForm));

        List<SettleVO> excelExportData = paymentSettleParentDao.getExcelExportData(queryForm);
        List<SettleVO> result = excelExportData.stream().peek(item -> {
            if (1 == item.getTradeType()) {
                item.setTradeTypeStr("支付单");
            } else {
                item.setTradeTypeStr("兑付单");
            }
            //1 结算状态 1:待结算，2:结算中，3:待确认，4:已结算
            if (1 == item.getSettleStatus()) {
                item.setSettleStatusStr("待结算");
            } else if (2 == item.getSettleStatus()) {
                item.setSettleStatusStr("结算中");
            } else if (4 == item.getSettleStatus()) {
                item.setSettleStatusStr("已结算");
            }
        }).collect(Collectors.toList());
        return result;
    }

    /**
     * 分页查询-结算明细
     *
     * @param queryForm
     * @return
     */
    public PageResult<SettleDetailVO> queryPageByParentSettleDetail(SettleDetailQueryForm queryForm) {

        PaymentSettleParentEntity paymentSettleParentEntity = paymentSettleParentDao.selectById(queryForm.getSettleId());

        LambdaQueryWrapper<DepartmentEntity> departmentWrapper = new LambdaQueryWrapper<>();
        departmentWrapper.eq(DepartmentEntity::getParentId, paymentSettleParentEntity.getDepartmentId());
        List<DepartmentEntity> departmentEntities = departmentDao.selectList(departmentWrapper);

        List<Long> departmentIds = departmentEntities.stream().map(DepartmentEntity::getDepartmentId).collect(Collectors.toList());


        LambdaQueryWrapper<PaymentSettleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(PaymentSettleEntity::getDepartmentId, departmentIds);
        queryWrapper.eq(PaymentSettleEntity::getTradeDate, paymentSettleParentEntity.getTradeDate());
        List<PaymentSettleEntity> paymentSettleEntities = paymentSettleDao.selectList(queryWrapper);
        List<Long> settleIds = paymentSettleEntities.stream().map(PaymentSettleEntity::getId).collect(Collectors.toList());

        queryForm.setSettleId(null);
        queryForm.setSettleIds(settleIds);
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<SettleDetailVO> list = paymentSettleDetailDao.queryPage(page, queryForm);
        PageResult<SettleDetailVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }

}
