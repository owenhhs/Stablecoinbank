package net.lab1024.sa.admin.module.system.employee.manager;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.lab1024.sa.admin.module.system.employee.dao.EmployeeOtpDao;
import net.lab1024.sa.admin.module.system.employee.domain.entity.EmployeeOtpEntity;
import net.lab1024.sa.base.common.util.OtpUtl;
import org.springframework.stereotype.Service;

/**
 * 员工 manager
 *
 * @Author 1024创新实验室: 胡克
 * @Date 2021-12-29 21:52:46
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@Service
public class EmployeeOtpManager extends ServiceImpl<EmployeeOtpDao, EmployeeOtpEntity> {

    public void saveEmployeeOtp(Long employeeId, Boolean enabledFlag) {
        // 保存otp配置
        EmployeeOtpEntity otpEntity = new EmployeeOtpEntity();
        otpEntity.setEmployeeId(employeeId);
        otpEntity.setSecretKey(OtpUtl.generateSecretKey());
        otpEntity.setEnabledFlag(enabledFlag);
        otpEntity.setBoundFlag(false);
        otpEntity.setDeletedFlag(false);
        this.save(otpEntity);
    }

    public EmployeeOtpEntity getEmployeeOtp(Long employeeId) {
        LambdaQueryWrapper<EmployeeOtpEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EmployeeOtpEntity::getEmployeeId, employeeId);
        return this.getOne(queryWrapper);
    }

}
