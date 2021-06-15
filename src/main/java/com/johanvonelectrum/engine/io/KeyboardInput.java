package com.johanvonelectrum.engine.io;

import com.johanvonelectrum.engine.Window;

import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;

public class KeyboardInput {

    public static void handle(Window window, int key, int scancode, int action, int mods) {
        if (action != GLFW_REPEAT)
            window.getLogger().info(key + ": " + action);
    }
}
