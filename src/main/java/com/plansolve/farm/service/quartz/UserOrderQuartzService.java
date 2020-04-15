package com.plansolve.farm.service.quartz;

import com.plansolve.farm.service.client.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
public class UserOrderQuartzService {

    @Autowired
    private OrderService orderService;

    /**
     * 给过期订单过期状态
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void userStatisticExcelQuartz() {
        orderService.overdueOrder();
    }

}
