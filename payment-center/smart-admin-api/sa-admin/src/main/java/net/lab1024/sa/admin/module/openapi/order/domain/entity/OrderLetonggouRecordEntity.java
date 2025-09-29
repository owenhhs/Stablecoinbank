package net.lab1024.sa.admin.module.openapi.order.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * letonggou平台订单申请记录表 实体类
 *
 * @Author Sunny
 * @Date 2024-09-05 17:14:49
 * @Copyright Sunny
 */

@Data
@TableName("t_order_letonggou_record")
public class OrderLetonggouRecordEntity {

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
     * 交易方式
     */
    private String paymentType;

    /**
     * 平台订单编号
     */
    private String tradeNo;

    /**
     * 支付平台订单号，支付宝、微信等
     */
    private String apiTradeNo;

    /**
     * 状态码，1-成功；其他为失败
     */
    private Integer code;

    /**
     * 请求信息
     */
    private String msg;

    /**
     * 支付跳转url
     */
    private String payurl;

    /**
     * 二维码链接
     */
    private String qrcode;

    /**
     * 小程序跳转url
     */
    private String urlscheme;

    /**
     * 1为支付成功，0为未支付
     */
    private Integer status;
    /**
     * 支付者账号
     */
    private String buyer;
    /**
     * 创建订单时间
     */
    private String addtime;
    /**
     * 订单完成时间
     */
    private String endtime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}