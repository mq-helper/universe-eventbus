package org.mqhelper.eventbus;

/**
 * @author SongyangJi
 * @date 2022/08/25
 */
public interface UniverseEventSubscriber {
    void handleEvent(Object event);
}
