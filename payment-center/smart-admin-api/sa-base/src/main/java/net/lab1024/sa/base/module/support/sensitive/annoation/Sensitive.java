package net.lab1024.sa.base.module.support.sensitive.annoation;


import net.lab1024.sa.base.module.support.sensitive.enumeration.SensitiveType;

import java.lang.annotation.*;


/**
 * @author yusun
 */
@Inherited
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Sensitive {

    SensitiveType type();

    int prefix() default 1;

    int suffix() default 1;

    String replaceChar() default "*";
}
