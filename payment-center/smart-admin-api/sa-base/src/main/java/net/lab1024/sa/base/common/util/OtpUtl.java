package net.lab1024.sa.base.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.jboss.aerogear.security.otp.Totp;

/**
 * @author 孙宇
 * @date 2024/11/13 22:20
 */
@Slf4j
public class OtpUtl {

    // 发行者（项目名），可为空，注：不允许包含冒号
    public static final String ISSUER = "payment-center.fxmch.com";

    // 生成的key长度( Generate secret key length)
    public static final int SECRET_SIZE = 32;

    // Java实现随机数算法
    public static final String RANDOM_NUMBER_ALGORITHM = "SHA1PRNG";


    /**
     * 生成一个SecretKey，外部绑定到用户
     *
     * @return SecretKey
     */
    public static String generateSecretKey() {
        SecureRandom sr;
        try {
            sr = SecureRandom.getInstance(RANDOM_NUMBER_ALGORITHM);
            sr.setSeed(getSeed());
            byte[] buffer = sr.generateSeed(SECRET_SIZE);
            Base32 codec = new Base32();
            byte[] bEncodedKey = codec.encode(buffer);
            String ret = new String(bEncodedKey);
            // 移除末尾的等号
            return ret.replaceAll("=+$", "");
        } catch (NoSuchAlgorithmException e) {
            // should never occur... configuration error
            throw new RuntimeException(e);
        }
    }

    private static byte[] getSeed() {
        String str = ISSUER + System.currentTimeMillis() + ISSUER;
        return str.getBytes(StandardCharsets.UTF_8);
    }


    /**
     * 生成二维码所需的字符串，注：这个format不可修改，否则会导致身份验证器无法识别二维码
     *
     * @param user   绑定到的用户名
     * @param secret 对应的secretKey
     * @return 二维码字符串
     */
    public static String getQrcode(String user, String secret) {
        Totp totp = new Totp(secret);
        return totp.uri(user);
    }

    /**
     * 验证用户提交的code是否匹配
     *
     * @param secret 用户绑定的secretKey
     * @param code   用户输入的code
     * @return 匹配成功与否
     */
    public static boolean checkCode(String secret, String code) {
        Totp totp = new Totp(secret);
        return totp.verify(code);
    }

}
