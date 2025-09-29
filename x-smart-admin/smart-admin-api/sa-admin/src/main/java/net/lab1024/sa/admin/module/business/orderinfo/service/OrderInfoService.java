package net.lab1024.sa.admin.module.business.orderinfo.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.merchantmanage.service.MerchantApiService;
import net.lab1024.sa.admin.module.business.orderinfo.dao.OrderInfoDao;
import net.lab1024.sa.admin.module.business.orderinfo.dao.SmsQueryRecordDao;
import net.lab1024.sa.admin.module.business.orderinfo.domain.entity.OrderInfoEntity;
import net.lab1024.sa.admin.module.business.orderinfo.domain.entity.SmsQueryRecordEntity;
import net.lab1024.sa.admin.module.business.orderinfo.domain.form.OrderInfoQueryExportForm;
import net.lab1024.sa.admin.module.business.orderinfo.domain.form.OrderInfoQueryForm;
import net.lab1024.sa.admin.module.business.orderinfo.domain.form.OrderInfoUpdateForm;
import net.lab1024.sa.admin.module.business.orderinfo.domain.vo.DashboardOrderTodayVO;
import net.lab1024.sa.admin.module.business.orderinfo.domain.vo.OrderInfoVO;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelInfoEntity;
import net.lab1024.sa.admin.module.business.payment.channel.manager.PaymentChannelInfoManager;
import net.lab1024.sa.admin.module.business.payment.channel.manager.PaymentChannelPayInfoPollingListManager;
import net.lab1024.sa.admin.module.system.datascope.constant.DataScopeViewTypeEnum;
import net.lab1024.sa.admin.module.system.department.manager.DepartmentCacheManager;
import net.lab1024.sa.admin.module.system.login.domain.RequestEmployee;
import net.lab1024.sa.admin.module.system.role.domain.entity.RoleDataScopeEntity;
import net.lab1024.sa.admin.module.system.role.manager.RoleEmployeeManager;
import net.lab1024.sa.admin.util.AdminRequestUtil;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.enumeration.OrderStatusEnum;
import net.lab1024.sa.base.common.util.SmartDateFormatterEnum;
import net.lab1024.sa.base.common.util.SmartLocalDateUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.config.AsyncConfig;
import net.lab1024.sa.base.module.support.file.service.FileService;
import net.lab1024.sa.base.module.support.sms.SmsService;
import net.lab1024.sa.base.module.support.sms.domain.SendSmsRequestVO;
import net.lab1024.sa.base.module.support.sms.enumeration.SmsBusinessEnum;
import net.lab1024.sa.base.module.support.sms.enumeration.SmsTypeEnum;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 订单信息表 Service
 *
 * @Author sunyu
 * @Date 2024-07-23 14:25:56
 * @Copyright sunyu
 */
@Slf4j
@Service
public class OrderInfoService {

    @Resource
    private OrderInfoDao orderInfoDao;
    @Resource(name = AsyncConfig.ASYNC_EXECUTOR_THREAD_NAME)
    private AsyncTaskExecutor asyncTaskExecutor;
    @Resource
    private MerchantApiService merchantApiService;
    @Resource
    private FileService fileService;
    @Resource
    private RoleEmployeeManager roleEmployeeManager;
    @Resource
    private SmsService smsService;
    @Resource
    private PaymentChannelInfoManager paymentChannelInfoManager;
    @Resource
    private PaymentChannelPayInfoPollingListManager paymentChannelPayInfoPollingListManager;
    @Resource
    private SmsQueryRecordDao smsQueryRecordDao;
    /**
     * 订单付款时间，默认15分钟
     */
    @Value("${zfx.pay.time:900}")
    private Long orderPayTime;

    @Value("${zfx.pay.route-reverse-time:900}")
    private Long orderRouteReverseTime;
    @Resource
    private DepartmentCacheManager departmentCacheManager;

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<OrderInfoVO> queryPage(OrderInfoQueryForm queryForm) {
        RequestEmployee requestUser = AdminRequestUtil.getRequestUser();
        List<RoleDataScopeEntity> roleDataScopeEntityList = roleEmployeeManager.getRoleDataScope(requestUser.getUserId());
        boolean dataScopeDepartment = roleDataScopeEntityList.stream().anyMatch(v -> DataScopeViewTypeEnum.DEPARTMENT.equalsValue(v.getViewType()));

        if (StringUtils.isNotEmpty(queryForm.getDepositHolder())) {
            queryForm.setDepositHolderHash(DigestUtils.sha256Hex(queryForm.getDepositHolder()));
        }
        List<Long> departmentIds = departmentCacheManager.getDepartmentSelfAndChildren(requestUser.getDepartmentId());
        queryForm.setDepartmentIds(departmentIds);
        queryForm.setAdministratorFlag(requestUser.getAdministratorFlag() || !dataScopeDepartment);
        // 不是超管并且没有该部门权限，不允许使用departmentId参数
        if (Objects.nonNull(queryForm.getDepartmentId()) && !queryForm.getAdministratorFlag() && !departmentIds.contains(requestUser.getDepartmentId())) {
            queryForm.setDepartmentId(null);
        }
        log.info("order queryPage:{}", JSON.toJSON(queryForm));

        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        page.addOrder(new OrderItem("create_time", false));
        List<OrderInfoVO> list = orderInfoDao.queryPage(page, queryForm);
        for (OrderInfoVO vo : list) {
            ResponseDTO<String> responseDTO = fileService.getFileUrlById(vo.getReceiptFileId());
            if (responseDTO.getOk()) {
                vo.setReceiptFileUrl(responseDTO.getData());
            }
        }
        PageResult<OrderInfoVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }


    public List<OrderInfoVO> getExcelExportData(OrderInfoQueryExportForm queryForm) {
        RequestEmployee requestUser = AdminRequestUtil.getRequestUser();
        List<RoleDataScopeEntity> roleDataScopeEntityList = roleEmployeeManager.getRoleDataScope(requestUser.getUserId());
        boolean dataScopeDepartment = roleDataScopeEntityList.stream().anyMatch(v -> DataScopeViewTypeEnum.DEPARTMENT.equalsValue(v.getViewType()));

        queryForm.setDepartmentIds(departmentCacheManager.getDepartmentSelfAndChildren(requestUser.getDepartmentId()));
        queryForm.setAdministratorFlag(requestUser.getAdministratorFlag() || !dataScopeDepartment);
        log.info("getExcelExportData:{}", JSON.toJSON(queryForm));

        List<OrderInfoVO> excelExportData = orderInfoDao.getExcelExportData(queryForm);
        List<OrderInfoVO> result = excelExportData.stream().peek(item->{
            if("qrcode".equals(item.getDepositType())){
                item.setDepositType("二維碼");
            }else{
                item.setDepositType("銀行卡");
            }
            //1 待确认 2 已确认 3 挂起
            if(1 == item.getStatus()){
                item.setStatusStr("待確認");
            }else if(2 == item.getStatus()){
                item.setStatusStr("已確認");
            }else if(3 == item.getStatus()){
                item.setStatusStr("掛起/取消");
            }else if(4 == item.getStatus()){
                item.setStatusStr("已过期");
            }
        }).collect(Collectors.toList());
        return result;
    }


    /**
     * 更新
     *
     * @param updateForm
     * @return
     */
    public ResponseDTO<String> update(OrderInfoUpdateForm updateForm) {
        RequestEmployee employee = AdminRequestUtil.getRequestUser();
        OrderInfoEntity orderInfoEntity = orderInfoDao.selectById(updateForm.getId());
        if (orderInfoEntity == null) {
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR);
        }
        // 超管可以确认过期订单
        if (OrderStatusEnum.EXPIRED.equalsValue(orderInfoEntity.getStatus()) && OrderStatusEnum.CONFIRM.equalsValue(updateForm.getStatus())
                && employee.getAdministratorFlag()) {
            updateOrderInfo(orderInfoEntity, updateForm);
            return ResponseDTO.ok();
        }
        if (!OrderStatusEnum.WAITING.equalsValue(orderInfoEntity.getStatus()) && !OrderStatusEnum.REFUND.equalsValue(updateForm.getStatus())) {
            return ResponseDTO.error(UserErrorCode.ORDER_STATUS_NON_OPERATIONAL);
        }
        if (OrderStatusEnum.CONFIRM.equalsValue(orderInfoEntity.getStatus()) && OrderStatusEnum.REFUND.equalsValue(updateForm.getStatus())) {
            return ResponseDTO.error(UserErrorCode.ORDER_STATUS_NON_OPERATIONAL);
        }
        if (OrderStatusEnum.REFUND.equalsValue(updateForm.getStatus())) {
            if (StringUtils.isEmpty(updateForm.getRefundReason().trim())) {
                return ResponseDTO.error(UserErrorCode.ORDER_STATUS_REFUND_REASON);
            }
            orderInfoEntity.setRefundReason(updateForm.getRefundReason().trim());
        }
        updateOrderInfo(orderInfoEntity, updateForm);
        return ResponseDTO.ok();
    }

    private void updateOrderInfo(OrderInfoEntity orderInfoEntity, OrderInfoUpdateForm updateForm) {
        orderInfoEntity.setStatus(updateForm.getStatus());
        orderInfoEntity.setUpdateTime(LocalDateTime.now());
        orderInfoEntity.setFinishTime(LocalDateTime.now());
        orderInfoDao.updateById(orderInfoEntity);

        asyncTaskExecutor.execute(() -> {
            OrderInfoEntity orderInfoEntity1 = orderInfoDao.selectById(updateForm.getId());
            merchantApiService.orderCallback(orderInfoEntity1);
        });
    }


    /**
     * 批量删除
     *
     * @param idList
     * @return
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        orderInfoDao.deleteBatchIds(idList);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        orderInfoDao.deleteById(id);
        return ResponseDTO.ok();
    }


    /**
     * 支付订单过去任务
     */
    @Scheduled(initialDelay = 2000, fixedRate = 10000)
    public void paymentOrderExpiredTask() {
        LambdaQueryWrapper<OrderInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfoEntity::getStatus, 1);
        // 15分钟前的订单
        queryWrapper.lt(OrderInfoEntity::getApplyTime, LocalDateTime.now().minusSeconds(orderPayTime));

        List<OrderInfoEntity> orderInfoEntityList = orderInfoDao.selectList(queryWrapper);

        for (OrderInfoEntity orderInfoEntity : orderInfoEntityList) {
            orderInfoEntity.setStatus(4);
            orderInfoEntity.setUpdateTime(LocalDateTime.now());
            orderInfoDao.updateById(orderInfoEntity);

            // 过期不回调
//            asyncTaskExecutor.execute(() -> {
//                merchantApiService.orderCallback(orderInfoEntity);
//            });
        }
    }

    /**
     * 支付订单释放路由计数
     */
    @Scheduled(initialDelay = 2000, fixedRate = 10000)
    public void paymentOrderReverseRouteCountTask() {
        LambdaQueryWrapper<OrderInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderInfoEntity::getStatus, 1);
        queryWrapper.eq(OrderInfoEntity::getRouteReverseFlag, 0);
        // 15分钟前的订单
        queryWrapper.lt(OrderInfoEntity::getApplyTime, LocalDateTime.now().minusSeconds(orderRouteReverseTime));

        List<OrderInfoEntity> orderInfoEntityList = orderInfoDao.selectList(queryWrapper);

        for (OrderInfoEntity orderInfoEntity : orderInfoEntityList) {
            orderInfoEntity.setRouteReverseFlag(1);
            orderInfoEntity.setUpdateTime(LocalDateTime.now());
            orderInfoDao.updateById(orderInfoEntity);

            //回冲支付轮询路由次数
            paymentChannelInfoManager.paymentChannelRouteReversed(orderInfoEntity.getPaymentChannelPayInfoId());

        }
    }

    /**
     * 支付订单发送短信，每分钟查询一次
     */
    @Scheduled(initialDelay = 2000, fixedRate = 60 * 1000)
    public void paymentOrderSendSms() {
        LambdaQueryWrapper<PaymentChannelInfoEntity> channelQueryWrapper = new LambdaQueryWrapper<>();
        channelQueryWrapper.eq(PaymentChannelInfoEntity::getStatus, 1);
        List<PaymentChannelInfoEntity> channelInfoEntityList = paymentChannelInfoManager.list(channelQueryWrapper);
        for (PaymentChannelInfoEntity channelInfoEntity : channelInfoEntityList) {

            long startTime;
            long endTime = System.currentTimeMillis() - 300000;
            LambdaQueryWrapper<SmsQueryRecordEntity> smsQueryRecordEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
            smsQueryRecordEntityLambdaQueryWrapper.eq(SmsQueryRecordEntity::getDepartmentId, channelInfoEntity.getDepartmentId());
            SmsQueryRecordEntity recordEntity = smsQueryRecordDao.selectOne(smsQueryRecordEntityLambdaQueryWrapper);
            if (recordEntity != null) {
                startTime = recordEntity.getQueryTime();
                recordEntity.setQueryTime(endTime);
                recordEntity.setUpdateTime(LocalDateTime.now());
                smsQueryRecordDao.updateById(recordEntity);
            } else {
                startTime = System.currentTimeMillis() - 360000;
                recordEntity = new SmsQueryRecordEntity();
                recordEntity.setDepartmentId(channelInfoEntity.getDepartmentId());
                recordEntity.setQueryTime(endTime);
                recordEntity.setUpdateTime(LocalDateTime.now());
                recordEntity.setCreateTime(LocalDateTime.now());
                smsQueryRecordDao.insert(recordEntity);
            }

            LambdaQueryWrapper<OrderInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(OrderInfoEntity::getDepartmentId, channelInfoEntity.getDepartmentId());
            queryWrapper.eq(OrderInfoEntity::getStatus, 1);
            // 5分钟前的订单
            queryWrapper.gt(OrderInfoEntity::getApplyTime, SmartLocalDateUtil.timestampToLocalDateTime(startTime));
            queryWrapper.lt(OrderInfoEntity::getApplyTime, SmartLocalDateUtil.timestampToLocalDateTime(endTime));
            List<OrderInfoEntity> orderInfoEntityList = orderInfoDao.selectList(queryWrapper);
            if (CollectionUtils.isEmpty(orderInfoEntityList)){
                log.debug("未查询到新的订单，departmentId:{}, startTime:{}, endTime:{}", channelInfoEntity.getDepartmentId(),
                        SmartLocalDateUtil.timestampToLocalDateTime(startTime), SmartLocalDateUtil.timestampToLocalDateTime(endTime));
                continue;
            }
            String phone = channelInfoEntity.getPhone();
            if (StringUtils.isEmpty(phone)){
                log.debug("商户手机号为空，不发送短信");
                continue;
            }
            log.debug("查询到新的订单，count:{}，departmentId:{}, startTime:{}, endTime:{}", orderInfoEntityList.size(),
                    channelInfoEntity.getDepartmentId(),
                    SmartLocalDateUtil.timestampToLocalDateTime(startTime), SmartLocalDateUtil.timestampToLocalDateTime(endTime));
            SendSmsRequestVO sendSmsRequestVO = new SendSmsRequestVO();
            sendSmsRequestVO.setPhoneNumber(phone);
            boolean sendSuccess = smsService.sendSms(sendSmsRequestVO, SmsTypeEnum.NOTIFY.getValue(), SmsBusinessEnum.ORDER_NOTIFY.getValue());
            if (!sendSuccess){
                log.error("order sms send fail departmentId:{}, phone:{}", channelInfoEntity.getDepartmentId(), phone);
            }
        }
    }

    public ResponseDTO<List<DashboardOrderTodayVO>> dashboardOrderDataToday() {
        RequestEmployee requestUser = AdminRequestUtil.getRequestUser();
        Long departmentId = requestUser.getAdministratorFlag() ? null : requestUser.getDepartmentId();
        String date = SmartLocalDateUtil.format(LocalDate.now(), SmartDateFormatterEnum.YMD);
        List<DashboardOrderTodayVO> dashboardOrderTodayVOList = orderInfoDao.queryDashboardOrderToday(departmentId, date);
        return ResponseDTO.ok(dashboardOrderTodayVOList);
    }


}
