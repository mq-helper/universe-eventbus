package org.mqhelper.eventbus;

/**
 * @author SongyangJi
 * @date 2022/08/28
 */
public class HelloEvent {
    private String greeting;

    public HelloEvent() {
    }

    public HelloEvent(String greeting) {
        this.greeting = greeting;
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    @Override
    public String toString() {
        return "HelloEvent{" +
            "greeting='" + greeting + '\'' +
            '}';
    }
}
