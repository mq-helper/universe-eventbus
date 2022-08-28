package org.mqhelper.eventbus.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.primitives.Primitives;
import org.mqhelper.eventbus.EventMessageConsumer;
import org.mqhelper.eventbus.EventSubscriber;
import org.mqhelper.eventbus.LocalSubscriberRegistry;
import org.mqhelper.eventbus.UniverseSubscriber;

import static com.google.common.base.Preconditions.checkArgument;
import static org.mqhelper.eventbus.impl.DefaultEventSubscriber.createDefaultEventSubscriber;

/**
 * @author SongyangJi
 * @date 2022/08/25
 */
public class DefaultLocalSubscriberRegistry implements LocalSubscriberRegistry {

    private final Map<Type/*event type*/, EventMessageConsumer> eventMessageConsumers =
        Maps.newHashMap();
    /**
     * use local cache to lazy load
     */
    private final LoadingCache<Type, ImmutableSet<Method>> subscriberMethodsCache =
        CacheBuilder.newBuilder()
            .weakKeys()
            .build(
                new CacheLoader<Type, ImmutableSet<Method>>() {
                    @Override
                    public ImmutableSet<Method> load(Type type) {
                        return getAnnotatedMethodsNotCached(type);
                    }
                });

    @Override
    public EventMessageConsumer getEventMessageConsumer(Object event) {
        Class<?> eventType = event.getClass();
        return eventMessageConsumers.get(eventType);
    }

    @Override
    public EventMessageConsumer getEventMessageConsumer(Type eventType) {
        return eventMessageConsumers.get(eventType);
    }

    @Override
    public Map<Type, EventMessageConsumer> listAllEventMessageConsumers() {
        return this.eventMessageConsumers;
    }

    @Override
    public void register(Object subscriberHolder) {
        synchronized (this) {
            Multimap<Type, EventSubscriber> allSubscribers = findAllSubscribers(subscriberHolder);
            for (Entry<Type, Collection<EventSubscriber>> entry : allSubscribers.asMap().entrySet()) {
                Type eventType = entry.getKey();
                Collection<EventSubscriber> eventSubscribers = entry.getValue();
                if (eventMessageConsumers.get(eventType) == null) {
                    eventMessageConsumers.put(eventType, new DefaultEventMessageConsumer());
                }
                EventMessageConsumer messageConsumer = eventMessageConsumers.get(eventType);
                for (EventSubscriber eventSubscriber : eventSubscribers) {
                    messageConsumer.registerEventSubscriber(eventSubscriber);
                }
            }
        }
    }

    @Override
    public void unregister(Object subscriberHolder) {
        synchronized (this) {
            Multimap<Type, EventSubscriber> allSubscribers = findAllSubscribers(subscriberHolder);
            for (Entry<Type, Collection<EventSubscriber>> entry : allSubscribers.asMap().entrySet()) {
                Type eventType = entry.getKey();
                Collection<EventSubscriber> eventSubscribers = entry.getValue();
                if (eventMessageConsumers.get(eventType) == null) {
                    continue;
                }
                EventMessageConsumer messageConsumer = eventMessageConsumers.get(eventType);
                for (EventSubscriber eventSubscriber : eventSubscribers) {
                    messageConsumer.unregisterEventSubscriber(eventSubscriber);
                }
            }
        }
    }

    private Multimap<Type, EventSubscriber> findAllSubscribers(Object subscriberHolder) {
        Multimap<Type, EventSubscriber> subscribersInHolder = HashMultimap.create();
        Class<?> clazz = subscriberHolder.getClass();
        for (Method method : getAnnotatedMethods(clazz)) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            Class<?> eventType = parameterTypes[0];
            subscribersInHolder.put(eventType, createDefaultEventSubscriber(subscriberHolder, method));
        }
        return subscribersInHolder;
    }

    private ImmutableSet<Method> getAnnotatedMethods(Type type) {
        return subscriberMethodsCache.getUnchecked(type);
    }

    private ImmutableSet<Method> getAnnotatedMethodsNotCached(Type type) {
        Class<?> clazz = (Class<?>)type;
        Set<Method> methodSet = Sets.newHashSet();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(UniverseSubscriber.class) && !method.isSynthetic()) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                checkArgument(
                    parameterTypes.length == 1,
                    "Method %s has @UniverseSubscriber annotation but has %s parameters. "
                        + "Subscriber methods must have exactly 1 parameter.",
                    method,
                    parameterTypes.length);

                checkArgument(
                    !parameterTypes[0].isPrimitive(),
                    "@UniverseSubscriber method %s's parameter is %s. "
                        + "Subscriber methods cannot accept primitives. "
                        + "Consider changing the parameter to %s.",
                    method,
                    parameterTypes[0].getName(),
                    Primitives.wrap(parameterTypes[0]).getSimpleName());
                methodSet.add(method);
            }
        }
        return ImmutableSet.copyOf(methodSet);
    }
}
