package net.lab1024.sa.admin.module.business.payment.channel.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.payment.channel.dao.*;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.*;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentChannelParentManager extends ServiceImpl<PaymentChannelParentDao, PaymentChannelParentEntity> {


    public PaymentChannelParentEntity getByDepartmentId(Long departmentId) {
        LambdaQueryWrapper<PaymentChannelParentEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaymentChannelParentEntity::getDepartmentId, departmentId);
        return getOne(queryWrapper);
    }


}

