package net.lab1024.sa.base.module.support.sms.domain;

import lombok.Data;

import java.util.List;

/**
 * @author 孙宇
 * @date 2024/11/23 16:55
 */
@Data
public class QxcioudSendMsgResponseVO {
    private String code;
    private String desc;
    private String uid;
    private List<Data> result;

    @lombok.Data
    public static class Data {
        private String status;
        private String desc;
        private String phone;
    }
}
