package com.plansolve.farm.util;

import com.plansolve.farm.model.properties.MessageProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @Author: 高一平
 * @Date: 2018/6/4
 * @Description:
 **/

@Slf4j
public class HttpUtil {

    public static String HTTP_GET(String URL) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(URL);
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            String result = EntityUtils.toString(httpEntity);
            log.info("==地址解析详情：" + result + "==");
            httpResponse.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String HTTP_POST(String URL, String Data) throws Exception {
        BufferedReader In = null;
        PrintWriter Out = null;
        HttpURLConnection HttpConn = null;
        try {
            URL url = new URL(URL);
            HttpConn = (HttpURLConnection) url.openConnection();
            HttpConn.setRequestMethod("POST");
            HttpConn.setDoInput(true);
            HttpConn.setDoOutput(true);

            Out = new PrintWriter(HttpConn.getOutputStream());
            Out.println(Data);
            Out.flush();

            if (HttpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                StringBuffer content = new StringBuffer();
                String tempStr = "";
                In = new BufferedReader(new InputStreamReader(HttpConn.getInputStream()));
                while ((tempStr = In.readLine()) != null) {
                    content.append(tempStr);
                }
                In.close();
                return content.toString();
            } else {
                throw new Exception("HTTP_POST_ERROR_RETURN_STATUS：" + HttpConn.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Out.close();
            HttpConn.disconnect();
        }
        return null;
    }

    /**
     * 发送验证码
     *
     * @param mobile
     * @param captcha
     * @return
     */
    public static String sendCaptcha(String mobile, String captcha) throws Exception {
        String HTTP_BACK_MESSAGE = HTTP_POST(MessageProperties.url,
                "id=" + MessageProperties.id
                        + "&pwd=" + MessageProperties.pwd
                        + "&to=" + mobile
                        + "&content=" + URLEncoder.encode("您好，您的验证码是" + captcha + "【农小满】", "gb2312")
                        + "&time=");
        return HTTP_BACK_MESSAGE;
    }

    /**
     * 发送短信
     *
     * @param mobile
     * @param content
     * @return
     * @throws Exception
     */
    public static String sendMessage(String mobile, String content) throws Exception {
        String HTTP_BACK_MESSAGE = HTTP_POST(MessageProperties.url,
                "id=" + MessageProperties.id
                        + "&pwd=" + MessageProperties.pwd
                        + "&to=" + mobile
                        + "&content=" + URLEncoder.encode(content, "gb2312")
                        + "&time=");
        return HTTP_BACK_MESSAGE;
    }

    /**
     * 发送通知
     *
     * @param mobile
     * @param notice
     * @return
     */
    public static String sendNotice(String mobile, String notice) throws Exception {
        String HTTP_BACK_MESSAGE = HTTP_POST(MessageProperties.url,
                "id=" + MessageProperties.id
                        + "&pwd=" + MessageProperties.pwd
                        + "&to=" + mobile
                        + "&content=" + URLEncoder.encode(notice + "【农小满】", "gb2312")
                        + "&time=");
        return HTTP_BACK_MESSAGE;
    }

}
