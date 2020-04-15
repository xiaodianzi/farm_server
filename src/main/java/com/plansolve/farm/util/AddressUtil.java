package com.plansolve.farm.util;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.plansolve.farm.exception.AddressException;
import com.plansolve.farm.model.client.AddressDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @Author: 高一平
 * @Date: 2018/10/15
 * @Description: 经纬度与地址之间的转换
 **/
@Slf4j
public class AddressUtil {

    private final static String AK = "214cfdxHdEf2HkjZEPtuW0TmAguOQg9z";
    private final static String SK = "3B8o6LVCrXp843FcLpUfQM7wkldEweQG";
    private final static String CONVERT_URI = "http://api.map.baidu.com/geocoder/v2/";

    /**
     * 将经纬度解析为具体位置
     *
     * @param latitude  纬度
     * @param longitude 经度
     * @return 具体的省市直辖市
     */
    public static AddressDTO convert(Double latitude, Double longitude) {
        try {
            String callback = "renderReverse";
            String location = latitude + "," + longitude;
            String output = "json";
            String pois = "1";
            String ak = AK;
            String url = CONVERT_URI
                    + "?callback=" + callback
                    + "&location=" + location
                    + "&output=" + output
                    + "&pois=" + pois
                    + "&ak=" + ak;

            /**
             * 成功：{"status":0,"result":{"location":{"lng":122.59991700000016,"lat":45.43094063985332},"formatted_address":"吉林省白城市洮南市","business":"","addressComponent":{"country":"中国","country_code":0,"country_code_iso":"CHN","country_code_iso2":"CN","province":"吉林省","city":"白城市","city_level":2,"district":"洮南市","town":"","adcode":"220881","street":"","street_number":"","direction":"","distance":""},"pois":[{"addr":"白城市洮南市","cp":" ","direction":"西","distance":"760","name":"隋家","poiType":"行政地标","point":{"x":122.60663783945047,"y":45.43179406744628},"tag":"行政地标;村庄","tel":"","uid":"9f97b1c548a87a06b74f54a0","zip":"","parent_poi":{"name":"","tag":"","addr":"","point":{"x":0.0,"y":0.0},"direction":"","distance":"","uid":""}}],"roads":[],"poiRegions":[],"sematic_description":"隋家西760米","cityCode":51}}
             * 失败：{"status":211,"message":"APP SN校验失败"}
             */
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            String result = EntityUtils.toString(httpEntity);
            log.info("==地址解析详情：" + result + "==");
            httpResponse.close();

            Gson gson = new Gson();
            result = result.substring(result.indexOf("(") + 1, result.lastIndexOf(")"));
            LinkedTreeMap map = gson.fromJson(result, LinkedTreeMap.class);
            Double status = (Double) map.get("status");
            if (status > 0) {
                String message = (String) map.get("message");
                log.error("【经纬度解析失败】status={}，message={}", status, message);
                return null;
            } else {
                AddressDTO addressDTO = new AddressDTO();
                addressDTO.setLatitude(latitude);
                addressDTO.setLongitude(longitude);

                LinkedTreeMap resultMap = (LinkedTreeMap) map.get("result");
                LinkedTreeMap addressComponent = (LinkedTreeMap) resultMap.get("addressComponent");
                String province = (String) addressComponent.get("province");
                addressDTO.setProvince(province);
                String city = (String) addressComponent.get("city");
                addressDTO.setCity(city);
                String district = (String) addressComponent.get("district");
                addressDTO.setCounty(district);
                String town = (String) addressComponent.get("town");
                addressDTO.setTown(town);
                String street = (String) addressComponent.get("street");
                String street_number = (String) addressComponent.get("street_number");
                addressDTO.setDetail(street + street_number);

                return addressDTO;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new AddressException(e.getMessage());
        }
    }

    /**
     * 计算signature签名
     *
     * @param paramsMap 所需参数
     *                  计算sn跟参数对出现顺序有关，
     *                  get请求请使用LinkedHashMap保存<key,value>，
     *                  该方法根据key的插入顺序排序；
     *                  post请使用TreeMap保存<key,value>，
     *                  该方法会自动将key按照字母a-z顺序排序。
     *                  所以get请求可自定义参数顺序（sn参数必须在最后）发送请求，
     *                  但是post请求必须按照字母a-z顺序填充body（sn参数必须在最后）。
     *                  以get请求为例：http://api.map.baidu.com/geocoder/v2/?address=百度大厦&output=json&ak=yourak，
     *                  paramsMap中先放入address，再放output，然后放ak，放入顺序必须跟get请求中对应参数的出现顺序保持一致。
     *                  Map paramsMap = new LinkedHashMap<String, String>();
     *                  paramsMap.put("address", "百度大厦");
     *                  paramsMap.put("output", "json");
     *                  paramsMap.put("ak", "yourak");
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String getSN(LinkedHashMap<String, String> paramsMap) throws UnsupportedEncodingException {
        /**
         * 调用下面的toQueryString方法，
         * 对LinkedHashMap内所有value作utf8编码，
         * 拼接返回结果address=%E7%99%BE%E5%BA%A6%E5%A4%A7%E5%8E%A6&output=json&ak=yourak
         */
        String paramsStr = toQueryString(paramsMap);

        /**
         * （假设sk=yoursk）
         * 对paramsStr前面拼接上/geocoder/v2/?，
         * 后面直接拼接yoursk得到/geocoder/v2/?address=%E7%99%BE%E5%BA%A6%E5%A4%A7%E5%8E%A6&output=json&ak=yourakyoursk
         */
        String wholeStr = new String("/geocoder/v2/?" + paramsStr + SK);

        /**
         * 对上面wholeStr再作utf8编码
         */
        String tempStr = URLEncoder.encode(wholeStr, "UTF-8");

        /**
         * 调用下面的MD5方法得到最后的sn签名7de5a22212ffaa9e326444c75a58f9a0
         */
        return MD5(tempStr);
    }

    /**
     * 对Map内所有value作utf8编码，拼接返回结果
     *
     * @param data
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String toQueryString(Map<?, ?> data) throws UnsupportedEncodingException {
        StringBuffer queryString = new StringBuffer();
        for (Entry<?, ?> pair : data.entrySet()) {
            queryString.append(pair.getKey() + "=");
            queryString.append(URLEncoder.encode((String) pair.getValue(), "UTF-8") + "&");
        }
        if (queryString.length() > 0) {
            queryString.deleteCharAt(queryString.length() - 1);
        }
        return queryString.toString();
    }

    /**
     * 来自stackoverflow的MD5计算方法，调用了MessageDigest库函数，并把byte数组结果转换成16进制
     *
     * @param md5
     * @return
     */
    private static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    /**
     * 判断该地址对象是否可以存入数据库
     *
     * @param addressDTO
     * @return
     */
    public static Boolean canSave(AddressDTO addressDTO) {
        if (!StringUtil.isEmpty(addressDTO.getProvince())
                && !StringUtil.isEmpty(addressDTO.getCity())
                && !StringUtil.isEmpty(addressDTO.getCounty())) {
            return true;
        } else {
            return false;
        }
    }

}
