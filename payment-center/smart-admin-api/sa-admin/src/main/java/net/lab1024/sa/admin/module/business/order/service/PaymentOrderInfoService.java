package net.lab1024.sa.admin.module.business.order.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.extra.servlet.ServletUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import net.lab1024.sa.admin.module.business.order.dao.PaymentOrderInfoDao;
import net.lab1024.sa.admin.module.business.order.domain.entity.PaymentOrderFailRecordEntity;
import net.lab1024.sa.admin.module.business.order.domain.entity.PaymentOrderInfoEntity;
import net.lab1024.sa.admin.module.business.order.domain.form.PaymentOrderFailRecordQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.form.PaymentOrderInfoExportQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.form.PaymentOrderInfoQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.form.PaymentOrderInfoReceiptForm;
import net.lab1024.sa.admin.module.business.order.domain.vo.PaymentOrderInfoListVO;
import net.lab1024.sa.admin.module.business.order.manager.PaymentOrderFailRecordManager;
import net.lab1024.sa.admin.module.business.order.manager.PaymentOrderInfoManager;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelInfoEntity;
import net.lab1024.sa.admin.module.business.payment.channel.manager.PaymentChannelInfoManager;
import net.lab1024.sa.admin.module.openapi.order.service.IPaymentPlatformService;
import net.lab1024.sa.base.common.code.OpenErrorCode;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.constant.RequestHeaderConst;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.enumeration.OrderPayStatusEnum;
import net.lab1024.sa.base.common.enumeration.OrderStatusEnum;
import net.lab1024.sa.base.common.util.SmartEnumUtil;
import net.lab1024.sa.base.common.util.SmartLocalDateUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.lab1024.sa.base.module.support.file.domain.vo.FileDownloadVO;
import net.lab1024.sa.base.module.support.file.service.FileService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 支付订单信息表 Service
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Service
public class PaymentOrderInfoService {

    @Resource
    private PaymentOrderInfoDao paymentOrderInfoDao;
    @Resource
    private PaymentOrderInfoManager paymentOrderInfoManager;
    @Resource
    private PaymentChannelInfoManager channelInfoManager;
    @Resource
    private FileService fileService;
    @Resource
    private HttpServletRequest request;
    @Resource
    private PaymentOrderFailRecordManager paymentOrderFailRecordManager;


    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<PaymentOrderInfoListVO> queryPage(PaymentOrderInfoQueryForm queryForm) {
        if (StringUtils.isNotEmpty(queryForm.getKeyword())) {
            queryForm.setKeywordHash(DigestUtils.sha256Hex(queryForm.getKeyword()));
        }
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        page.addOrder(new OrderItem("tpoi.create_time", false));
        List<PaymentOrderInfoListVO> list = paymentOrderInfoDao.queryPage(page, queryForm);
        long currentTime = System.currentTimeMillis();
        for (PaymentOrderInfoListVO vo : list) {
            // 处理过期状态
            if (vo.getPayStatus().equals(1) && vo.getStatus().equals(1) && currentTime > vo.getExpiredTime()) {
                vo.setPayStatus(4);
                vo.setStatus(3);
            }
            // 获取文件链接
            ResponseDTO<String> responseDTO = fileService.getFileUrlByFileId(vo.getBillFileId());
            if (responseDTO.getOk()) {
                vo.setBillFileUrl(responseDTO.getData());
            }
        }
        PageResult<PaymentOrderInfoListVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }


    public long countExcelExportData(PaymentOrderInfoExportQueryForm queryForm) {
        return paymentOrderInfoDao.countExcelExportData(queryForm);
    }

    public List<PaymentOrderInfoListVO> getExcelExportData(PaymentOrderInfoExportQueryForm queryForm) {
        Page<?> page = new Page<>(queryForm.getPageNum(), queryForm.getPageSize());
        List<PaymentOrderInfoListVO> excelExportData = paymentOrderInfoDao.getExcelExportData(page, queryForm);
        List<PaymentOrderInfoListVO> result = excelExportData.stream().map(item->{
            //支付状态 1 待付款 2 已付款 3 已取消 4 已过期
            OrderPayStatusEnum payStatusEnum = SmartEnumUtil.getEnumByValue(item.getPayStatus(), OrderPayStatusEnum.class);
            item.setPayStatusStr(payStatusEnum.getDesc());

            //确认状态 1 待確認 2 已確認 3 掛起 4 已過期
            OrderStatusEnum orderStatusEnum = SmartEnumUtil.getEnumByValue(item.getStatus(), OrderStatusEnum.class);
            item.setStatusStr(orderStatusEnum.getDesc());

            if(item.getPayTime() != 0){
                item.setPayTimeLocal(SmartLocalDateUtil.getDateForTimestamp(item.getPayTime()));
            }
            if(item.getFinishedTime() != 0){
                item.setFinishedTimeLocal(SmartLocalDateUtil.getDateForTimestamp(item.getFinishedTime()));
            }
            return item;
        }).collect(Collectors.toList());
        return result;
    }

    public ResponseDTO<String> orderReceiptUpload(PaymentOrderInfoReceiptForm form) {
        PaymentOrderInfoEntity orderInfoEntity = paymentOrderInfoManager.getById(form.getId());
        if (orderInfoEntity == null) {
            return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST);
        }
        if (orderInfoEntity.getBillFileId() != null && orderInfoEntity.getBillFileId() > 0) {
            return ResponseDTO.error(UserErrorCode.ORDER_RECEIPT_EXISTED);
        }

        String userAgent = ServletUtil.getHeaderIgnoreCase(request, RequestHeaderConst.USER_AGENT);
        ResponseDTO<FileDownloadVO> fileDownloadVOResponseDTO = fileService.getDownloadFile(form.getFileId(), userAgent);
        if (!fileDownloadVOResponseDTO.getOk()) {
            return ResponseDTO.error(OpenErrorCode.FILE_UPLOAD_FAIL);
        }

        PaymentOrderInfoEntity orderInfoNew = new PaymentOrderInfoEntity();
        orderInfoNew.setId(orderInfoEntity.getId());
        orderInfoNew.setBillFileId(form.getFileId());
        orderInfoNew.setUpdateTime(LocalDateTime.now());
        paymentOrderInfoManager.updateById(orderInfoNew);

        PaymentChannelInfoEntity paymentChannelInfoVO = channelInfoManager.getById(orderInfoEntity.getChannelId());
        if (paymentChannelInfoVO == null) {
            return ResponseDTO.error(OpenErrorCode.SYSTEM_ERROR);
        }
        IPaymentPlatformService platformService = IPaymentPlatformService.get(paymentChannelInfoVO.getImplCode());

        MultipartFile multipartFile = new MockMultipartFile(
                fileDownloadVOResponseDTO.getData().getMetadata().getFileName(),
                fileDownloadVOResponseDTO.getData().getMetadata().getFileName(),
                fileDownloadVOResponseDTO.getData().getMetadata().getFileFormat(),
                fileDownloadVOResponseDTO.getData().getData()
        );
        platformService.paymentReceipt(paymentChannelInfoVO, orderInfoEntity.getOrderNo(), multipartFile);

        return ResponseDTO.ok();
    }


    public PageResult<PaymentOrderFailRecordEntity> queryOrderFailRecord(PaymentOrderFailRecordQueryForm form) {
        Page<?> page = SmartPageUtil.convert2PageQuery(form);
        page.addOrder(new OrderItem("create_time", false));
        List<PaymentOrderFailRecordEntity> list = paymentOrderFailRecordManager.queryPage(page, form);
        return SmartPageUtil.convert2PageResult(page, list);
    }
}
