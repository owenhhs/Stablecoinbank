package net.lab1024.sa.admin.module.openapi.order.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * @author 孙宇
 * @date 2025/03/11 21:24
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LtgExtVo extends ChannelExtVO {
    private String apiInfo;
    private Map<String, String> appAkSk;
}

