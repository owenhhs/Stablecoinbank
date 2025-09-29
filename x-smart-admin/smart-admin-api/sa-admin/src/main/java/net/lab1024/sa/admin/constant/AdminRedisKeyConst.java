package net.lab1024.sa.admin.constant;

import net.lab1024.sa.base.constant.RedisKeyConst;

/**
 * redis key 常量类
 *
 * @Author 1024创新实验室-主任:卓大
 * @Date 2022-01-07 18:59:22
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
public class AdminRedisKeyConst extends RedisKeyConst {

    public static final String PAYMENT_CHANNEL_ROUTE_CACHE = "payment_channel:route_cache:";

    public static final String PAYMENT_CHANNEL_INFO_ADD_UPDATE_LOCK = "payment_channel_info:add_update_lock:";

    public static final String LOGIN_SCRIP_CACHE = "login:scribe:";

    public static final String LOGIN_OTP_ERROR_COUNT = "login:otp:error:count:";
}
