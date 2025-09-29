package net.lab1024.sa.base.module.support.sensitive.mybatisplugins.intercptor;

import cn.hutool.crypto.symmetric.AES;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.base.module.support.sensitive.mybatisplugins.annoation.EncryptedColumn;
import net.lab1024.sa.base.module.support.sensitive.mybatisplugins.annoation.EncryptedTable;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;

/**
 * 解密拦截器
 *
 * @author yusun
 */
@Slf4j
@Intercepts({
        @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})
})
@Component
public class DecryptInterceptor implements Interceptor {

    @Value("${mybatis-plus.encrypt.secretKey:}")
    private String encryptSecretKey;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object resultObject = invocation.proceed();
        if (Objects.isNull(resultObject)) {
            return null;
        }
        if (resultObject instanceof ArrayList) {
            //基于selectList
            ArrayList resultList = (ArrayList) resultObject;
            if (!resultList.isEmpty() && needToDecrypt(resultList.get(0))) {
                for (Object result : resultList) {
                    //逐一解密
                    decryptObject(result);
                }
            }
        } else if (needToDecrypt(resultObject)) {
            //基于selectOne
            decryptObject(resultObject);
        }
        return resultObject;
    }

    /**
     * 校验该实例的类是否被@EncryptedTable所注解
     */
    private boolean needToDecrypt(Object object) {
        if (object == null) {
            return false;
        }
        Class<?> objectClass = object.getClass();
        EncryptedTable sensitiveData = AnnotationUtils.findAnnotation(objectClass, EncryptedTable.class);
        return Objects.nonNull(sensitiveData);
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    private <T> T decryptObject(T result) throws Exception {
        //取出resultType的类
        Class<?> resultClass = result.getClass();
        Field[] declaredFields = resultClass.getDeclaredFields();
        for (Field field : declaredFields) {
            //取出所有被EncryptedColumn注解的字段
            EncryptedColumn sensitiveField = field.getAnnotation(EncryptedColumn.class);
            if (!Objects.isNull(sensitiveField)) {
                field.setAccessible(true);
                Object object = field.get(result);
                //只支持String的解密
                if (object instanceof String) {
                    String value = (String) object;
                    //对注解的字段进行逐一解密
                    field.set(result, decrypt(value));
                }
            }
        }
        return result;
    }

    private String decrypt(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        try {
            AES aes = new AES(encryptSecretKey.getBytes(StandardCharsets.UTF_8));
            byte[] decryptedBytes = aes.decrypt(value);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("decrypt error value:{}", value, e);
            return value;
        }
    }
}
