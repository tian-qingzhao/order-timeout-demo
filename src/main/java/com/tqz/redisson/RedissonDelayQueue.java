package com.tqz.redisson;

import org.redisson.Redisson;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * 这个类在创建的时候会去初始化延迟队列，创建一个RedissonClient对象，
 * 之后通过RedissonClient对象获取到DelayedQueue和BlockingQueue对象，传入的队列名字。
 * 当延迟队列创建之后，会开启一个延迟任务的消费线程，这个线程会一直从BlockingQueue中通过take方法阻塞获取延迟任务。
 * 添加任务的时候是通过DelayedQueue的offer方法添加的。
 *
 * <p>redisson_delay_queue_timeout:order是sorted set数据类型，存放所有延迟任务，按到期时间戳排序；
 * order是目标队列，存放已到延迟时间的任务，消费者从中获取任务。redisson_delay_queue_channel:order是channel，
 * 用于通知客户端开启延迟任务。任务提交时，Redisson会将任务放到redisson_delay_queue_timeout:order中，最后执行的时间为提交时间戳+延迟时间。
 * Redisson客户端内部通过监听redisson_delay_queue_channel:order提交延迟任务，
 * 可保证将到延迟时间的任务从redisson_delay_queue_timeout:order移至order。消费者可从order获取延迟任务。
 * 所以从这可以看出，Redisson的延迟任务的实现跟前面说的MQ的实现都是殊途同归，最开始任务放到中间的一个地方，
 * 叫做redisson_delay_queue_timeout:order，然后会开启一个类似于定时任务的一个东西，
 * 去判断这个中间地方的消息是否到了延迟时间，到了再放到最终的目标的队列供消费者消费。
 * Redisson的这种实现方式比监听Redis过期key的实现方式更加可靠，因为消息都存在list和sorted set数据类型中，所以消息很少丢。
 *
 * @author <a href="https://github.com/tian-qingzhao">tianqingzhao</a>
 * @since 2024/9/11 14:46
 */
@Component
public class RedissonDelayQueue {

    private static final Logger log = LoggerFactory.getLogger(RedissonDelayQueue.class);

    private RBlockingQueue<String> blockingQueue;
    private RDelayedQueue<String> delayQueue;

    @PostConstruct
    public void init() {
        initDelayQueue();
        startDelayQueueConsumer();
    }

    private void initDelayQueue() {
        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer();
        serverConfig.setAddress("redis://localhost:6379");
        RedissonClient redissonClient = Redisson.create(config);
        blockingQueue = redissonClient.getBlockingQueue("orderTimeOutQueue");
        delayQueue = redissonClient.getDelayedQueue(blockingQueue);
    }

    private void startDelayQueueConsumer() {
        new Thread(() -> {
            while (true) {
                try {
                    String task = blockingQueue.take();
                    log.info("接收到延迟任务: {}", task);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "order-timeout-consumer").start();
    }

    public void offerTask(String task, long seconds) {
        log.info("添加延迟任务:{} 延迟时间:{}s", task, seconds);
        delayQueue.offer(task, seconds, TimeUnit.SECONDS);
    }
}