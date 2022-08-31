package org.mqhelper.eventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author SongyangJi
 * @date 2022/08/25
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface UniverseSubscriber {
    /**
     * @return event有多个subscriber时的处理顺序
     */
    int order() default 0;

    /**
     * @return 失败是否重试
     */
    boolean retryIfFail() default false;

    Class<? extends Exception> exceptionWhenRetry() default Exception.class;

}
