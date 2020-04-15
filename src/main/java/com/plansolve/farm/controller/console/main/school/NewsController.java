package com.plansolve.farm.controller.console.main.school;

import com.plansolve.farm.controller.console.BaseController;
import com.plansolve.farm.model.Page;
import com.plansolve.farm.model.console.PageDTO;
import com.plansolve.farm.model.console.common.NewsDTO;
import com.plansolve.farm.model.database.school.News;
import com.plansolve.farm.service.base.common.NewsBaseService;
import com.plansolve.farm.service.console.ConsoleNewsService;
import com.plansolve.farm.util.DateUtils;
import com.plansolve.farm.util.HtmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/3/29
 * @Description:
 **/

@Controller
@Slf4j
@RequestMapping(value = "/manger/school/news")
public class NewsController extends BaseController {

    @Autowired
    private ConsoleNewsService newsService;
    @Autowired
    private NewsBaseService newsBaseService;

    /**
     * 新闻列表页
     *
     * @return
     */
    @RequestMapping(value = "/newsList")
    public String newsList() {
        return "console/news/list";
    }

    /**
     * 分页查询
     *
     * @param limit
     * @param offset
     * @param title
     * @param address
     * @param isValid
     * @return
     */
    @RequestMapping(value = "/list")
    @ResponseBody
    public PageDTO newsList(@RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "0") Integer offset,
                            String title, String address, Boolean isValid) {
        Integer pageNo = getPage(limit, offset);
        Page<News> page = new Page<>();
        page.setPageSize(limit);
        page.setPageNo(pageNo);

        Page<News> newsPage = newsService.pageNews(page, title, address, isValid);
        PageDTO<NewsDTO> pageDTO = new PageDTO<>();
        pageDTO.setTotal(newsPage.getTotal());

        List<NewsDTO> dtos = new ArrayList<>();
        List<News> news = newsPage.getRows();
        if (news != null && news.size() > 0) {
            for (News newsItem : news) {
                NewsDTO dto = new NewsDTO();
                BeanUtils.copyProperties(newsItem, dto);
                dto.setCreateTime(DateUtils.formatDateTime(newsItem.getCreateTime()));
                dto.setReleaseTime(DateUtils.formatDateTime(newsItem.getReleaseTime()));
                if (newsItem.getIsValid()) {
                    dto.setIsValid("有效");
                } else {
                    dto.setIsValid("无效");
                }
                dtos.add(dto);
            }
        }
        pageDTO.setRows(dtos);
        return pageDTO;
    }

    /**
     * 跳转新闻编辑页
     * 加载修改对象
     *
     * @return
     */
    @RequestMapping(value = "/editNewsPage")
    public String editNewsPage(@RequestParam(required = false) Integer idNews, Model model) {
        News news;
        if (idNews != null && idNews > 0) {
            news = newsBaseService.getNews(idNews);
            String detail = HtmlUtil.changeHtmlToDetail(news.getDetail());
            news.setDetail(detail);
        } else {
            news = new News();
            news.setIsValid(false);
        }
        model.addAttribute("item", news);
        return "console/news/edit";
    }

    /**
     * 添加/修改新闻
     *
     * @param news        新闻信息
     * @param time        发布时间
     * @param pictureFile icon图片
     * @param imageFile   详情页banner图
     * @param files       详情内容图
     * @return
     */
    @RequestMapping(value = "/editNews")
    public String editNews(News news,
                           @RequestParam(required = false) String time,
                           @RequestParam(value = "pictureFile", required = false) MultipartFile pictureFile,
                           @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                           @RequestParam(value = "files", required = false) MultipartFile[] files) {
        // 设置新闻添加人
        news.setIdAdminUser(0);

        if (time != null && !time.trim().isEmpty()) {
            time = time.replace(".0", "");
            news.setReleaseTime(DateUtils.parseDate(time));
        }
        newsService.save(news, pictureFile, imageFile, files);
        return "redirect:newsList";
    }

}
