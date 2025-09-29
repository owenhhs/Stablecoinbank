package net.lab1024.sa.base.module.support.merchant.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 商户表 实体类
 *
 * @Author sunyu
 * @Date 2024-07-24 16:21:32
 * @Copyright sunyu
 */

@Data
@TableName("t_merchant_manage")
public class MerchantManageEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商户号AK
     */
    private String merNo;

    /**
     * 商户名称
     */
    private String merName;

    /**
     * 商户秘钥SK
     */
    private String secretKey;

    /**
     * rsa 解密公钥
     */
    private String rsaPublicKey;

    /**
     * 商户状态，0-禁用；1-启用；
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