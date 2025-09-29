package net.lab1024.sa.admin.module.business.payment.channel.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.lab1024.sa.admin.module.business.payment.channel.dao.PaymentChannelBusinessCfgDao;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelBusinessCfgEntity;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelInfoEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 支付渠道业务范围配置表  Manager
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */
@Service
public class PaymentChannelBusinessCfgManager extends ServiceImpl<PaymentChannelBusinessCfgDao, PaymentChannelBusinessCfgEntity> {

    @Resource
    private PaymentChannelInfoManager paymentChannelInfoManager;

    public List<PaymentChannelBusinessCfgEntity> findAll() {
        List<PaymentChannelInfoEntity> channelInfoEntityList = paymentChannelInfoManager.list();
        List<Long> channelIdList = channelInfoEntityList.stream().map(PaymentChannelInfoEntity::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(channelIdList)) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<PaymentChannelBusinessCfgEntity> channelBusinessQuery = new LambdaQueryWrapper<>();
        channelBusinessQuery.in(PaymentChannelBusinessCfgEntity::getChannelId, channelIdList);
        return baseMapper.selectList(channelBusinessQuery);
    }

}
