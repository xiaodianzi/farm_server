package com.plansolve.farm.model.client.school;

import lombok.Data;

/**
 * @Author: 高一平
 * @Date: 2019/5/16
 * @Description:
 **/

@Data
public class CerealsRealTimePriceDTO {

    private String address; // 新闻显示城市 吉林/东北三省/全国

    private String title; // 新闻标题

    private String source; // 新闻来源

    private String createTime;

    private String url; // 新闻详情页链接

}
