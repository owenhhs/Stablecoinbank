package net.lab1024.sa.admin.module.openapi.order.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.emumeration.CurrencyEnum;
import net.lab1024.sa.admin.emumeration.LetonggouPaymentFlagEnum;
import net.lab1024.sa.admin.emumeration.PaymentPlatformEnum;
import net.lab1024.sa.admin.module.business.order.domain.entity.CashOrderDetailEntity;
import net.lab1024.sa.admin.module.business.order.domain.entity.CashOrderInfoEntity;
import net.lab1024.sa.admin.module.business.order.domain.entity.PaymentOrderInfoEntity;
import net.lab1024.sa.admin.module.business.order.domain.form.CashOrderManualForm;
import net.lab1024.sa.admin.module.business.order.manager.CashOrderDetailManager;
import net.lab1024.sa.admin.module.business.order.manager.CashOrderInfoManager;
import net.lab1024.sa.admin.module.business.order.manager.PaymentOrderFailRecordManager;
import net.lab1024.sa.admin.module.business.order.manager.PaymentOrderInfoManager;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelInfoEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentUserEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import net.lab1024.sa.admin.module.business.payment.channel.manager.PaymentChannelInfoManager;
import net.lab1024.sa.admin.module.business.payment.channel.manager.PaymentUserManager;
import net.lab1024.sa.admin.module.openapi.order.domain.form.*;
import net.lab1024.sa.admin.module.openapi.order.domain.vo.*;
import net.lab1024.sa.base.common.code.OpenErrorCode;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.domain.RequestMerchant;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.SystemEnvironment;
import net.lab1024.sa.base.common.enumeration.*;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.*;
import net.lab1024.sa.base.constant.RedisKeyConst;
import net.lab1024.sa.base.module.support.config.ConfigKeyEnum;
import net.lab1024.sa.base.module.support.config.ConfigService;
import net.lab1024.sa.base.module.support.file.constant.FileFolderTypeEnum;
import net.lab1024.sa.base.module.support.file.domain.vo.FileUploadVO;
import net.lab1024.sa.base.module.support.file.service.FileService;
import net.lab1024.sa.base.module.support.redis.RedisService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 孙宇
 * @date 2024/07/25 22:47
 */
@Slf4j
@Service
public class OrderService {
    @Resource
    private RedisService redisService;
    @Resource
    private PaymentOrderInfoManager paymentOrderInfoManager;
    @Resource
    private CashOrderInfoManager cashOrderInfoManager;
    @Resource
    private CashOrderDetailManager cashOrderDetailManager;
    @Resource
    private PaymentChannelInfoManager channelInfoManager;
    @Resource
    private OrderPushService orderPushService;
    @Resource
    private FileService fileService;
    @Resource
    private PaymentUserManager paymentUserManager;
    @Resource
    private PaymentOrderFailRecordManager paymentOrderFailRecordManager;
    @Resource
    private PaymentChannelInfoManager paymentChannelInfoManager;
    @Resource
    private ConfigService configService;

    @Value("${zfx.pay.chooseurl}")
    private String chooseUrlPrefix;

    @Value("${zfx.pay.url}")
    private String payUrlPrefix;

    @Value("${zfx.pay.urlv2}")
    private String payUrlPrefixV2;

    /**
     * 订单付款时间，默认15分钟
     */
    @Value("${zfx.pay.time:900}")
    private Long orderPayTime;
    @Value("${zfx.pay.api.domain}")
    private String domain;
    @Value("${zfx.user.sms.phone}")
    private String phone;
    @Resource
    private SystemEnvironment systemEnvironment;


    public ResponseDTO<OrderDepositVO> orderDeposit(RequestMerchant requestMerchant, OrderDepositForm depositForm) throws Exception {
        log.info("orderDeposit, depositForm:{}", depositForm);
        String redisKey = RedisKeyConst.Order.ORDER_DEPOSIT + depositForm.getOrderNo();
        boolean lock = RedisLockUtil.tryLock(redisKey);
        if (!lock) {
            return ResponseDTO.error(OpenErrorCode.REPEAT_SUBMIT);
        }
        try {
            // 查询定时是否已经存在
            PaymentOrderInfoEntity orderInfoEntity = paymentOrderInfoManager.queryByOrderNo(depositForm.getOrderNo());
            if (orderInfoEntity != null) {
                log.warn("订单号已存在，orderNo:{}", depositForm.getOrderNo());
                return ResponseDTO.error(OpenErrorCode.FORM_REPEAT_SUBMIT);
            }

//            //为兼容交易所在depositType=qrcode时不明确paymentChannel的问题 begin
//            if (DepositTypeEnum.QRCODE.getValue().equals(depositForm.getDepositType())) {
//
//                PaymentChannelInfoVO paymentChannelInfoVO = new PaymentChannelInfoVO();
//                paymentChannelInfoVO.setId(-1L);
//                paymentChannelInfoVO.setBusinessId(-1L);
//
//                PaymentApplyResultVO applyResultVO = new PaymentApplyResultVO();
//                applyResultVO.setThirdpartyOrderNo("---");
//                applyResultVO.setSubMerName("---");
//
//                // 保存订单信息
//                orderInfoEntity = saveOrderInfo(paymentChannelInfoVO, depositForm, applyResultVO, true);
//
//                OrderDepositVO orderDepositVO = new OrderDepositVO();
//
//                String token = RandomStringUtils.randomAlphanumeric(32);
//                String orderPayTokenKey = RedisKeyConst.Order.ORDER_PAY_TOKEN + token;
//                redisService.set(orderPayTokenKey, orderInfoEntity, orderPayTime);
//
//                orderDepositVO.setPayUrl(chooseUrlPrefix + token);
//
//                return ResponseDTO.ok(orderDepositVO);
//            }
//            //为兼容交易所在depositType=qrcode时不明确paymentChannel的问题 end

            // 保存用户并替换userid
            PaymentUserEntity userEntity = paymentUserManager.getByUserNameOrUserId(depositForm.getDepositHolder(), depositForm.getUserId());
            depositForm.setUserId(String.valueOf(userEntity.getId()));

            //增加新疆用户支付判断 begin
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
            //增加新疆用户支付判断 end

            Set<Long> channelIdList = new HashSet<>();
            PaymentChannelInfoVO paymentChannelInfoVO = null;
            PaymentApplyResultVO applyResultVO = null;
            // 根据渠道配置逻辑路由，如果申请订单失败，重试3次
            for (int i = 0; i < 3; i++) {
                paymentChannelInfoVO = channelInfoManager.getPaymentChannelInfo(depositForm.getOrderNo(), PaymentTypeEnum.payment,
                        channelIdList, depositForm, isXj);
                if (paymentChannelInfoVO == null) {
                    if (isXj && !isXjForce) {
                        // 未匹配到新疆收款方式，且不强制使用新疆收款方式
                        isXj = false;
                    }
                    log.error("orderNo:{} 未路由到可用支付渠道，当前重试次数:{}", depositForm.getOrderNo(), i);
                    continue;
                }
                channelIdList.add(paymentChannelInfoVO.getId());
                try {
                    applyResultVO = IPaymentPlatformService.get(paymentChannelInfoVO.getImplCode())
                            .apply(paymentChannelInfoVO, depositForm);
                    if (applyResultVO != null) {
                        break;
                    }
                } catch (BusinessException e) {
                    log.error("orderNo:{} BusinessException[{}] 未路由到可用支付渠道，即将重试，当前重试次数:{}", depositForm.getOrderNo(), e.getMessage(), i);
                }
            }
            if (applyResultVO == null) {
                log.warn("未匹配到支付渠道，orderNo:{}", depositForm.getOrderNo());
                return ResponseDTO.error(OpenErrorCode.NO_MATCH_CHANNEL);
            }

            // 保存订单信息
            orderInfoEntity = saveOrderInfo(requestMerchant, paymentChannelInfoVO, depositForm, applyResultVO, userEntity.getId(), false);

            OrderDepositVO orderDepositVO = new OrderDepositVO();
            if (StringUtils.isNotEmpty(applyResultVO.getPaymentUrl())) {
                orderDepositVO.setPayUrl(applyResultVO.getPaymentUrl());
            } else {
                String token = RandomStringUtils.randomAlphanumeric(32);
                String orderPayTokenKey = RedisKeyConst.Order.ORDER_PAY_TOKEN + token;
                redisService.set(orderPayTokenKey, orderInfoEntity, (orderInfoEntity.getExpiredTime() - System.currentTimeMillis()) / 1000);

                if (applyResultVO.isNewCashier()) {
                    String lang = "zh-cn";
                    if (!CurrencyEnum.CNY.equalsValue(depositForm.getCurrency())) {
                        lang = "en";
                    }
                    orderDepositVO.setPayUrl(payUrlPrefixV2 + lang + "/" + token);
                } else {
                    orderDepositVO.setPayUrl(payUrlPrefix + token);
                }
            }
            // 组织订单信息
            getPaymentOrderInfo(orderDepositVO, orderInfoEntity);
            return ResponseDTO.ok(orderDepositVO);
        } catch (Exception e) {
            paymentOrderFailRecordManager.savePaymentOrderFailRecord(depositForm.getOrderNo(), JSONObject.toJSONString(depositForm), e.getMessage());
            orderFailSendSms();
            throw e;
        } finally {
            RedisLockUtil.unlock(redisKey);
        }
    }

    public void getPaymentOrderInfo(OrderDepositVO orderDepositVO, PaymentOrderInfoEntity orderInfoEntity) throws Exception {
        String orderNo = orderInfoEntity.getOrderNo();
        Long channelId = orderInfoEntity.getChannelId();
        // 获取商户信息
        PaymentChannelInfoEntity paymentChannelInfoVO = channelInfoManager.getById(channelId);
        if (paymentChannelInfoVO == null) {
            log.error("收银台 - 商户信息获取不到, orderNo:{}, channelId:{}", orderNo, channelId);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }
        IPaymentPlatformService platformService = IPaymentPlatformService.get(paymentChannelInfoVO.getImplCode());
        PaymentOrderInfoVO paymentOrderInfoVO = platformService.getPaymentOrderInfo(paymentChannelInfoVO, orderNo);
        if (paymentOrderInfoVO == null) {
            return;
        }
        if (Objects.isNull(paymentOrderInfoVO.getOrderInfo())) {
            PaymentOrderInfoVO.OrderInfo orderInfo = new PaymentOrderInfoVO.OrderInfo();
            orderInfo.setAmount(orderInfoEntity.getAmount());
            orderInfo.setPayer(orderInfoEntity.getAccountName());
            orderDepositVO.setOrderInfo(orderInfo);
        }
        orderDepositVO.setQrCodeInfo(paymentOrderInfoVO.getQrCodeInfo());
        if (Objects.nonNull(orderDepositVO.getQrCodeInfo()) &&
                StringUtils.isNotEmpty(orderDepositVO.getQrCodeInfo().getQrcodeContent())) {
            String qrcodeUrl = genQrcode(orderDepositVO.getQrCodeInfo().getQrcodeContent(), orderInfoEntity);
            orderDepositVO.getQrCodeInfo().setQrcode(qrcodeUrl);
            orderDepositVO.getQrCodeInfo().setQrcodeContent(null);
        }
        orderDepositVO.setBankInfo(paymentOrderInfoVO.getBankInfo());
        orderDepositVO.setExpireTime(orderInfoEntity.getExpiredTime());
        orderDepositVO.setServerTime(System.currentTimeMillis());

    }

    private PaymentOrderInfoEntity saveOrderInfo(RequestMerchant requestMerchant, PaymentChannelInfoVO paymentChannelInfoVO, OrderDepositForm depositForm, PaymentApplyResultVO applyResultVO, Long userId, boolean needChooseChnl) {
        PaymentOrderInfoEntity orderInfoEntity = new PaymentOrderInfoEntity();
        orderInfoEntity.setPlatformId(requestMerchant.getId());
        orderInfoEntity.setChannelId(paymentChannelInfoVO.getId());
        orderInfoEntity.setBusinessId(paymentChannelInfoVO.getBusinessId());
        orderInfoEntity.setOrderNo(depositForm.getOrderNo());
        orderInfoEntity.setDepositType(depositForm.getDepositType());
        orderInfoEntity.setPaymentChannel(depositForm.getPaymentChannel());
        orderInfoEntity.setAmount(depositForm.getAmount());
        orderInfoEntity.setAccountName(depositForm.getDepositHolder());
        orderInfoEntity.setAccountNameHash(DigestUtils.sha256Hex(depositForm.getDepositHolder()));
        orderInfoEntity.setBankAccount(depositForm.getBankAccount());
//        orderInfoEntity.setBankCode(depositForm.getBankCode());
        orderInfoEntity.setDepositRemark(depositForm.getDepositRemark());
        orderInfoEntity.setUserId(userId);
        orderInfoEntity.setCallback(depositForm.getCallback());
        orderInfoEntity.setLandingUrl(depositForm.getLandingUrl());
        long applyTime = System.currentTimeMillis();
        orderInfoEntity.setApplyTime(applyTime);

        // 计算订单过期时间，默认1小时
        long orderPayTime = 3600;
        if (!needChooseChnl && StringUtils.isNotEmpty(paymentChannelInfoVO.getExt())) {
            ChannelExtVO extObj = JSONObject.parseObject(paymentChannelInfoVO.getExt(), ChannelExtVO.class);
            Long payTime = extObj.getOrderPayTime();
            if (payTime != null && payTime > 0) {
                orderPayTime = payTime;
            }
        }
        long expiredTime = applyTime + (orderPayTime * 1000);

        orderInfoEntity.setExpiredTime(expiredTime);
        orderInfoEntity.setName(depositForm.getName());
        orderInfoEntity.setClientIp(depositForm.getClientIp());
        orderInfoEntity.setDevice(depositForm.getDevice());
        orderInfoEntity.setEmail(depositForm.getEmail());
        orderInfoEntity.setCurrency(depositForm.getCurrency());
        orderInfoEntity.setCountry(depositForm.getCountry());
        orderInfoEntity.setExt(depositForm.getExt());
        orderInfoEntity.setThirdpartyOrderNo(applyResultVO.getThirdpartyOrderNo());
        orderInfoEntity.setSubMerName(applyResultVO.getSubMerName());
        if (needChooseChnl) {
            orderInfoEntity.setPaymentStatus(0); //0--待选择渠道
        } else {
            orderInfoEntity.setPaymentStatus(1);
        }
        orderInfoEntity.setStatus(1);
        orderInfoEntity.setCreateTime(LocalDateTime.now());
        orderInfoEntity.setUpdateTime(LocalDateTime.now());
        paymentOrderInfoManager.save(orderInfoEntity);
        return orderInfoEntity;
    }

    private PaymentOrderInfoEntity updateOrderInfoAfterChoose(PaymentOrderInfoEntity orderInfoEntity, PaymentChannelInfoVO paymentChannelInfoVO, PaymentApplyResultVO applyResultVO) {
        orderInfoEntity.setChannelId(paymentChannelInfoVO.getId());
        orderInfoEntity.setBusinessId(paymentChannelInfoVO.getBusinessId());
        orderInfoEntity.setThirdpartyOrderNo(applyResultVO.getThirdpartyOrderNo());
        orderInfoEntity.setSubMerName(applyResultVO.getSubMerName());

        // 此时订单才生效，需要更新applyTime
        long applyTime = System.currentTimeMillis();
        orderInfoEntity.setApplyTime(applyTime);

        // 计算订单过期时间，默认1小时
        long orderPayTime = 3600;
        if (StringUtils.isNotEmpty(paymentChannelInfoVO.getExt())) {
            ChannelExtVO extObj = JSONObject.parseObject(paymentChannelInfoVO.getExt(), ChannelExtVO.class);
            orderPayTime = extObj.getOrderPayTime();
        }
        long expiredTime = applyTime + (orderPayTime * 1000);
        orderInfoEntity.setExpiredTime(expiredTime);

        orderInfoEntity.setPaymentStatus(1);
        orderInfoEntity.setStatus(1);
        orderInfoEntity.setUpdateTime(LocalDateTime.now());

        paymentOrderInfoManager.updateById(orderInfoEntity);
        return orderInfoEntity;
    }

    public ResponseDTO<OrderDepositDetailsVO> orderDepositDetail(RequestMerchant requestMerchant, OrderDepositDetailsForm depositForm) {
        log.info("orderDepositDetail, orderNo:{}", depositForm.getOrderNo());
        PaymentOrderInfoEntity orderInfoEntity = paymentOrderInfoManager.queryByOrderNo(depositForm.getOrderNo());
        if (orderInfoEntity == null) {
            return ResponseDTO.error(OpenErrorCode.DATA_NOT_EXIST);
        }
        PaymentChannelInfoEntity paymentChannelInfoVO = channelInfoManager.getById(orderInfoEntity.getChannelId());
        if (paymentChannelInfoVO == null) {
            return ResponseDTO.error(OpenErrorCode.DATA_NOT_EXIST);
        }
        if (PaymentPlatformEnum.ltg.equalsValue(paymentChannelInfoVO.getImplCode())) {
            return ResponseDTO.error(OpenErrorCode.LTG_ORDER_UNSUPPORTED);
        }
        if (PaymentPlatformEnum.np.equalsValue(paymentChannelInfoVO.getImplCode())) {
            return ResponseDTO.error(OpenErrorCode.NP_ORDER_UNSUPPORTED);
        }

        IPaymentPlatformService platformService = IPaymentPlatformService.get(paymentChannelInfoVO.getImplCode());
        ReceiverInfoVO receiverInfoVO = platformService.getReceiverInfo(orderInfoEntity.getOrderNo());

        PaymentOrderInfoEntity orderInfoNew = new PaymentOrderInfoEntity();
        orderInfoNew.setId(orderInfoEntity.getId());

        if (orderInfoEntity.getStatus() == 1) {
            OrderDetailVO orderDetailVO = platformService.getPaymentOrderDetail(paymentChannelInfoVO, orderInfoEntity.getOrderNo());
            if (orderDetailVO.getStatus().equals(2)) {
                orderInfoNew.setPaymentStatus(2);
            }
            orderInfoNew.setStatus(orderDetailVO.getStatus());
            orderInfoNew.setUpdateTime(LocalDateTime.now());
            paymentOrderInfoManager.updateById(orderInfoNew);
        }

        OrderDepositDetailsVO orderDepositDetailsVO = new OrderDepositDetailsVO();

        orderDepositDetailsVO.setOrderNo(orderInfoEntity.getOrderNo());
        orderDepositDetailsVO.setDepositType(orderInfoEntity.getDepositType());
        orderDepositDetailsVO.setDepositRemark(orderInfoEntity.getDepositRemark());
        orderDepositDetailsVO.setDepositHolder(orderInfoEntity.getAccountName());
        orderDepositDetailsVO.setAmount(orderInfoEntity.getAmount());
        if (StringUtils.isNotEmpty(receiverInfoVO.getBankAccountName())) {
            orderDepositDetailsVO.setAccount(receiverInfoVO.getBankAccount());
            orderDepositDetailsVO.setName(receiverInfoVO.getBankAccountName());
            orderDepositDetailsVO.setBank(receiverInfoVO.getBankName());
        } else if (StringUtils.isNotEmpty(receiverInfoVO.getOwnerAccount())) {
            orderDepositDetailsVO.setAccount(receiverInfoVO.getOwnerAccount());
            orderDepositDetailsVO.setName(receiverInfoVO.getOwnerName());
        }
        LocalDateTime applyTime = SmartLocalDateUtil.getDateForTimestamp(orderInfoEntity.getApplyTime());
        orderDepositDetailsVO.setTime(applyTime);
        orderDepositDetailsVO.setStatus(orderInfoNew.getStatus());
        orderDepositDetailsVO.setPayStatus(orderInfoNew.getPaymentStatus());
        orderDepositDetailsVO.setUpdateTime(orderInfoNew.getUpdateTime());
        orderDepositDetailsVO.setExt(orderInfoEntity.getExt());

        return ResponseDTO.ok(orderDepositDetailsVO);
    }

    public String paymentPage(String token, Model model, String userAgent) throws Exception {
        String pageName = "404";
        String redisKey = RedisKeyConst.Order.ORDER_PAY_TOKEN + token;
        PaymentOrderInfoEntity orderInfoEntity = redisService.getObject(redisKey, PaymentOrderInfoEntity.class);
        if (orderInfoEntity == null) {
            log.error("payment page token not exist, redisKey:{}", redisKey);
            return pageName;
        }
        log.info("payment page, orderNo:{}, token:{}", orderInfoEntity.getOrderNo(), token);
        orderInfoEntity = paymentOrderInfoManager.getById(orderInfoEntity.getId());

        PaymentChannelInfoEntity paymentChannelInfoVO = channelInfoManager.getById(orderInfoEntity.getChannelId());
        if (paymentChannelInfoVO == null) {
            log.error("payment page channel not exist, redisKey:{}", redisKey);
            return pageName;
        }

        long remainingTime = orderInfoEntity.getExpiredTime() - System.currentTimeMillis();

        model.addAttribute("token", token);
        model.addAttribute("price", orderInfoEntity.getAmount());
        model.addAttribute("time", Math.max(remainingTime / 1000, 0));
        model.addAttribute("callback", orderInfoEntity.getLandingUrl());
        model.addAttribute("domain", domain);
        model.addAttribute("payer", orderInfoEntity.getAccountName());
        model.addAttribute("showRefresh", false);

        ReceiverInfoVO receiverInfoVO = IPaymentPlatformService.get(paymentChannelInfoVO.getImplCode())
                .getReceiverInfo(orderInfoEntity.getOrderNo());

        boolean isMobile = DeviceUtil.isMobile(userAgent);
        boolean isCny = CurrencyEnum.CNY.equalsValue(orderInfoEntity.getCurrency());
        String i18n = isCny ? "" : "-en";

        if (DepositTypeEnum.QRCODE.equalsValue(orderInfoEntity.getDepositType())) {
            model.addAttribute("paymentChannel", isCny ? PaymentChannelEnum.valueOf(orderInfoEntity.getPaymentChannel()).getDesc() : orderInfoEntity.getPaymentChannel());
        }

        if (PaymentPlatformEnum.ltg.equals(receiverInfoVO.getChannel())) {
            log.info("payment page imgUrl:{}", receiverInfoVO.getImgUrl());
            model.addAttribute("qrcode", receiverInfoVO.getImgUrl());
            pageName = (isMobile ? "mobile-" : "") + "qrcode-payment" + i18n;
        } else if (PaymentPlatformEnum.suding.equals(receiverInfoVO.getChannel())) {
            if (StringUtils.isNotEmpty(receiverInfoVO.getImgUrl())) {
                model.addAttribute("qrcode", receiverInfoVO.getImgUrl());
            } else if (StringUtils.isNotEmpty(receiverInfoVO.getPayUrl())) {
                String qrcodeUrl = genQrcode(receiverInfoVO.getPayUrl(), orderInfoEntity);
                model.addAttribute("qrcode", qrcodeUrl);
                model.addAttribute("paymentChannel", isCny ? PaymentChannelEnum.Union.getDesc() : PaymentChannelEnum.Union.getValue());
            }
            pageName = (isMobile ? "mobile-" : "") + "qrcode-payment" + i18n;

        } else if (PaymentPlatformEnum.letonggou.equals(receiverInfoVO.getChannel()) || PaymentPlatformEnum.styfwl.equals(receiverInfoVO.getChannel())) {
            String qrcodeUrl = null;
            if (StringUtils.isNotEmpty(receiverInfoVO.getPageUrl())) {
                qrcodeUrl = genQrcode(receiverInfoVO.getPageUrl(), orderInfoEntity);
            } else if (StringUtils.isNotEmpty(receiverInfoVO.getWechatUrl())) {
                qrcodeUrl = genQrcode(receiverInfoVO.getWechatUrl(), orderInfoEntity);
            } else if (StringUtils.isNotEmpty(receiverInfoVO.getOwnerQrcode())) {
                qrcodeUrl = genQrcode(receiverInfoVO.getOwnerQrcode(), orderInfoEntity);
            }
            if (StringUtils.isEmpty(qrcodeUrl)) {
                return "error";
            }
            model.addAttribute("qrcode", qrcodeUrl);
            pageName = (isMobile ? "mobile-" : "") + "qrcode-payment" + i18n;

        } else if (PaymentPlatformEnum.fxmch.equals(receiverInfoVO.getChannel())) {
            model.addAttribute("account", receiverInfoVO.getBankAccountName());
            if (StringUtils.isNotEmpty(receiverInfoVO.getPayUrl())) {
                model.addAttribute("qrcode", receiverInfoVO.getPayUrl());
                model.addAttribute("accountNo", receiverInfoVO.getBankAccount());
                model.addAttribute("showRefresh", true);

                pageName = (isMobile ? "mobile-" : "") + "qrcode-payment" + i18n;
            } else {
                // 收款人信息
                model.addAttribute("address", receiverInfoVO.getBankBranch());
                model.addAttribute("bank", receiverInfoVO.getBankName());
                model.addAttribute("cardno", receiverInfoVO.getBankAccount());

                pageName = (isMobile ? "mobile-" : "") + "payment" + i18n;
            }
        }
        log.info("payment page, pageName:{}", pageName);
        return pageName;
    }

    public String paymentChoosePage(String token, Model model, String userAgent) throws Exception {
        String redisKey = RedisKeyConst.Order.ORDER_PAY_TOKEN + token;
        PaymentOrderInfoEntity orderInfoEntity = redisService.getObject(redisKey, PaymentOrderInfoEntity.class);
        if (orderInfoEntity == null) {
            return "404";
        }
        orderInfoEntity = paymentOrderInfoManager.getById(orderInfoEntity.getId());

        if (orderInfoEntity.getPaymentStatus() != 0) {
            return "404";
        }

        long remainingTime = (orderPayTime * 1000) - (System.currentTimeMillis() - orderInfoEntity.getApplyTime());

        model.addAttribute("token", token);
        model.addAttribute("price", orderInfoEntity.getAmount());
        model.addAttribute("time", Math.max(remainingTime / 1000, 0));
        model.addAttribute("callback", orderInfoEntity.getLandingUrl());
        model.addAttribute("domain", domain);
        model.addAttribute("payer", orderInfoEntity.getAccountName());
        model.addAttribute("showRefresh", false);

        boolean isMobile = userAgent != null && (userAgent.matches(".*Mobile.*")
                || userAgent.matches(".*iPhone.*")
                || userAgent.matches(".*Android.*")
                || userAgent.matches(".*iPad.*")
                || userAgent.matches(".*iPod.*"));

        boolean isCny = CurrencyEnum.CNY.equalsValue(orderInfoEntity.getCurrency());
        String i18n = "";
        if (!isCny) {
            i18n = "-en";
        }

        if (isMobile) {
            return "mobile-qrcode-payment-choose" + i18n;
        }
        return "qrcode-payment-choose" + i18n;
    }

    //为兼容交易所在depositType=qrcode时不明确paymentChannel的问题
    public ResponseDTO<OrderDepositVO> paymentChoosed(String token, String paymentChannel) throws Exception {

        String redisKey = RedisKeyConst.Order.ORDER_PAY_TOKEN + token;
        PaymentOrderInfoEntity orderInfoEntity = redisService.getObject(redisKey, PaymentOrderInfoEntity.class);
        if (orderInfoEntity == null) {
            return ResponseDTO.error(OpenErrorCode.ORDER_PAYMENT_OVERDUE_WEB);
        }
        orderInfoEntity = paymentOrderInfoManager.getById(orderInfoEntity.getId());

        log.info("paymentChoosed, orderInfo:{}", orderInfoEntity);
        String redisKeyLock = RedisKeyConst.Order.ORDER_DEPOSIT + orderInfoEntity.getOrderNo();
        boolean lock = RedisLockUtil.tryLock(redisKeyLock);
        if (!lock) {
            return ResponseDTO.error(OpenErrorCode.REPEAT_SUBMIT);
        }
        try {
            if (0 != orderInfoEntity.getPaymentStatus()) {
                return ResponseDTO.error(OpenErrorCode.ORDER_PAYMENT_OVERDUE_WEB);
            }

            if (!DepositTypeEnum.QRCODE.getValue().equals(orderInfoEntity.getDepositType())) {
                return ResponseDTO.error(OpenErrorCode.ORDER_PAYMENT_OVERDUE_WEB);
            }

            orderInfoEntity.setPaymentChannel(paymentChannel);

            OrderDepositForm depositForm = new OrderDepositForm();
            depositForm.setOrderNo(orderInfoEntity.getOrderNo());
            depositForm.setDepositType(orderInfoEntity.getDepositType());
            depositForm.setPaymentChannel(orderInfoEntity.getPaymentChannel());
            depositForm.setAmount(orderInfoEntity.getAmount());
            depositForm.setCurrency(orderInfoEntity.getCurrency());
            depositForm.setCountry(orderInfoEntity.getCountry());
            depositForm.setDepositHolder(orderInfoEntity.getAccountName());
            depositForm.setBankAccount(orderInfoEntity.getBankAccount());
            if (StringUtils.isNotEmpty(orderInfoEntity.getDepositRemark())) {
                depositForm.setDepositRemark(orderInfoEntity.getDepositRemark());
            }
            depositForm.setCallback(orderInfoEntity.getCallback());
            depositForm.setLandingUrl(orderInfoEntity.getLandingUrl());
            depositForm.setName(orderInfoEntity.getName());
            depositForm.setClientIp(orderInfoEntity.getClientIp());
            depositForm.setDevice(orderInfoEntity.getDevice());
            depositForm.setEmail(orderInfoEntity.getEmail());
            depositForm.setExt(orderInfoEntity.getExt());

            //增加新疆用户支付判断 begin
            boolean isXj = depositForm.getDepositHolder().length() > 4 && "CNY".equals(depositForm.getCurrency());
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
            //增加新疆用户支付判断 end

            Set<Long> channelIdList = new HashSet<>();
            PaymentChannelInfoVO paymentChannelInfoVO = null;
            PaymentApplyResultVO applyResultVO = null;
            // 根据渠道配置逻辑路由，如果申请订单失败，重试3次
            for (int i = 0; i < 3; i++) {
                paymentChannelInfoVO = channelInfoManager.getPaymentChannelInfo(depositForm.getOrderNo(), PaymentTypeEnum.payment,
                        channelIdList, depositForm, isXj);
                if (paymentChannelInfoVO == null) {
                    if (!isXj || isXjForce) {
                        log.error("paymentChoosed:: orderNo:{} 未路由到可用支付渠道，当前重试次数:{}，退出(0)！", depositForm.getOrderNo(), i);
                        return ResponseDTO.error(OpenErrorCode.NO_MATCH_CHANNEL);
                    }
                    // 未匹配到新疆收款方式，且不强制使用新疆收款方式
                    paymentChannelInfoVO = channelInfoManager.getPaymentChannelInfo(depositForm.getOrderNo(), PaymentTypeEnum.payment,
                            channelIdList, depositForm, false);
                }
                if (paymentChannelInfoVO == null) {
                    log.error("paymentChoosed:: orderNo:{} 未路由到可用支付渠道，当前重试次数:{}，退出(1)！", depositForm.getOrderNo(), i);
                    return ResponseDTO.error(OpenErrorCode.NO_MATCH_CHANNEL);
                }
                channelIdList.add(paymentChannelInfoVO.getId());
                try {
                    applyResultVO = IPaymentPlatformService.get(paymentChannelInfoVO.getImplCode())
                            .apply(paymentChannelInfoVO, depositForm);
                    if (applyResultVO != null) {
                        break;
                    }
                } catch (BusinessException e) {
                    log.error("未路由到可用支付渠道，即将重试，当前重试次数:{}", i);
                }
            }
            if (applyResultVO == null) {
                log.warn("未匹配到支付渠道，orderNo:{}", depositForm.getOrderNo());
                return ResponseDTO.error(OpenErrorCode.NO_MATCH_CHANNEL);
            }

            // 保存订单信息
            orderInfoEntity = updateOrderInfoAfterChoose(orderInfoEntity, paymentChannelInfoVO, applyResultVO);

            OrderDepositVO orderDepositVO = new OrderDepositVO();
            if (StringUtils.isNotEmpty(applyResultVO.getPaymentUrl())) {
                orderDepositVO.setPayUrl(applyResultVO.getPaymentUrl());
            } else {
                orderDepositVO.setPayUrl(payUrlPrefix + token);
            }
            return ResponseDTO.ok(orderDepositVO);
        } finally {
            RedisLockUtil.unlock(redisKeyLock);
        }
    }


    private String genQrcode(String content, PaymentOrderInfoEntity orderInfoEntity) throws Exception {

        if (orderInfoEntity.getQrocdeId() != null && orderInfoEntity.getQrocdeId() != 0) {
            ResponseDTO<String> fileResponse = fileService.getFileUrlByFileId(orderInfoEntity.getQrocdeId());
            return fileResponse.getData();
        }

        File file = null;
        try {
            file = QrcodeUtil.generateQRCode(content, 300, 300, "png", "temp1_" + orderInfoEntity.getPaymentChannel());
            ResponseDTO<FileUploadVO> responseDTO = fileService.fileUploadLocalFile(file, 1);

            PaymentOrderInfoEntity orderInfoNew = new PaymentOrderInfoEntity();
            orderInfoNew.setId(orderInfoEntity.getId());

            orderInfoNew.setQrocdeId(responseDTO.getData().getFileId());
            orderInfoNew.setUpdateTime(LocalDateTime.now());
            paymentOrderInfoManager.updateById(orderInfoNew);

            return responseDTO.getData().getFileUrl();
        } finally {
            if (file != null) {
                file.deleteOnExit();
            }
        }
    }

    public ResponseDTO<String> payStatus(OrderDepositPayStatusForm payStatusForm) {
        String redisKey = RedisKeyConst.Order.ORDER_PAY_TOKEN + payStatusForm.getToken();
        PaymentOrderInfoEntity orderInfoEntity = redisService.getObject(redisKey, PaymentOrderInfoEntity.class);
        if (orderInfoEntity == null) {
            return ResponseDTO.error(OpenErrorCode.ORDER_PAYMENT_OVERDUE);
        }
        log.info("订单取消支付 orderNo:{}, payStatus:{}", orderInfoEntity.getOrderNo(), payStatusForm.getPayStatus());
        orderInfoEntity = paymentOrderInfoManager.getById(orderInfoEntity.getId());
        if (orderInfoEntity.getPaymentStatus().equals(payStatusForm.getPayStatus())) {
            redisService.delete(redisKey);
            return ResponseDTO.ok();
        }
        if (orderInfoEntity.getPaymentStatus() > 1 || orderInfoEntity.getStatus() > 1) {
            String msg = String.format("订单%s，请勿重复操作", SmartEnumUtil.getEnumByValue(orderInfoEntity.getPaymentStatus(), OrderPayStatusEnum.class));
            if (orderInfoEntity.getStatus() > 1) {
                msg = String.format("订单%s，请勿重复操作", SmartEnumUtil.getEnumByValue(orderInfoEntity.getStatus(), OrderStatusEnum.class));
            }
            return ResponseDTO.error(UserErrorCode.ORDER_STATUS_COMPLETE, msg);
        }

        PaymentOrderInfoEntity orderInfoNew = new PaymentOrderInfoEntity();
        orderInfoNew.setId(orderInfoEntity.getId());

        orderInfoNew.setPaymentStatus(payStatusForm.getPayStatus());
        if (payStatusForm.getPayStatus() > 2) {
            orderInfoNew.setStatus(payStatusForm.getPayStatus());
        } else {
            orderInfoNew.setStatus(orderInfoEntity.getStatus());
        }
        orderInfoNew.setUpdateTime(LocalDateTime.now());
        paymentOrderInfoManager.updateById(orderInfoNew);

        PaymentChannelInfoEntity paymentChannelInfoVO = channelInfoManager.getById(orderInfoEntity.getChannelId());
        if (paymentChannelInfoVO == null) {
            return ResponseDTO.error(OpenErrorCode.SYSTEM_ERROR);
        }

        IPaymentPlatformService platformService = IPaymentPlatformService.get(paymentChannelInfoVO.getImplCode());
        platformService.paymentCancel(paymentChannelInfoVO, orderInfoEntity.getOrderNo());

        try {
            orderPushService.paymentOrderPush(orderInfoEntity.getPlatformId(), orderInfoEntity.getOrderNo(), orderInfoNew.getStatus(),
                    orderInfoNew.getPaymentStatus(), orderInfoEntity.getCallback(), orderInfoEntity.getExt());
        } catch (Exception e) {
            log.error("payStatus orderPush channelId:{}, orderNo:{}, exception", orderInfoEntity.getChannelId(),
                    orderInfoEntity.getOrderNo(), e);
            return ResponseDTO.userErrorParam("系统异常");
        }
        redisService.delete(redisKey);
        return ResponseDTO.ok();
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> paymentAffirm(String token, MultipartFile file) {
        String redisKey = RedisKeyConst.Order.ORDER_PAY_TOKEN + token;
        PaymentOrderInfoEntity orderInfoEntity = redisService.getObject(redisKey, PaymentOrderInfoEntity.class);
        if (orderInfoEntity == null) {
            return ResponseDTO.error(OpenErrorCode.ORDER_PAYMENT_OVERDUE);
        }
        log.info("收银台 - 确认付款 orderNo:{}", orderInfoEntity.getOrderNo());
        String redisLock = RedisKeyConst.Order.ORDER_STATUS_UPDATE_LOCK + orderInfoEntity.getOrderNo();
        boolean lock = RedisLockUtil.tryLock(redisLock);
        if (!lock) {
            return ResponseDTO.error(OpenErrorCode.REPEAT_SUBMIT);
        }
        try {
            orderInfoEntity = paymentOrderInfoManager.getById(orderInfoEntity.getId());
            if (orderInfoEntity.getPaymentStatus() > 2) {
                String msg = String.format("订单%s，请勿重复操作", SmartEnumUtil.getEnumByValue(orderInfoEntity.getPaymentStatus(), OrderPayStatusEnum.class));
                if (orderInfoEntity.getStatus() > 2) {
                    msg = String.format("订单%s，请勿重复操作", SmartEnumUtil.getEnumByValue(orderInfoEntity.getStatus(), OrderStatusEnum.class));
                }
                redisService.delete(redisKey);
                return ResponseDTO.error(UserErrorCode.ORDER_STATUS_COMPLETE, msg);
            }
            PaymentOrderInfoEntity orderInfoNew = new PaymentOrderInfoEntity();
            orderInfoNew.setId(orderInfoEntity.getId());
            if (orderInfoEntity.getPaymentStatus() == 1) {
                orderInfoNew.setPaymentStatus(2);
                orderInfoNew.setUpdateTime(LocalDateTime.now());
                paymentOrderInfoManager.updateById(orderInfoNew);
            }
        } finally {
            RedisLockUtil.unlock(redisLock);
        }
        ResponseDTO<FileUploadVO> fileResponseDTO = fileService.fileUpload(file, FileFolderTypeEnum.PAYMENT_BILL.getValue(), null);
        if (!fileResponseDTO.getOk() || fileResponseDTO.getData() == null) {
            throw new BusinessException(OpenErrorCode.FILE_UPLOAD_FAIL);
        }
        PaymentOrderInfoEntity orderInfoNew = new PaymentOrderInfoEntity();
        orderInfoNew.setId(orderInfoEntity.getId());
        orderInfoNew.setBillFileId(fileResponseDTO.getData().getFileId());
        orderInfoNew.setUpdateTime(LocalDateTime.now());
        paymentOrderInfoManager.updateById(orderInfoNew);

        PaymentChannelInfoEntity paymentChannelInfoVO = channelInfoManager.getById(orderInfoEntity.getChannelId());
        if (paymentChannelInfoVO == null) {
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }
        IPaymentPlatformService platformService = IPaymentPlatformService.get(paymentChannelInfoVO.getImplCode());
        platformService.paymentReceipt(paymentChannelInfoVO, orderInfoEntity.getOrderNo(), file);

        redisService.delete(redisKey);
        return ResponseDTO.ok();
    }


    public Map<String, String> notifyNoahPaymentOrder(Map<String, Object> params) {
        String orderNo = params.get("cus_order_sn").toString();
        Map<String, String> map = new HashMap<>(1);

        String redisLock = RedisKeyConst.Order.ORDER_STATUS_UPDATE_LOCK + orderNo;
        boolean lock = RedisLockUtil.tryLock(redisLock);
        if (!lock) {
            map.put("result", "error");
            map.put("message", "Concurrency limitation");
            return map;
        }
        try {
            PaymentOrderInfoEntity orderInfoEntity = paymentOrderInfoManager.queryByOrderNo(orderNo);
            if (orderInfoEntity == null) {
                map.put("result", "error");
                map.put("message", "not found");
                return map;
            }
            PaymentChannelInfoEntity paymentChannelInfoVO = channelInfoManager.getById(orderInfoEntity.getChannelId());
            if (paymentChannelInfoVO == null) {
                map.put("result", "error");
                map.put("message", "not found");
                return map;
            }

            IPaymentPlatformService platformService = IPaymentPlatformService.get(paymentChannelInfoVO.getImplCode());
            boolean signVerify = platformService.signVerify(paymentChannelInfoVO, params);
            if (!signVerify) {
                map.put("result", "error");
                map.put("message", "Signature verification failure");
                return map;
            }

            PaymentOrderInfoEntity orderInfoNew = new PaymentOrderInfoEntity();
            orderInfoNew.setId(orderInfoEntity.getId());

            if ("success".equals(params.get("result"))) {
                orderInfoNew.setPaymentStatus(2);
                orderInfoNew.setStatus(2);
            } else {
                orderInfoNew.setStatus(3);
            }
            orderInfoNew.setFinishedTime(System.currentTimeMillis());
            orderInfoNew.setUpdateTime(LocalDateTime.now());
            paymentOrderInfoManager.updateById(orderInfoNew);

            try {
                orderPushService.paymentOrderPush(orderInfoEntity.getPlatformId(), orderInfoEntity.getOrderNo(), orderInfoNew.getStatus(),
                        orderInfoNew.getPaymentStatus(), orderInfoEntity.getCallback(), orderInfoEntity.getExt());
            } catch (Exception e) {
                log.error("noah orderPush channelId:{}, orderNo:{}, exception", paymentChannelInfoVO.getId(), orderNo, e);
                map.put("result", "error");
                map.put("message", "system error");
                return map;
            }

            map.put("result", "success");
            return map;
        } finally {
            RedisLockUtil.unlock(redisLock);
        }
    }


    public String notifyLetonggouPaymentOrder(Integer pid, String tradeNo, String outTradeNo, String type,
                                              String name, String money, String tradeStatus, String param,
                                              String sign, String signType) {
        log.info("notifyLetonggouPaymentOrder, pid:{}, tradeNo:{}, outTradeNo:{}, type:{}, name:{}, money:{}, " +
                "tradeStatus:{}, param:{}, sign:{}, signType:{}", pid, tradeNo, outTradeNo, type, name, money, tradeStatus, param, sign, signType);
        String redisLock = RedisKeyConst.Order.ORDER_STATUS_UPDATE_LOCK + outTradeNo;
        boolean lock = RedisLockUtil.tryLock(redisLock);
        if (!lock) {
            log.info("notifyLetonggouPaymentOrder, lock tradeNo:{}", tradeNo);
            return "Concurrency limitation";
        }
        try {
            Map<String, Object> params = new HashMap<>(1);
            params.put("pid", pid);
            params.put("trade_no", tradeNo);
            params.put("out_trade_no", outTradeNo);
            params.put("type", type);
            params.put("name", name);
            params.put("money", money);
            params.put("trade_status", tradeStatus);
            params.put("param", param);
            params.put("sign", sign);
            params.put("sign_type", signType);

            log.info("notifyLetonggouPaymentOrder params:{}", params);

            PaymentOrderInfoEntity orderInfoEntity = paymentOrderInfoManager.queryByOrderNo(outTradeNo);
            if (orderInfoEntity == null) {
                log.info("notifyLetonggouPaymentOrder, order not found tradeNo:{}", tradeNo);
                return "not found";
            }
            PaymentChannelInfoEntity paymentChannelInfoVO = channelInfoManager.getById(orderInfoEntity.getChannelId());
            if (paymentChannelInfoVO == null) {
                log.info("notifyLetonggouPaymentOrder, channel not found tradeNo:{}", tradeNo);
                return "not found";
            }

            IPaymentPlatformService platformService = IPaymentPlatformService.get(paymentChannelInfoVO.getImplCode());
            boolean signVerify = platformService.signVerify(paymentChannelInfoVO, params);
            if (!signVerify) {
                log.info("notifyLetonggouPaymentOrder, Signature verification failure tradeNo:{}", tradeNo);
                return "Signature verification failure";
            }

            OrderDetailVO orderDetailVO = platformService.updateOrderDetail(paymentChannelInfoVO, params);
            PaymentOrderInfoEntity orderInfoNew = new PaymentOrderInfoEntity();
            orderInfoNew.setId(orderInfoEntity.getId());
            if (orderDetailVO.getStatus().equals(2)) {
                orderInfoNew.setPaymentStatus(2);
            }
            orderInfoNew.setStatus(orderDetailVO.getStatus());
            if (StringUtils.isNotEmpty(orderDetailVO.getEndtime())) {
                long endTime = SmartLocalDateUtil.dateToTimestamp(orderDetailVO.getEndtime());
                if (endTime > 0) {
                    orderInfoNew.setFinishedTime(endTime);
                }
            }
            if (!systemEnvironment.isProd()) {
                if (tradeStatus.equals("TRADE_SUCCESS")) {
                    orderInfoNew.setPaymentStatus(2);
                    orderInfoNew.setStatus(2);
                } else {
                    orderInfoNew.setStatus(3);
                }
                orderInfoNew.setFinishedTime(System.currentTimeMillis());
            }
            orderInfoNew.setUpdateTime(LocalDateTime.now());
            paymentOrderInfoManager.updateById(orderInfoNew);

            try {
                orderPushService.paymentOrderPush(orderInfoEntity.getPlatformId(), orderInfoEntity.getOrderNo(), orderInfoNew.getStatus(),
                        orderInfoNew.getPaymentStatus(), orderInfoEntity.getCallback(), orderInfoEntity.getExt());
            } catch (Exception e) {
                log.error("Letonggou orderPush channelId:{}, orderNo:{}, exception", paymentChannelInfoVO.getId(), outTradeNo, e);
                return "system error";
            }
            return "success";
        } catch (Exception e) {
            log.error("notifyLetonggouPaymentOrder, orderNo:{}, exception", outTradeNo, e);
            return "error";
        } finally {
            RedisLockUtil.unlock(redisLock);
        }
    }


    public ResponseDTO notifyFxmchPaymentOrder(String orderNo, Integer status, String authorization, String date) {
        log.info("notifyFxmchPaymentOrder orderNo:{}, status:{}", orderNo, status);
        String redisLock = RedisKeyConst.Order.ORDER_STATUS_UPDATE_LOCK + orderNo;
        boolean lock = RedisLockUtil.tryLock(redisLock);
        if (!lock) {
            return ResponseDTO.error(OpenErrorCode.REPEAT_SUBMIT);
        }
        try {
            PaymentOrderInfoEntity orderInfoEntity = paymentOrderInfoManager.queryByOrderNo(orderNo);
            if (orderInfoEntity == null) {
                return ResponseDTO.userErrorParam("订单不存在");
            }
            PaymentChannelInfoEntity paymentChannelInfoVO = channelInfoManager.getById(orderInfoEntity.getChannelId());
            if (paymentChannelInfoVO == null) {
                return ResponseDTO.userErrorParam("渠道不存在");
            }
            Map<String, Object> params = new HashMap<>(4);
            params.put("orderNo", orderNo);
            params.put("status", status);
            params.put("authorization", authorization);
            params.put("date", date);
            IPaymentPlatformService platformService = IPaymentPlatformService.get(paymentChannelInfoVO.getImplCode());
            boolean signVerify = platformService.signVerify(paymentChannelInfoVO, params);
            if (!signVerify) {
                return ResponseDTO.userErrorParam("签名验证失败");
            }

            OrderDetailVO orderDetailVO = platformService.updateOrderDetail(paymentChannelInfoVO, params);
            PaymentOrderInfoEntity orderInfoNew = new PaymentOrderInfoEntity();
            orderInfoNew.setId(orderInfoEntity.getId());
            orderInfoNew.setPaymentStatus(orderInfoEntity.getPaymentStatus());

            if (orderDetailVO.getStatus().equals(2)) {
                orderInfoNew.setPaymentStatus(2);
            }
            orderInfoNew.setStatus(orderDetailVO.getStatus());
            if (StringUtils.isNotEmpty(orderDetailVO.getEndtime())) {
                long endTime = SmartLocalDateUtil.dateToTimestamp(orderDetailVO.getEndtime());
                if (endTime > 0) {
                    orderInfoNew.setFinishedTime(endTime);
                }
            }
            orderInfoNew.setUpdateTime(LocalDateTime.now());
            boolean updateResult = paymentOrderInfoManager.updateById(orderInfoNew);
            log.info("notifyFxmchPaymentOrder updateResult:{}", updateResult);

            // 如果状态是过期，则不回调
            if (orderInfoNew.getPaymentStatus().equals(4) || orderInfoNew.getStatus().equals(4)) {
                return ResponseDTO.ok();
            }

            try {
                orderPushService.paymentOrderPush(orderInfoEntity.getPlatformId(), orderInfoEntity.getOrderNo(), orderInfoNew.getStatus(),
                        orderInfoNew.getPaymentStatus(), orderInfoEntity.getCallback(), orderInfoEntity.getExt());
            } catch (Exception e) {
                log.error("fxmch orderPush channelId:{}, orderNo:{}, exception", paymentChannelInfoVO.getId(), orderNo, e);
                return ResponseDTO.userErrorParam("系统异常");
            }
            return ResponseDTO.ok();
        } finally {
            RedisLockUtil.unlock(redisLock);
        }
    }

    public ResponseDTO notifyPayrockPaymentOrder(String cartId,
                                                 String orderAmount,
                                                 String originalAmount,
                                                 String signature,
                                                 String orderTime,
                                                 String completedTime,
                                                 String paymentReference,
                                                 String currency,
                                                 String version,
                                                 String statusMessage,
                                                 String status) {
        log.info("notifyPayrockPaymentOrder, orderNo:{}, status:{}, message:{}", cartId, status, statusMessage);

        String redisLock = RedisKeyConst.Order.ORDER_STATUS_UPDATE_LOCK + cartId;
        boolean lock = RedisLockUtil.tryLock(redisLock);
        if (!lock) {
            return ResponseDTO.error(OpenErrorCode.REPEAT_SUBMIT);
        }
        try {
            PaymentOrderInfoEntity orderInfoEntity = paymentOrderInfoManager.queryByOrderNo(cartId);
            if (orderInfoEntity == null) {
                return ResponseDTO.userErrorParam("订单不存在");
            }
            PaymentChannelInfoEntity paymentChannelInfoVO = channelInfoManager.getById(orderInfoEntity.getChannelId());
            if (paymentChannelInfoVO == null) {
                return ResponseDTO.userErrorParam("渠道不存在");
            }
            Map<String, Object> params = new HashMap<>(4);
            params.put("cartId", cartId);
            params.put("status", status);
            params.put("signature", signature);
            params.put("amount", orderAmount);
            params.put("currency", currency);
            params.put("version", version);
            params.put("orderTime", orderTime);

            IPaymentPlatformService platformService = IPaymentPlatformService.get(paymentChannelInfoVO.getImplCode());
            boolean signVerify = platformService.signVerify(paymentChannelInfoVO, params);
            if (!signVerify) {
                return ResponseDTO.userErrorParam("签名验证失败");
            }

            OrderDetailVO orderDetailVO = platformService.updateOrderDetail(paymentChannelInfoVO, params);
            PaymentOrderInfoEntity orderInfoNew = new PaymentOrderInfoEntity();
            orderInfoNew.setId(orderInfoEntity.getId());
            if (orderDetailVO.getStatus().equals(2)) {
                orderInfoNew.setPaymentStatus(2);
            }
            // 更新订单金额
            orderInfoNew.setAmount(new BigDecimal(orderAmount));
            orderInfoNew.setStatus(orderDetailVO.getStatus());
            if (StringUtils.isNotEmpty(orderTime)) {
                orderInfoNew.setApplyTime(Long.valueOf(orderTime));
            }
            if (StringUtils.isNotEmpty(completedTime) && !"null".equals(completedTime)) {
                orderInfoNew.setFinishedTime(Long.valueOf(completedTime));
            }

            orderInfoNew.setUpdateTime(LocalDateTime.now());
            boolean updateResult = paymentOrderInfoManager.updateById(orderInfoNew);
            log.info("notifyPayrockPaymentOrder updateResult:{}", updateResult);

            try {
                orderPushService.paymentOrderPush(orderInfoEntity.getPlatformId(), orderInfoEntity.getOrderNo(), orderInfoNew.getStatus(),
                        orderInfoNew.getPaymentStatus(), orderInfoEntity.getCallback(), orderInfoEntity.getExt());
            } catch (Exception e) {
                log.error("notifyPayrockPaymentOrder orderPush channelId:{}, orderNo:{}, exception", paymentChannelInfoVO.getId(), cartId, e);
                return ResponseDTO.userErrorParam("系统异常");
            }
            return ResponseDTO.ok();
        } finally {
            RedisLockUtil.unlock(redisLock);
        }
    }

    public String notifyLtgPaymentOrder(Map<String, Object> params) {

        String mchOrderNo = (String) params.get("mchOrderNo");
        String redisLock = RedisKeyConst.Order.ORDER_STATUS_UPDATE_LOCK + mchOrderNo;
        boolean lock = RedisLockUtil.tryLock(redisLock);
        if (!lock) {
            return "repeat submit";
        }
        try {
            PaymentOrderInfoEntity orderInfoEntity = paymentOrderInfoManager.queryByOrderNo(mchOrderNo);
            if (orderInfoEntity == null) {
                return "order not found";
            }
            PaymentChannelInfoEntity paymentChannelInfoVO = channelInfoManager.getById(orderInfoEntity.getChannelId());
            if (paymentChannelInfoVO == null) {
                return "channel not found";
            }

            IPaymentPlatformService platformService = IPaymentPlatformService.get(paymentChannelInfoVO.getImplCode());
//            boolean signVerify = platformService.signVerify(paymentChannelInfoVO, params);
//            if (!signVerify) {
//                return ResponseDTO.userErrorParam("签名验证失败");
//            }
            OrderDetailVO orderDetailVO = platformService.updateOrderDetail(paymentChannelInfoVO, params);
            PaymentOrderInfoEntity orderInfoNew = new PaymentOrderInfoEntity();
            orderInfoNew.setId(orderInfoEntity.getId());
            if (orderDetailVO.getStatus().equals(2)) {
                orderInfoNew.setPaymentStatus(orderDetailVO.getStatus());
            }

            // 更新订单金额
            BigDecimal amount = BigDecimal.valueOf(Long.parseLong((String) params.get("amount")))
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            orderInfoNew.setAmount(amount);
            orderInfoNew.setStatus(orderDetailVO.getStatus());
            String createdAt = (String) params.get("createdAt");
            if (StringUtils.isNotEmpty(createdAt)) {
                orderInfoNew.setApplyTime(Long.valueOf(createdAt));
            }
            String successTime = (String) params.get("successTime");
            if (StringUtils.isNotEmpty(successTime)) {
                orderInfoNew.setFinishedTime(Long.valueOf(successTime));
            }
            orderInfoNew.setUpdateTime(LocalDateTime.now());
            boolean updateResult = paymentOrderInfoManager.updateById(orderInfoNew);
            log.info("notifyLtgPaymentOrder updateResult:{}", updateResult);

            try {
                orderPushService.paymentOrderPush(orderInfoEntity.getPlatformId(), orderInfoEntity.getOrderNo(), orderInfoNew.getStatus(),
                        orderInfoNew.getPaymentStatus(), orderInfoEntity.getCallback(), orderInfoEntity.getExt());
            } catch (Exception e) {
                log.error("notifyLtgPaymentOrder orderPush channelId:{}, orderNo:{}, exception", paymentChannelInfoVO.getId(), mchOrderNo, e);
                return "system error";
            }
            return "success";
        } finally {
            RedisLockUtil.unlock(redisLock);
        }
    }


    public String notifyEypPaymentOrder(Map<String, Object> body) {
        String orderNo = (String) body.get("merchant_order_no");
        String redisLock = RedisKeyConst.Order.ORDER_STATUS_UPDATE_LOCK + orderNo;
        boolean lock = RedisLockUtil.tryLock(redisLock);
        if (!lock) {
            return OpenErrorCode.REPEAT_SUBMIT.getMsg();
        }
        try {
            PaymentOrderInfoEntity orderInfoEntity = paymentOrderInfoManager.queryByOrderNo(orderNo);
            if (orderInfoEntity == null) {
                return "Order does not exist";
            }
            PaymentChannelInfoEntity paymentChannelInfoVO = channelInfoManager.getById(orderInfoEntity.getChannelId());
            if (paymentChannelInfoVO == null) {
                return "Channel does not exist";
            }

            IPaymentPlatformService platformService = IPaymentPlatformService.get(paymentChannelInfoVO.getImplCode());
            boolean signVerify = platformService.signVerify(paymentChannelInfoVO, body);
            if (!signVerify) {
                return "Signature verification failure";
            }
            OrderDetailVO orderDetailVO = platformService.updateOrderDetail(paymentChannelInfoVO, body);
            PaymentOrderInfoEntity orderInfoNew = new PaymentOrderInfoEntity();
            orderInfoNew.setId(orderInfoEntity.getId());
            if (orderDetailVO.getStatus().equals(2)) {
                orderInfoNew.setPaymentStatus(2);
            }
            // 更新订单金额
            orderInfoNew.setStatus(orderDetailVO.getStatus());
            String succeededAt = (String) body.get("succeeded_at");
            if (StringUtils.isNotEmpty(succeededAt)) {
                orderInfoNew.setFinishedTime(SmartLocalDateUtil.dateToTimestamp(succeededAt));
            }
            orderInfoNew.setUpdateTime(LocalDateTime.now());
            boolean updateResult = paymentOrderInfoManager.updateById(orderInfoNew);
            log.info("notifyEypPaymentOrder updateResult:{}", updateResult);

            try {
                orderPushService.paymentOrderPush(orderInfoEntity.getPlatformId(), orderInfoEntity.getOrderNo(), orderInfoNew.getStatus(),
                        orderInfoNew.getPaymentStatus(), orderInfoEntity.getCallback(), orderInfoEntity.getExt());
            } catch (Exception e) {
                log.error("notifyEypPaymentOrder orderPush channelId:{}, orderNo:{}, exception", paymentChannelInfoVO.getId(), orderNo, e);
                return "System error";
            }
            return "success";
        } finally {
            RedisLockUtil.unlock(redisLock);
        }
    }


    public String notifyEypCashOrder(Map<String, Object> body) {
        String orderNo = (String) body.get("merchant_order_no");
        String redisLock = RedisKeyConst.Order.ORDER_STATUS_UPDATE_LOCK + orderNo;
        boolean lock = RedisLockUtil.tryLock(redisLock);
        if (!lock) {
            return OpenErrorCode.REPEAT_SUBMIT.getMsg();
        }
        try {
            CashOrderDetailEntity orderInfoEntity = cashOrderDetailManager.queryBySubOrderNo(orderNo);
            if (orderInfoEntity == null) {
                return "Cash order does not exist";
            }
            PaymentChannelInfoEntity paymentChannelInfoVO = channelInfoManager.getById(orderInfoEntity.getChannelId());
            if (paymentChannelInfoVO == null) {
                return "Channel does not exist";
            }
            IPaymentPlatformService platformService = IPaymentPlatformService.get(paymentChannelInfoVO.getImplCode());
            boolean signVerify = platformService.signVerify(paymentChannelInfoVO, body);
            if (!signVerify) {
                return "Signature verification failure";
            }
            OrderWithdrawDetailVO orderDetailVO = platformService.updateWithdrawOrderDetail(paymentChannelInfoVO, body);
            if (WithdrawSubOrderStatusEnum.SUCCESSED.equals(orderDetailVO.getStatus())) {
                orderInfoEntity.setStatus(3);
            } else if (WithdrawSubOrderStatusEnum.FAILED.equals(orderDetailVO.getStatus())) {
                orderInfoEntity.setStatus(4);
            }
            // 更新订单金额
            String succeededAt = (String) body.get("succeeded_at");
            if (StringUtils.isNotEmpty(succeededAt)) {
                orderInfoEntity.setFinishedTime(SmartLocalDateUtil.dateToTimestamp(succeededAt));
            }
            orderInfoEntity.setUpdateTime(LocalDateTime.now());
            boolean updateResult = cashOrderDetailManager.updateById(orderInfoEntity);

            //检查订单状态
            checkOrderStatus(orderInfoEntity.getOrderNo());

            log.info("notifyEypCashOrder updateResult:{}", updateResult);
            return "success";
        } finally {
            RedisLockUtil.unlock(redisLock);
        }
    }


    public String notifyLypPaymentOrder(Map<String, Object> body) {
        String orderNo = (String) body.get("orderNo");
        String redisLock = RedisKeyConst.Order.ORDER_STATUS_UPDATE_LOCK + orderNo;
        boolean lock = RedisLockUtil.tryLock(redisLock);
        if (!lock) {
            return OpenErrorCode.REPEAT_SUBMIT.getMsg();
        }
        try {
            PaymentOrderInfoEntity orderInfoEntity = paymentOrderInfoManager.queryByOrderNo(orderNo);
            if (orderInfoEntity == null) {
                return "Order does not exist";
            }
            PaymentChannelInfoEntity paymentChannelInfoVO = channelInfoManager.getById(orderInfoEntity.getChannelId());
            if (paymentChannelInfoVO == null) {
                return "Channel does not exist";
            }

            IPaymentPlatformService platformService = IPaymentPlatformService.get(paymentChannelInfoVO.getImplCode());
            boolean signVerify = platformService.signVerify(paymentChannelInfoVO, body);
            if (!signVerify) {
                return "Signature verification failure";
            }
            OrderDetailVO orderDetailVO = platformService.updateOrderDetail(paymentChannelInfoVO, body);
            PaymentOrderInfoEntity orderInfoNew = new PaymentOrderInfoEntity();
            orderInfoNew.setId(orderInfoEntity.getId());
            if (orderDetailVO.getStatus().equals(2)) {
                orderInfoNew.setPaymentStatus(2);
            }
            // 更新订单金额
            orderInfoNew.setStatus(orderDetailVO.getStatus());
            String succeededAt = (String) body.get("opTime");
            if (StringUtils.isNotEmpty(succeededAt)) {
                orderInfoNew.setFinishedTime(SmartLocalDateUtil.dateToTimestamp(succeededAt));
            }
            orderInfoNew.setUpdateTime(LocalDateTime.now());
            boolean updateResult = paymentOrderInfoManager.updateById(orderInfoNew);
            log.info("notifyEypPaymentOrder updateResult:{}", updateResult);

            try {
                orderPushService.paymentOrderPush(orderInfoEntity.getPlatformId(), orderInfoEntity.getOrderNo(), orderInfoNew.getStatus(),
                        orderInfoNew.getPaymentStatus(), orderInfoEntity.getCallback(), orderInfoEntity.getExt());
            } catch (Exception e) {
                log.error("notifyEypPaymentOrder orderPush channelId:{}, orderNo:{}, exception", paymentChannelInfoVO.getId(), orderNo, e);
                return "System error";
            }
            return "success";
        } finally {
            RedisLockUtil.unlock(redisLock);
        }
    }

    public String notifyLypCashOrder(Map<String, Object> body) {
        String orderNo = (String) body.get("orderNo");
        String redisLock = RedisKeyConst.Order.ORDER_STATUS_UPDATE_LOCK + orderNo;
        boolean lock = RedisLockUtil.tryLock(redisLock);
        if (!lock) {
            return OpenErrorCode.REPEAT_SUBMIT.getMsg();
        }
        try {
            CashOrderDetailEntity orderInfoEntity = cashOrderDetailManager.queryBySubOrderNo(orderNo);
            if (orderInfoEntity == null) {
                return "Cash order does not exist";
            }
            PaymentChannelInfoEntity paymentChannelInfoVO = channelInfoManager.getById(orderInfoEntity.getChannelId());
            if (paymentChannelInfoVO == null) {
                return "Channel does not exist";
            }
            IPaymentPlatformService platformService = IPaymentPlatformService.get(paymentChannelInfoVO.getImplCode());
            boolean signVerify = platformService.signVerify(paymentChannelInfoVO, body);
            if (!signVerify) {
                return "Signature verification failure";
            }
            OrderWithdrawDetailVO orderDetailVO = platformService.updateWithdrawOrderDetail(paymentChannelInfoVO, body);
            if (WithdrawSubOrderStatusEnum.SUCCESSED.equals(orderDetailVO.getStatus())) {
                orderInfoEntity.setStatus(WithdrawSubOrderStatusEnum.SUCCESSED.getValue());
            } else if (WithdrawSubOrderStatusEnum.FAILED.equals(orderDetailVO.getStatus())) {
                orderInfoEntity.setStatus(WithdrawSubOrderStatusEnum.FAILED.getValue());
            }
            // 更新订单金额
            String succeededAt = (String) body.get("opTime");
            if (StringUtils.isNotEmpty(succeededAt)) {
                orderInfoEntity.setFinishedTime(SmartLocalDateUtil.dateToTimestamp(succeededAt));
            }
            orderInfoEntity.setUpdateTime(LocalDateTime.now());
            boolean updateResult = cashOrderDetailManager.updateById(orderInfoEntity);

            //检查订单状态
            checkOrderStatus(orderInfoEntity.getOrderNo());

            log.info("notifyLypCashOrder updateResult:{}", updateResult);
            return "success";
        } finally {
            RedisLockUtil.unlock(redisLock);
        }
    }


    public ResponseDTO notifyXxxxxxxxPaymentOrder(String outTradeNo, String status) {
        log.info("notifyXxxxxxxxxPaymentOrder orderNo:{}, status:{}", outTradeNo, status);

        PaymentOrderInfoEntity orderInfoEntity = paymentOrderInfoManager.queryByOrderNo(outTradeNo);
        if (orderInfoEntity == null) {
            return ResponseDTO.userErrorParam("not found");
        }
        PaymentChannelInfoEntity paymentChannelInfoVO = channelInfoManager.getById(orderInfoEntity.getChannelId());
        if (paymentChannelInfoVO == null) {
            return ResponseDTO.userErrorParam("not found");
        }

        LetonggouPaymentFlagEnum paymentFlagEnum = SmartEnumUtil.getEnumByValue(orderInfoEntity.getPaymentChannel(), LetonggouPaymentFlagEnum.class);

        String amount = orderInfoEntity.getAmount().toString();
        String queryString;
        if (StringUtils.isNotEmpty(orderInfoEntity.getThirdpartyOrderNo())) {
            queryString = "money=" + amount + "&name=" + orderInfoEntity.getName() +
                    "&out_trade_no=" + orderInfoEntity.getOrderNo() + "&pid=" + paymentChannelInfoVO.getMerAk() +
                    "&trade_no=" + orderInfoEntity.getThirdpartyOrderNo() + "&trade_status=" + status + "&type=" +
                    paymentFlagEnum.getPaymentFlag();
        } else {
            // TRADE_SUCCESS
            queryString = "money=" + amount + "&name=" + orderInfoEntity.getName() +
                    "&out_trade_no=" + orderInfoEntity.getOrderNo() + "&pid=" + paymentChannelInfoVO.getMerAk() + "&trade_status=" + status + "&type=" +
                    paymentFlagEnum.getPaymentFlag();
        }

        String sign = DigestUtils.md5Hex(queryString + paymentChannelInfoVO.getMerSk()).toLowerCase();
        String result = notifyLetonggouPaymentOrder(Integer.valueOf(paymentChannelInfoVO.getMerAk()), orderInfoEntity.getThirdpartyOrderNo(), orderInfoEntity.getOrderNo(),
                paymentFlagEnum.getPaymentFlag(), orderInfoEntity.getName(), amount,
                status, "", sign, "MD5");
        return ResponseDTO.ok(result);
    }


    public String notifyNpPaymentOrder(Map<String, Object> body) {
        String orderNo = (String) body.get("bill_no");
        String redisLock = RedisKeyConst.Order.ORDER_STATUS_UPDATE_LOCK + orderNo;
        boolean lock = RedisLockUtil.tryLock(redisLock);
        if (!lock) {
            return OpenErrorCode.REPEAT_SUBMIT.getMsg();
        }
        try {
            PaymentOrderInfoEntity orderInfoEntity = paymentOrderInfoManager.queryByOrderNo(orderNo);
            if (orderInfoEntity == null) {
                return "Order does not exist";
            }
            PaymentChannelInfoEntity paymentChannelInfoVO = channelInfoManager.getById(orderInfoEntity.getChannelId());
            if (paymentChannelInfoVO == null) {
                return "Channel does not exist";
            }

            IPaymentPlatformService platformService = IPaymentPlatformService.get(paymentChannelInfoVO.getImplCode());
            Map<String, Object> params = new HashMap<>();
            params.put("bill_no", body.get("bill_no"));
            params.put("sign", body.get("sign"));

            if (StringUtils.isNotEmpty(body.getOrDefault("bill_status", "").toString())) {
                params.put("bill_status", body.get("bill_status"));
                params.put("sys_no", body.get("sys_no"));
            }
            boolean signVerify = platformService.signVerify(paymentChannelInfoVO, params);
            if (!signVerify) {
                return "Signature verification failure";
            }

            OrderDetailVO orderDetailVO = platformService.updateOrderDetail(paymentChannelInfoVO, body);

            PaymentOrderInfoEntity orderInfoNew = new PaymentOrderInfoEntity();
            orderInfoNew.setId(orderInfoEntity.getId());
            if (orderDetailVO.getStatus().equals(2)) {
                orderInfoNew.setPaymentStatus(2);
            } else if (orderDetailVO.getStatus().equals(3)) {
                orderInfoNew.setPaymentStatus(3);
            }
            // 更新订单金额
            orderInfoNew.setStatus(orderDetailVO.getStatus());
            orderInfoNew.setFinishedTime(SmartLocalDateUtil.dateToTimestamp(orderDetailVO.getEndtime()));
            orderInfoNew.setUpdateTime(LocalDateTime.now());
            boolean updateResult = paymentOrderInfoManager.updateById(orderInfoNew);
            log.info("notifyNpPaymentOrder updateResult:{}", updateResult);

            try {
                orderPushService.paymentOrderPush(orderInfoEntity.getPlatformId(), orderInfoEntity.getOrderNo(), orderInfoNew.getStatus(),
                        orderInfoNew.getPaymentStatus(), orderInfoEntity.getCallback(), orderInfoEntity.getExt());
            } catch (Exception e) {
                log.error("notifyNpPaymentOrder orderPush channelId:{}, orderNo:{}, exception", paymentChannelInfoVO.getId(), orderNo, e);
                return "System error";
            }
            return "success";
        } finally {
            RedisLockUtil.unlock(redisLock);
        }
    }


    public String notifyNpCashOrder(Map<String, Object> body) {
        String orderNo = (String) body.get("bill_no");
        String redisLock = RedisKeyConst.Order.ORDER_STATUS_UPDATE_LOCK + orderNo;
        boolean lock = RedisLockUtil.tryLock(redisLock);
        if (!lock) {
            return OpenErrorCode.REPEAT_SUBMIT.getMsg();
        }
        try {
            CashOrderDetailEntity orderInfoDetailEntity = cashOrderDetailManager.queryBySubOrderNo(orderNo);
            if (orderInfoDetailEntity == null) {
                return "Cash order does not exist";
            }
            PaymentChannelInfoEntity paymentChannelInfoVO = channelInfoManager.getById(orderInfoDetailEntity.getChannelId());
            if (paymentChannelInfoVO == null) {
                return "Channel does not exist";
            }
            IPaymentPlatformService platformService = IPaymentPlatformService.get(paymentChannelInfoVO.getImplCode());
            Map<String, Object> params = new HashMap<>();
            params.put("bill_no", body.get("bill_no"));
            params.put("sign", body.get("sign"));

            if (StringUtils.isNotEmpty(body.getOrDefault("bill_status", "").toString())) {
                params.put("bill_status", body.get("bill_status"));
                params.put("sys_no", body.get("sys_no"));
            }
            boolean signVerify = platformService.signVerify(paymentChannelInfoVO, params);
            if (!signVerify) {
                return "Signature verification failure";
            }
            OrderWithdrawDetailVO orderDetailVO = platformService.updateWithdrawOrderDetail(paymentChannelInfoVO, body);
            if (WithdrawSubOrderStatusEnum.SUCCESSED.equals(orderDetailVO.getStatus())) {
                orderInfoDetailEntity.setStatus(WithdrawSubOrderStatusEnum.SUCCESSED.getValue());
            } else if (WithdrawSubOrderStatusEnum.FAILED.equals(orderDetailVO.getStatus())) {
                orderInfoDetailEntity.setStatus(WithdrawSubOrderStatusEnum.FAILED.getValue());
            }
            orderInfoDetailEntity.setFinishedTime(SmartLocalDateUtil.dateToTimestamp(orderDetailVO.getEndtime()));
            orderInfoDetailEntity.setUpdateTime(LocalDateTime.now());
            boolean updateResult = cashOrderDetailManager.updateById(orderInfoDetailEntity);

            //检查订单状态
            checkOrderStatus(orderInfoDetailEntity.getOrderNo());

            log.info("notifyNpCashOrder updateResult:{}", updateResult);
            return "success";
        } finally {
            RedisLockUtil.unlock(redisLock);
        }
    }

    public void checkOrderStatus(String orderNo) {
        CashOrderInfoEntity orderInfoEntity = cashOrderInfoManager.queryByOrderNo(orderNo);
        if (orderInfoEntity == null) {
            log.error("np notify 订单不存在:{}", orderNo);
            return;
        }
        BigDecimal amountSuccessed = cashOrderDetailManager.sumSubOrdersSuccessed(orderNo);
        if (orderInfoEntity.getAmount().compareTo(amountSuccessed) == 0) {
            CashOrderInfoEntity orderInfoNew = new CashOrderInfoEntity();
            orderInfoNew.setId(orderInfoEntity.getId());
            orderInfoNew.setStatus(WithdrawOrderStatusEnum.Successed.getValue());
            orderInfoNew.setFinishedTime(System.currentTimeMillis());
            orderInfoNew.setUpdateTime(LocalDateTime.now());
            cashOrderInfoManager.updateById(orderInfoNew);
        }
    }






    public ResponseDTO<String> paymentSwitch(String token) {
        String redisKey = RedisKeyConst.Order.ORDER_PAY_TOKEN + token;
        PaymentOrderInfoEntity orderInfoEntity = redisService.getObject(redisKey, PaymentOrderInfoEntity.class);
        if (orderInfoEntity == null) {
            return ResponseDTO.error(OpenErrorCode.ORDER_PAYMENT_OVERDUE_WEB);
        }
        log.info("paymentSwitch orderNo:{}", orderInfoEntity.getOrderNo());
        orderInfoEntity = paymentOrderInfoManager.getById(orderInfoEntity.getId());

        if (!orderInfoEntity.getPaymentStatus().equals(1) || !orderInfoEntity.getStatus().equals(1)) {
            return ResponseDTO.error(OpenErrorCode.ORDER_PAYMENT_SWITCH_NOT_ALLOW);
        }

        String switchCacheKey = RedisKeyConst.Order.ORDER_PAY_SWITCH_CACHE + token;
        Integer c = redisService.getObject(switchCacheKey, Integer.class);
        if (c != null && c > 0) {
            return ResponseDTO.error(OpenErrorCode.ORDER_PAYMENT_SWITCH_COUNT);
        }

        PaymentChannelInfoEntity paymentChannelInfoVO = channelInfoManager.getById(orderInfoEntity.getChannelId());
        if (paymentChannelInfoVO == null) {
            return ResponseDTO.error(OpenErrorCode.SYSTEM_ERROR_WEB);
        }
        IPaymentPlatformService platformService = IPaymentPlatformService.get(paymentChannelInfoVO.getImplCode());
        PaymentApplyResultVO applyResultVO = platformService.paymentSwitch(paymentChannelInfoVO, orderInfoEntity.getOrderNo());
        if (applyResultVO == null) {
            log.warn("未匹配到支付渠道，orderNo:{}", orderInfoEntity.getOrderNo());
            return ResponseDTO.error(OpenErrorCode.NO_MATCH_CHANNEL_WEB);
        }
        // 更新订单信息
        PaymentOrderInfoEntity orderInfoNew = new PaymentOrderInfoEntity();
        orderInfoNew.setId(orderInfoEntity.getId());
        orderInfoNew.setSubMerName(applyResultVO.getSubMerName());
        orderInfoNew.setThirdpartyOrderNo(applyResultVO.getThirdpartyOrderNo());
        orderInfoNew.setUpdateTime(LocalDateTime.now());
        paymentOrderInfoManager.updateById(orderInfoNew);

        redisService.set(switchCacheKey, 1, (orderInfoEntity.getExpiredTime() - System.currentTimeMillis()) / 1000);

        return ResponseDTO.ok();
    }

    /**
     * 出金申请（接口）
     *
     * @param requestMerchant
     * @param form
     * @return
     * @throws Exception
     */
    public ResponseDTO<String> orderWithdraw(RequestMerchant requestMerchant, OrderWithdrawForm form) throws Exception {
        log.info("orderWithdraw, form:{}", form);
        String redisKey = RedisKeyConst.Order.ORDER_WITHDRAW + form.getOrderNo();
        boolean lock = RedisLockUtil.tryLock(redisKey);
        if (!lock) {
            return ResponseDTO.error(OpenErrorCode.REPEAT_SUBMIT);
        }
        try {
            // 查询定时是否已经存在
            CashOrderInfoEntity orderInfoEntity = cashOrderInfoManager.queryByOrderNo(form.getOrderNo());
            if (orderInfoEntity != null) {
                log.warn("订单已存在，orderNo:{}", form.getOrderNo());
                return ResponseDTO.error(OpenErrorCode.FORM_REPEAT_SUBMIT, "该出金订单已存在");
            }

            // 保存用户并替换userid
            PaymentUserEntity userEntity = paymentUserManager.getByUserNameOrUserId(form.getAccountHolder(), form.getUserId());
            form.setUserId(String.valueOf(userEntity.getId()));

            //判断是否为大额出金，如果是，则保存、返回
            if (false) {
                //大额出金订单，请记录到表，等待人工处理
                // 保存订单信息
                orderInfoEntity = saveWithdrawOrderInfo(requestMerchant, form, true, "大额出金订单");
                return ResponseDTO.ok();
            }

            // 保存订单信息
            orderInfoEntity = saveWithdrawOrderInfo(requestMerchant, form, false, "");

            //非大额出金交易
            Set<Long> channelIdList = new HashSet<>();
            PaymentChannelInfoVO paymentChannelInfoVO = null;
            PaymentApplyResultVO applyResultVO = null;
            // 根据渠道配置逻辑路由，如果申请订单失败，重试3次
            int batchNo = 1;
            int seqNo = 1;
            String subOrderNo = form.getOrderNo() + String.format("%02d", batchNo) + String.format("%02d", seqNo);
            for (int i = 0; i < 3; i++) {
                paymentChannelInfoVO = channelInfoManager.getWithdrawChannelInfo(form.getOrderNo(), PaymentTypeEnum.cash, channelIdList, form);
                if (paymentChannelInfoVO == null) {
//                    return ResponseDTO.error(OpenErrorCode.NO_MATCH_CHANNEL);
                    //匹配渠道失败，更新订单信息，等待人工处理
                    orderInfoEntity.setStatus(WithdrawOrderStatusEnum.Pending.getValue());
                    orderInfoEntity.setManualFlag(CashOrderInfoEntity.MANUAL_FLAG_YES);
                    orderInfoEntity.setManualReason("未匹配到出金渠道");
                    orderInfoEntity.setUpdateTime(LocalDateTime.now());
                    cashOrderInfoManager.updateById(orderInfoEntity);

                    return ResponseDTO.ok();
                }
                channelIdList.add(paymentChannelInfoVO.getId());
                try {
                    applyResultVO = IPaymentPlatformService.get(paymentChannelInfoVO.getImplCode())
                            .applyForWithdraw(paymentChannelInfoVO, form, subOrderNo);
                    if (applyResultVO != null) {
                        break;
                    }
                } catch (BusinessException e) {
                    log.error("未路由到可用出金渠道，即将重试，当前重试次数:{}", i);
                }
            }
            if (applyResultVO == null) {
                log.warn("未匹配到出金渠道，orderNo:{}", form.getOrderNo());
//                return ResponseDTO.error(OpenErrorCode.NO_MATCH_CHANNEL);

                //匹配渠道失败，更新订单信息，等待人工处理
                orderInfoEntity.setStatus(WithdrawOrderStatusEnum.Pending.getValue());
                orderInfoEntity.setManualFlag(CashOrderInfoEntity.MANUAL_FLAG_YES);
                orderInfoEntity.setManualReason("未匹配到出金渠道");
                orderInfoEntity.setUpdateTime(LocalDateTime.now());
                cashOrderInfoManager.updateById(orderInfoEntity);

                return ResponseDTO.ok();
            }
            orderInfoEntity.setChannelId(paymentChannelInfoVO.getId());
            orderInfoEntity.setStatus(WithdrawOrderStatusEnum.Processing.getValue());
            orderInfoEntity.setUpdateTime(LocalDateTime.now());
            cashOrderInfoManager.updateById(orderInfoEntity);

            //插入detail表
            //     * 渠道类型 1-自有对接渠道、2-外部渠道
            CashOrderDetailEntity orderDetailEntity = saveWithdrawOrderDetail(requestMerchant.getId(),
                    orderInfoEntity, batchNo, seqNo, subOrderNo, orderInfoEntity.getAmount(),
                    WithdrawChnlTypeEnum.CONNECTED_CHANNEL, paymentChannelInfoVO.getId(),
                    WithdrawSubOrderStatusEnum.PROCESSING, "",
                    applyResultVO.getThirdpartyOrderNo(), null, null);


            return ResponseDTO.ok();
        } finally {
            RedisLockUtil.unlock(redisKey);
        }
    }

    /**
     * 出金交易人工处理操作（从控制台来）
     *
     * @param form
     * @return
     * @throws Exception
     */
//    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<OrderWithdrawVO> manualWithdraw(CashOrderManualForm form) throws Exception {
        log.info("manualWithdraw, form:{}", form);
        String redisKey = RedisKeyConst.Order.ORDER_WITHDRAW + ":" + form.getOrderNo();
        boolean lock = RedisLockUtil.tryLock(redisKey);
        if (!lock) {
            return ResponseDTO.error(OpenErrorCode.SYSTEM_ERROR);
        }
        try {
            CashOrderInfoEntity orderInfoEntity = cashOrderInfoManager.queryByOrderNo(form.getOrderNo());
            if (orderInfoEntity == null) {
                log.error("出金订单不存在，orderNo:{}", form.getOrderNo());
                return ResponseDTO.error(OpenErrorCode.ORDER_WITHDRAW_NOTFOUND_OR_WRONG_STATUS);
            }
            if (orderInfoEntity.getStatus() != 1 || orderInfoEntity.getManualFlag() != 1) {
                //1-待处理
                log.error("出金订单不存在，orderNo:{}", form.getOrderNo());
                return ResponseDTO.error(OpenErrorCode.ORDER_WITHDRAW_NOTFOUND_OR_WRONG_STATUS);
            }

            //检查t_cash_order_detail下该订单是否有状态为1-处理中的子订单
            long countInProgress = cashOrderDetailManager.countSubOrdersInProgress(orderInfoEntity.getOrderNo());
            if (countInProgress > 0L) {
                log.error("出金订单(orderNo:{})存在{}个正在处理中的子订单，不能人工处理！！", form.getOrderNo(), countInProgress);
                return ResponseDTO.error(OpenErrorCode.ORDER_WITHDRAW_NOTFOUND_OR_WRONG_STATUS);
            }

            //检查出金金额是否正确
            BigDecimal amountSuccessed = cashOrderDetailManager.sumSubOrdersSuccessed(orderInfoEntity.getOrderNo());
            if (amountSuccessed.compareTo(BigDecimal.ZERO) < 0) {
                log.error("查询出金订单(orderNo:{})已经成功的子订单的总金额失败！！", form.getOrderNo());
                return ResponseDTO.error(OpenErrorCode.SYSTEM_ERROR);
            }
            BigDecimal amountLeft = orderInfoEntity.getAmount().subtract(amountSuccessed);

            BigDecimal formAmountSum = BigDecimal.ZERO;
            for (int i = 0; i < form.getSubOrderList().size(); i++) {
                CashOrderManualForm.SubOrder subOrder = form.getSubOrderList().get(i);
                //检查参数
                if (subOrder.getChnlType() == 1) {//自有渠道
                    PaymentChannelInfoEntity channelInfo = channelInfoManager.getById(subOrder.getChannelId());
                    if (channelInfo == null) {
                        log.error("出金订单(orderNo:{})的人工子订单传入未知的自有渠道(id:{})！", form.getOrderNo(), subOrder.getChannelId());
                        return ResponseDTO.error(OpenErrorCode.PARAM_ERROR, "人工子订单传入未知的自有渠道" + subOrder.getChannelId());
                    }
                } else if (subOrder.getChnlType() == 2) { //外部渠道
                    if (StringUtils.isEmpty(subOrder.getThirdpartyOrderNo())) {
                        log.error("出金订单(orderNo:{})的人工子订单(外部渠道{})未传入外部订单号！", form.getOrderNo(), subOrder.getChannelId());
                        return ResponseDTO.error(OpenErrorCode.PARAM_ERROR, "人工子订单(外部渠道" + subOrder.getChannelId() + ")未传入外部订单号！");
                    }
                    if (Objects.isNull(subOrder.getBillFileId())) {
                        log.error("出金订单(orderNo:{})的人工子订单(外部渠道{})未传入回单！", form.getOrderNo(), subOrder.getChannelId());
                        return ResponseDTO.error(OpenErrorCode.PARAM_ERROR, "人工子订单(外部渠道" + subOrder.getChannelId() + ")未传入回单！");
                    }
                    if (Objects.isNull(subOrder.getFinishedTime())) {
                        log.error("出金订单(orderNo:{})的人工子订单(外部渠道{})未传入完成时间！", form.getOrderNo(), subOrder.getChannelId());
                        return ResponseDTO.error(OpenErrorCode.PARAM_ERROR, "人工子订单(外部渠道" + subOrder.getChannelId() + ")未传入完成时间！");
                    }
                } else {
                    log.error("出金订单(orderNo:{})的人工子订单传入未知的chnlType({})！", form.getOrderNo(), subOrder.getChnlType());
                    return ResponseDTO.error(OpenErrorCode.PARAM_ERROR);
                }

                if (BigDecimal.ZERO.compareTo(subOrder.getAmount()) >= 0) {
                    log.error("出金订单(orderNo:{})的人工子订单传入不正确的金额({})！", form.getOrderNo(), subOrder.getAmount());
                    return ResponseDTO.error(OpenErrorCode.PARAM_ERROR, "人工子订单传入不正确的金额" + subOrder.getAmount());
                }

                formAmountSum = formAmountSum.add(subOrder.getAmount());
            }
            if (amountLeft.compareTo(formAmountSum) != 0) {
                log.error("出金订单(orderNo:{})剩余待处理金额{}与人工处理总金额{}不一致！！", form.getOrderNo(), amountLeft, formAmountSum);
                return ResponseDTO.error(OpenErrorCode.PARAM_ERROR);
            }

            // 更新订单信息
            orderInfoEntity.setCurrBatchNo(orderInfoEntity.getCurrBatchNo() + 1);
            orderInfoEntity.setStatus(2); //2-处理中
            orderInfoEntity.setManualRemark(form.getManualRemark());
            orderInfoEntity.setUpdateTime(LocalDateTime.now());
            if (form.getSubOrderList().size() > 1) {
                orderInfoEntity.setManualSplitFlag(1);
            }
            cashOrderInfoManager.updateById(orderInfoEntity);

            int batchNo = orderInfoEntity.getCurrBatchNo();
            int seqNo = 0;
            //插入子订单
            for (int i = 0; i < form.getSubOrderList().size(); i++) {
                seqNo++;
                String subOrderNo = orderInfoEntity.getOrderNo() + String.format("%02d", batchNo) + String.format("%02d", seqNo);

                CashOrderManualForm.SubOrder subOrder = form.getSubOrderList().get(i);

                //外部渠道
                if (subOrder.getChnlType() == 2) {
                    //插入detail表
                    saveWithdrawOrderDetail(orderInfoEntity.getPlatformId(),
                            orderInfoEntity, batchNo, seqNo, subOrderNo, subOrder.getAmount(),
                            WithdrawChnlTypeEnum.EXTERNAL_CHANNEL, subOrder.getChannelId(),
                            WithdrawSubOrderStatusEnum.SUCCESSED, "默认成功",
                            subOrder.getThirdpartyOrderNo(), subOrder.getBillFileId(), subOrder.getFinishedTime());

                    continue;
                }

                //自有渠道
                Set<Long> channelIdList = new HashSet<>();
                channelIdList.add(subOrder.getChannelId());
                PaymentChannelInfoVO paymentChannelInfoVO = null;
                PaymentApplyResultVO applyResultVO = null;
                // 根据渠道配置逻辑路由
                paymentChannelInfoVO = channelInfoManager.getWithdrawChannelInfo(form.getOrderNo(), PaymentTypeEnum.cash, channelIdList, orderInfoEntity.getWithdrawType(),
                        orderInfoEntity.getAccountHolder(), subOrder.getAmount(), orderInfoEntity.getCurrency(), orderInfoEntity.getCountry(), orderInfoEntity.getUserId());
                if (paymentChannelInfoVO == null) {
                    //匹配渠道失败，更新订单信息，等待人工处理
                    log.error("出金订单(orderNo:{})的人工子订单指定的出金渠道({})当前无法路由！", form.getOrderNo(), subOrder.getChannelId());
                    saveWithdrawOrderDetail(orderInfoEntity.getPlatformId(),
                            orderInfoEntity, batchNo, seqNo, subOrderNo, orderInfoEntity.getAmount(),
                            WithdrawChnlTypeEnum.CONNECTED_CHANNEL, subOrder.getChannelId(),
                            WithdrawSubOrderStatusEnum.FAILED, "当前无法路由",
                            null, -1L, -1L);

                    continue;
                }

                try {
                    OrderWithdrawForm withdrawForm = new OrderWithdrawForm();
                    withdrawForm.setOrderNo(orderInfoEntity.getOrderNo());
                    withdrawForm.setClientIp(orderInfoEntity.getClientIp());
                    withdrawForm.setUserId(orderInfoEntity.getUserId());
                    withdrawForm.setWithdrawType(orderInfoEntity.getWithdrawType());
                    withdrawForm.setWithdrawChannelList(JSON.parseArray(orderInfoEntity.getWithdrawChannelList(), String.class));
                    withdrawForm.setAmount(orderInfoEntity.getAmount());
                    withdrawForm.setAmountUSDT(orderInfoEntity.getAmountUsdt());
                    withdrawForm.setCurrency(orderInfoEntity.getCurrency());
                    withdrawForm.setCountry(orderInfoEntity.getCountry());
                    withdrawForm.setAccountHolder(orderInfoEntity.getAccountHolder());
                    withdrawForm.setBankAccount(orderInfoEntity.getBankAccount());
                    withdrawForm.setBankName(orderInfoEntity.getBankName());
                    withdrawForm.setBankBranch(orderInfoEntity.getBankBranch());
                    withdrawForm.setBankProvince(orderInfoEntity.getBankProvince());
                    withdrawForm.setBankCity(orderInfoEntity.getBankCity());
                    withdrawForm.setRemark(orderInfoEntity.getRemark());
                    withdrawForm.setCallback(orderInfoEntity.getCallback());
                    withdrawForm.setExt(orderInfoEntity.getExt());
//                    withdrawForm.setEmail(orderInfoEntity.getEmail());

                    applyResultVO = IPaymentPlatformService.get(paymentChannelInfoVO.getImplCode())
                            .applyForWithdraw(paymentChannelInfoVO, withdrawForm, subOrderNo);
                    if (applyResultVO == null) {
                        //失败
                        log.error("出金订单(orderNo:{})的人工子订单调用指定的出金渠道({})失败！", form.getOrderNo(), subOrder.getChannelId());
                        saveWithdrawOrderDetail(orderInfoEntity.getPlatformId(),
                                orderInfoEntity, batchNo, seqNo, subOrderNo, orderInfoEntity.getAmount(),
                                WithdrawChnlTypeEnum.CONNECTED_CHANNEL, subOrder.getChannelId(),
                                WithdrawSubOrderStatusEnum.FAILED, "调用失败",
                                null, -1L, -1L);
                    } else {
                        saveWithdrawOrderDetail(orderInfoEntity.getPlatformId(),
                                orderInfoEntity, batchNo, seqNo, subOrderNo, orderInfoEntity.getAmount(),
                                WithdrawChnlTypeEnum.CONNECTED_CHANNEL, subOrder.getChannelId(),
                                WithdrawSubOrderStatusEnum.PROCESSING, "申请成功",
                                applyResultVO.getThirdpartyOrderNo(), -1L, -1L);
                    }
                } catch (BusinessException e) {
                    //失败
                    log.error("出金订单(orderNo:{})的人工子订单调用指定的出金渠道({})失败！", form.getOrderNo(), subOrder.getChannelId());
                    saveWithdrawOrderDetail(orderInfoEntity.getPlatformId(),
                            orderInfoEntity, batchNo, seqNo, subOrderNo, orderInfoEntity.getAmount(),
                            WithdrawChnlTypeEnum.CONNECTED_CHANNEL, subOrder.getChannelId(),
                            WithdrawSubOrderStatusEnum.FAILED, e.getMessage(),
                            null, -1L, -1L);

                }

            }

            OrderWithdrawVO orderDepositVO = new OrderWithdrawVO();
            return ResponseDTO.ok(orderDepositVO);
        } finally {
            RedisLockUtil.unlock(redisKey);
        }
    }

    /**
     * 记录出金主订单信息
     *
     * @param form
     * @param needManualProc
     * @param manualReason
     * @return
     */
    private CashOrderInfoEntity saveWithdrawOrderInfo(RequestMerchant requestMerchant, OrderWithdrawForm form, boolean needManualProc, String manualReason) {
        CashOrderInfoEntity orderInfoEntity = new CashOrderInfoEntity();
        orderInfoEntity.setPlatformId(requestMerchant.getId());
        orderInfoEntity.setOrderNo(form.getOrderNo());
        orderInfoEntity.setWithdrawType(form.getWithdrawType());
        orderInfoEntity.setWithdrawChannelList(JSON.toJSONString(form.getWithdrawChannelList()));
        orderInfoEntity.setAmount(form.getAmount());
        orderInfoEntity.setAmountUsdt(form.getAmountUSDT());
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
        orderInfoEntity.setCallback(form.getCallback());
        orderInfoEntity.setApplyTime(System.currentTimeMillis());
//        orderInfoEntity.setName(form.getName());
        orderInfoEntity.setClientIp(form.getClientIp());
        orderInfoEntity.setUserId(form.getUserId());
//        orderInfoEntity.setDevice(form.getDevice());
//        orderInfoEntity.setEmail(form.getEmail());
        orderInfoEntity.setExt(form.getExt());

        if (needManualProc) {
            //待人工处理
            orderInfoEntity.setManualFlag(CashOrderInfoEntity.MANUAL_FLAG_YES);
            orderInfoEntity.setManualReason(manualReason);
            orderInfoEntity.setStatus(WithdrawOrderStatusEnum.Pending.getValue());
        } else {
            //自动处理
            orderInfoEntity.setManualFlag(CashOrderInfoEntity.MANUAL_FLAG_NO);
            orderInfoEntity.setStatus(WithdrawOrderStatusEnum.init.getValue());
        }

        orderInfoEntity.setCurrBatchNo(1);

        orderInfoEntity.setCreateTime(LocalDateTime.now());
        orderInfoEntity.setUpdateTime(LocalDateTime.now());
        cashOrderInfoManager.save(orderInfoEntity);
        return orderInfoEntity;
    }

    /**
     * 记录出金子订单表
     *
     * @param form
     * @param batchNo
     * @param seqNo
     * @param subOrderNo
     * @param amount
     * @param chnlType
     * @param chnlId
     * @param status
     * @param errmsg
     * @param thirdpartyOrderNo
     * @param billFileId
     * @param finishedTime
     * @return
     */
    private CashOrderDetailEntity saveWithdrawOrderDetail(Long platformId, CashOrderInfoEntity form,
                                                          int batchNo, int seqNo, String subOrderNo, BigDecimal amount,
                                                          WithdrawChnlTypeEnum chnlType, Long chnlId,
                                                          WithdrawSubOrderStatusEnum status, String errmsg,
                                                          String thirdpartyOrderNo, Long billFileId, Long finishedTime) {
        CashOrderDetailEntity orderInfoEntity = new CashOrderDetailEntity();
        orderInfoEntity.setPlatformId(platformId);
        orderInfoEntity.setOrderNo(form.getOrderNo());
        orderInfoEntity.setBatchNo(batchNo);
        orderInfoEntity.setApplyTime(System.currentTimeMillis());
        orderInfoEntity.setSeqNo(seqNo);
        orderInfoEntity.setSubOrderNo(subOrderNo);
        orderInfoEntity.setChnlType(chnlType.getValue());
        orderInfoEntity.setChannelId(chnlId);
        orderInfoEntity.setAmount(amount);
        orderInfoEntity.setAmountUsdt(amount.divide(form.getAmount()).multiply(form.getAmountUsdt()));
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
        orderInfoEntity.setCallback(form.getCallback());
        orderInfoEntity.setExt(form.getExt());

//        if (chnlType == 2) { //外部渠道  直接赋值成功
//            orderInfoEntity.setStatus(2);
//        } else {
//            orderInfoEntity.setStatus(1);
//        }
        orderInfoEntity.setStatus(status.getValue());
        orderInfoEntity.setErrmsg(errmsg);

        orderInfoEntity.setRetry(0);

        orderInfoEntity.setThirdpartyOrderNo(thirdpartyOrderNo);
        orderInfoEntity.setBillFileId(billFileId);
        orderInfoEntity.setFinishedTime(finishedTime);

        orderInfoEntity.setCreateTime(LocalDateTime.now());
        orderInfoEntity.setUpdateTime(LocalDateTime.now());
        cashOrderDetailManager.save(orderInfoEntity);
        return orderInfoEntity;
    }

    /**
     * 出金订单查询
     *
     * @param requestMerchant
     * @param form
     * @return
     */
    public ResponseDTO<OrderWithdrawDetailsVO> orderWithdrawDetail(RequestMerchant requestMerchant, OrderWithdrawDetailsForm form) {

        CashOrderInfoEntity orderInfoEntity = cashOrderInfoManager.queryByOrderNo(form.getOrderNo());
        if (orderInfoEntity == null) {
            return ResponseDTO.error(OpenErrorCode.DATA_NOT_EXIST);
        }

        OrderWithdrawDetailsVO withdrawDetailsVO = new OrderWithdrawDetailsVO();
        withdrawDetailsVO.setOrderNo(orderInfoEntity.getOrderNo());
        withdrawDetailsVO.setAmount(orderInfoEntity.getAmount());
        withdrawDetailsVO.setExt(orderInfoEntity.getExt());
        withdrawDetailsVO.setReqTime(SmartLocalDateUtil.getDateForTimestamp(orderInfoEntity.getApplyTime()));
        withdrawDetailsVO.setStatus(orderInfoEntity.getStatus());

        if (Objects.equals(orderInfoEntity.getStatus(), WithdrawOrderStatusEnum.Successed.getValue())) {
            withdrawDetailsVO.setEndTime(SmartLocalDateUtil.getDateForTimestamp(orderInfoEntity.getFinishedTime()));

            List<OrderWithdrawDetailsVO.WithdrawDetail> list = new ArrayList<>();
            List<CashOrderDetailEntity> suborders = cashOrderDetailManager.querySuccessedSubOrdersByOrderNo(orderInfoEntity.getOrderNo());
            for (CashOrderDetailEntity entity : suborders) {
                OrderWithdrawDetailsVO.WithdrawDetail detail = new OrderWithdrawDetailsVO.WithdrawDetail();
                detail.setChnlId(String.valueOf(entity.getChannelId()));
//                detail.setChnlName(String.valueOf(entity.getC));
                detail.setAmount(entity.getAmount());
                list.add(detail);
            }
            withdrawDetailsVO.setList(list);
        }

        return ResponseDTO.ok(withdrawDetailsVO);
    }

    /**
     * 出金回调示例接口
     *
     * @param orderNo
     * @param orderAmount
     * @param originalAmount
     * @param signature
     * @param orderTime
     * @param completedTime
     * @param paymentReference
     * @param currency
     * @param version
     * @param statusMessage
     * @param status
     * @return
     */
    public ResponseDTO notifyXXXWithdrawOrder(String orderNo,
                                              String orderAmount,
                                              String originalAmount,
                                              String signature,
                                              String orderTime,
                                              String completedTime,
                                              String paymentReference,
                                              String currency,
                                              String version,
                                              String statusMessage,
                                              String status) {
        log.info("notifyXXXWithdrawOrder, orderNo:{}, status:{}, message:{}", orderNo, status, statusMessage);

        String redisLock = RedisKeyConst.Order.ORDER_STATUS_UPDATE_LOCK + orderNo;
        boolean lock = RedisLockUtil.tryLock(redisLock);
        if (!lock) {
            return ResponseDTO.error(OpenErrorCode.REPEAT_SUBMIT);
        }
        try {
            CashOrderDetailEntity orderInfoEntity = cashOrderDetailManager.queryBySubOrderNo(orderNo);
            if (orderInfoEntity == null) {
                return ResponseDTO.userErrorParam("订单不存在");
            }
            PaymentChannelInfoEntity paymentChannelInfoVO = channelInfoManager.getById(orderInfoEntity.getChannelId());
            if (paymentChannelInfoVO == null) {
                return ResponseDTO.userErrorParam("渠道不存在");
            }
            Map<String, Object> params = new HashMap<>(4);
            params.put("cartId", orderNo);
            params.put("status", status);
            params.put("signature", signature);
            params.put("amount", orderAmount);
            params.put("currency", currency);
            params.put("version", version);
            params.put("orderTime", orderTime);

            IPaymentPlatformService platformService = IPaymentPlatformService.get(paymentChannelInfoVO.getImplCode());
            boolean signVerify = platformService.signVerify(paymentChannelInfoVO, params);
            if (!signVerify) {
                return ResponseDTO.userErrorParam("签名验证失败");
            }

            OrderWithdrawDetailVO orderDetailVO = platformService.updateWithdrawOrderDetail(paymentChannelInfoVO, params);
            if (WithdrawSubOrderStatusEnum.PROCESSING.equals(orderDetailVO.getStatus())) {
                return ResponseDTO.ok();
            }
            switch (orderDetailVO.getStatus()) {
                case SUCCESSED://成功
                    orderInfoEntity.setStatus(2);
                    break;
                case FAILED://失败
                    orderInfoEntity.setStatus(3);
                    break;
            }
            // 更新订单完成时间
            if (StringUtils.isNotEmpty(completedTime) && !"null".equals(completedTime)) {
                orderInfoEntity.setFinishedTime(Long.valueOf(completedTime));
            }

            orderInfoEntity.setUpdateTime(LocalDateTime.now());
            boolean updateResult = cashOrderDetailManager.updateById(orderInfoEntity);
            log.info("notifyXXXWithdrawOrder updateResult:{}", updateResult);

            //TODO 可以查询住任务的其他子任务状态，同时更改主任务状态

            return ResponseDTO.ok();
        } finally {
            RedisLockUtil.unlock(redisLock);
        }
    }

    /**
     * 数据清洗
     */
    public void dataEncrypt() {

        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.submit(() -> {
            List<PaymentOrderInfoEntity> orderInfoEntities = paymentOrderInfoManager.list();
            for (PaymentOrderInfoEntity orderInfoEntity : orderInfoEntities) {
                orderInfoEntity.setAccountNameHash(DigestUtils.sha256Hex(orderInfoEntity.getAccountName()));
            }
            paymentOrderInfoManager.updateBatchById(orderInfoEntities);
        });

//        List<PaymentChannelInfoEntity> channelInfoEntities = channelInfoManager.list();
//        channelInfoManager.updateBatchById(channelInfoEntities);
    }

    public ResponseDTO<PaymentOrderInfoVO> getPaymentOrderInfo(String token) throws Exception {
        String redisKey = RedisKeyConst.Order.ORDER_PAY_TOKEN + token;
        PaymentOrderInfoEntity orderInfoEntity = redisService.getObject(redisKey, PaymentOrderInfoEntity.class);
        if (orderInfoEntity == null) {
            return ResponseDTO.error(OpenErrorCode.ORDER_PAYMENT_OVERDUE);
        }
        log.info("收银台 - 获取订单信息 orderNo:{}", orderInfoEntity.getOrderNo());
        orderInfoEntity = paymentOrderInfoManager.getById(orderInfoEntity.getId());

        // 获取商户信息
        PaymentChannelInfoEntity paymentChannelInfoVO = channelInfoManager.getById(orderInfoEntity.getChannelId());
        if (paymentChannelInfoVO == null) {
            log.error("收银台 - 商户信息获取不到, orderNo:{}, channelId:{}", orderInfoEntity.getOrderNo(), orderInfoEntity.getChannelId());
            return ResponseDTO.error(OpenErrorCode.SYSTEM_ERROR);
        }
        IPaymentPlatformService platformService = IPaymentPlatformService.get(paymentChannelInfoVO.getImplCode());
        PaymentOrderInfoVO paymentOrderInfoVO = platformService.getPaymentOrderInfo(paymentChannelInfoVO, orderInfoEntity.getOrderNo());
        if (paymentOrderInfoVO == null) {
            return ResponseDTO.error(OpenErrorCode.SYSTEM_ERROR, "商户不支持新版收银台");
        }
        if (Objects.isNull(paymentOrderInfoVO.getOrderInfo())) {
            PaymentOrderInfoVO.OrderInfo orderInfo = new PaymentOrderInfoVO.OrderInfo();
            orderInfo.setAmount(orderInfoEntity.getAmount());
            orderInfo.setPayer(orderInfoEntity.getAccountName());
            paymentOrderInfoVO.setOrderInfo(orderInfo);
        }
        if (Objects.nonNull(paymentOrderInfoVO.getQrCodeInfo()) &&
                StringUtils.isNotEmpty(paymentOrderInfoVO.getQrCodeInfo().getQrcodeContent())) {
            String qrcodeUrl = genQrcode(paymentOrderInfoVO.getQrCodeInfo().getQrcodeContent(), orderInfoEntity);
            paymentOrderInfoVO.getQrCodeInfo().setQrcode(qrcodeUrl);
            paymentOrderInfoVO.getQrCodeInfo().setQrcodeContent(null);
        }
        paymentOrderInfoVO.setServerTime(System.currentTimeMillis());
        paymentOrderInfoVO.setExpireTime(orderInfoEntity.getExpiredTime());
        paymentOrderInfoVO.setJumpUrl(orderInfoEntity.getLandingUrl());
        log.info("收银台 - 获取订单信息返回数据, orderNo:{}, response:{}", orderInfoEntity.getOrderNo(), paymentOrderInfoVO);
        return ResponseDTO.ok(paymentOrderInfoVO);
    }

    public ResponseDTO<String> confirmPaymentOrder(String token, MultipartFile file) {
        String redisKey = RedisKeyConst.Order.ORDER_PAY_TOKEN + token;
        PaymentOrderInfoEntity orderInfoEntity = redisService.getObject(redisKey, PaymentOrderInfoEntity.class);
        if (orderInfoEntity == null) {
            return ResponseDTO.error(OpenErrorCode.ORDER_PAYMENT_OVERDUE);
        }
        return paymentConfirm(orderInfoEntity.getOrderNo(), file, redisKey);
    }

    public ResponseDTO<String> paymentConfirm(String orderNo, MultipartFile file, String redisKey) {
        log.info("收银台 - 确认付款 orderNo:{}", orderNo);
        String redisLock = RedisKeyConst.Order.ORDER_STATUS_OPERATE_LOCK + orderNo;
        boolean lock = RedisLockUtil.tryLock(redisLock);
        if (!lock) {
            log.error("收银台 - 确认付款 请求频繁, orderNo:{}", orderNo);
            return ResponseDTO.error(OpenErrorCode.REPEAT_SUBMIT);
        }
        PaymentOrderInfoEntity orderInfoEntity;
        try {
            orderInfoEntity = paymentOrderInfoManager.queryByOrderNo(orderNo);
            if (orderInfoEntity.getPaymentStatus() > 2) {
                String msg = String.format("订单%s，请勿重复操作", SmartEnumUtil.getEnumByValue(orderInfoEntity.getPaymentStatus(), OrderPayStatusEnum.class));
                if (orderInfoEntity.getStatus() > 2) {
                    msg = String.format("订单%s，请勿重复操作", SmartEnumUtil.getEnumByValue(orderInfoEntity.getStatus(), OrderStatusEnum.class));
                }
                // 尝试上传付款凭证
                uploadFile(orderInfoEntity, file);
                if (Objects.nonNull(redisKey)) {
                    redisService.delete(redisKey);
                }
                return ResponseDTO.error(UserErrorCode.ORDER_STATUS_COMPLETE, msg);
            }
            PaymentOrderInfoEntity orderInfoNew = new PaymentOrderInfoEntity();
            orderInfoNew.setId(orderInfoEntity.getId());
            if (orderInfoEntity.getPaymentStatus() == 1) {
                orderInfoNew.setPaymentStatus(2);
                orderInfoNew.setUpdateTime(LocalDateTime.now());
                paymentOrderInfoManager.updateById(orderInfoNew);
            }

        } finally {
            RedisLockUtil.unlock(redisLock);
        }
        // 尝试上传付款凭证
        uploadFile(orderInfoEntity, file);
        if (Objects.nonNull(redisKey)) {
            redisService.delete(redisKey);
        }
        log.info("收银台 - 确认付款完成 orderNo:{}", orderInfoEntity.getOrderNo());
        return ResponseDTO.ok();
    }



    @Async
    public void uploadFile(PaymentOrderInfoEntity orderInfoEntity, MultipartFile file) {
        if (orderInfoEntity.getBillFileId() != null && orderInfoEntity.getBillFileId() > 0) {
            return;
        }
        log.info("收银台 - 确认付款  {}, fileSize:{}", file.getName(), file.getSize());
        ResponseDTO<FileUploadVO> fileResponseDTO = fileService.fileUpload(file, FileFolderTypeEnum.PAYMENT_BILL.getValue(), null);
        if (!fileResponseDTO.getOk() || fileResponseDTO.getData() == null) {
            log.error("收银台 - 确认付款 付款凭证上传失败, orderNo:{}, fileResponseDTO:{}", orderInfoEntity.getOrderNo(), fileResponseDTO);
            return;
        }
        PaymentOrderInfoEntity orderInfoNew = new PaymentOrderInfoEntity();
        orderInfoNew.setId(orderInfoEntity.getId());
        orderInfoNew.setBillFileId(fileResponseDTO.getData().getFileId());
        orderInfoNew.setUpdateTime(LocalDateTime.now());
        paymentOrderInfoManager.updateById(orderInfoNew);

        PaymentChannelInfoEntity paymentChannelInfoVO = channelInfoManager.getById(orderInfoEntity.getChannelId());
        if (paymentChannelInfoVO == null) {
            log.error("收银台 - 确认付款 商户信息获取不到, orderNo:{}, channelId:{}", orderInfoEntity.getOrderNo(), orderInfoEntity.getChannelId());
            return;
        }
        IPaymentPlatformService platformService = IPaymentPlatformService.get(paymentChannelInfoVO.getImplCode());
        platformService.paymentReceipt(paymentChannelInfoVO, orderInfoEntity.getOrderNo(), file);
    }

    public ResponseDTO<String> cancelPaymentOrder(String token) {
        String redisKey = RedisKeyConst.Order.ORDER_PAY_TOKEN + token;
        PaymentOrderInfoEntity orderInfoEntity = redisService.getObject(redisKey, PaymentOrderInfoEntity.class);
        if (orderInfoEntity == null) {
            return ResponseDTO.error(OpenErrorCode.ORDER_PAYMENT_OVERDUE);
        }
        return paymentCancel(orderInfoEntity.getOrderNo(), redisKey);
    }

    public ResponseDTO<String> paymentCancel(String orderNo, String redisKey) {
        log.info("收银台 - 取消付款 orderNo:{}", orderNo);
        String redisLock = RedisKeyConst.Order.ORDER_STATUS_OPERATE_LOCK + orderNo;
        boolean lock = RedisLockUtil.tryLock(redisLock);
        if (!lock) {
            log.error("收银台 - 取消付款 请求频繁, orderNo:{}", orderNo);
            return ResponseDTO.error(OpenErrorCode.REPEAT_SUBMIT);
        }
        try {
            PaymentOrderInfoEntity orderInfoEntity = paymentOrderInfoManager.queryByOrderNo(orderNo);
            if (orderInfoEntity.getPaymentStatus() >= 3) {
                if (Objects.nonNull(redisKey)) {
                    redisService.delete(redisKey);
                }
                return ResponseDTO.ok();
            }
            if (orderInfoEntity.getPaymentStatus() > 1 || orderInfoEntity.getStatus() > 1) {
                String msg = String.format("订单%s，请勿重复操作", SmartEnumUtil.getEnumByValue(orderInfoEntity.getPaymentStatus(), OrderPayStatusEnum.class));
                if (orderInfoEntity.getStatus() > 1) {
                    msg = String.format("订单%s，请勿重复操作", SmartEnumUtil.getEnumByValue(orderInfoEntity.getStatus(), OrderStatusEnum.class));
                }
                return ResponseDTO.error(UserErrorCode.ORDER_STATUS_COMPLETE, msg);
            }

            PaymentOrderInfoEntity orderInfoNew = new PaymentOrderInfoEntity();
            orderInfoNew.setId(orderInfoEntity.getId());
            orderInfoNew.setStatus(3);
            orderInfoNew.setPaymentStatus(3);
            orderInfoNew.setUpdateTime(LocalDateTime.now());
            paymentOrderInfoManager.updateById(orderInfoNew);

            // 获取商户信息
            PaymentChannelInfoEntity paymentChannelInfoVO = channelInfoManager.getById(orderInfoEntity.getChannelId());
            if (paymentChannelInfoVO == null) {
                log.error("收银台 - 取消付款 商户信息获取不到, orderNo:{}, channelId:{}", orderInfoEntity.getOrderNo(), orderInfoEntity.getChannelId());
                return ResponseDTO.error(OpenErrorCode.SYSTEM_ERROR);
            }
            IPaymentPlatformService platformService = IPaymentPlatformService.get(paymentChannelInfoVO.getImplCode());
            platformService.paymentCancel(paymentChannelInfoVO, orderInfoEntity.getOrderNo());
            if (Objects.nonNull(redisKey)) {
                redisService.delete(redisKey);
            }
            log.info("收银台 - 取消付款成功 orderNo:{}", orderInfoEntity.getOrderNo());

            Set<String> noPush = new HashSet<>();
            noPush.add(PaymentPlatformEnum.np.getValue());

            if (!noPush.contains(paymentChannelInfoVO.getImplCode())) {
                log.info("cancelPaymentOrder paymentOrderPush, orderNo:{}", orderInfoEntity.getOrderNo());
                try {
                    orderPushService.paymentOrderPush(orderInfoEntity.getPlatformId(), orderInfoEntity.getOrderNo(), orderInfoNew.getStatus(),
                            orderInfoNew.getPaymentStatus(), orderInfoEntity.getCallback(), orderInfoEntity.getExt());
                } catch (Exception e) {
                    log.error("payStatus orderPush channelId:{}, orderNo:{}, exception", orderInfoEntity.getChannelId(),
                            orderInfoEntity.getOrderNo(), e);
                    return ResponseDTO.userErrorParam("系统异常");
                }
            }
            log.info("收银台 - 取消付款处理完成 orderNo:{}", orderInfoEntity.getOrderNo());
            return ResponseDTO.ok();
        } finally {
            RedisLockUtil.unlock(redisLock);
        }
    }


    @Async
    public void orderFailSendSms() {
        if (StringUtils.isEmpty(phone)) {
            return;
        }
        LambdaQueryWrapper<PaymentChannelInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentChannelInfoEntity::getImplCode, "fxmch");
        queryWrapper.last("limit 1");
        PaymentChannelInfoEntity channelInfoVO = paymentChannelInfoManager.getOne(queryWrapper);

        String date =SmartLocalDateUtil.getGMTDate();
        String contentType = "application/json";
        String uri = "/openapi/sms/send";
        String signature = SignatureUtil.genSignature("GET", uri, contentType, date, null, channelInfoVO.getMerSk());
        String authorization = channelInfoVO.getMerAk() + ":" + signature;

        Map<String, String> headers = new HashMap<>(4);
        headers.put("Authorization", authorization);
        headers.put("Content-Type", contentType);
        headers.put("Date", date);
        headers.put(LogIdUtil.REQUEST_ID, LogIdUtil.getRequestId());

        Map<String, Object> params = new HashMap<>();
        params.put("phone", phone);
        params.put("smsType", 1);
        params.put("businessType", "order_fail");

        String resultStr = HttpUtil.apiGet(channelInfoVO.getDomain(), uri, params, headers);

        log.info("fxmch sms request, mercode:{}, params:{}, result:{}", channelInfoVO.getMerCode(), params, resultStr);
        if (StringUtils.isEmpty(resultStr)) {
            log.error("fxmch sms result is empty, mercode:{}, result:{}", channelInfoVO.getMerCode(), resultStr);
            throw new BusinessException(OpenErrorCode.SYSTEM_ERROR);
        }
        ResponseDTO<String> responseDTO = JSONObject.parseObject(resultStr, new TypeReference<ResponseDTO<String>>() {});
        if (!responseDTO.getOk()) {
            log.error("短信发送失败，responseDTO:{}", responseDTO);
        }
    }

}
