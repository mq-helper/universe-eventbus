package org.mqhelper.eventbus.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.mqhelper.eventbus.UniverseEventSubscriber;
import org.mqhelper.eventbus.SubscriberExceptionHandler;

import static org.mqhelper.eventbus.util.UniverseEventBusLogger.logger;

/**
 * @author SongyangJi
 * @date 2022/08/25
 */
public class DefaultUniverseEventSubscriber implements UniverseEventSubscriber {

    /**
     * Executor to use for dispatching events to this subscriber.
     */
    private final Executor executor;
    /**
     * Subscriber method.
     */
    private final Method method;
    private final SubscriberExceptionHandler subscriberExceptionHandler;
    /**
     * The object with the subscriber method.
     */
    private final Object target;

    private DefaultUniverseEventSubscriber(Object target, Method method, Executor executor) {
        this.target = target;
        this.method = method;
        this.executor = executor;
        subscriberExceptionHandler = new DefaultSubscriberExceptionHandler();
    }

    @Override
    public void handleEvent(Object event) {
        executor.execute(
            () -> {
                try {
                    invokeSubscriberMethod(event);
                } catch (InvocationTargetException e) {
                    subscriberExceptionHandler.handleException(e.getCause());
                } catch (Exception e) {
                    logger.error("invokeSubscriberMethod error", e);
                }
            });
    }

    @Override
    public final int hashCode() {
        return method.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        /*
         * method is the basic register unit
         */
        if (obj instanceof DefaultUniverseEventSubscriber) {
            DefaultUniverseEventSubscriber that = (DefaultUniverseEventSubscriber)obj;
            return method.equals(that.method);
        }
        return false;
    }

    public static DefaultUniverseEventSubscriber createDefaultEventSubscriber(Object target, Method method) {
        return new DefaultUniverseEventSubscriber(target, method, Executors.newCachedThreadPool());
    }

    public static DefaultUniverseEventSubscriber createDefaultEventSubscriber(Object target, Method method, Executor executor) {
        return new DefaultUniverseEventSubscriber(target, method, executor);
    }

    void invokeSubscriberMethod(Object event) throws InvocationTargetException {
        try {
            method.invoke(target, event);
        } catch (IllegalArgumentException e) {
            throw new Error("Method rejected target/argument: " + event, e);
        } catch (IllegalAccessException e) {
            throw new Error("Method became inaccessible: " + event, e);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof Error) {
                throw (Error)e.getCause();
            }
            throw e;
        }
    }
}
