package com.plansolve.farm.model.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author: 高一平
 * @Date: 2018/6/11
 * @Description: 文件保存相关设置
 **/
@Component
@ConfigurationProperties(prefix = "file")
public class FileProperties {

    public static String fileRealPath;

    public static String fileUrlPath;

    public void setFileRealPath(String fileRealPath) {
        this.fileRealPath = fileRealPath;
    }

    public void setFileUrlPath(String fileUrlPath) {
        this.fileUrlPath = fileUrlPath;
    }

}
