package com.tqz.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.stereotype.Component;

/**
 * 监听redis的key过期的监听器.
 *
 * @author <a href="https://github.com/tian-qingzhao">tianqingzhao</a>
 * @since 2024/9/11 15:35
 */
@Component
public class MyRedisKeyExpiredListener implements ApplicationListener<RedisKeyExpiredEvent> {

    private static final Logger log = LoggerFactory.getLogger(MyRedisKeyExpiredListener.class);

    @Override
    public void onApplicationEvent(RedisKeyExpiredEvent event) {
        byte[] body = event.getSource();

        log.info("获取到延迟消息：{}", new String(body));
    }

}