package net.lab1024.sa.base.module.support.sms.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import net.lab1024.sa.base.module.support.sms.domain.SmsQxcioudRecordEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @author 孙宇
 * @date 2024/11/08 00:05
 */
@Mapper
@Component
public interface SmsQxcioudRecordDao extends BaseMapper<SmsQxcioudRecordEntity>  {

}
