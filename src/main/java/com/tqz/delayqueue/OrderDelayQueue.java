package com.tqz.delayqueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 1. offer方法会根据compareTo实现对任务排序，将最先执行的任务放在队列头部。
 * 2. take方法获取队列头部元素，即最早需要执行的任务。
 * 3. 通过getDelay返回值判断任务是否需要立刻执行。
 * 如果需要执行，则返回任务。如果不需要，则等待任务的延迟时间剩余时间。
 *
 * @author <a href="https://github.com/tian-qingzhao">tianqingzhao</a>
 * @since 2024/9/11 9:37
 */
public class OrderDelayQueue {

    private static final Logger log = LoggerFactory.getLogger(OrderDelayQueue.class);

    public static void main(String[] args) {
        DelayQueue<OrderDelayed> orderDelayQueue = new DelayQueue<>();

        new Thread(() -> {
            while (true) {
                try {
                    OrderDelayed orderDelayed = orderDelayQueue.take();
                    log.info("获取到延迟任务:{}", orderDelayed.getTaskContent());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        log.info("提交延迟任务");

        orderDelayQueue.offer(new OrderDelayed("5s的订单执行了", 5L));
        orderDelayQueue.offer(new OrderDelayed("3s的订单执行了", 3L));
        orderDelayQueue.offer(new OrderDelayed("8s的订单执行了", 8L));
    }
}
