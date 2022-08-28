package org.mqhelper.eventbus.impl;

import org.mqhelper.eventbus.EventMessageConsumer;
import org.mqhelper.eventbus.UniverseEventPublisher;

/**
 * @author SongyangJi
 * @date 2022/08/26
 */
public class LocalUniverseEventPublisher implements UniverseEventPublisher {

    private final DefaultLocalSubscriberRegistry localSubscriberRegistry;

    public LocalUniverseEventPublisher(DefaultLocalSubscriberRegistry localSubscriberRegistry) {
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
