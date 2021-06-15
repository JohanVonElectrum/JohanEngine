package com.johanvonelectrum.engine;

import com.johanvonelectrum.engine.io.KeyboardInput;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
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
        this.logger = LogManager.getLogger("Window (" + this.title + ")");
        this.width = width;
        this.height = height;

        init();
    }

    private void init() {
        logger.info("Creating the window...");

        logger.debug("Creating error print stream...");
        GLFWErrorCallback.createPrint(System.err).set();

        logger.debug("Initializing glfw...");
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        logger.debug("Setting up window hints...");
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        logger.debug("Creating window...");
        this.id = glfwCreateWindow(width, height, title, NULL, NULL);
        if (this.id == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        logger.debug("Setting up key callback...");
        glfwSetKeyCallback(this.id, (id, key, scancode, action, mods) -> {
            KeyboardInput.handle(this, key, scancode, action, mods);
        });

        tryCenter();

        logger.debug("Making the OpenGL context current...");
        glfwMakeContextCurrent(this.id);

        logger.info("Enabling v-sync...");
        glfwSwapInterval(1);

        logger.debug("Showing the window.");
        glfwShowWindow(this.id);

        logger.debug("Creating the GLCapabilities instance...");
        logger.debug("Enabling OpenGL bindings for use...");
        GL.createCapabilities();

        logger.debug("Setting the clear color...");
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
    }

    private void tryCenter() {
        logger.debug("Trying to center the window in the primary monitor...");
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

    public long getId() {
        return id;
    }

    public Logger getLogger() {
        return logger;
    }

    public String getTitle() {
        return title;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
