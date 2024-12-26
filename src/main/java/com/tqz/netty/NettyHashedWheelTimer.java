package com.tqz.netty;

import io.netty.util.HashedWheelTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * netty的时间轮.
 *
 * <p>时间轮会被分成很多格子（本示例中的8就代表了8个格子），一个格子代表一段时间（本示例中的100就代表一个格子是100ms），每800ms会走一圈。
 * 当任务提交的之后，会根据任务的到期时间进行hash取模，计算出这个任务的执行时间所在具体的格子，
 * 然后添加到这个格子中，通过如果这个格子有多个任务，会用链表来保存。所以这个任务的添加有点像HashMap储存元素的原理。
 * HashedWheelTimer内部会开启一个线程，轮询每个格子，找到到了延迟时间的任务，然后执行。
 * 由于HashedWheelTimer也是单线程来处理任务，所以跟Timer一样，长时间运行的任务会导致其他任务的延时处理。
 * Redisson中的客户端延迟任务就是基于Netty的HashedWheelTimer实现的。
 *
 * @author <a href="https://github.com/tian-qingzhao">tianqingzhao</a>
 * @since 2024/9/11 14:41
 */
public class NettyHashedWheelTimer {

    private static final Logger log = LoggerFactory.getLogger(NettyHashedWheelTimer.class);

    public static void main(String[] args) {
        HashedWheelTimer timer = new HashedWheelTimer(100, TimeUnit.MILLISECONDS, 8);
        timer.start();

        log.info("提交延迟任务");
        timer.newTimeout(timeout -> log.info("执行延迟任务"), 3, TimeUnit.SECONDS);
        timer.newTimeout(timeout -> log.info("执行延迟任务"), 5, TimeUnit.SECONDS);
        timer.newTimeout(timeout -> log.info("执行延迟任务"), 8, TimeUnit.SECONDS);
        timer.newTimeout(timeout -> log.info("执行延迟任务"), 10, TimeUnit.SECONDS);
        timer.newTimeout(timeout -> log.info("执行延迟任务"), 15, TimeUnit.SECONDS);
    }
}