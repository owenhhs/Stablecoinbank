package net.lab1024.sa.base.common.domain;

import java.time.LocalDateTime;

/**
 * 请求商户
 * @author 孙宇
 * @date 2024/07/25 23:21
 */
public interface RequestMerchant {
    Long getId();

    String getMerNo();

    String getMerName();

    String getSecretKey();

    Integer getStatus();

    LocalDateTime getCreateTime();

    LocalDateTime getUpdateTime();
}
