package org.mqhelper.eventbus.impl;

import java.util.Collection;

import com.google.common.collect.Sets;
import org.mqhelper.eventbus.EventMessageConsumer;
import org.mqhelper.eventbus.UniverseEventSubscriber;

/**
 * @author SongyangJi
 * @date 2022/08/25
 */
public class DefaultEventMessageConsumer implements EventMessageConsumer {

    private final Collection<UniverseEventSubscriber> universeEventSubscribers = Sets.newHashSet();

    @Override
    public void dispatchEvent(Object event) {
        for (UniverseEventSubscriber universeEventSubscriber : universeEventSubscribers) {
            universeEventSubscriber.handleEvent(event);
        }
    }

    @Override
    public void registerEventSubscriber(UniverseEventSubscriber universeEventSubscriber) {
        synchronized (this) {
            universeEventSubscribers.add(universeEventSubscriber);
        }
    }

    @Override
    public void unregisterEventSubscriber(UniverseEventSubscriber universeEventSubscriber) {
        synchronized (this) {
            universeEventSubscribers.remove(universeEventSubscriber);
        }
    }
}
