package org.mqhelper.eventbus;

/**
 * EventMessageConsumer manages those eventHandlers which have the same type event.
 *
 * @author SongyangJi
 * @date 2022/08/25
 */
public interface EventMessageConsumer {
    void dispatchEvent(Object event);

    void registerEventSubscriber(EventSubscriber eventSubscriber);

    void unregisterEventSubscriber(EventSubscriber eventSubscriber);
}
