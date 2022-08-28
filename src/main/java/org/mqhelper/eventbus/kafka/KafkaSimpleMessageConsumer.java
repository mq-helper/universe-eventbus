package org.mqhelper.eventbus.kafka;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.Collections;

import com.alibaba.fastjson.JSONObject;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.mqhelper.eventbus.BaseEventMessage;
import org.mqhelper.eventbus.EventMessageConsumer;

import static org.mqhelper.eventbus.util.UniverseEventBusLogger.logger;

/**
 * Just for demo
 *
 * @author SongyangJi
 * @date 2022/08/28
 */
public class KafkaSimpleMessageConsumer {
    private final EventMessageConsumer eventMessageConsumer;
    private final Type eventType;
    private final KafkaConsumer<String, String/*BaseEventMessage json string*/> kafkaConsumer;

    public KafkaSimpleMessageConsumer(Type eventType, EventMessageConsumer eventMessageConsumer,
        KafkaConsumer<String, String> kafkaConsumer) {
        this.eventType = eventType;
        this.eventMessageConsumer = eventMessageConsumer;
        this.kafkaConsumer = kafkaConsumer;
    }

    public void work() {
        // Subscribe to the topic.
        String topic = KafkaEventRouter.routingToTopic(eventType);
        kafkaConsumer.subscribe(Collections.singletonList(topic));
        for (; ; ) {
            try {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(1)); // todo
                for (ConsumerRecord<String, String> record : records) {
                    Object event;
                    try {
                        String baseEventMessageStr = record.value();
                        logger.debug("baseEventMessageStr={}", baseEventMessageStr);
                        BaseEventMessage baseEventMessage = JSONObject.parseObject(baseEventMessageStr,
                            BaseEventMessage.class);
                        String eventPayload = baseEventMessage.getEventPayload();
                        event = JSONObject.parseObject(eventPayload, eventType);
                    } catch (Exception e) {
                        logger.error("Event message deserialize fail", e);
                        continue;
                    }
                    try {
                        eventMessageConsumer.dispatchEvent(event);
                    } catch (Exception e) {
                        logger.error("eventMessageConsumer.dispatchEvent fail", e);
                    }
                }
            } catch (Exception e) {
                logger.error("kafkaConsumer.poll error", e);
            }
        }
    }
}
