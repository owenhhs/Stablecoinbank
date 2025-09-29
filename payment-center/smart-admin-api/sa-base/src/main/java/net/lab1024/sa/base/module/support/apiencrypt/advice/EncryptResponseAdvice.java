package net.lab1024.sa.base.module.support.apiencrypt.advice;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.base.common.annoation.ApiAuth;
import net.lab1024.sa.base.common.code.OpenErrorCode;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.enumeration.DataTypeEnum;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.module.support.apiencrypt.annotation.ApiEncrypt;
import net.lab1024.sa.base.module.support.apiencrypt.service.ApiEncryptService;
import net.lab1024.sa.base.module.support.apiencrypt.util.ApiEncryptRsaUtil;
import net.lab1024.sa.base.module.support.merchant.domain.vo.MerchantManageVO;
import net.lab1024.sa.base.module.support.merchant.manager.MerchantManageManager;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * 加密
 *
 * @Author 1024创新实验室-主任:卓大
 * @Date 2023/10/24 09:52:58
 * @Wechat zhuoda1024
 * @Email lab1024@163.com
 * @Copyright  <a href="https://1024lab.net">1024创新实验室</a>，Since 2012
 */


@Slf4j
@ControllerAdvice
public class EncryptResponseAdvice implements ResponseBodyAdvice<ResponseDTO> {

    @Resource
    private ApiEncryptService apiEncryptService;

    private static final String RESPONSE_AES_KEY_HEADER = "X-Res-Aes-Key";

    @Resource
    private MerchantManageManager merchantManageManager;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.hasMethodAnnotation(ApiEncrypt.class) || returnType.getContainingClass().isAnnotationPresent(ApiEncrypt.class);
    }

    @Override
    public ResponseDTO beforeBodyWrite(ResponseDTO body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (Objects.isNull(body) || Objects.isNull(body.getData())) {
            return body;
        }

        ApiAuth apiAuth = getApiAuth(returnType);
        String encrypt;
        if (apiAuth != null) {
            HttpHeaders requestHeaders = request.getHeaders();
            String authorization = requestHeaders.getFirst("Authorization");
            if (StringUtils.isEmpty(authorization)) {
                return body;
            }
            String[] authSignature = authorization.split(":");
            if (authSignature.length != 2) {
                throw new BusinessException(OpenErrorCode.NO_PERMISSION);
            }
            String ak = authSignature[0];
            MerchantManageVO merchantManageVO = merchantManageManager.queryByMerchantNo(ak);
            if (merchantManageVO == null) {
                return body;
            }
            String aesKey = RandomStringUtils.randomAlphanumeric(48);
            Map<String, String> resultMap = null;
            try {
                resultMap = ApiEncryptRsaUtil.encrypt(JSON.toJSONString(body.getData()), aesKey, merchantManageVO.getRsaPublicKey());
            } catch (Exception e) {
                log.error("数据加密失败, ", e);
                return body;
            }
            encrypt = resultMap.get(ApiEncryptRsaUtil.ENCRYPT_DATA_KEY);
            String cipherAesKey = resultMap.get(ApiEncryptRsaUtil.ENCRYPT_AES_KEY);

            log.debug("cipherAesKey:{}", cipherAesKey);
            response.getHeaders().add(RESPONSE_AES_KEY_HEADER, cipherAesKey);

        } else {
            encrypt = apiEncryptService.encrypt(JSON.toJSONString(body.getData()));
        }
        body.setData(encrypt);
        body.setDataType(DataTypeEnum.ENCRYPT.getValue());
        return body;
    }

    private static ApiAuth getApiAuth(MethodParameter parameter) {
        ApiAuth apiAuth = parameter.getMethodAnnotation(ApiAuth.class);
        if (apiAuth == null) {
            apiAuth = parameter.getContainingClass().getAnnotation(ApiAuth.class);
        }
        return apiAuth;
    }
}


