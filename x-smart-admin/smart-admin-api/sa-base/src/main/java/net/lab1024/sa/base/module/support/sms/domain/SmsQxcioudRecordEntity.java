package net.lab1024.sa.base.module.support.sms.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author 孙宇
 * @date 2024/11/08 00:06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_sms_qxcioud_record")
public class SmsQxcioudRecordEntity {

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    private String templateCode;

    private Integer smsType;

    private String businessType;
    private String sendMessage;

    private String uid;
    private String sendCode;
    private String sendDesc;
    private String sendTime;
    private String sendPhone;
    private String reportTime;
    private String reportStatus;
    private String reportDesc;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
