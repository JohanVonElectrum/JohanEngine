package com.johanvonelectrum.engine.layers;

import com.johanvonelectrum.engine.events.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Layer {

    protected final Logger logger;

    public abstract Layer init();
    public abstract void begin();
    public abstract void render(float deltaTime);
    public abstract void end();
    public abstract void onEvent(Event event);
    public abstract void dispose();

    public Layer(String name) {
        logger = LogManager.getLogger(name);
    }
}
