package com.johanvonelectrum.engine.events;

import java.util.LinkedList;
import java.util.Queue;

public class EventSystem {

    private final Queue<Event> eventBus = new LinkedList<>();

    public void push(Event event) {
        eventBus.add(event);
    }

    public Event pop() {
        return eventBus.poll();
    }

    public boolean isEmpty() {
        return eventBus.isEmpty();
    }
}
