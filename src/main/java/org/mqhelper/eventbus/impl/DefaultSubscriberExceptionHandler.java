package org.mqhelper.eventbus.impl;

import org.mqhelper.eventbus.SubscriberExceptionHandler;

import static org.mqhelper.eventbus.util.UniverseEventBusLogger.logger;

/**
 * @author SongyangJi
 * @date 2022/08/26
 */
public class DefaultSubscriberExceptionHandler implements SubscriberExceptionHandler {
    @Override
    public void handleException(Throwable exception) {
        logger.error("", exception);
    }
}
