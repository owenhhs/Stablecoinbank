package net.lab1024.sa.admin.module.business.payment.channel.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentUserEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.form.PaymentUserQueryForm;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentUserVO;
import net.lab1024.sa.admin.module.business.payment.channel.manager.PaymentUserManager;
import net.lab1024.sa.base.common.code.SystemErrorCode;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 孙宇
 * @date 2025/04/14 21:33
 */
@Slf4j
@Service
public class PaymentUserService {

    @Resource
    private PaymentUserManager paymentUserManager;

    public PageResult<PaymentUserVO> queryPage(PaymentUserQueryForm queryForm) {
        if (StringUtils.isNotEmpty(queryForm.getUserName())) {
            queryForm.setUserName(DigestUtils.md5Hex(queryForm.getUserName()));
        }
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        page.addOrder(new OrderItem("create_time", false));
        List<PaymentUserVO> list = paymentUserManager.queryPage(page, queryForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }

    public void updateUserBlack(Long userId, Integer black) {
        if (userId == null || black == null) {
            throw new BusinessException(UserErrorCode.PARAM_ERROR, "参数不能为空");
        }
        PaymentUserEntity paymentUserEntity = new PaymentUserEntity();
        paymentUserEntity.setId(userId);
        paymentUserEntity.setBlack(black);
        paymentUserEntity.setUpdateTime(LocalDateTime.now());
        paymentUserManager.updateById(paymentUserEntity);
    }
}
