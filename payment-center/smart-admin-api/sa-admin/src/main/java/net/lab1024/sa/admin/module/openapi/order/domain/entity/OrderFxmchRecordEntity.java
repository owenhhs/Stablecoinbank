package net.lab1024.sa.admin.module.openapi.order.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 渠道管理平台请求记录 实体类
 *
 * @Author Sunny
 * @Date 2024-09-12 16:39:39
 * @Copyright Sunny
 */

@Data
@TableName("t_order_fxmch_record")
public class OrderFxmchRecordEntity {

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
     * 状态码，0-成功；其他为失败
     */
    private Integer code;

    /**
     * 请求信息
     */
    private String msg;

    /**
     * 二维码地址
     */
    private String payurl;

    /**
     * 收款人银行
     */
    private String bankInfo;

    /**
     * 开户行
     */
    private String bankName;

    /**
     * 银行卡号
     */
    private String bankNo;

    /**
     * 银行账户
     */
    private String username;

    private String subMerName;

    /**
     * 1 待确认 2 已确认 3 挂起
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}