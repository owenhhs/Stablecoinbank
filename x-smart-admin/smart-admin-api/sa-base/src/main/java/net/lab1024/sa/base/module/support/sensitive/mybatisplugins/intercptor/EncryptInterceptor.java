package net.lab1024.sa.base.module.support.sensitive.mybatisplugins.intercptor;

import cn.hutool.crypto.symmetric.AES;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.conditions.update.Update;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.parser.JsqlParserSupport;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.base.module.support.sensitive.mybatisplugins.annoation.EncryptedColumn;
import net.lab1024.sa.base.module.support.sensitive.mybatisplugins.annoation.EncryptedTable;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 加密拦截器
 *
 * @author yusun
 */
@Slf4j
public class EncryptInterceptor extends JsqlParserSupport implements InnerInterceptor {
    /**
     * 变量占位符正则
     */
    private static final Pattern PARAM_PAIRS_RE = Pattern.compile("#\\{ew\\.paramNameValuePairs\\.(" + Constants.WRAPPER_PARAM + "\\d+)\\}");

    private final String encryptSecretKey;

    public EncryptInterceptor(String encryptSecretKey) {
        this.encryptSecretKey = encryptSecretKey;
    }

    /**
     * 如果查询条件是加密数据列，那么要将查询条件进行数据加密。
     * 例如，手机号加密存储后，按手机号查询时，先把要查询的手机号进行加密，再和数据库存储的加密数据进行匹配
     */
    @Override
    public void beforeQuery(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        if (Objects.isNull(parameterObject)) {
            return;
        }
        if (!(parameterObject instanceof Map)) {
            return;
        }
        Map paramMap = (Map) parameterObject;
        // 参数去重，否则多次加密会导致查询失败
        Set set = (Set) paramMap.values().stream().collect(Collectors.toSet());
        for (Object param : set) {
            /**
             *  仅支持类型是自定义Entity的参数，不支持mapper的参数是QueryWrapper、String等，例如：
             *
             *  支持：findList(@Param(value = "query") UserEntity query);
             *  支持：findPage(@Param(value = "query") UserEntity query, Page<UserEntity> page);
             *
             *  不支持：findOne(@Param(value = "mobile") String mobile);
             *  不支持：findList(QueryWrapper wrapper);
             */
            if (param == null || param instanceof AbstractWrapper || param instanceof String) {
                // Wrapper、String类型查询参数，无法获取参数变量上的注解，无法确认是否需要加密，因此不做判断
                continue;
            }
            if (annotateWithEncrypt(param.getClass())) {
                encryptEntity(param);
            }
        }
    }

    /**
     * 新增、更新数据时，如果包含隐私数据，则进行加密
     */
    @Override
    public void beforeUpdate(Executor executor, MappedStatement mappedStatement, Object parameterObject) throws SQLException {
        if (Objects.isNull(parameterObject)) {
            return;
        }
        // 通过MybatisPlus自带API（save、insert等）新增数据库时
        if (!(parameterObject instanceof Map)) {
            if (annotateWithEncrypt(parameterObject.getClass())) {
                encryptEntity(parameterObject);
            }
            return;
        }
        Map paramMap = (Map) parameterObject;
        Object param;
        // 通过MybatisPlus自带API（update、updateById等）修改数据库时
        if (paramMap.containsKey(Constants.ENTITY) && null != (param = paramMap.get(Constants.ENTITY))) {
            if (annotateWithEncrypt(param.getClass())) {
                encryptEntity(param);
            }
            return;
        }
        // 通过在mapper.xml中自定义API修改数据库时
        if (paramMap.containsKey("entity") && null != (param = paramMap.get("entity"))) {
            if (annotateWithEncrypt(param.getClass())) {
                encryptEntity(param);
            }
            return;
        }
        // 通过UpdateWrapper、LambdaUpdateWrapper修改数据库时
        if (paramMap.containsKey(Constants.WRAPPER) && null != (param = paramMap.get(Constants.WRAPPER))) {
            if (param instanceof Update && param instanceof AbstractWrapper) {
                Class<?> entityClass = mappedStatement.getParameterMap().getType();
                if (annotateWithEncrypt(entityClass)) {
                    try {
                        encryptWrapper(entityClass, param);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            return;
        }
    }

    /**
     * 校验该实例的类是否被@EncryptedTable所注解
     */
    private boolean annotateWithEncrypt(Class<?> objectClass) {
        EncryptedTable sensitiveData = AnnotationUtils.findAnnotation(objectClass, EncryptedTable.class);
        return Objects.nonNull(sensitiveData);
    }

    /**
     * 通过API（save、updateById等）修改数据库时
     *
     * @param parameter
     */
    private void encryptEntity(Object parameter) {
        //取出parameterType的类
        Class<?> resultClass = parameter.getClass();
        Field[] declaredFields = resultClass.getDeclaredFields();
        for (Field field : declaredFields) {
            //取出所有被EncryptedColumn注解的字段
            EncryptedColumn sensitiveField = field.getAnnotation(EncryptedColumn.class);
            if (!Objects.isNull(sensitiveField)) {
                field.setAccessible(true);
                Object object = null;
                try {
                    object = field.get(parameter);
                } catch (IllegalAccessException e) {
                    continue;
                }
                //只支持String的解密
                if (object instanceof String) {
                    String value = (String) object;
                    //对注解的字段进行逐一加密
                    try {
                        field.set(parameter, encrypt(value));
                    } catch (IllegalAccessException e) {
                        continue;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    /**
     * 通过UpdateWrapper、LambdaUpdateWrapper修改数据库时
     *
     * @param entityClass
     * @param ewParam
     */
    private void encryptWrapper(Class<?> entityClass, Object ewParam) throws Exception {
        AbstractWrapper updateWrapper = (AbstractWrapper) ewParam;
        String sqlSet = updateWrapper.getSqlSet();
        String[] elArr = sqlSet.split(",");
        Map<String, String> propMap = new HashMap<>(elArr.length);
        Arrays.stream(elArr).forEach(el -> {
            String[] elPart = el.split("=");
            propMap.put(elPart[0], elPart[1]);
        });

        //取出parameterType的类
        Field[] declaredFields = entityClass.getDeclaredFields();
        for (Field field : declaredFields) {
            //取出所有被EncryptedColumn注解的字段
            EncryptedColumn sensitiveField = field.getAnnotation(EncryptedColumn.class);
            if (Objects.isNull(sensitiveField)) {
                continue;
            }
            String el = propMap.get(field.getName());
            Matcher matcher = PARAM_PAIRS_RE.matcher(el);
            if (matcher.matches()) {
                String valueKey = matcher.group(1);
                Object value = updateWrapper.getParamNameValuePairs().get(valueKey);
                updateWrapper.getParamNameValuePairs().put(valueKey, encrypt(value.toString()));
            }
        }

        Method[] declaredMethods = entityClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            //取出所有被EncryptedColumn注解的字段
            EncryptedColumn sensitiveField = method.getAnnotation(EncryptedColumn.class);
            if (Objects.isNull(sensitiveField)) {
                continue;
            }
            String el = propMap.get(method.getName());
            Matcher matcher = PARAM_PAIRS_RE.matcher(el);
            if (matcher.matches()) {
                String valueKey = matcher.group(1);
                Object value = updateWrapper.getParamNameValuePairs().get(valueKey);
                updateWrapper.getParamNameValuePairs().put(valueKey, encrypt(value.toString()));
            }
        }
    }

    private String encrypt(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        try {
            //  AES 加密 并转为 base64
            AES aes = new AES(encryptSecretKey.getBytes(StandardCharsets.UTF_8));
            return aes.encryptBase64(str);
        } catch (Exception e) {
            log.error("encrypt error value:{}", str, e);
            return str;
        }
    }

}
