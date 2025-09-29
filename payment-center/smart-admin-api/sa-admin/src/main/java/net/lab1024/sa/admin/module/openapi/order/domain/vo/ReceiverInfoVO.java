package net.lab1024.sa.admin.module.openapi.order.domain.vo;

import lombok.Data;
import net.lab1024.sa.admin.emumeration.PaymentPlatformEnum;

/**
 * @author 孙宇
 * @date 2024/09/05 00:29
 */
@Data
public class ReceiverInfoVO {

    /**
     * 渠道
     */
    private PaymentPlatformEnum channel;

    /**
     * 页面地址
     */
    private String pageUrl;

    /**
     * 微信小程序链接，用于js跳转
     */
    private String wechatUrl;

    /**
     * 支付链接，用于生成二维码或自行导向
     */
    private String payUrl;

    /**
     * 二维码图片地址
     */
    private String imgUrl;

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
}
