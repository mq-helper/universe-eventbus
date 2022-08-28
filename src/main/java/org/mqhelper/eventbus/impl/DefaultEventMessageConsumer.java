package org.mqhelper.eventbus.impl;

import java.util.Collection;

import com.google.common.collect.Sets;
import org.mqhelper.eventbus.EventMessageConsumer;
import org.mqhelper.eventbus.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mqhelper.eventbus.Constants.UNIVERSE_EVENT;

/**
 * @author SongyangJi
 * @date 2022/08/25
 */
public class DefaultEventMessageConsumer implements EventMessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(UNIVERSE_EVENT);
    private final Collection<EventSubscriber> eventSubscribers = Sets.newHashSet();

    @Override
    public void dispatchEvent(Object event) {
        for (EventSubscriber eventSubscriber : eventSubscribers) {
            eventSubscriber.handleEvent(event);
        }
    }

    @Override
    public void registerEventSubscriber(EventSubscriber eventSubscriber) {
        synchronized (this) {
            eventSubscribers.add(eventSubscriber);
        }
    }

    @Override
    public void unregisterEventSubscriber(EventSubscriber eventSubscriber) {
        synchronized (this) {
            eventSubscribers.remove(eventSubscriber);
        }
    }
}
