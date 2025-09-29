package net.lab1024.sa.admin.util;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;

/**
* URLEncode工具包
* Payment 支付Demo(JAVA)
* ============================================================================
* 版权所有 2018-2026 Payment，并保留所有权利。
* ----------------------------------------------------------------------------
* 这不是一个自由软件！您只能在不用于商业目的的前提下对程序代码进行修改和
* 使用；不允许对程序代码以任何形式任何目的的再发布。
* ============================================================================
*/

public class URLEncoderUtil {
    /**
     * Java实现PHP中的http_build_query()效果
     *
     * @param array key=value形式的二位数组
     * @return
     */
    public static String http_build_query(Map<String, String> array) {
        String reString = "";
        //遍历数组形成akey=avalue&bkey=bvalue&ckey=cvalue形式的的字符串
        Iterator it = array.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry) it.next();
            String key = entry.getKey();
            String value = entry.getValue();
            reString += key + "=" + value + "&";
        }
        reString = reString.substring(0, reString.length() - 1);
        //将得到的字符串进行处理得到目标格式的字符串
        try {
            reString = java.net.URLEncoder.encode(reString, "utf-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        reString = reString.replace("%3D", "=").replace("%26", "&");
        return reString;
    }
}
