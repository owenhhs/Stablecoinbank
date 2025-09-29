package net.lab1024.sa.admin.util;

import net.lab1024.sa.base.common.enumeration.LogTypeEnum;
import net.lab1024.sa.base.common.util.HttpUtil;
import net.lab1024.sa.base.common.util.SignatureUtil;
import net.lab1024.sa.base.common.util.SmartLocalDateUtil;
import org.apache.commons.lang3.EnumUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Test {

    private static final Pattern SAFE_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");


    public static void main(String[] args) {
//        req();
//        int batchNo = 1;
//        String reqNo = "01";
//        System.out.println(String.format("%02d", batchNo) + reqNo);

//        logSearch();

        System.out.println(EnumUtils.isValidEnum(LogTypeEnum.class, "ERROR"));
    }

    public static String sanitizeInput(String userInput) throws IllegalArgumentException {
        if (!SAFE_PATTERN.matcher(userInput).matches()) {
            throw new IllegalArgumentException("非法字符");
        }
        return userInput;
    }

    public static String escapeShellArgument(String input) {
        // 转义单引号，并用单引号包裹整个输入
        return "'" + input.replace("'", "'\"'\"'") + "'";
    }

    public static void logSearch() {
        try {
            // 1. 定义命令参数（推荐分解为列表，避免 shell 注入）
            ProcessBuilder processBuilder = new ProcessBuilder();
            String[] command = {
                    "/bin/sh", "-c",
                    "cat */*.log | grep -- 'Starting' | tail -n 100"
            };

            // Linux/macOS
            processBuilder.command(command);
            // Windows
            // processBuilder.command("cmd.exe", "/c", "dir");

            // 2. 设置工作目录（可选）
            processBuilder.directory(new File("/Users/yusun/work/self/payment-center/logs/payment_center/sa-admin/dev"));

            // 3. 启动进程
            Process process = processBuilder.start();

            // 4. 读取输出流
            InputStream inputStream = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // 5. 读取错误流
            InputStream errorStream = process.getErrorStream();
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }

            // 6. 等待命令完成（可设置超时）
            boolean isFinished = process.waitFor(10, TimeUnit.SECONDS);
            if (!isFinished) {
                process.destroy(); // 强制终止命令
                System.err.println("Command timed out.");
            }

            // 7. 获取退出码
            int exitCode = process.exitValue();
            System.out.println("Exit Code: " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void req() {

//        String ak = "AK20240915406337";
//        String sk = "f3sKuFRSQSILAOhMKGagBQuzIoHVSwXZ";
//        String host = "https://api.fxmch.com";
        String ak = "AK20240725001033";
        String sk = "HeUgR3PWUI6oXfbSN7w15R747vTC3FSa";
        String host = "https://api-zc.fxmch.com";

        String method = "POST";
        String contentType = "application/json";


        String uri = "/openapi/order/v1/ly/commgr/deposit";

        String date = SmartLocalDateUtil.getGMTDate();

        LinkedHashMap<String, Object> contentMap = new LinkedHashMap<>();
        contentMap.put("orderNo", "202501270041347925");
        contentMap.put("depositHolder", "测试");
        contentMap.put("bankAccount", "1111222233334444");
        contentMap.put("callback", "https://api.fxmch.com/notify/fxmch/payment/order/result");
        contentMap.put("landingUrl", "https://my.zfx.com");
        contentMap.put("country", "CN");
        contentMap.put("currency", "CNY");
        contentMap.put("amount", 1600.00);

//        contentMap.put("depositType", "qrcode");
//        contentMap.put("paymentChannel", "WeChat");

        contentMap.put("depositType", "bank");
        contentMap.put("paymentChannel", "bank");

        String signature = SignatureUtil.genSignature(method, uri, contentType, date, contentMap, sk);

        System.out.println(date);

        String Authorization = ak + ":" + signature;

        System.out.println("Authorization :: " + Authorization);

        LinkedHashMap<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("Authorization", Authorization);
        headerMap.put("Content-Type", contentType);
        headerMap.put("Date", date);


        String resp = HttpUtil.apiPost(host, uri, contentMap, headerMap);

        System.out.println("resp :: " + resp);

    }
}
