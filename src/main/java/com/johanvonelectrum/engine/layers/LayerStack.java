package com.johanvonelectrum.engine.layers;

import com.johanvonelectrum.engine.events.Event;
import com.johanvonelectrum.engine.events.EventSystem;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class LayerStack {

    private List<Layer> layers = new LinkedList<>();

    public void add(Layer layer) {
        layers.add(layer);
    }

    public void init() {
        for (Layer layer: layers) {
            layer.init();
        }
    }

    public void handle(EventSystem eventSystem) {
        while (!eventSystem.isEmpty())
            onEvent(eventSystem.pop());
    }

    private void onEvent(Event event) {
        for (Layer layer : layers) {
            if (event.isHandled())
                break;

            layer.onEvent(event);
        }
    }

    public void render(float deltaTime) {
        ListIterator<Layer> layerStack = layers.listIterator(layers.size());
        while (layerStack.hasPrevious()) {
            Layer layer = layerStack.previous();
            layer.begin();
            layer.render(deltaTime);
            layer.end();
        }
    }

    public void dispose() {
        for (Layer layer: layers) {
            layer.dispose();
        }
    }

}
