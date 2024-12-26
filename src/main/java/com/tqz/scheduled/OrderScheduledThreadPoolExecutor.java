package com.tqz.scheduled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 由于Timer在使用上有一定的问题，所以在JDK1.5版本的时候官方增加了ScheduledThreadPoolExecutor，
 * 作用跟Timer差不多，主要作用是解决单线程和异常崩溃等问题。
 *
 * <p>ScheduledThreadPoolExecutor继承了ThreadPoolExecutor，也就是继承了线程池，所以可以有很多个线程来执行任务。
 * 在构造的时候会传入一个DelayedWorkQueue阻塞队列，所以线程池内部的阻塞队列是DelayedWorkQueue。
 * 在提交延迟任务的时候，任务会被封装一个任务会被封装成ScheduledFutureTask对象，然后放到DelayedWorkQueue阻塞队列中。
 * ScheduledFutureTask实现了前面提到的Delayed接口，所以其实可以猜到DelayedWorkQueue会根据
 * ScheduledFutureTask对于Delayed接口的实现来排序，所以线程能够获取到最早到延迟时间的任务。
 * 当线程从DelayedWorkQueue中获取到需要执行的任务之后就会执行任务。
 *
 * @author <a href="https://github.com/tian-qingzhao">tianqingzhao</a>
 * @since 2024/9/11 15:51
 */
public class OrderScheduledThreadPoolExecutor {

    private static final Logger log = LoggerFactory.getLogger(OrderScheduledThreadPoolExecutor.class);

    public static void main(String[] args) {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2, new ThreadPoolExecutor.CallerRunsPolicy());

        log.info("提交延迟任务");

        executor.schedule(() -> log.info("执行延迟任务"), 3, TimeUnit.SECONDS);
    }
}