package com.plansolve.farm.util;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @Author: 高一平
 * @Date: 2018/10/15
 * @Description:
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class AddressUtilTest {

    @Test
    public void convert() {
        Double latitude = 45.430941;
        Double longitude = 122.599917;
        AddressUtil.convert(latitude, longitude);
    }

    // 高德地图省市县
    @Value("classpath:static/wechat/js/china_city_data.json")
    private Resource addresses;

    // 将高德地图省市县数据格式化为所需数据格式
    @Test
    public void address() throws IOException {
        String string = IOUtils.toString(addresses.getInputStream(), Charset.forName("UTF-8"));

        string = string.replace(" ", "");
        Gson gson = new Gson();
        ArrayList<LinkedTreeMap> provinceList = gson.fromJson(string, ArrayList.class);

        String[][][] address = new String[50][50][50];
        for (int i = 0; i < provinceList.size(); i++) {
            LinkedTreeMap province = provinceList.get(i);
            address[i][0][0] = (String) province.get("name");
            ArrayList cityList = (ArrayList) province.get("cityList");
            for (int j = 0; j < cityList.size(); j++) {
                LinkedTreeMap city = (LinkedTreeMap) cityList.get(j);
                address[i][j + 1][0] = (String) city.get("name");
                ArrayList countyList = (ArrayList) city.get("cityList");
                for (int z = 0; z < countyList.size(); z++) {
                    LinkedTreeMap county = (LinkedTreeMap) countyList.get(z);
                    address[i][j + 1][z + 1] = (String) county.get("name");
                }
            }
        }
        String provinceStr = "\"0\",[";
        for (int i = 0; i < 50; i++) {
            if (address[i][0][0] != null && address[i][0][0].length() > 0) {
                provinceStr = provinceStr + "\"" + address[i][0][0] + "\"" + ",";
            }
            String cityStr = "\"0_" + i + "\",[";
            for (int j = 0; j < 49; j++) {
                if (address[i][j + 1][0] != null && address[i][j + 1][0].length() > 0) {
                    cityStr = cityStr + "\"" + address[i][j + 1][0] + "\"" + ",";
                }
                String countyStr = "\"0_" + i + "_" + j + "\",[";
                for (int z = 0; z < 49; z++) {
                    if (address[i][j + 1][z + 1] != null && address[i][j + 1][z + 1].length() > 0) {
                        countyStr = countyStr + "\"" + address[i][j + 1][z + 1] + "\"" + ",";
                    }
                }
                if (!countyStr.equals("\"0_" + i + "_" + j + "\",[")) {
                    countyStr = countyStr.substring(0, countyStr.length() - 1) + "]";
                    countyStr = "dsy.add(" + countyStr + ");";
                    System.out.println(countyStr);
                }
            }
            if (!cityStr.equals("\"0_" + i + "\",[")) {
                cityStr = cityStr.substring(0, cityStr.length() - 1) + "]";
                cityStr = "dsy.add(" + cityStr + ");";
                System.out.println(cityStr);
            }
        }
        if (!provinceStr.equals("\"0\",[")) {
            provinceStr = provinceStr.substring(0, provinceStr.length() - 1) + "]";
            provinceStr = "dsy.add(" + provinceStr + ");";
            System.out.println(provinceStr);
        }
    }
}