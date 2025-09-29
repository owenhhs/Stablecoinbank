package net.lab1024.sa.admin.module.openapi.order.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * lyp订单流水记录表 实体类
 *
 * @Author Sunny
 * @Date 2024-10-09 14:35:52
 * @Copyright Sunny
 */

@Data
@TableName("t_order_lyp_record")
public class OrderLypRecordEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 渠道商户id
     */
    private Long channelId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 交易类型
     */
    private String tradeType;

    /**
     * 銀行類型，bank/qrcode
     */
    private String depositType;

    /**
     * 二維碼渠道，deposit_type=qrcode时有值
     */
    private String paymentChannel;

    /**
     * 订单金额
     */
    private BigDecimal amount;

    /**
     * 状态码，0-成功；其他为失败
     */
    private Integer code;

    /**
     * 请求信息
     */
    private String msg;

    /**
     * 查看訂單狀態信息的鏈接
     */
    private String orderInfoLink;

    /**
     * 二维码
     */
    private String qrCodeContent;

    /**
     * 银行账户
     */
    private String account;

    /**
     * 户名
     */
    private String accountName;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 支行名称
     */
    private String bankBranch;

    /**
     * 银行代码
     */
    private String bankCode;

    /**
     * 1 待确认 2 已确认 3 挂起
     */
    private Integer status;

    /**
     * 处理结果 （没有存款，取消存款，確認存款）
     */
    private String result;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}