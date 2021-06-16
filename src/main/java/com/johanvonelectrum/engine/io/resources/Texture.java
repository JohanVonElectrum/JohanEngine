package com.johanvonelectrum.engine.io.resources;

import java.nio.ByteBuffer;

public class Texture {

    private final ByteBuffer buffer;
    private final int width, height, channels;

    public Texture(ByteBuffer buffer, int width, int height, int channels) {
        this.buffer = buffer;
        this.width = width;
        this.height = height;
        this.channels = channels;
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getChannels() {
        return channels;
    }
}
