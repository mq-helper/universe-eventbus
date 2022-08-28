package org.mqhelper.eventbus;

/**
 * @author SongyangJi
 * @date 2022/08/25
 */
public interface SubscriberExceptionHandler {
    void handleException(Throwable exception);
}
