package com.plansolve.farm.config;

import com.plansolve.farm.model.properties.FileProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @Author: 高一平
 * @Date: 2018/7/12
 * @Description: 映射项目外文件路径
 **/

@Configuration
public class FileConfigurer extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/file/**").addResourceLocations("file:" + FileProperties.fileRealPath);
        super.addResourceHandlers(registry);
    }

}
