package com.johanvonelectrum.engine.events;

public class Event {

    private boolean handled = false;

    public boolean isHandled() {
        return handled;
    }

    public void setHandled() {
        this.handled = true;
    }
}
