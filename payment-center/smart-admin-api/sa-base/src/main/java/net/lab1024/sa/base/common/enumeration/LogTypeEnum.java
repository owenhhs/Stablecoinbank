package net.lab1024.sa.base.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author 1024创新实验室-主任:卓大
 * @Date 2023/10/25 09:47:13
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>，Since 2012
 */

@Getter
@AllArgsConstructor
public enum LogTypeEnum implements BaseEnum {
    /**
     * 默认
     */
    all("all", "所有日志"),

    /**
     * DEBUG日志
     */
    debug("debug", "DEBUG日志"),

    info("info", "INFO日志"),

    error("error", "ERROR日志"),

    warn("warn", "WARN日志"),
    ;
    private final String value;

    private final String desc;

}
