package net.lab1024.sa.base.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import net.lab1024.sa.base.module.support.sensitive.mybatisplugins.intercptor.EncryptInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * mp 插件
 *
 * @Author 1024创新实验室-主任: 卓大
 * @Date 2021-09-02 20:21:10
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>
 */
@EnableTransactionManagement
@Configuration
public class MybatisPlusConfig {

    @Value("${mybatis-plus.encrypt.enable:false}")
    private boolean encryptEnable;

    @Value("${mybatis-plus.encrypt.secretKey:}")
    private String encryptSecretKey;

    /**
     * 分页插件
     */
    @Bean
    public MybatisPlusInterceptor paginationInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 加解密拦截器
        if (encryptEnable) {
            interceptor.addInnerInterceptor(new EncryptInterceptor(encryptSecretKey));
        }
        return interceptor;
    }

}
