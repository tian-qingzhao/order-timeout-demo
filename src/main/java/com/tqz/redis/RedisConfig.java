package com.tqz.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * KeyExpirationEventMessageListener 实现了对 __keyevent@*__:expiredchannel 的监听。
 * 当KeyExpirationEventMessageListener收到Redis发布的过期Key的消息的时候，会发布 redisKeyExpiredEvent 事件。
 * 所以我们只需要监听 RedisKeyExpiredEvent 事件就可以拿到过期消息的Key，也就是延迟消息。
 * 对 RedisKeyExpiredEvent 事件的监听实现 {@link MyRedisKeyExpiredListener} .
 *
 * <p>1. 生产者需指定发送到哪个channel，消费者订阅该channel获得消息（channel即MQ中的topic）。
 * 2. Redis中默认有许多channel，其中__keyevent@<db>__:expired是指Redis数据库序号为db的过期key事件的channel。
 * 3. Redis过期key后，会在__keyevent@<db>__:expired发布一个事件，监听此事件可获取过期key。
 * 基于监听过期key的原理实现延迟任务：将延迟任务设为key，过期时间设为延迟时间；
 * 监听__keyevent@<db>__:expired，获取到延迟任务即为到达过期时间。
 *
 * <p>虽然延迟任务可以用这种方式实现，但存在延迟和丢消息的风险。
 * Redis过期事件发布不是在key到达过期时间时立即发布，而是在key被清除后才发布。
 * Redis的发布订阅模式并没有消息持久化机制，如果没有订阅者，消息会丢失。
 * 如果服务重启期间有消息发布但没有订阅者，消息会丢失。
 * 监听__keyevent@<db>__:expired channel会通知所有过期事件，包括不需要的。
 * 为了只消费特定类型的消息，需自行添加标记，如消息key加前缀，并在消费时判断带前缀的key。
 *
 * @author <a href="https://github.com/tian-qingzhao">tianqingzhao</a>
 * @since 2024/9/11 15:24
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();

        container.setConnectionFactory(connectionFactory);

        // 下面这种方式是灵活配置，针对每个库的失效key做处理
        // container.addMessageListener(new RedisExpiredListener(), new PatternTopic("__keyevent@0__:expired"));

        // Spring Data 的 KeyExpirationEventMessageListener 已经实现了监听__keyevent@*__:expired这个channel这个功能，
        // __keyevent@*__:expired中的*代表通配符的意思，监听所有的数据库。

        return container;
    }

    @Bean
    public KeyExpirationEventMessageListener redisKeyExpirationListener(RedisMessageListenerContainer container) {
        return new KeyExpirationEventMessageListener(container);
    }

}