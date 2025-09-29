package net.lab1024.sa.base.common.util;

import java.util.Base64;

/**
* BASE64编码解码工具包
* Payment 支付Demo(JAVA)
* ============================================================================
* 版权所有 2018-2026 Payment，并保留所有权利。
* ----------------------------------------------------------------------------
* 这不是一个自由软件！您只能在不用于商业目的的前提下对程序代码进行修改和
* 使用；不允许对程序代码以任何形式任何目的的再发布。
* ============================================================================
*/

public class Base64Util {


    /** */
    /**
     * <p>
     * BASE64字符串解码为二进制数据
     * </p>
     *
     * @param base64
     * @return
     */
    public static byte[] decode(String base64) {
        return Base64.getMimeDecoder().decode(base64);
    }

    /** */
    /**
     * <p>
     * 二进制数据编码为BASE64字符串
     * </p>
     *
     * @param bytes
     * @return
     */
    public static String encode(byte[] bytes){
        return Base64.getMimeEncoder().encodeToString(bytes);
    }
}
