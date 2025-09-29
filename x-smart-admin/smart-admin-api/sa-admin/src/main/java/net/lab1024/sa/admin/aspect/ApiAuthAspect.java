package net.lab1024.sa.admin.aspect;

import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.business.merchantmanage.domain.vo.MerchantManageVO;
import net.lab1024.sa.admin.module.business.merchantmanage.manager.MerchantManageManager;
import net.lab1024.sa.base.common.annoation.ApiAuth;
import net.lab1024.sa.base.common.code.OpenErrorCode;
import net.lab1024.sa.base.common.domain.BaseRequest;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SignatureUtil;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @author 孙宇
 * @date 2024/07/26 00:55
 */
@Slf4j
@Aspect
@Component
public class ApiAuthAspect {

    @Resource
    private HttpServletRequest request;
    @Resource
    private MerchantManageManager merchantManageManager;

    @Pointcut("execution(public * net.lab1024.sa.admin.module.openapi.*.controller.*.*(..))")
    public void api() {
    }

    @Around("api()")
    public Object aroundApi(ProceedingJoinPoint joinPoint) throws Throwable {
        // 查找ApiAuth注解，先从方法上查找，方法上未找到然后从该对象类中查找(不找父类)
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = methodSignature.getMethod();
        log.debug("方法{}.{}进入接口AOP", joinPoint.getTarget().getClass().getName(), targetMethod.getName());
        ApiAuth apiAuth = targetMethod.getDeclaredAnnotation(ApiAuth.class);
        if (apiAuth == null) {
            apiAuth = joinPoint.getTarget().getClass().getAnnotation(ApiAuth.class);
            log.debug("方法{}.{}上没有ApiAuth注解，检查该类是否有ApiAuth注解:{}", joinPoint.getTarget().getClass().getName(), targetMethod.getName(), apiAuth != null);
        }
        if (apiAuth != null) {
            log.debug("方法{}.{}上有ApiAuth注解，需要进行鉴权", joinPoint.getTarget().getClass().getName(), targetMethod.getName());
            String authorization = request.getHeader("Authorization");
            String[] authSignature = authorization.split(":");
            if (authSignature.length != 2) {
                throw new BusinessException(OpenErrorCode.NO_PERMISSION);
            }
            String ak = authSignature[0];
            String signature = authSignature[1];

            MerchantManageVO merchantManageVO = merchantManageManager.queryByMerchantNo(ak);
            if (merchantManageVO == null || merchantManageVO.getStatus() == 0) {
                throw new BusinessException(OpenErrorCode.NO_PERMISSION);
            }
            // 签名鉴权
            Object body = getArgs(joinPoint);
            if (!SignatureUtil.checkSignature(request, body, merchantManageVO.getSecretKey(), signature)) {
                throw new BusinessException(OpenErrorCode.VERIFY_SIGNATURE_FAILURE);
            }
            SmartRequestUtil.setRequestMerchant(merchantManageVO);
        }
        return joinPoint.proceed(joinPoint.getArgs());
    }

    private Object getArgs(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof BaseRequest) {
                return arg;
            }
        }
        return null;
    }

}
