package com.tqz.infinitepolling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 无限轮询的意思就是开启一个线程不停的去轮询任务，当这些任务到达了延迟时间，那么就执行任务。
 * 任务可以存在数据库又或者是内存。这种操作简单，但是就是效率低下，每次都得遍历所有的任务。不推荐使用。
 *
 * @author <a href="https://github.com/tian-qingzhao">tianqingzhao</a>
 * @since 2024/9/11 15:55
 */
public class InfinitePollingTest {

    private static final Logger log = LoggerFactory.getLogger(InfinitePollingTest.class);

    private final static BlockingQueue<String> QUEUE = new ArrayBlockingQueue<>(1024);

    public static void main(String[] args) {
        QUEUE.add("test");
        while (true) {
            try {
                String content = QUEUE.take();
                // 这里可以根据获取到的内容作为条件查询数据库、redis等，判断任务是否到时间

                log.info("获取到任务：{}", content);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
