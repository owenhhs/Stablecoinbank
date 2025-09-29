package net.lab1024.sa.base.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.core.util.ReflectionUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author 孙宇
 * @date 2024/07/26 21:52
 */
@Slf4j
public class SignatureUtil {

    public static boolean checkSignature(HttpServletRequest request, Object body, String sk, String signature) {
        String method = request.getMethod();
        String requestUri = request.getRequestURI();
        String date = request.getHeader("Date");
        String contentType = request.getHeader("Content-Type");
        log.info("requestUri:{}, body:{}", requestUri, body);
        String checkSignature = SignatureUtil.genSignature(method, requestUri, contentType, date, body, sk);
        log.info("check signature:{}", checkSignature);
        log.info("signature:{}", signature);
        return checkSignature.equals(signature);
    }

    public static String genSignature(String method, String uri, String contentType, String date, Object body, String sk) {
        String query = joinQueryParams(body);
        String md5Str = DigestUtils.md5Hex(method + uri + query + contentType + date);
        byte[] sha1Byte = DigestUtils.sha1(md5Str + sk);
        return Base64.getEncoder().encodeToString(sha1Byte);
    }

    private static String joinQueryParams(Object body) {
        if (body == null) {
            return "";
        }
        Map<String, Object> params;
        if (body instanceof Map) {
            params = (Map<String, Object>) body;
            if (params.isEmpty()) {
                return "";
            }
        } else {
            Class<?> clazz = body.getClass();
            // 获取所有属性
            Field[] fields = clazz.getDeclaredFields();
            if (fields.length == 0) {
                return "";
            }
            // 遍历并打印属性信息
            params = new HashMap<>(fields.length);
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = ReflectionUtil.getFieldValue(field, body);
                if (value != null) {
                    params.put(field.getName(), value);
                }
            }
        }
        List<String> keyList = params.keySet().stream().sorted().collect(Collectors.toList());
        return keyList.stream().map(v -> v + "=" + params.get(v)).collect(Collectors.joining("&"));
    }


    public static void main(String[] args) {

        String method = "POST";
        String uri = "/deposit-callback/fxcoin/202501232337502984";
        String contentType = "application/json";
        String date = SmartLocalDateUtil.getGMTDate();
        String ak = "";
        String sk = "";
        Map<String, Object> params = new HashMap<>(1);
        params.put("orderNo", "202501232337502984");
        params.put("status", 2);
        params.put("payStatus", 2);
        params.put("ext", "");

        String signature = SignatureUtil.genSignature(method, uri, contentType, date, params, sk);

        System.out.println(date);
        System.out.println(ak + ":" + signature);

    }
}
