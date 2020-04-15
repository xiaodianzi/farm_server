package com.plansolve.farm.util;

/**
 * @Author: 高一平
 * @Date: 2019/3/11
 * @Description:
 **/
public class HtmlUtil {

    /**
     * "<a href=\"#editForm\" onclick=\"editForm('" + key + "','" + value + "')\" class=\"btn btn-xs btn-primary\" data-toggle=\"modal\">编辑</a>"
     *
     * @return
     */
    public static String getButtonHtml(Boolean isModal, String href, String onclick, String color, String buttonName) {
        String html = "<a";
        if (isModal) {
            html = html + " href=\"#" + href + "\" data-toggle=\"modal\"";
        } else {
            html = html + " href=\"" + href + "\"";
        }
        if (onclick != null && !onclick.isEmpty()) {
            html = html + " onclick=\"" + onclick + "\"";
        }
        html = html + " class=\"btn btn-xs btn-" + color + "\">" + buttonName + "</a>";
        return html;
    }

    /**
     * "<img src="picture" width="100%"/>"
     *
     * @return
     */
    public static String getImgHtml(String src, String width) {
        String html = "<img src=\"" + src;
        if (width != null && !width.isEmpty()) {
            html = html + "\" width=\"" + width + "\"/>";
        } else {
            html = html + "\"/>";
        }
        return html;
    }

    /**
     * 将页面信息转换为html语句
     *
     * @param detail
     * @return
     */
    public static String changeDetailToHtml(String detail) {
        String html = "";
        // 消除空格
        detail = detail.replace("\t", " ");
        detail = detail.replace("  ", "");
        String[] detailItems = detail.split("\r\n");
        if (detailItems != null && detailItems.length > 0) {
            for (String detailItem : detailItems) {
                if (detailItem.startsWith("[图片")) {
                    detailItem = detailItem.replace("[图片(", "<img src=\"");
                    detailItem = detailItem.replace(")]", "\" width=\"100%\"/>");
                    html = html + detailItem;
                } else if (!detailItem.trim().isEmpty()){
                    detailItem = "<p>" + detailItem + "</p>";
                    html = html + detailItem;
                }
            }
        }
        html = html.replace("\r\n", "");
        return html;
    }

    /**
     * 将html语句转换为页面信息
     *
     * @param html
     * @return
     */
    public static String changeHtmlToDetail(String html) {
        html = html.replace("<p>", "");
        html = html.replace("</p>", "\r\n");
        html = html.replace("<img src=\"", "[图片(");
        html = html.replace("\" width=\"100%\"/>", ")]\r\n");
        return html;
    }
}
