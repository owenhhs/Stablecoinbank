package net.lab1024.sa.admin.module.business.payment.channel.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.payment.channel.dao.PaymentUserDao;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentUserEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.form.PaymentUserQueryForm;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentUserVO;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 支付用户表  Manager
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */
@Slf4j
@Service
public class PaymentUserManager extends ServiceImpl<PaymentUserDao, PaymentUserEntity> {


    public PaymentUserEntity getByUserNameOrUserId(String userName, String userId) {
        String usernameHash = DigestUtils.md5Hex(userName);

        PaymentUserEntity userEntity = null;
        if (StringUtils.isNotEmpty(userId)) {
            userEntity = getByUserId(userId);
        }
        if (userEntity == null) {
            userEntity = getByUserNameHash(usernameHash);
        }

        if (userEntity == null) {
            userEntity = save(userName, usernameHash, userId);
        } else if (StringUtils.isNotEmpty(userId)) {
            // userEntity userid为空
            if (StringUtils.isEmpty(userEntity.getExtUserId())) {
                userEntity.setExtUserId(userId);
                baseMapper.updateById(userEntity);
            } else if (!userEntity.getExtUserId().equals(userId)) {
                userEntity = save(userName, usernameHash, userId);
            }
        }
        return userEntity;
    }

    public PaymentUserEntity save(String userName, String usernameHash, String userId) {
        PaymentUserEntity userEntity = new PaymentUserEntity();
        userEntity.setExtUserId(userId);
        userEntity.setUsername(userName);
        userEntity.setUsernameHash(usernameHash);
        userEntity.setIsXj(userName.length() > 4 ? 1 : 0);
        userEntity.setBlack(0);
        userEntity.setCreateTime(LocalDateTime.now());
        userEntity.setUpdateTime(LocalDateTime.now());
        baseMapper.insert(userEntity);
        return userEntity;
    }

    public PaymentUserEntity getByUserId(String userId) {
        LambdaQueryWrapper<PaymentUserEntity> queryWrapper = new LambdaQueryWrapper<PaymentUserEntity>()
                .eq(PaymentUserEntity::getExtUserId, userId);
        return baseMapper.selectOne(queryWrapper);
    }

    public PaymentUserEntity getByUserNameHash(String usernameHash) {
        LambdaQueryWrapper<PaymentUserEntity> queryWrapper = new LambdaQueryWrapper<PaymentUserEntity>()
                .eq(PaymentUserEntity::getUsernameHash, usernameHash)
                .orderByAsc(PaymentUserEntity::getExtUserId).last("limit 1");
        return baseMapper.selectOne(queryWrapper);
    }

    public List<PaymentUserVO> queryPage(Page page, PaymentUserQueryForm queryForm) {
        return baseMapper.queryPage(page, queryForm);
    }
}
