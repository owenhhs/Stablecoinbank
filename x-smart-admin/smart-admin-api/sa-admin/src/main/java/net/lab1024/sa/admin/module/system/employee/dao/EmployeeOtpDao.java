package net.lab1024.sa.admin.module.system.employee.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.lab1024.sa.admin.module.system.employee.domain.entity.EmployeeOtpEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * 员工多因子信息 dao
 */
@Mapper
@Component
public interface EmployeeOtpDao extends BaseMapper<EmployeeOtpEntity> {


}