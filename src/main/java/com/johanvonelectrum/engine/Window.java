package com.johanvonelectrum.engine;

import com.johanvonelectrum.engine.io.KeyboardInput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    private long id;
    private Logger logger;
    private String title;
    private int width, height;

    public Window(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;

        init();

        this.logger = LogManager.getLogger("[Window (" + this.id + ")]");
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        this.id = glfwCreateWindow(width, height, title, NULL, NULL);
        if (this.id == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        glfwSetKeyCallback(this.id, KeyboardInput::handle);

        tryCenter();

        glfwMakeContextCurrent(this.id);

        glfwSwapInterval(1);

        glfwShowWindow(this.id);

        GL.createCapabilities();

        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
    }

    private void tryCenter() {
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(this.id, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                    this.id,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }
    }

    public boolean keepRunning() {
        return !glfwWindowShouldClose(this.id);
    }

    public void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

        glfwSwapBuffers(this.id); // swap the color buffers

        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();
    }
}
