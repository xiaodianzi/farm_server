package com.plansolve.farm.model.client.school;

import lombok.Data;

/**
 * @Author: 高一平
 * @Date: 2019/5/9
 * @Description:
 **/
@Data
public class NewsDetailDTO {

    private String title; // 新闻标题

    private String detail; // 文字详情

    private String source; // 新闻来源

    private String releaseTime; // 新闻发布时间

    private String image; // 新闻详情页banner图片

}
