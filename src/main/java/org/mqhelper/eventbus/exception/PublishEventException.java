package org.mqhelper.eventbus.exception;

/**
 * @author SongyangJi
 * @date 2022/08/28
 */
public class PublishEventException extends RuntimeException {
    public PublishEventException(String message) {
        super(message);
    }
}
