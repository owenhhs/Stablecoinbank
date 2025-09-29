package net.lab1024.sa.admin.aspect;

import cn.hutool.extra.servlet.ServletUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.payment.channel.domain.entity.PaymentChannelIpWhitelistEntity;
import net.lab1024.sa.admin.module.business.payment.channel.manager.PaymentChannelIpWhitelistManager;
import net.lab1024.sa.base.common.annoation.IpWhitelist;
import net.lab1024.sa.base.common.code.SystemErrorCode;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author 孙宇
 * @date 2024/07/26 00:55
 */
@Slf4j
@Aspect
@Component
public class IpWhitelistAspect {

    @Resource
    private HttpServletRequest request;

    @Resource
    private HttpServletResponse response;

    @Resource
    private PaymentChannelIpWhitelistManager ipWhitelistManager;

    @Pointcut("execution(public * net.lab1024.sa.admin.module.openapi.*.controller.*.*(..))")
    public void api() {
    }

    @Around("api()")
    public Object aroundApi(ProceedingJoinPoint joinPoint) throws Throwable {
        // 查找ipWhitelistAuth注解，先从方法上查找，方法上未找到然后从该对象类中查找(不找父类)
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = methodSignature.getMethod();
        log.debug("方法{}.{}进入接口AOP", joinPoint.getTarget().getClass().getName(), targetMethod.getName());
        IpWhitelist ipWhitelistAuth = targetMethod.getDeclaredAnnotation(IpWhitelist.class);
        if (ipWhitelistAuth == null) {
            ipWhitelistAuth = joinPoint.getTarget().getClass().getAnnotation(IpWhitelist.class);
            log.debug("方法{}.{}上没有ipWhitelistAuth注解，检查该类是否有ipWhitelistAuth注解:{}", joinPoint.getTarget().getClass().getName(), targetMethod.getName(), ipWhitelistAuth != null);
        }
        if (ipWhitelistAuth != null) {
            String ip = ServletUtil.getClientIP(request);

            LambdaQueryWrapper<PaymentChannelIpWhitelistEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(PaymentChannelIpWhitelistEntity::getIp, ip);
            queryWrapper.eq(PaymentChannelIpWhitelistEntity::getDelFlag, 0);
            long count = ipWhitelistManager.count(queryWrapper);
            if (count == 0) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                String errorMessage = "IP [" + ip + "] is not in the whitelist. Contact administrator for access.";
                return ResponseDTO.error(SystemErrorCode.FORBIDDEN, errorMessage);
            }
        }
        return joinPoint.proceed(joinPoint.getArgs());
    }

}
