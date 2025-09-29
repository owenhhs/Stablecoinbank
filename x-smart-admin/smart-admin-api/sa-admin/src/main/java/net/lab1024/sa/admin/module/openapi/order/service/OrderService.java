package net.lab1024.sa.admin.module.openapi.order.service;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.orderinfo.domain.entity.OrderInfoEntity;
import net.lab1024.sa.admin.module.business.orderinfo.manager.OrderInfoManager;
import net.lab1024.sa.admin.module.business.payment.channel.domain.dto.PaymentChannelInfoRouteDTO;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelParentEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentUserEntity;
import net.lab1024.sa.admin.module.business.payment.channel.manager.PaymentChannelInfoManager;
import net.lab1024.sa.admin.module.business.payment.channel.manager.PaymentUserManager;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderDepositDetailsForm;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderDepositForm;
import net.lab1024.sa.admin.module.openapi.order.domain.vo.OrderDepositDetailsVO;
import net.lab1024.sa.admin.module.openapi.order.domain.vo.OrderDepositVO;
import net.lab1024.sa.admin.module.system.employee.dao.EmployeeDao;
import net.lab1024.sa.base.common.code.OpenErrorCode;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.enumeration.DepositTypeEnum;
import net.lab1024.sa.base.common.enumeration.PaymentChannelEnum;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.constant.RedisKeyConst;
import net.lab1024.sa.base.module.support.config.ConfigKeyEnum;
import net.lab1024.sa.base.module.support.config.ConfigService;
import net.lab1024.sa.base.module.support.file.constant.FileFolderTypeEnum;
import net.lab1024.sa.base.module.support.file.domain.vo.FileUploadVO;
import net.lab1024.sa.base.module.support.file.service.FileService;
import net.lab1024.sa.base.module.support.redis.RedisAtomicClient;
import net.lab1024.sa.base.module.support.redis.RedisService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 孙宇
 * @date 2024/07/25 22:47
 */
@Service
@Slf4j
public class OrderService {
    @Resource
    private RedisAtomicClient redisAtomicClient;
    @Resource
    private RedisService redisService;
    @Resource
    private OrderInfoManager orderInfoManager;
    @Resource
    private PaymentChannelInfoManager paymentChannelInfoManager;
    @Resource
    private EmployeeDao employeeDao;
    @Resource
    private FileService fileService;

    @Resource
    private ConfigService configService;
    @Resource
    private PaymentUserManager paymentUserManager;

    @Value("${zfx.pay.url}")
    private String payUrlPrefix;
    /**
     * 订单付款时间，默认15分钟
     */
    @Value("${zfx.pay.time:900}")
    private Long orderPayTime;
    @Value("${zfx.pay.api.domain}")
    private String domain;


    public ResponseDTO<OrderDepositVO> orderDeposit(OrderDepositForm depositForm) {
        try {
            log.info("orderDeposit请求参数depositForm:{}", JSON.toJSON(depositForm));
            OrderDepositVO result = new OrderDepositVO();
            OrderInfoEntity orderInfoEntity = orderInfoManager.queryByOrderNo(depositForm.getOrderNo());
            if (orderInfoEntity != null) {
                return ResponseDTO.error(OpenErrorCode.FORM_REPEAT_SUBMIT);
            }

            PaymentChannelParentEntity parentEntity = paymentChannelInfoManager.getParentInfoByMerCode(depositForm.getMerCode());
            if (parentEntity == null) {
                log.error("order deposit 渠道不存在, merCode:{}", depositForm.getMerCode());
                return ResponseDTO.error(OpenErrorCode.DATA_NOT_EXIST);
            }

            // 保存用户并替换userid
            PaymentUserEntity userEntity = paymentUserManager.getByUserNameOrUserId(depositForm.getDepositHolder(), depositForm.getUserId());
            if (userEntity.getBlack().equals(1) && !DepositTypeEnum.BANK.equalsValue(depositForm.getDepositType())) {
                return ResponseDTO.error(OpenErrorCode.USER_BLACK_LIST);
            }
            depositForm.setUserId(String.valueOf(userEntity.getId()));

            boolean isXj = userEntity.getIsXj().equals(1) && "CNY".equals(depositForm.getCurrency());
            boolean isXjForce = false;
            if (isXj) {
                String xjForce = configService.getConfigValue(ConfigKeyEnum.XJ_FORCE);
                Set<String> xjForceSet = new HashSet<>();
                if (org.apache.commons.lang3.StringUtils.isNotEmpty(xjForce)) {
                    xjForceSet.addAll(Arrays.asList(xjForce.split(",")));
                }
                String type;
                if ("bank".equalsIgnoreCase(depositForm.getDepositType())) {
                    type = depositForm.getDepositType();
                } else {
                    type = depositForm.getPaymentChannel();
                }
                isXjForce = xjForceSet.contains(type);
            }

            //路由通道
            PaymentChannelInfoRouteDTO paymentChannelInfo;
            try {
                paymentChannelInfo = paymentChannelInfoManager.getPaymentChannelInfo(parentEntity.getDepartmentId(),
                        depositForm.getAmount(), depositForm.getDepositType(), depositForm.getPaymentChannel(),
                        depositForm.getOrderNo(), depositForm.getDepositHolder(), depositForm.getCurrency(),
                        depositForm.getCountry(), userEntity.getId(), isXj);
            } catch (BusinessException e) {
                if (!isXj || isXjForce) {
                    throw e;
                }
                // 未匹配到新疆收款方式，且不强制使用新疆收款方式
                paymentChannelInfo = paymentChannelInfoManager.getPaymentChannelInfo(parentEntity.getDepartmentId(),
                        depositForm.getAmount(), depositForm.getDepositType(), depositForm.getPaymentChannel(),
                        depositForm.getOrderNo(), depositForm.getDepositHolder(), depositForm.getCurrency(),
                        depositForm.getCountry(), userEntity.getId(), false);
            }

            orderInfoEntity = new OrderInfoEntity();
            orderInfoEntity.setDepartmentId(paymentChannelInfo.getDepartmentId());
            orderInfoEntity.setOrderNo(depositForm.getOrderNo());
            orderInfoEntity.setDepositType(depositForm.getDepositType());
            orderInfoEntity.setPaymentChannel(depositForm.getPaymentChannel());
            orderInfoEntity.setAmount(depositForm.getAmount());
            orderInfoEntity.setCurrency(depositForm.getCurrency());
            orderInfoEntity.setCountry(depositForm.getCountry());
            orderInfoEntity.setDepositHolder(depositForm.getDepositHolder());
            orderInfoEntity.setDepositHolderHash(DigestUtils.sha256Hex(depositForm.getDepositHolder()));
            orderInfoEntity.setBankAccount(depositForm.getBankAccount());
            orderInfoEntity.setDepositRemark(depositForm.getDepositRemark());
            orderInfoEntity.setCallback(depositForm.getCallback());
            orderInfoEntity.setLandingPage(depositForm.getLandingUrl());
            orderInfoEntity.setUserId(userEntity.getId());
            orderInfoEntity.setApplyTime(LocalDateTime.now());
            orderInfoEntity.setStatus(1);
            orderInfoEntity.setCreateTime(LocalDateTime.now());
            orderInfoEntity.setUpdateTime(LocalDateTime.now());

            //获取收款人信息
            orderInfoEntity.setCollectionBank(paymentChannelInfo.getBankInfo());
            orderInfoEntity.setCollectionBankAddress(paymentChannelInfo.getBankName());
            orderInfoEntity.setCollectionCardNo(paymentChannelInfo.getBankNo());
            orderInfoEntity.setCollectionHolder(paymentChannelInfo.getUsername());
            orderInfoEntity.setPaymentChannelBusinessId(paymentChannelInfo.getPaymentChannelBusinessId());
            orderInfoEntity.setPaymentChannelPayInfoId(paymentChannelInfo.getPaymentChannelPayInfoId());
            orderInfoManager.save(orderInfoEntity);

            //String token = RandomStringUtils.randomAlphanumeric(32);
            //String orderPayTokenKey = RedisKeyConst.Order.ORDER_PAY_TOKEN + token;
            //redisService.set(orderPayTokenKey, orderInfoEntity, 1000 * orderPayTime);

            //如果是微信/支付宝返回二维码，如果是银行卡的话，返回银行卡信息
            BeanUtil.copyProperties(paymentChannelInfo, result);
            result.setMerName(parentEntity.getMerName());
            return ResponseDTO.ok(result);
        } finally {
            //lock.close();
        }
    }


    public ResponseDTO<OrderDepositDetailsVO> orderDepositDetail(OrderDepositDetailsForm depositForm) {
//        Long requestUserId = SmartRequestUtil.getRequestUserId();
//        EmployeeEntity employeeEntity = employeeDao.selectById(requestUserId);
//
//        Long departmentId = employeeEntity.getDepartmentId();

        OrderInfoEntity orderInfoEntity = orderInfoManager.queryByOrderNo(depositForm.getOrderNo());

        if (orderInfoEntity == null) {
            return ResponseDTO.error(OpenErrorCode.DATA_NOT_EXIST);
        }
        OrderDepositDetailsVO orderDepositDetailsVO = new OrderDepositDetailsVO();

        orderDepositDetailsVO.setOrderNo(orderInfoEntity.getOrderNo());
        orderDepositDetailsVO.setDepositType(orderInfoEntity.getDepositType());
        orderDepositDetailsVO.setDepositRemark(orderInfoEntity.getDepositRemark());
        orderDepositDetailsVO.setDepositHolder(orderInfoEntity.getDepositHolder());
        orderDepositDetailsVO.setAmount(orderInfoEntity.getAmount());
        orderDepositDetailsVO.setCurrency(orderInfoEntity.getCurrency());
        orderDepositDetailsVO.setCountry(orderInfoEntity.getCountry());
        orderDepositDetailsVO.setAccount(orderInfoEntity.getCollectionCardNo());
        orderDepositDetailsVO.setName(orderInfoEntity.getCollectionHolder());
        orderDepositDetailsVO.setBank(orderInfoEntity.getCollectionBank());
        orderDepositDetailsVO.setTime(orderInfoEntity.getApplyTime());
        orderDepositDetailsVO.setStatus(orderInfoEntity.getStatus());
        orderDepositDetailsVO.setUpdateTime(orderInfoEntity.getUpdateTime());
        return ResponseDTO.ok(orderDepositDetailsVO);
    }

    public String paymentPage(String token, Model model, String userAgent) {
        String redisKey = RedisKeyConst.Order.ORDER_PAY_TOKEN + token;
        OrderInfoEntity orderInfoEntity = redisService.getObject(redisKey, OrderInfoEntity.class);
        if (orderInfoEntity == null) {
            return "404";
//            orderInfoEntity = orderInfoManager.queryByOrderNo("AK20240725001033", "11");
        }

        model.addAttribute("token", token);
        model.addAttribute("price", orderInfoEntity.getAmount());
        model.addAttribute("time", orderPayTime);
        model.addAttribute("account", orderInfoEntity.getCollectionHolder());
        model.addAttribute("bank", orderInfoEntity.getCollectionBank());
        model.addAttribute("address", orderInfoEntity.getCollectionBankAddress());
        model.addAttribute("cardno", orderInfoEntity.getCollectionCardNo());
        model.addAttribute("callback", orderInfoEntity.getCallback());
        model.addAttribute("domain", domain);

        boolean isMobile = userAgent != null && (userAgent.matches(".*Mobile.*")
                || userAgent.matches(".*iPhone.*")
                || userAgent.matches(".*Android.*")
                || userAgent.matches(".*iPad.*")
                || userAgent.matches(".*iPod.*"));

        if (DepositTypeEnum.QRCODE.equalsValue(orderInfoEntity.getDepositType())) {

            model.addAttribute("qrcode", "https://payment-test-1327269739.cos.ap-hongkong.myqcloud.com/qrcode-test.png");

            if (PaymentChannelEnum.WECHAT.equalsValue(orderInfoEntity.getPaymentChannel())) {
                model.addAttribute("paymentChannel", PaymentChannelEnum.WECHAT.getDesc());
            } else if (PaymentChannelEnum.ALIPAY.equalsValue(orderInfoEntity.getPaymentChannel())) {
                model.addAttribute("paymentChannel", PaymentChannelEnum.ALIPAY.getDesc());
            }

            if (isMobile) {
                return "mobile-qrcode-payment";
            }
            return "qrcode-payment";
        } else {
            if (isMobile) {
                return "mobile-payment";
            }
            return "payment";
        }
    }

//    public ResponseDTO<String> payStatus(OrderDepositPayStatusForm payStatusForm) {
//        String redisKey = RedisKeyConst.Order.ORDER_PAY_TOKEN + payStatusForm.getToken();
//        OrderInfoEntity orderInfoEntity = redisService.getObject(redisKey, OrderInfoEntity.class);
//        if (orderInfoEntity == null) {
//            return ResponseDTO.error(OpenErrorCode.ORDER_PAYMENT_OVERDUE);
//        }
//        if (orderInfoEntity.getPayStatus() == 1) {
//            orderInfoEntity.setPayStatus(payStatusForm.getPayStatus());
//            orderInfoManager.save(orderInfoEntity);
//        }
//        redisService.delete(redisKey);
//        return ResponseDTO.ok();
//    }
//
//    public ResponseDTO<String> paymentAffirm(MultipartFile file, String token) {
//        String redisKey = RedisKeyConst.Order.ORDER_PAY_TOKEN +token;
//        OrderInfoEntity orderInfoEntity = redisService.getObject(redisKey, OrderInfoEntity.class);
//        if (orderInfoEntity == null) {
//            return ResponseDTO.error(OpenErrorCode.ORDER_PAYMENT_OVERDUE);
//        }
//        ResponseDTO<FileUploadVO> fileUploadVOResponseDTO = fileService.fileUpload(file, 1, null);
//        if (!fileUploadVOResponseDTO.getOk()) {
//            return ResponseDTO.error(fileUploadVOResponseDTO);
//        }
//        if (orderInfoEntity.getPayStatus() == 1) {
//            orderInfoEntity.setPayStatus(2);
//            orderInfoEntity.setDepositFileId(fileUploadVOResponseDTO.getData().getFileId());
//            orderInfoManager.save(orderInfoEntity);
//        }
//        redisService.delete(redisKey);
//
//        return ResponseDTO.ok();
//    }

    public void orderDepositReceipt(MultipartFile file, String orderNo) {
        OrderInfoEntity orderInfoEntity = orderInfoManager.queryByOrderNo(orderNo);
        if (orderInfoEntity == null) {
            throw new BusinessException(OpenErrorCode.DATA_NOT_EXIST);
        }
        ResponseDTO<FileUploadVO> responseDTO = fileService.fileUpload(file, FileFolderTypeEnum.COMMON.getValue(), null);
        if (!responseDTO.getOk()) {
            throw new BusinessException(responseDTO.getMsg());
        }
        orderInfoEntity.setReceiptFileId(responseDTO.getData().getFileId());
        orderInfoManager.saveOrUpdate(orderInfoEntity);
    }

    public void orderDepositCancel(String orderNo) {
        log.info("订单取消，orderNo：{}", orderNo);
        OrderInfoEntity orderInfoEntity = orderInfoManager.queryByOrderNo(orderNo);
        if (orderInfoEntity == null) {
            throw new BusinessException(OpenErrorCode.DATA_NOT_EXIST);
        }
        orderInfoEntity.setStatus(3);
        orderInfoEntity.setUpdateTime(LocalDateTime.now());
        orderInfoManager.saveOrUpdate(orderInfoEntity);

        //判断原路由渠道是否为polling方式路由，如果是，则需要反向处理。
        paymentChannelInfoManager.paymentChannelRouteReversed(orderInfoEntity.getPaymentChannelPayInfoId());
    }

    public OrderDepositVO orderDepositSwitch(String orderNo) {
        log.info("订单切换付款码，orderNo：{}", orderNo);
        OrderInfoEntity orderInfoEntity = orderInfoManager.queryByOrderNo(orderNo);
        if (orderInfoEntity == null) {
            throw new BusinessException(OpenErrorCode.DATA_NOT_EXIST);
        }
        boolean isXj = orderInfoEntity.getDepositHolder().length() > 4 && "CNY".equals(orderInfoEntity.getCurrency());

        //判断原路由渠道是否为polling方式路由，如果是，则需要反向处理。
        paymentChannelInfoManager.paymentChannelRouteReversed(orderInfoEntity.getPaymentChannelPayInfoId());

        //路由通道
//        PaymentChannelInfoRouteDTO paymentChannelInfo = paymentChannelInfoManager.getPaymentChannelInfo(orderInfoEntity.getAmount(),
//                orderInfoEntity.getDepositType(), orderInfoEntity.getPaymentChannel(), orderNo, orderInfoEntity.getDepositHolder(),
//                orderInfoEntity.getCurrency(), orderInfoEntity.getCountry(), isXj);
//        orderInfoEntity.setDepartmentId(paymentChannelInfo.getDepartmentId());
//        orderInfoEntity.setCollectionBank(paymentChannelInfo.getBankInfo());
//        orderInfoEntity.setCollectionBankAddress(paymentChannelInfo.getBankName());
//        orderInfoEntity.setCollectionCardNo(paymentChannelInfo.getBankNo());
//        orderInfoEntity.setCollectionHolder(paymentChannelInfo.getUsername());
//        orderInfoEntity.setPaymentChannelBusinessId(paymentChannelInfo.getPaymentChannelBusinessId());
//        orderInfoEntity.setPaymentChannelPayInfoId(paymentChannelInfo.getPaymentChannelPayInfoId());
//        orderInfoManager.saveOrUpdate(orderInfoEntity);
//
//        return SmartBeanUtil.copy(paymentChannelInfo, OrderDepositVO.class);
        return null;
    }


    /**
     * 数据清洗
     */
    public void dataEncrypt() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(() -> {
            List<OrderInfoEntity> orderInfoEntities = orderInfoManager.list();
            for (OrderInfoEntity orderInfoEntity : orderInfoEntities) {
                orderInfoEntity.setDepositHolderHash(DigestUtils.sha256Hex(orderInfoEntity.getDepositHolder()));
            }
            orderInfoManager.updateBatchById(orderInfoEntities);
        });

    }

}
