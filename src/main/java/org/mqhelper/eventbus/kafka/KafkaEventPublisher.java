package org.mqhelper.eventbus.kafka;

import java.util.concurrent.Future;

import com.alibaba.fastjson.JSONObject;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.RetriableException;
import org.mqhelper.eventbus.BaseEventMessage;
import org.mqhelper.eventbus.EventPublisher;
import org.mqhelper.eventbus.exception.PublishEventException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mqhelper.eventbus.Constants.UNIVERSE_EVENT;
import static org.mqhelper.eventbus.kafka.KafkaEventRouter.routingToTopic;

/**
 * @author SongyangJi
 * @date 2022/08/26
 */
public class KafkaEventPublisher implements EventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(UNIVERSE_EVENT);

    private final KafkaProducer<String, String/*BaseEventMessage json string*/> kafkaProducer;

    public KafkaEventPublisher(KafkaProducer<String, String> kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public void publish(Object event) {
        // todo
        // manually create topic

        String msg;
        try {
            String eventPayload = JSONObject.toJSONString(event);
            BaseEventMessage baseEventMessage = new BaseEventMessage(eventPayload);
            msg = JSONObject.toJSONString(baseEventMessage);
        } catch (Exception e) {
            throw new PublishEventException(e.getMessage());
        }

        String topic = routingToTopic(event);
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, msg);
        Future<RecordMetadata> sendFuture = kafkaProducer.send(record, (metadata, exception) -> {
            if (exception == null) {
                logger.debug("event={}, metadata={}", event, metadata);
            } else {
                if (exception instanceof RetriableException) {
                    logger.error("publish event failed with RetriableException", exception);
                } else {
                    logger.error("publish event failed with UnRetriableException", exception);
                }
                throw new PublishEventException(exception.getMessage());
            }
        });
        // Maybe an immediate failure
        if (sendFuture.isDone()) {
            try {
                sendFuture.get();
            } catch (Exception e) {
                throw new PublishEventException(e.getMessage());
            }
        }
    }

}
