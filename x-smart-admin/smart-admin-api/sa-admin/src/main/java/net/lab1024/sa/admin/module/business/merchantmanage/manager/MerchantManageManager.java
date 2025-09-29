package net.lab1024.sa.admin.module.business.merchantmanage.manager;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import net.lab1024.sa.admin.module.business.merchantmanage.domain.entity.MerchantManageEntity;
import net.lab1024.sa.admin.module.business.merchantmanage.dao.MerchantManageDao;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import net.lab1024.sa.admin.module.business.merchantmanage.domain.vo.MerchantManageVO;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.constant.CacheKeyConst;
import net.lab1024.sa.base.module.support.redis.RedisService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 商户表  Manager
 *
 * @Author sunyu
 * @Date 2024-07-24 16:21:32
 * @Copyright sunyu
 */
@Service
public class MerchantManageManager extends ServiceImpl<MerchantManageDao, MerchantManageEntity> {

    @Resource
    private RedisService redisService;

    public MerchantManageVO queryByMerchantNo(String merchantNo) {
        String redisKey = CacheKeyConst.Open.OPEN_MERCHANT_CACHE_KEY + merchantNo;
        MerchantManageEntity merchantManageEntity = redisService.getObject(redisKey, MerchantManageEntity.class);
        if (merchantManageEntity == null) {
            QueryWrapper<MerchantManageEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("mer_no", merchantNo);
            merchantManageEntity = baseMapper.selectOne(queryWrapper);
            redisService.set(redisKey, merchantManageEntity, 86400L);
        }
        return SmartBeanUtil.copy(merchantManageEntity, MerchantManageVO.class);
    }

}
