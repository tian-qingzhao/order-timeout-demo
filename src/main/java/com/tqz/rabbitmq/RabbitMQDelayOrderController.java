package com.tqz.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;

@RestController
public class RabbitMQDelayOrderController {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQDelayOrderController.class);

    @Resource
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/rabbitmq/addDelay")
    public void addDelay(@RequestParam(value = "delay", defaultValue = "test") String delay) {
        // 消息ID，需要封装到CorrelationData中
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        log.info("提交延迟任务，请求标识信息：{}", correlationData);

        // 发送消息
        rabbitTemplate.convertAndSend(RabbitMQConfig.DELAY_ORDER_EXCHANGE_NAME, RabbitMQConfig.DELAY_ORDER_ROUTING_KEY_NAME, delay, correlationData);
    }

}