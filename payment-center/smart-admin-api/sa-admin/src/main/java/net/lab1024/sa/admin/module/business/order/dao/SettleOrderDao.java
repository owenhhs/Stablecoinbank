package net.lab1024.sa.admin.module.business.order.dao;

import java.util.List;
import net.lab1024.sa.admin.module.business.order.domain.entity.SettleOrderEntity;
import net.lab1024.sa.admin.module.business.order.domain.form.SettleOrderQueryForm;
import net.lab1024.sa.admin.module.business.order.domain.vo.PaymentOrderInfoListVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * 结算单订单关联表 Dao
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:43
 * @Copyright Sunny
 */

@Mapper
@Component
public interface SettleOrderDao extends BaseMapper<SettleOrderEntity> {

    /**
     * 分页 查询
     *
     * @param page
     * @param queryForm
     * @return
     */
    List<PaymentOrderInfoListVO> queryPage(Page page, @Param("queryForm") SettleOrderQueryForm queryForm);


}
