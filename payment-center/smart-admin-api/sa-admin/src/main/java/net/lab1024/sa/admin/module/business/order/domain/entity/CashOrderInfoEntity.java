package net.lab1024.sa.admin.module.business.order.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 兑付订单信息表 实体类
 *
 * @Author Sunny
 * @Date 2024-09-03 15:16:42
 * @Copyright Sunny
 */

@Data
@TableName("t_cash_order_info")
public class CashOrderInfoEntity {

    /**
     * 人工处理标志
     */
    public static final int MANUAL_FLAG_YES = 1;
    public static final int MANUAL_FLAG_NO  = 0;


    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 平台ID
     */
    private Long platformId;

    /**
     * 渠道id
     */
    private Long channelId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 出金類型，bank
     */
    private String withdrawType;

    /**
     * 限定出金渠道
     */
    private String withdrawChannelList;

    /**
     * 出金金额
     */
    private BigDecimal amount;

    /**
     * 出金金额（USDT）
     */
    private BigDecimal amountUsdt;

    /**
     * 币种
     */
    private String currency;

    /**
     * 国家
     */
    private String country;

    /**
     * 银行账户名称
     */
    private String accountHolder;

    /**
     * 银行卡号码
     */
    private String bankAccount;

    /**
     * 银行代码
     */
    private String bankCode;

    /**
     * 收款银行名称
     */
    private String bankName;

    /**
     * 收款银行分行名称
     */
    private String bankBranch;

    /**
     * 收款银行所在省份
     */
    private String bankProvince;

    /**
     * 收款银行所在城市
     */
    private String bankCity;

    /**
     * 出金备注
     */
    private String remark;

    /**
     * 订单确认通知回调地址
     */
    private String callback;

    /**
     * 用户编号
     */
    private String userId;

    /**
     * 用户IP地址
     */
    private String clientIp;

    /**
     * 用户设备类型
     */
    private String device;

    /**
     * 额外参数
     */
    private String ext;

    /**
     * 交易状态 1-待处理 2-处理中 3-已完成
     */
    private Integer status;

    /**
     * 当前批次号
     */
    private Integer currBatchNo;

    /**
     * 人工处理标志：0-自动处理 1-人工处理
     */
    private Integer manualFlag;

    /**
     * 人工处理原因
     */
    private String manualReason;

    /**
     * 人工拆单标志 0-未拆单 1-拆单
     */
    private Integer manualSplitFlag;

    /**
     * 人工处理备注
     */
    private String manualRemark;

    /**
     * 申请时间
     */
    private Long applyTime;

    /**
     * 订单完成时间
     */
    private Long finishedTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}