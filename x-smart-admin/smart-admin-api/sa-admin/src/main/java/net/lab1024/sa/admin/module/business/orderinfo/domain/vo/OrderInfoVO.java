package net.lab1024.sa.admin.module.business.orderinfo.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import net.lab1024.sa.base.module.support.sensitive.mybatisplugins.annoation.EncryptedColumn;
import net.lab1024.sa.base.module.support.sensitive.mybatisplugins.annoation.EncryptedTable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单信息表 列表VO
 *
 * @Author sunyu
 * @Date 2024-07-23 14:25:56
 * @Copyright sunyu
 */

@Data
@EncryptedTable
public class OrderInfoVO {


    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    @ExcelIgnore
    private Long id;

    /**
     * 部门ID
     */
    @ExcelIgnore
    private Long departmentId;


    /**
     * 订单编号
     */
    @ExcelProperty("订单编号")
    private String orderNo;

    /**
     * 銀行類型，bank/qrcode
     */
    @ExcelProperty("银行类型")
    private String depositType;

    /**
     * 二維碼渠道，deposit_type=qrcode时有值
     */
    @ExcelProperty("二维码渠道")
    private String paymentChannel;

    /**
     * 存款金额
     */
    @ExcelProperty("存款金額")
    private BigDecimal amount;

    @ExcelProperty("币种")
    private String currency;

    @ExcelProperty("国家")
    private String country;

    /**
     * 存款人
     */
    @ExcelProperty("存款人")
    @EncryptedColumn
    private String depositHolder;

    /**
     * 存款账号
     */
    @ExcelProperty("存款账号")
    @EncryptedColumn
    private String bankAccount;

    /**
     * 订单确认回调地址
     */
    @ExcelIgnore
    private String callback;
    /**
     * 落地页地址
     */
    @ExcelIgnore
    private String landingPage;


    /**
     * 存款备注
     */
    @ExcelIgnore
    private String depositRemark;


    /**
     * 收款银行
     */
    @ExcelProperty("收款银行")
    private String collectionBank;

    /**
     * 开户行
     */
    @ExcelProperty("开户行")
    private String collectionBankAddress;

    /**
     * 收款银行卡号
     */
    @ExcelProperty("收款銀行卡號")
    private String collectionCardNo;

    /**
     * 收款银行卡持有人
     */
    @ExcelProperty("收款銀行卡持有人")
    private String collectionHolder;

    /**
     * 回单文件id
     */
    @ExcelIgnore
    private Long receiptFileId;
    
    /**
     * 申请时间
     */
    @ExcelProperty("申請時間")
    private LocalDateTime applyTime;

    /**
     * 订单处理状态 1 待确认 2 已确认 3 挂起
     */
    @ExcelIgnore
    private Integer status;

    @ExcelProperty("訂單處理狀態")
    private String statusStr;

    /**
     * 退款原因
     */
    @ExcelProperty("退款原因")
    private String refundReason;

    /**
     * 回单文件链接
     */
    @ExcelIgnore
    private String receiptFileUrl;

    /**
     * 用户ID
     */
    @ExcelIgnore
    private Long userId;

}