package net.lab1024.sa.admin.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.base.common.code.OpenErrorCode;
import net.lab1024.sa.base.common.exception.BusinessException;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

@Slf4j
public class HttpUtil {

    /**
     * 创建连接池管理器
     */
    private static final PoolingHttpClientConnectionManager connectionManager
            = new PoolingHttpClientConnectionManager();
    {
        connectionManager.setMaxTotal(1000);
        connectionManager.setDefaultMaxPerRoute(50);
    }

    private static String getResult(CloseableHttpResponse response) throws Exception {
        return getResult(response, 200);
    }

    private static String getResult(CloseableHttpResponse response, int successCode) throws Exception {
        String result = StringUtils.EMPTY;
        try {
            // 获取响应实体
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
            } else {
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() != successCode) {
                    throw new BusinessException("system error");
                }
            }
        } finally {
            response.close();
        }
        return result;
    }

    public static String apiGet(String host, String uri, Map<String, String> extraHeader) {
        String returnStr = "";
        CloseableHttpClient httpclient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
        try {
            String url = host + uri;
            log.info(url);
            HttpGet httpGet = new HttpGet(url);
            if (extraHeader != null) {
                extraHeader.forEach(httpGet::setHeader);
            }
            // 执行POST请求.
            CloseableHttpResponse response = httpclient.execute(httpGet);
            returnStr = getResult(response);
        } catch (Exception e) {
            log.error("apiGet error, uri:{}", uri, e);
        }
        return returnStr;
    }


    /**
     * 通用post方法
     */
    public static String apiPostForm(String host, String uri, String params, Map<String, String> extraHeader) {
        String returnStr = "";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            String url;
            if (uri.contains("?")) {
                url = host + uri + "&" + params;
            } else {
                url = host + uri + "?" + params;
            }
            HttpPost httpPost = new HttpPost(url);

            StringEntity entity = new StringEntity("");
            entity.setContentType(ContentType.APPLICATION_FORM_URLENCODED.getMimeType());
            httpPost.setEntity(entity);

            if (extraHeader != null) {
                extraHeader.forEach(httpPost::setHeader);
            }
            // 执行POST请求.
            CloseableHttpResponse response = httpclient.execute(httpPost);
            returnStr = getResult(response);
        } catch (Exception e) {
            log.error("apiPost error, uri:{}", uri, e);
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error("apiPost error", e);
            }
        }
        return returnStr;
    }

    public static String apiPost(String host, String uri, Map<String, Object> param, Map<String, String> extraHeader) {
        String returnStr = "";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            String url = host + uri;
            HttpPost httpPost = new HttpPost(url);
            String postData = JSONObject.toJSONString(param);
            StringEntity stringEntity = new StringEntity(postData, ContentType.APPLICATION_JSON);
            httpPost.setEntity(stringEntity);
            if (extraHeader != null) {
                extraHeader.forEach(httpPost::setHeader);
            }
            // 执行POST请求.
            CloseableHttpResponse response = httpclient.execute(httpPost);
            returnStr = getResult(response);
        } catch (Exception e) {
            log.error("apiPost error, uri:{}", uri, e);
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error("apiPost error", e);
            }
        }
        return returnStr;
    }


    public static String uploadFile(String url, MultipartFile file, Map<String, Object> params, Map<String, String> extraHeader) {
        String returnStr = "";
        try {
            OkHttpClient client = new OkHttpClient();
            // 创建 RequestBody，用于封装构建请求体的内容
            RequestBody fileBody = RequestBody.create(file.getBytes());

            // 创建 MultipartBody 对象，用于构建表单请求体
            MultipartBody.Builder paramsBuilder = new MultipartBody.Builder();
            paramsBuilder.addFormDataPart("file", file.getName(), fileBody);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                paramsBuilder.addFormDataPart(entry.getKey(), entry.getValue().toString());
            }
            RequestBody requestBody = paramsBuilder.setType(MultipartBody.FORM) .build();

            // 构建请求对象
            Request.Builder builder = new Request.Builder()
                    .url(url)
                    .post(requestBody);
            if (extraHeader != null) {
                extraHeader.forEach(builder::addHeader);
            }
            Request request = builder.build();

            // 发送请求并获取响应
            Call call = client.newCall(request);
            Response response = call.execute();
            if (response.isSuccessful()) {
                // 请求成功，处理响应内容
                returnStr = response.body().string();
            } else {
                // 请求失败，处理错误情况
                log.error("请求失败: " + response.code());
                throw new BusinessException("system error");
            }
        } catch (Exception e) {
            log.error("upload file error, uri:{}", url, e);
        }
        return returnStr;
    }



    /**
     * 通用put方法
     */
    public static String apiPut(String host, String uri, Map<String, Object> param, Map<String, String> extraHeader) {
        String returnStr = "";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            String url = host + uri;
            HttpPut httpPut = new HttpPut(url);
            String postData = JSONObject.toJSONString(param);
            StringEntity stringEntity = new StringEntity(postData, ContentType.APPLICATION_JSON);
            httpPut.setEntity(stringEntity);
            if (extraHeader != null) {
                extraHeader.forEach(httpPut::setHeader);
            }

            // 执行PUT请求.
            CloseableHttpResponse response = httpclient.execute(httpPut);
            returnStr = getResult(response);
        } catch (Exception e) {
            log.error("apiPut error, uri:{}", uri, e);
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error("apiPut error", e);
            }
        }
        return returnStr;
    }

    /**
     * 通用delete方法
     */
    public static String apiDelete(String host, String uri, Map<String, String> extraHeader) {
        return apiDelete(host, uri, extraHeader, 200);
    }
    public static String apiDelete(String host, String uri, Map<String, String> extraHeader, int successCode) {
        String returnStr = "";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            String url = host + uri;
            HttpDelete httpDelete = new HttpDelete(url);
            if (extraHeader != null) {
                extraHeader.forEach(httpDelete::setHeader);
            }

            // 执行DELETE请求.
            CloseableHttpResponse response = httpclient.execute(httpDelete);
            returnStr = getResult(response, successCode);
        } catch (Exception e) {
            log.error("apiDelete error, uri:{}", uri, e);
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error("apiDelete error", e);
            }
        }
        return returnStr;
    }

    /**
     * 通用patch方法
     */
    public static String apiPatch(String host, String uri, Map<String, Object> param, Map<String, String> extraHeader) {
        String returnStr = "";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            String url = host + uri;
            HttpPatch httpPatch = new HttpPatch(url);
            String postData = JSONObject.toJSONString(param);
            StringEntity stringEntity = new StringEntity(postData, ContentType.APPLICATION_JSON);
            httpPatch.setEntity(stringEntity);

            if (extraHeader != null) {
                extraHeader.forEach(httpPatch::setHeader);
            }
            // 执行PUT请求.
            CloseableHttpResponse response = httpclient.execute(httpPatch);
            returnStr = getResult(response);
        } catch (Exception e) {
            log.error("apiPatch error", e);
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error("apiPatch error", e);
            }
        }
        return returnStr;
    }


    public static void main(String[] args) {
        System.out.println(new BigDecimal("1001.00"));
    }
}
