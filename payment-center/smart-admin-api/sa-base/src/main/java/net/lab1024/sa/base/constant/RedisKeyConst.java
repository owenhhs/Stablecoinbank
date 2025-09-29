package net.lab1024.sa.base.constant;

/**
 * redis key 常量类
 *
 * @Author 1024创新实验室: 罗伊
 * @Date 2022-05-30 21:22:12
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
public class RedisKeyConst {

    public static final String SEPARATOR = ":";

    public static class Support {

        public static final String FILE_PRIVATE_VO = "file:private:";

        public static final String SERIAL_NUMBER_LAST_INFO = "serial-number:last-info";

        public static final String SERIAL_NUMBER = "serial-number:";

        public static final String CAPTCHA = "captcha:";

    }

    public static class Order {
        public static final String ORDER_DEPOSIT = "order:deposit:lock:";
        public static final String ORDER_WITHDRAW = "order:withdraw:lock:";

        public static final String ORDER_INFO = "order:info:cache:";

        public static final String ORDER_PAY_TOKEN = "order:pay:token:cache:";

        public static final String ORDER_PAY_SWITCH_CACHE = "order:pay:token:payment:switch:";

        public static final String ORDER_STATUS_UPDATE_LOCK = "order:status:lock:update:";

        public static final String PAYMENT_ORDER_STATUS_TASK_LOCK = "order:payment:status-task-lock:";

        public static final String WITHDRAW_ORDER_STATUS_TASK_LOCK = "order:withdraw:status-task-lock:";

        public static final String ORDER_CALLBACK_LOCK = "order:status:lock:callback:";

        public static final String ORDER_STATUS_OPERATE_LOCK = "order:status:lock:operate:";



    }
}
