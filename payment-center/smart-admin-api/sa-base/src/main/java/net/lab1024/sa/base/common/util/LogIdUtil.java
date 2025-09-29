package net.lab1024.sa.base.common.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;

/**
 * @author 孙宇
 * @date 2024/09/15 16:33
 */
public class LogIdUtil {

    public final static String REQUEST_ID = "x-request-id";

    public static void setRequestId(HttpServletRequest request) {
        if (request == null) {
            return;
        }
        String requestId = request.getHeader(REQUEST_ID);
        if (StringUtils.isEmpty(requestId)) {
            requestId = RandomStringUtils.randomAlphanumeric(32);
        }
        MDC.put(REQUEST_ID, requestId);
        request.setAttribute(REQUEST_ID, requestId);
    }

    public static String getRequestId() {
        String requestId = MDC.get(REQUEST_ID);
        if (StringUtils.isEmpty(requestId)) {
            requestId = RandomStringUtils.randomAlphanumeric(32);
        }
        return requestId;
    }
}
