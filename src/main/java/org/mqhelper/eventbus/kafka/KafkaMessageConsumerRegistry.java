package org.mqhelper.eventbus.kafka;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.mqhelper.eventbus.EventMessageConsumer;
import org.mqhelper.eventbus.LocalSubscriberRegistry;

/**
 * Just for demo
 *
 * @author SongyangJi
 * @date 2022/08/28
 */
public class KafkaMessageConsumerRegistry {
    private final LocalSubscriberRegistry localSubscriberRegistry;
    private final Map<String, Object> props;
    private final Set<Type> registry = Sets.newHashSet();

    public KafkaMessageConsumerRegistry(Map<String, Object> props, LocalSubscriberRegistry localSubscriberRegistry) {
        this.props = props;
        this.localSubscriberRegistry = localSubscriberRegistry;
    }

    public void registerKafkaMessageConsumer(Type eventType) {
        EventMessageConsumer eventMessageConsumer = localSubscriberRegistry.getEventMessageConsumer(eventType);
        registerKafkaMessageConsumer(eventType, eventMessageConsumer);
    }

    public void registerKafkaMessageConsumer(Type eventType, EventMessageConsumer eventMessageConsumer) {
        synchronized (this) {
            if (registry.contains(eventType)) {
                return;
            }
            registry.add(eventType);

            KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(props);
            KafkaSimpleMessageConsumer kafkaSimpleMessageConsumer = new KafkaSimpleMessageConsumer(eventType,
                eventMessageConsumer, kafkaConsumer);
            new Thread(kafkaSimpleMessageConsumer::work).start();
        }
    }

    public void registerKafkaMessageConsumer(Map<Type/*event type*/, EventMessageConsumer> eventMessageConsumers) {
        eventMessageConsumers.forEach(
            this::registerKafkaMessageConsumer);
    }

    public LocalSubscriberRegistry getLocalSubscriberRegistry() {
        return localSubscriberRegistry;
    }
}
