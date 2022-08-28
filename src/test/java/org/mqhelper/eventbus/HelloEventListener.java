package org.mqhelper.eventbus;

/**
 * @author SongyangJi
 * @date 2022/08/28
 */
public class HelloEventListener {

    @UniverseSubscriber
    public void on(HelloEvent event) {
        System.out.println("process HelloEvent" + event);
    }
}
