package net.lab1024.sa.base.module.support.sensitive.advice;

import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.module.support.sensitive.annoation.Sensitive;
import net.lab1024.sa.base.module.support.sensitive.enumeration.SensitiveType;
import net.lab1024.sa.base.module.support.sensitive.util.SensitiveUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;


@Slf4j
@Component
@ControllerAdvice
public class SensitiveAdvice implements ResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (o != null) {
            // 对返回参数进行脱敏处理，这里可以实现自己的脱敏逻辑
            if (o instanceof ResponseEntity) {
                ResponseEntity<?> responseEntity = (ResponseEntity<?>) o;
                Object originalBody = responseEntity.getBody();
                if (originalBody instanceof ResponseDTO) {
                    // 对originalBody进行脱敏处理
                    try {
                        transformData(originalBody);
                        return new ResponseEntity(originalBody, responseEntity.getStatusCode());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                // 对body进行脱敏处理
                try {
                    if (o instanceof ResponseDTO) {
                        transformData(o);
                        return o;
                    }
                    return o;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

        }
        return null;
    }


    private <T> void transformData(T o) throws IllegalAccessException {
        if (((ResponseDTO<?>) o).getData() instanceof List) {
            List newData = desensitization((List)((ResponseDTO<?>) o).getData());
            ((ResponseDTO<List>) o).setData(newData);
        } else {
            Object newData = desensitization(((ResponseDTO<?>) o).getData());
            ((ResponseDTO<Object>) o).setData(newData);
        }
    }

    private <T> T desensitization(T o) throws IllegalAccessException {
        if (o == null) {
            return o;
        }
        if (o instanceof List) {
            List<?> newList = list((List<?>) o);
            return (T) newList;
        }
        Class<?> clazz = o.getClass();
        if (clazz.isPrimitive() || clazz.getName().startsWith("java.") || isBasicOrWrapperType(o.getClass())) {
            return o;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> fieldType = field.getType();
            if (String.class.isAssignableFrom(fieldType)) {
                if (field.isAnnotationPresent(Sensitive.class)) {
                    Sensitive sensitive = field.getAnnotation(Sensitive.class);
                    Object value = field.get(o);
                    if (SensitiveType.CUSTOM.equals(sensitive.type())) {
                        String newValue = SensitiveUtil.custom((String) value, sensitive.prefix(), sensitive.suffix(), sensitive.replaceChar());
                        field.set(o, newValue);
                    } else {
                        Function<String, String> function = sensitive.type().getFunction();
                        String newValue = function.apply((String)value);
                        field.set(o, newValue);
                    }
                }
            } else if (List.class.isAssignableFrom(fieldType)) {
                List<?> listValue = (List<?>) field.get(o);
                if (listValue != null) {
                    List<?> newList = list(listValue);
                    field.set(o, newList);
                }
            } else if (!fieldType.isPrimitive() && !fieldType.getName().startsWith("java.")) {
                customObject(field, o);
            }
        }
        return o;
    }

    /**
     * 判断字段是否为基本类型或其包装类型
     * @param fieldType 字段
     * @return 是否为基本类型或包装类型
     */
    public static boolean isBasicOrWrapperType(Class<?> fieldType) {
        return fieldType.isPrimitive() ||
                fieldType == String.class ||
                fieldType == Integer.class ||
                fieldType == Double.class ||
                fieldType == Float.class ||
                fieldType == Long.class ||
                fieldType == Short.class ||
                fieldType == Byte.class ||
                fieldType == Boolean.class ||
                fieldType == Character.class ||
                fieldType == BigInteger.class ||
                fieldType == BigDecimal.class ||
                fieldType == LocalDateTime.class ||
                fieldType == LocalDate.class ||
                fieldType == LocalTime.class ||
                fieldType == Date.class;
    }

    private <T> void customObject(Field field, T o) throws IllegalAccessException {
        Object value = field.get(o);
        Object newValue = desensitization(value);
        field.set(o, newValue);
    }

    private List<?> list(List<?> listValue) throws IllegalAccessException {
        List<Object> newList = new ArrayList<>();
        for (Object element : listValue) {
            Object newElement = desensitization(element);
            newList.add(newElement);
        }
        return newList;
    }

}
