package org.mqhelper.eventbus;

import java.lang.reflect.Type;

import com.alibaba.fastjson2.JSON;

/**
 * @author SongyangJi
 * @date 2022/08/25
 */
public class BaseEventMessage {
    // TODO
    /*other fields*/

    private String eventPayload;

    public BaseEventMessage() {
    }

    public BaseEventMessage(String eventPayload) {
        this.eventPayload = eventPayload;
    }

    public Object extractEvent(Type type) {
        return JSON.parseObject(this.eventPayload, type);
    }

    public String getEventPayload() {
        return eventPayload;
    }

    public void setEventPayload(String eventPayload) {
        this.eventPayload = eventPayload;
    }

    @Override
    public String toString() {
        return "BaseEventMessage{" +
            "eventPayload='" + eventPayload + '\'' +
            '}';
    }
}
