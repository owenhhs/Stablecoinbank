package net.lab1024.sa.admin.module.business.skucfg.manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.lab1024.sa.admin.module.business.skucfg.dao.SkuCfgDao;
import net.lab1024.sa.admin.module.business.skucfg.domain.entity.SkuCfgEntity;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 商品配置  Manager
 *
 * @Author sunyu
 * @Date 2024-07-23 14:25:50
 * @Copyright sunyu
 */
@Service
public class SkuCfgManager extends ServiceImpl<SkuCfgDao, SkuCfgEntity> {

    public SkuCfgEntity getLastSkuCfg() {
        QueryWrapper<SkuCfgEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("limit 1");
        return baseMapper.selectOne(queryWrapper);
    }

}
