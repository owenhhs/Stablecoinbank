package net.lab1024.sa.admin.module.openapi.order.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 速鼎订单流水 实体类
 *
 * @Author Sunny
 * @Date 2024-09-04 15:18:34
 * @Copyright Sunny
 */

@Data
@TableName("t_order_suding_record")
public class OrderSudingRecordEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商户id
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
    private String paymentFlag;

    /**
     * 状态码
     */
    private Integer status;

    /**
     * 交易單成立與否
     */
    private String result;

    /**
     * 请求信息
     */
    private String message;

    /**
     * 平台订单编号
     */
    private String orderSn;

    /**
     * 币别
     */
    private String currencyType;

    /**
     * 提单金额
     */
    private BigDecimal originalAmount;

    /**
     * 交易金额
     */
    private BigDecimal orderAmount;

    /**
     * 轉換系統預設幣值⾦額
     */
    private BigDecimal exchangeAmount;

    /**
     * ⽀付連結，供商⼾產⽣⼆維碼或⾃⾏導向，與 payment_img 擇⼀使⽤，只能訪問⼀次
     */
    private String paymentUri;

    /**
     * 已轉成⼆維碼之⽀付連結，與 payment_uri 擇⼀使⽤，只能訪問⼀次。payment_img 未端之256為⼆維碼size，可⾃⾏更換，size range 64 ~ 512。
     */
    private String paymentImg;

    // 网银
    // 收款者银行名称
    private String bankName;
    // 收款者银行支行
    private String bankBranch;
    // 收款者银行账号
    private String bankAccount;
    // 收款者银行户名
    private String bankAccountName;

    // 非网银
    // 收款者姓名
    private String ownerName;
    // 收款者昵称
    private String ownerNickName;
    // 收款码url
    private String ownerUrlLink;
    // 收款者账号
    private String ownerAccount;
    // 收款码二维码内容
    private String ownerQrcode;
    // 收款码二维码链接
    private String ownerQrcodeUrl;
    // 收款者手机号
    private String ownerPhoneNumber;
    /**
     * 交易單不成立時返回商⼾請求數據，供商⼾技術⼈員debug使⽤
     */
    private String requestData;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}