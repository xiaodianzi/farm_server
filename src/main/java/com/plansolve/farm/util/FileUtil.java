package com.plansolve.farm.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2018/11/20
 * @Description:
 **/
@Slf4j
public class FileUtil {

    /**
     * 读取TXT文件内容
     *
     * @param path
     * @return
     */
    public List<String> readTXT(String path) {
        try {
            List<String> list = new ArrayList<>();
            File file = new File(path);
            String txtCode = getTXTCode(file);

            InputStream inputStream = new FileInputStream(file);
            InputStreamReader fileReader = new InputStreamReader(inputStream, txtCode);
            BufferedReader reader = new BufferedReader(fileReader);

            // 按行读取字符串
            String string;
            while ((string = reader.readLine()) != null) {
                list.add(string);
            }
            reader.close();
            fileReader.close();
            return list;
        } catch (IOException e) {
            log.error("【文件读取失败】");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取TXT文件字符集
     *
     * @param file
     * @return
     * @throws Exception
     */
    public String getTXTCode(File file) {
        try {
            InputStream inputStream = new FileInputStream(file);
            byte[] head = new byte[3];
            inputStream.read(head);

            String code = "GBK";  //或gb2312
            if (head[0] == -1 && head[1] == -2) code = "UTF-16";
            else if (head[0] == -2 && head[1] == -1) code = "Unicode";
            else if (head[0] == -17 && head[1] == -69 && head[2] == -65) code = "UTF-8";

            inputStream.close();
            return code;
        } catch (IOException e) {
            log.error("【获取文件字符集失败】");
            e.printStackTrace();
            return null;
        }
    }
}
