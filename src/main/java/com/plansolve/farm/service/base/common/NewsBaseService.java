package com.plansolve.farm.service.base.common;

import com.plansolve.farm.model.Page;
import com.plansolve.farm.model.database.school.News;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author: 高一平
 * @Date: 2019/5/8
 * @Description:
 **/
public interface NewsBaseService {

    public News getNews(Integer idNews);

    public Page<News> pageNews(Page<News> page);

}
