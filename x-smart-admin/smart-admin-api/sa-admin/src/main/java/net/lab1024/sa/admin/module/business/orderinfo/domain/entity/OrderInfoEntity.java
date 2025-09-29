package net.lab1024.sa.admin.module.business.orderinfo.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import net.lab1024.sa.base.module.support.sensitive.mybatisplugins.annoation.EncryptedColumn;
import net.lab1024.sa.base.module.support.sensitive.mybatisplugins.annoation.EncryptedTable;

/**
 * 订单信息表 实体类
 *
 * @Author sunyu
 * @Date 2024-07-23 14:25:56
 * @Copyright sunyu
 */
@EncryptedTable
@Data
@TableName("t_order_info")
public class OrderInfoEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 部门ID
     */
    private Long departmentId;


    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 銀行類型，bank/qrcode
     */
    private String depositType;

    /**
     * 二維碼渠道，deposit_type=qrcode时有值
     */
    private String paymentChannel;

    /**
     * 存款金额
     */
    private BigDecimal amount;

    /**
     * 币种
     */
    private String currency;

    /**
     * 国家
     */
    private String country;

    /**
     * 存款人
     */
    @EncryptedColumn
    private String depositHolder;

    private String depositHolderHash;

    /**
     * 存款账号
     */
    @EncryptedColumn
    private String bankAccount;

    /**
     * 订单确认回调地址
     */
    private String callback;
    /**
     * 落地页地址
     */
    private String landingPage;


    /**
     * 存款备注
     */
    private String depositRemark;


    /**
     * 收款银行
     */
    private String collectionBank;

    /**
     * 开户行
     */
    private String collectionBankAddress;

    /**
     * 收款银行卡号
     */
    private String collectionCardNo;

    /**
     * 收款银行卡持有人
     */
    private String collectionHolder;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 申请时间
     */
    private LocalDateTime applyTime;

    private LocalDateTime finishTime;

    /**
     * 回单文件id
     */
    private Long receiptFileId;

    /**
     * 订单处理状态 1 待确认 2 已确认 3 挂起
     */
    private Integer status;

    /**
     * 退款原因
     */
    private String refundReason;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    private Long paymentChannelBusinessId;

    private Long paymentChannelPayInfoId;

    /**
     * 路由释放标志 0-未释放 1-已释放
     */
    private Integer routeReverseFlag;
}