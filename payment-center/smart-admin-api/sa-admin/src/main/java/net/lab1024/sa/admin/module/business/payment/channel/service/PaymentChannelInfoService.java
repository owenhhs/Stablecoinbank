package net.lab1024.sa.admin.module.business.payment.channel.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import net.lab1024.sa.admin.module.business.payment.channel.dao.PaymentChannelInfoDao;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelInfoEntity;
import net.lab1024.sa.admin.module.business.payment.channel.domain.form.PaymentChannelInfoAddForm;
import net.lab1024.sa.admin.module.business.payment.channel.domain.form.PaymentChannelInfoQueryForm;
import net.lab1024.sa.admin.module.business.payment.channel.domain.form.PaymentChannelInfoUpdateForm;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoOptionVO;
import net.lab1024.sa.admin.module.business.payment.channel.domain.vo.PaymentChannelInfoVO;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.PageResult;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 渠道基本信息表 Service
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Service
public class PaymentChannelInfoService {

    @Resource
    private PaymentChannelInfoDao paymentChannelInfoDao;

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<PaymentChannelInfoVO> queryPage(PaymentChannelInfoQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<PaymentChannelInfoVO> list = paymentChannelInfoDao.queryPage(page, queryForm);
        PageResult<PaymentChannelInfoVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(PaymentChannelInfoAddForm addForm) {
        if (addForm.getInterfaceType() == 1) {
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "不支持添加API接口方式渠道！");
        }

        PaymentChannelInfoEntity paymentChannelInfoEntity = SmartBeanUtil.copy(addForm, PaymentChannelInfoEntity.class);

        //对非API方式，自动添加implCode以及商户密钥等信息
        PaymentChannelInfoEntity fmchChnl = paymentChannelInfoDao.selectById(1000000003L);
        if (fmchChnl == null) {
            return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST);
        }
        paymentChannelInfoEntity.setMerAk(fmchChnl.getMerAk());
        paymentChannelInfoEntity.setMerSk(fmchChnl.getMerSk());
        paymentChannelInfoEntity.setImplCode(fmchChnl.getImplCode());
        paymentChannelInfoEntity.setDomain(fmchChnl.getDomain());
        paymentChannelInfoEntity.setExt(fmchChnl.getExt());

        paymentChannelInfoEntity.setCreateTime(LocalDateTime.now());
        paymentChannelInfoEntity.setUpdateTime(LocalDateTime.now());
        paymentChannelInfoDao.insert(paymentChannelInfoEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     * @param updateForm
     * @return
     */
    public ResponseDTO<String> update(PaymentChannelInfoUpdateForm updateForm) {
        if (updateForm.getInterfaceType() == 1) {
            if (StringUtils.isEmpty(updateForm.getMerAk()) || StringUtils.isEmpty(updateForm.getMerSk())) {
                return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "API接口方式下AK或SK不能为空！");
            }
        }

        PaymentChannelInfoEntity paymentChannelInfoEntity = SmartBeanUtil.copy(updateForm, PaymentChannelInfoEntity.class);

        PaymentChannelInfoEntity orign = paymentChannelInfoDao.selectById(paymentChannelInfoEntity.getId());
        if (orign == null) {
            return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST);
        }

        //不允许从 非API  改成 API
        if (orign.getInterfaceType() == 2 && paymentChannelInfoEntity.getInterfaceType() == 1) {
            return ResponseDTO.error(UserErrorCode.PARAM_ERROR, "不支持从【非API】改为【API】接口方式！");
        }

        if (orign.getInterfaceType() == 1 && paymentChannelInfoEntity.getInterfaceType() == 2) {
            //从 API 改成 非API
            PaymentChannelInfoEntity fmchChnl = paymentChannelInfoDao.selectById(1000000003L);
            if (fmchChnl == null) {
                return ResponseDTO.error(UserErrorCode.DATA_NOT_EXIST);
            }
            paymentChannelInfoEntity.setMerAk(fmchChnl.getMerAk());
            paymentChannelInfoEntity.setMerSk(fmchChnl.getMerSk());
            paymentChannelInfoEntity.setImplCode(fmchChnl.getImplCode());
            paymentChannelInfoEntity.setDomain(fmchChnl.getDomain());
            paymentChannelInfoEntity.setExt(fmchChnl.getExt());
        }

        paymentChannelInfoEntity.setUpdateTime(LocalDateTime.now());
        paymentChannelInfoDao.updateById(paymentChannelInfoEntity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     *
     * @param idList
     * @return
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        paymentChannelInfoDao.deleteBatchIds(idList);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long id) {
        if (null == id){
            return ResponseDTO.ok();
        }

        paymentChannelInfoDao.deleteById(id);
        return ResponseDTO.ok();
    }


    /**
     * 查询列表
     * @param flag 0-不区分（所有） 1-支付渠道 2-出金渠道
     * @return
     */
    public List<PaymentChannelInfoOptionVO> queryOptions(int flag) {
        LambdaQueryWrapper<PaymentChannelInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        if (flag == 1) {
            queryWrapper.eq(PaymentChannelInfoEntity::getPaymentFlag, 1);
        } else if (flag == 2) {
            queryWrapper.eq(PaymentChannelInfoEntity::getCashFlag, 1);
        }
        List<PaymentChannelInfoEntity> channelInfoEntityList = paymentChannelInfoDao.selectList(queryWrapper);
        return channelInfoEntityList.stream().map(v -> {
            PaymentChannelInfoOptionVO vo = new PaymentChannelInfoOptionVO();
            vo.setId(v.getId());
            vo.setMerName(v.getMerName());
            vo.setMerCode(v.getMerCode());
            return vo;
        }).collect(Collectors.toList());
    }
}
