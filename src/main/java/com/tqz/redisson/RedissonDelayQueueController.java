package com.tqz.redisson;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * redisson延迟队列控制器.
 *
 * @author <a href="https://github.com/tian-qingzhao">tianqingzhao</a>
 * @since 2024/9/11 14:47
 */
@RestController
public class RedissonDelayQueueController {

    @Resource
    private RedissonDelayQueue redissonDelayQueue;

    @GetMapping("/addDelay")
    public void addDelay(@RequestParam(value = "delay",defaultValue = "test") String delay) {
        redissonDelayQueue.offerTask(delay, 10);
    }

}