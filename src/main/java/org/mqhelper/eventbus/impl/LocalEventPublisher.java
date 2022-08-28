package org.mqhelper.eventbus.impl;

import org.mqhelper.eventbus.EventMessageConsumer;
import org.mqhelper.eventbus.EventPublisher;

/**
 * @author SongyangJi
 * @date 2022/08/26
 */
public class LocalEventPublisher implements EventPublisher {

    private final DefaultLocalSubscriberRegistry localSubscriberRegistry;

    public LocalEventPublisher(DefaultLocalSubscriberRegistry localSubscriberRegistry) {
        this.localSubscriberRegistry = localSubscriberRegistry;
    }

    @Override
    public void publish(Object event) {
        EventMessageConsumer messageConsumer = localSubscriberRegistry.getEventMessageConsumer(event);
        if (messageConsumer != null) {
            messageConsumer.dispatchEvent(event);
        }
    }
}
