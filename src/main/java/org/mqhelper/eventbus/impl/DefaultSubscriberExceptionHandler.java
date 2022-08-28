package org.mqhelper.eventbus.impl;

import org.mqhelper.eventbus.SubscriberExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mqhelper.eventbus.Constants.UNIVERSE_EVENT;

/**
 * @author SongyangJi
 * @date 2022/08/26
 */
public class DefaultSubscriberExceptionHandler implements SubscriberExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(UNIVERSE_EVENT);
    @Override
    public void handleException(Throwable exception) {
        logger.error("",exception);
    }
}
