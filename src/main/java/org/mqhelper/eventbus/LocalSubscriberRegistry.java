package org.mqhelper.eventbus;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author SongyangJi
 * @date 2022/08/25
 */
public interface LocalSubscriberRegistry {
    EventMessageConsumer getEventMessageConsumer(Object event);

    EventMessageConsumer getEventMessageConsumer(Type eventType);

    Map<Type, EventMessageConsumer> listAllEventMessageConsumers();

    void register(Object subscriberHolder);

    void unregister(Object subscriberHolder);
}
