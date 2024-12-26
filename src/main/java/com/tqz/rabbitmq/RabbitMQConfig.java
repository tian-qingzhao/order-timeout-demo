package com.tqz.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息发送的时候会将消息发送到orderExchange这个交换机上，由于orderExchange绑定了orderQueue，
 * 所以消息会被路由到orderQueue这个队列上，由于orderQueue没有消费者消费消息，并且又设置了过期时间，
 * 所以当消息过期之后，消息就被放到绑定的orderDelayExchange死信交换机中，消息到达orderDelayExchange交换机后，
 * 由于跟orderDelayQueue进行了绑定，所以消息就被路由到orderDelayQueue中，消费者就能从orderDelayQueue中拿到消息了。
 *
 * <p>上面说的队列与交换机的绑定关系，就是上面的配置类所干的事。
 * 其实从这个单从消息流转的角度可以看出，RabbitMQ跟RocketMQ实现有相似之处。
 * 消息最开始都并没有放到最终消费者消费的队列中，而都是放到一个中间队列中，
 * 等消息到了过期时间或者说是延迟时间，消息就会被放到最终的队列供消费者消息。
 * 只不过RabbitMQ需要你显示的手动指定消息所在的中间队列，而RocketMQ是在内部已经做好了这块逻辑。
 *
 * <p>除了基于RabbitMQ的死信队列来做，RabbitMQ官方还提供了延时插件，也可以实现延迟消息的功能，
 * 这个插件的大致原理也跟上面说的一样，延时消息会被先保存在一个中间的地方，
 * 然后有一个定时任务去查询最近需要被投递的消息，将其投递到目标队列中。
 *
 * @author <a href="https://github.com/tian-qingzhao">tianqingzhao</a>
 * @since 2024/9/11 16:07
 */
@Configuration
public class RabbitMQConfig {

    public static final String DELAY_ORDER_EXCHANGE_NAME = "delayOrderExchange";

    public static final String DELAY_ORDER_QUEUE_NAME = "delayOrderQueue";

    public static final String DELAY_ORDER_ROUTING_KEY_NAME = "delayOrderRoutingKey";

    public static final String DEAD_ORDER_EXCHANGE_NAME = "deadOrderExchange";

    public static final String DEAD_ORDER_QUEUE_NAME = "deadOrderQueue";

    public static final String DEAD_ORDER_ROUTING_KEY_NAME = "deadOrderRoutingKey";

    private static final Integer DELAY_ORDER_TIMEOUT_MILLIS = 30000;

    /**
     * 延迟订单的交换机、队列，以及交换机、队列绑定的路由键信息.
     */

    @Bean
    public DirectExchange delayOrderExchange() {
        return new DirectExchange(DELAY_ORDER_EXCHANGE_NAME);
    }

    @Bean
    public Queue delayOrderQueue() {
        return QueueBuilder
                //指定队列名称，并持久化
                .durable(DELAY_ORDER_QUEUE_NAME)
                //设置队列的超时时间为3秒，也就是延迟任务的时间
                .ttl(DELAY_ORDER_TIMEOUT_MILLIS)
                //指定死信交换机
                .deadLetterExchange(DEAD_ORDER_EXCHANGE_NAME).deadLetterRoutingKey(DEAD_ORDER_ROUTING_KEY_NAME).build();
    }

    @Bean
    public Binding delayOrderQueueBinding() {
        return BindingBuilder.bind(delayOrderQueue()).to(delayOrderExchange()).with(DELAY_ORDER_ROUTING_KEY_NAME);
    }


    /**
     * 死信订单的交换机、队列，以及交换机、队列绑定的路由键信息.
     * 也就是延迟订单超过 {@link #DELAY_ORDER_TIMEOUT_MILLIS} 之后没有被处理就会发送到死信队列里.
     */

    @Bean
    public DirectExchange deadOrderExchange() {
        return new DirectExchange(DEAD_ORDER_EXCHANGE_NAME);
    }

    @Bean
    public Queue deadOrderQueue() {
        return QueueBuilder
                //指定队列名称，并持久化
                .durable(DEAD_ORDER_QUEUE_NAME).build();
    }

    @Bean
    public Binding deadOrderQueueBinding() {
        return BindingBuilder.bind(deadOrderQueue()).to(deadOrderExchange()).with(DEAD_ORDER_ROUTING_KEY_NAME);
    }

}