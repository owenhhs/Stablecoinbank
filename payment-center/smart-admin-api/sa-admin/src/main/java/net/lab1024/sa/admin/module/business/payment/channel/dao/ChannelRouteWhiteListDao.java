package net.lab1024.sa.admin.module.business.payment.channel.dao;

import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.ChannelRouteWhiteListEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * 支付渠道路由白名单 Dao
 *
 * @Author Sunny
 * @Date 2024-10-16 15:02:53
 * @Copyright Sunny
 */

@Mapper
@Component
public interface ChannelRouteWhiteListDao extends BaseMapper<ChannelRouteWhiteListEntity> {

}
