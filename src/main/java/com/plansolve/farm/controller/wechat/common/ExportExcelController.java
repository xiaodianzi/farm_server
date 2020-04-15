package com.plansolve.farm.controller.wechat.common;

import com.plansolve.farm.model.SysConstant;
import com.plansolve.farm.model.database.user.User;
import com.plansolve.farm.model.properties.FileProperties;
import com.plansolve.farm.util.AppHttpUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;

/**
 * @Author: 高一平
 * @Date: 2018/10/22
 * @Description:
 **/
@Controller
@RequestMapping("/wechat/export/excel")
public class ExportExcelController {

    /**
     * 获取管理员名单
     *
     * @return
     * @throws IOException
     */
    private String getAdmin() throws IOException {
        File file = new File(FileProperties.fileRealPath + "admin.txt");
        if (file.exists()) {
            InputStream inputStream = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer buffer = new StringBuffer();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(line);
            }
            inputStream.close();
            return buffer.toString();
        } else {
            return "";
        }
    }

    /**
     * 读取用户统计文件
     *
     * @param response
     * @param isOnLine 是否在线阅读
     * @throws IOException
     */
    @GetMapping("/userExcel")
    public void userExcel(HttpServletResponse response, Boolean isOnLine) throws IOException {
        User user = (User) AppHttpUtil.getSessionAttribute(SysConstant.CURRENT_USER);
        String admin = getAdmin();
        if (admin.contains(user.getMobile())) {
            String userExcelPath = FileProperties.fileRealPath + "user/UserDataExcel.xlsx";
            File file = new File(userExcelPath);

            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
            byte[] buf = new byte[1024];
            int len = 0;

            response.reset(); // 非常重要
            if (isOnLine) { // 在线打开方式
                URL u = new URL("file:///" + userExcelPath);
                response.setContentType(u.openConnection().getContentType());
                response.setHeader("Content-Disposition", "inline; filename=" + file.getName());
                // 文件名应该编码成UTF-8
            } else { // 纯下载方式
                response.setContentType("application/x-msdownload");
                response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
            }
            OutputStream out = response.getOutputStream();
            while ((len = inputStream.read(buf)) > 0)
                out.write(buf, 0, len);
            inputStream.close();
            out.close();
        }
    }

}
