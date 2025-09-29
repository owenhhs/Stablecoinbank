package net.lab1024.sa.admin.module.business.payment.channel.domain.vo;

import lombok.Data;
import net.lab1024.sa.base.module.support.sensitive.mybatisplugins.annoation.EncryptedColumn;
import net.lab1024.sa.base.module.support.sensitive.mybatisplugins.annoation.EncryptedTable;

import java.time.LocalDateTime;

/**
 * @author 孙宇
 * @date 2025/04/14 21:35
 */
@Data
@EncryptedTable
public class PaymentUserVO {
    private Long id;
    @EncryptedColumn
    private String username;
    private String extUserId;
    private Integer isXj;
    private Integer black;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
