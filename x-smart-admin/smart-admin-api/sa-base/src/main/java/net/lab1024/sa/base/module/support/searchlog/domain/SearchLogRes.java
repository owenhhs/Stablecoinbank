package net.lab1024.sa.base.module.support.searchlog.domain;

import lombok.Data;

/**
 * @author 孙宇
 * @date 2025/03/17 14:45
 */
@Data
public class SearchLogRes {
    private String ts;
    private String level;
    private String requestId;
    private String className;
    private String thread;
    private String msg;
    private String stack;
}
