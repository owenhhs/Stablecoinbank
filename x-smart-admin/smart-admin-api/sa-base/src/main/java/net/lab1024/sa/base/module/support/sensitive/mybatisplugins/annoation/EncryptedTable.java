package net.lab1024.sa.base.module.support.sensitive.mybatisplugins.annoation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 需要加解密的实体类用这个注解
 *
 * @author yusun
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EncryptedTable {
}
