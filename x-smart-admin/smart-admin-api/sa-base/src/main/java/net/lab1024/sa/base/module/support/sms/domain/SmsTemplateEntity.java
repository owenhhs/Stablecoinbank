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
@TableName("t_sms_template")
public class SmsTemplateEntity {

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    private String platform;

    private String templateCode;

    private String templateName;

    private String templateContent;

    private Integer smsType;

    private String businessType;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
