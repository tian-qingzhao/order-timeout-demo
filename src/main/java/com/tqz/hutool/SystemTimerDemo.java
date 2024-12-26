package com.tqz.hutool;

import cn.hutool.cron.timingwheel.SystemTimer;
import cn.hutool.cron.timingwheel.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hutool工具类也提供了延迟任务的实现SystemTimer，底层也是用的时间轮.
 *
 * @author <a href="https://github.com/tian-qingzhao">tianqingzhao</a>
 * @since 2024/9/11 15:07
 */
public class SystemTimerDemo {

    private static final Logger log = LoggerFactory.getLogger(SystemTimerDemo.class);

    public static void main(String[] args) {
        SystemTimer systemTimer = new SystemTimer();
        systemTimer.start();

        log.info("提交延迟任务");
        systemTimer.addTask(new TimerTask(() -> log.info("执行延迟任务"), 3000));
    }
}