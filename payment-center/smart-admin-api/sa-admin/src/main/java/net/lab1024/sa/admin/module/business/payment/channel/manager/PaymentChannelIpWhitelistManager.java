package net.lab1024.sa.admin.module.business.payment.channel.manager;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.order.dao.CashOrderInfoDao;
import net.lab1024.sa.admin.module.business.order.dao.PaymentOrderInfoDao;
import net.lab1024.sa.admin.module.business.order.domain.vo.OrderCountVO;
import net.lab1024.sa.admin.module.business.payment.channel.dao.ChannelRouteWhiteListDao;
import net.lab1024.sa.admin.module.business.payment.channel.dao.PaymentChannelInfoDao;
import net.lab1024.sa.admin.module.business.payment.channel.dao.PaymentChannelIpWhitelistDao;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.ChannelRouteWhiteListEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelInfoEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelIpWhitelistEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderDepositForm;
import net.lab1024.sa.admin.module.openapi.order.domain.form.OrderWithdrawForm;
import net.lab1024.sa.base.common.enumeration.PaymentTypeEnum;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartBigDecimalUtil;
import net.lab1024.sa.base.common.util.SmartDateFormatterEnum;
import net.lab1024.sa.base.common.util.SmartLocalDateUtil;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
public class PaymentChannelIpWhitelistManager extends ServiceImpl<PaymentChannelIpWhitelistDao, PaymentChannelIpWhitelistEntity> {


}
