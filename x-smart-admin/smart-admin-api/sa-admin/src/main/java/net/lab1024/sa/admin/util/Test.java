package net.lab1024.sa.admin.util;

import net.lab1024.sa.base.common.util.SignatureUtil;
import net.lab1024.sa.base.common.util.SmartLocalDateUtil;

import java.util.LinkedHashMap;

public class Test {

    public static void main(String[] args ) {
        req();
//        int batchNo = 1;
//        String reqNo = "01";
//        System.out.println(String.format("%02d", batchNo) + reqNo);

    }

    public static void req() {


//        String ak = "AK20240915406337";
//        String sk = "f3sKuFRSQSILAOhMKGagBQuzIoHVSwXZ";
//        String host = "https://api.fxmch.com";
        String ak = "AK20240725001033";
        String sk = "HeUgR3PWUI6oXfbSN7w15R747vTC3FSa";
        String host = "http://localhost:1024/";
//        String host = "https://api-zc.fxmch.com";

        String method = "POST";
        String contentType = "application/json";


        String uri = "/openapi/order/v1/ly/commgr/deposit";

        String date = SmartLocalDateUtil.getGMTDate();

        LinkedHashMap<String, Object>  contentMap = new LinkedHashMap<>();
        contentMap.put("orderNo", "202501212018347928");
        contentMap.put("depositHolder", "测试");
        contentMap.put("bankAccount", "1111222233334444");
        contentMap.put("callback", "https://api.fxmch.com/notify/fxmch/payment/order/result");
        contentMap.put("landingUrl", "https://my.zfx.com");
        contentMap.put("country", "CN");
        contentMap.put("currency", "CNY");
        contentMap.put("amount", 600.00);

        contentMap.put("depositType", "qrcode");
        contentMap.put("paymentChannel", "Alipay");

//        contentMap.put("depositType", "bank");
//        contentMap.put("paymentChannel", "bank");

        String signature = SignatureUtil.genSignature(method, uri, contentType, date, contentMap, sk);

        System.out.println(date);

        String Authorization = ak + ":" + signature;

        System.out.println("Authorization :: " + Authorization);

        LinkedHashMap<String, String>  headerMap = new LinkedHashMap<>();
        headerMap.put("Authorization", Authorization);
        headerMap.put("Content-Type", contentType);
        headerMap.put("Date", date);


        String resp = HttpUtil.apiPost(host, uri, contentMap, headerMap);

        System.out.println("resp :: " + resp);

    }
}
