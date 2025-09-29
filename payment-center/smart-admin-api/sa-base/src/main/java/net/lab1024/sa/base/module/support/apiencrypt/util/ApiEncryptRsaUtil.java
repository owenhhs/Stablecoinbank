package net.lab1024.sa.base.module.support.apiencrypt.util;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.symmetric.AES;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.base.common.util.Base64Util;
import net.lab1024.sa.base.common.util.RSAUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 孙宇
 * @date 2025/07/09 15:07
 */
@Slf4j
public class ApiEncryptRsaUtil {

    private static final String AES_MODE = "CBC";

    private static final String AES_PADDING = "PKCS7Padding";

    public static final String ENCRYPT_DATA_KEY = "data";
    public static final String ENCRYPT_AES_KEY = "cipherAesKey";

    public static String decrypt(String data, String aesKey, String rsaPrivateKey) throws Exception {
        String clearAesKey = RSAUtil.privateDecrypt2(aesKey, rsaPrivateKey);
        String key = clearAesKey.substring(0, 32);
        String iv = clearAesKey.substring(32);
        log.debug("key:{}, iv:{}, data:{}", key, iv, data);
        AES aes = new AES(AES_MODE, AES_PADDING, key.getBytes(CharsetUtil.CHARSET_UTF_8), iv.getBytes(CharsetUtil.CHARSET_UTF_8));
        byte[] encryptedBytes = Base64Util.decode(data);
        return aes.decryptStr(encryptedBytes);
    }

    public static Map<String, String> encrypt(String data, String aesKey, String rsaPublicKey) throws Exception {
        log.debug("aesKey:{}, rsaPublicKey:{}, data:{}", aesKey, rsaPublicKey, data);
        String cipherAesKey = RSAUtil.publicEncrypt2(aesKey, rsaPublicKey);
        String key = aesKey.substring(0, 32);
        String iv = aesKey.substring(32);
        AES aes = new AES(AES_MODE, AES_PADDING, key.getBytes(CharsetUtil.CHARSET_UTF_8), iv.getBytes(CharsetUtil.CHARSET_UTF_8));
        String encryptData = aes.encryptBase64(data);
        Map<String, String> map = new HashMap<>();
        map.put(ENCRYPT_DATA_KEY, encryptData);
        map.put(ENCRYPT_AES_KEY, cipherAesKey);
        return map;
    }

    public static void main(String[] args) throws Exception {
//        String clearAesKey = "123456789012345678901234567890123456789012345678";
//        String key = clearAesKey.substring(0, 32);
//        String iv = clearAesKey.substring(32);
//        System.out.println(clearAesKey);
//        System.out.println(key);
//        System.out.println(iv);
        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmO7r6HwID2xhryrZXev/\n" +
                "VhBobKSgJzRqHupBJhaeIiWFYy88Fn6fMR+Tbfx0tXUEexvz7lSuBbYPaYZXwdys\n" +
                "EJZe7CqfPNJnmuUJE54zSOb2Pnl/AXc0JSx0nji+vlOE7ovFlo6mgsSR86cj78J+\n" +
                "x5FhSHdh4a8RPyQ2UWyHJdsvDzT5jJPTdHSOiZ85QQtzZndspbQSdJTnxWYqAr/6\n" +
                "Pu1zOJWvJYo9cvpCuIFzKY6r515v2u8pjvcGsXj7otyElhah//pgtPYwCeHE6zob\n" +
                "IfezzsxxfJfEHwSFRtYV0/8j0CT/lOAZU/iycPEQx1nkncedcSSaPQkWEQPSz3Dp\n" +
                "2wIDAQAB";
        String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCY7uvofAgPbGGv\n" +
                "Ktld6/9WEGhspKAnNGoe6kEmFp4iJYVjLzwWfp8xH5Nt/HS1dQR7G/PuVK4Ftg9p\n" +
                "hlfB3KwQll7sKp880mea5QkTnjNI5vY+eX8BdzQlLHSeOL6+U4Tui8WWjqaCxJHz\n" +
                "pyPvwn7HkWFId2HhrxE/JDZRbIcl2y8PNPmMk9N0dI6JnzlBC3Nmd2yltBJ0lOfF\n" +
                "ZioCv/o+7XM4la8lij1y+kK4gXMpjqvnXm/a7ymO9waxePui3ISWFqH/+mC09jAJ\n" +
                "4cTrOhsh97POzHF8l8QfBIVG1hXT/yPQJP+U4BlT+LJw8RDHWeSdx51xJJo9CRYR\n" +
                "A9LPcOnbAgMBAAECggEAA/jAe6ss8mmYJ9pvBrdoTC3k6F1mI16DGwZuZrTTqoNa\n" +
                "8XZ6kwL602XEiGi0BzpgwWm6bggY9OEDiXQJt2kptsyQ7oU02XmFY0prb+z92/jT\n" +
                "kE/LcphL7GM5LBrBKWQ14ZQTr5NI08CTIcbHciSU8k3ePumKyzemtQMVvguZfuwm\n" +
                "BNgdKPYgNJXlKafSgr+Am2g14hi9xFZUyJPSAM6TEqqCq17pWmoEtysF0+foFhyi\n" +
                "BysaLbH55t5VU/Z4LUdzmru3FzArmJ51Fr4xfkk8S6flWtjJGSW7J7q0dwG+CQnk\n" +
                "4CltSQxhBuSNrRBkKBUN7sewLzVPR1kDtT0P3R6esQKBgQDPWAVt8r7PuQs+P/zL\n" +
                "PlgUfeWkJozmrRNlOuZG+pBjRt7GO+XacVE4xEmI2A+KcVyGumvFL5yUi24HFGGV\n" +
                "JOY6a5REs46UyguqJj1teStonlozwn7ApRHKEX0nDXBHFok/hg4c0rCRyrbrweJL\n" +
                "em21KBBPslQH1WFvjwXO6+7jDQKBgQC80j1h7PdvBMwFc0z4mp009RS/bQJ9vd1k\n" +
                "n3dgYXF86WEzeP1VjrbkneI2RxC+mgFkE89Ekge85Wri/a+Yd2ZdFw7gBTzAERzN\n" +
                "5/YhObGn2ewp5q32V4yMonT9GhS6xLDdqbSZllDoz7Zo4nz9Lqh/gOdKTS4N8bA3\n" +
                "KlZKjgBmhwKBgQCulhAkTySJ5eboqJpkTH9/6a5GdMtppMxTDbA+jdasMU19n4vg\n" +
                "TlA0u1qrpjoXuXJOY4RBSeDPV0PzJB8ypRpSkKkM3CAvep0tzPsmRcPeWI/dS2TN\n" +
                "M3DyM52tF2y1/uzcFzWk4WcZq5Ywl1XDdjgQNMbfRGnrGqnrdINQg2rntQKBgExW\n" +
                "s3pj06WOyu1pMDVSTTyb2UeLSvaLTHbiAeF1g+x8bk/hGor8p/TF4C8RjDODEAzl\n" +
                "9WZDo90643viFJwPqdL4/4qSk30TbhHRNjE4ohq6gEBwguSVgL1a8PFjGf5ohxkX\n" +
                "A20U2KFHWPgOmXnC41ypQNlJWtVIYXXsJ/b8ci0BAoGAFaC9uQVO15sS4iDjTbHN\n" +
                "aiq6vxokOFCJEAWZgfP5CrtV3da6tB6WoBr/UxGQ6wat49ShFZwyelDRlSZKZTWe\n" +
                "0NVGvQ4miAckt8tZuwuAGEMA+lV/83VD7N5JpvmhhfVW+gDkhSDxBvvH3N7iBE0d\n" +
                "tHdOZMWhzRj8wEVi2cHoC9U=";

        Map<String,String> resultMap = encrypt("{\"age\": 1}", "bkxT5qmzjXlEiLFv5M31jXxHjGxXz2gQDDyPe9t3NabjHtdj", publicKey);
        System.out.println(resultMap);

        String data = decrypt(resultMap.get(ENCRYPT_DATA_KEY), resultMap.get(ENCRYPT_AES_KEY), privateKey);
        System.out.println(data);

//        String key = "bkxT5qmzjXlEiLFv5M31jXxHjGxXz2gQ";
//        String iv = "DDyPe9t3NabjHtdj";
//        String data = "5ab500c780106fd11b1cb621a2ce79cc";
//
//        AES aes = new AES(AES_MODE, AES_PADDING, key.getBytes(CharsetUtil.CHARSET_UTF_8), iv.getBytes(CharsetUtil.CHARSET_UTF_8));
////        byte[] encryptedBytes = Base64Util.decode(data);
//        String cleanCipherText = data.replaceAll("\\s+", "");
//
//        byte[] encryptedBytes = Base64.decode(cleanCipherText);
//        if (encryptedBytes.length % 16 != 0) {
//            throw new RuntimeException("密文长度异常：" + encryptedBytes.length + "字节（必须是16的整数倍）");
//        }
//        String aaa = aes.decryptStr(encryptedBytes);
//        System.out.println(aaa);
    }

}
