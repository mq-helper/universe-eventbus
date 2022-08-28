package org.mqhelper.eventbus.kafka;

import java.lang.reflect.Type;

/**
 * @author SongyangJi
 * @date 2022/08/28
 */
public class KafkaEventRouter {
    public static String routingToTopic(Object event) {
        Class<?> type = event.getClass();
        return type.getName();
    }

    public static String routingToTopic(Type type) {
        return type.getTypeName();
    }


}
