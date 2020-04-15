package com.plansolve.farm.service.quartz;

import com.plansolve.farm.model.properties.FileProperties;
import com.plansolve.farm.service.wechat.StatisticService;
import com.plansolve.farm.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;

/**
 * @Author: 高一平
 * @Date: 2018/10/25
 * @Description:
 * (1)cron：cron表达式，指定任务在特定时间执行;
 * (2)fixedDelay：表示上一次任务执行完成后多久再次执行，参数类型为long，单位ms;
 * (3)fixedDelayString：与fixedDelay含义一样，只是参数类型变为String;
 * (4)fixedRate：表示按一定的频率执行任务，参数类型为long，单位ms;
 * (5)fixedRateString: 与fixedRate的含义一样，只是将参数类型变为String;
 * (6)initialDelay：表示延迟多久再第一次执行任务，参数类型为long，单位ms;
 * (7)initialDelayString：与initialDelay的含义一样，只是将参数类型变为String;
 * (8)zone：时区，默认为当前时区，一般没有用到。
 **/

@Component
@Slf4j
public class UserQuartzService {

    @Autowired
    private StatisticService statisticService;

    /**
     * 定时生成用户详情表
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void userExcelQuartz() {
        try {
            statisticService.updateUserExcel("user/UserDataExcel.xlsx");
            log.info("用户详情文件更新成功");
        } catch (Exception e) {
            log.error("用户详情文件更新失败");
            e.printStackTrace();
        }
    }

    /**
     * 定时生成用户统计表
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void userStatisticExcelQuartz() {
        try {
            String originExtName = DateUtils.formatDate(DateUtils.getDate_PastOrFuture_Day(new Date(), -2), "yyyyMMdd");
            String originName = "user/UserStatisticDataExcel-" + originExtName + ".xlsx";
            String extName = DateUtils.formatDate(DateUtils.getDate_PastOrFuture_Day(new Date(), -1), "yyyyMMdd");
            String name = "user/UserStatisticDataExcel-" + extName + ".xlsx";
            // 查看是否存在历史记录文件，若存在，在历史记录文件基础上更新局部，若不存在，则重新统计全部记录
            File file = new File(originName);
            if (file.exists()) {
                // 更新局部
                statisticService.updateUserStatisticExcel(name, originName, false);
                log.info("【局部更新用户统计文件成功】文件保存地址：{}" + FileProperties.fileRealPath + name);
            } else {
                // 更新全部
                statisticService.updateUserStatisticExcel(name, "user/UserStatisticDataExcel-origin.xlsx", true);
                log.info("【生成用户统计文件成功】文件保存地址：{}" + FileProperties.fileRealPath + name);
            }
        } catch (Exception e) {
            log.error("用户统计文件更新失败");
            e.printStackTrace();
        }
    }

}
