package com.plansolve.farm.service.console.Impl;

import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.database.school.News;
import com.plansolve.farm.model.properties.FileProperties;
import com.plansolve.farm.repository.school.NewsRepository;
import com.plansolve.farm.service.common.FileService;
import com.plansolve.farm.service.console.ConsoleNewsService;
import com.plansolve.farm.util.HtmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: 高一平
 * @Date: 2019/3/29
 * @Description:
 **/
@Slf4j
@Service
public class ConsoleNewsServiceImpl implements ConsoleNewsService {

    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private FileService fileService;

    /**
     * 添加新闻
     *
     * @param news  新闻信息
     * @param pictureFile 列表项图片
     * @param imageFile 详情页BANNER
     * @param files 详情页内置图
     * @return
     */
    @Override
    public News save(News news, MultipartFile pictureFile, MultipartFile imageFile, MultipartFile[] files) {
        if (news.getIsValid() == null) {
            news.setIsValid(false);
        }
        if (news.getIdNews() != null && news.getIdNews() > 0) {
            // 更改
            News oldNews = newsRepository.findByIdNews(news.getIdNews());
            news.setCreateTime(oldNews.getCreateTime());

            if (news.getReleaseTime() == null) {
                news.setReleaseTime(oldNews.getReleaseTime());
            }

            news.setPicture(oldNews.getPicture());
            news.setImage(oldNews.getImage());
        } else {
            // 新增
            news.setCreateTime(new Date());
            if (news.getReleaseTime() == null) {
                news.setReleaseTime(new Date());
            }

            news.setPicture("http://tgzj.plan-solve.com/plansolve/file/news/default.png");
            news.setImage(" ");
        }

        /*******************************************更改图片 保存现图 原图不删*********************************************/
        // 新闻ICON图片
        if (pictureFile != null && !pictureFile.isEmpty()) {
            try {
                String filename = fileService.saveFileByPath(FileProperties.fileRealPath, SysConstant.NEWS_ICON_PICTURE, pictureFile);
                news.setPicture("http:" + FileProperties.fileUrlPath + SysConstant.NEWS_ICON_PICTURE + filename);
            } catch (IOException e) {
                log.error("[文件保存失败]");
                e.printStackTrace();
            }

            /*if (!news.getPicture().equals(oldNews.getPicture())) {
                // 图片保存成功
                String picture = oldNews.getPicture();
                String pictureName = picture.substring(picture.lastIndexOf("/"), picture.length());
                fileService.deleteFileByPath(FileProperties.fileRealPath, SysConstant.NEWS_ICON_PICTURE, pictureName);
            }*/
        }

        // 新闻详情页BANNER图
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String filename = fileService.saveFileByPath(FileProperties.fileRealPath, SysConstant.NEWS_DETAIL_BANNER_PICTURE, imageFile);
                news.setImage(FileProperties.fileUrlPath + SysConstant.NEWS_DETAIL_BANNER_PICTURE + filename);
            } catch (IOException e) {
                log.error("[文件保存失败]");
                e.printStackTrace();
            }
        }

        // 新闻详情页内置图
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                try {
                    String originalFilename = file.getOriginalFilename();
                    String filename = fileService.saveFileByPath(FileProperties.fileRealPath, SysConstant.NEWS_DETAIL_PICTURE, file);
                    String detail = news.getDetail();
                    detail = detail.replace(originalFilename, FileProperties.fileUrlPath + SysConstant.NEWS_DETAIL_PICTURE + filename);
                    news.setDetail(detail);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        /*******************************************更改图片 保存现图 原图不删*********************************************/

        String detail = HtmlUtil.changeDetailToHtml(news.getDetail());
        news.setDetail(detail);

        news = newsRepository.save(news);
        if (news.getUrl() == null || news.getUrl().isEmpty()) {
            news.setUrl(SysConstant.NEWS_DETAIL_URL + news.getIdNews());
            news = newsRepository.save(news);
        }
        return news;
    }

    /**
     * 删除新闻
     *
     * @param idNews
     */
    @Override
    public void delete(Integer idNews) {
    }

    /**
     * 新闻信息分页查询
     *
     * @param title
     * @param address
     * @param isValid
     * @param page
     * @return
     */
    @Override
    public com.plansolve.farm.model.Page<News> pageNews(com.plansolve.farm.model.Page<News> page, String title, String address, Boolean isValid) {
        Sort sort = new Sort(Sort.Direction.DESC, "releaseTime");
        Pageable pageable = new PageRequest(page.getPageNo(), page.getPageSize(), sort);
        org.springframework.data.domain.Page<News> newsPage = newsRepository.findAll(new Specification<News>() {
            @Override
            public Predicate toPredicate(Root<News> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if (isValid != null) {
                    Predicate predicate = criteriaBuilder.equal(root.get("isValid").as(Boolean.class), isValid);
                    predicates.add(predicate);
                }
                if (title != null && !title.isEmpty()) {
                    Predicate predicate = criteriaBuilder.like(root.get("title").as(String.class), "%" + title + "%");
                    predicates.add(predicate);
                }
                if (address != null && !address.isEmpty()) {
                    Predicate predicate = criteriaBuilder.like(root.get("address").as(String.class), "%" + address + "%");
                    predicates.add(predicate);
                }

                query.where(predicates.toArray(new Predicate[predicates.size()]));
                return query.getRestriction();
            }
        }, pageable);
        page.setTotal(newsPage.getTotalElements());
        page.setRows(newsPage.getContent());
        return page;
    }
}
