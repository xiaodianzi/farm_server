package com.plansolve.farm.service.console;

import com.plansolve.farm.model.Page;
import com.plansolve.farm.model.database.school.News;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/3/29
 * @Description:
 **/
public interface ConsoleNewsService {

    /**
     * 添加新闻
     *
     * @param news
     * @return
     */
    public News save(News news, MultipartFile pictureFile, MultipartFile imageFile, MultipartFile[] files);

    /**
     * 删除新闻
     *
     * @param idNews
     */
    public void delete(Integer idNews);

    /**
     * 分页条件查询
     *
     * @param page
     * @param title
     * @param address
     * @param isValid
     * @return
     */
    public Page<News> pageNews(Page<News> page, String title, String address, Boolean isValid);

}
