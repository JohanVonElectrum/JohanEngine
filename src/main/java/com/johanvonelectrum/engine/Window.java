package com.johanvonelectrum.engine;

import com.johanvonelectrum.engine.config.AppConfig;
import com.johanvonelectrum.engine.io.KeyboardInput;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    public static final int[] DEFAULT_WINDOW_SIZE = new int[] { 1600, 900 };

    private long id;
    private Logger logger;
    private AppConfig appConfig;

    public Window(AppConfig appConfig) {
        this.appConfig = appConfig;
        this.logger = LogManager.getLogger("Window (" + this.appConfig.getTitle() + ")");

        logger.trace(this.appConfig);

        initWindow(this.appConfig);
    }

    protected void initWindow(AppConfig config) {
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
        this.id = glfwCreateWindow(DEFAULT_WINDOW_SIZE[0], DEFAULT_WINDOW_SIZE[1], this.appConfig.getTitle(), NULL, NULL);
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

        logger.debug("Showing the window...");
        glfwShowWindow(this.id);

        logger.debug("Creating the GLCapabilities instance...");
        logger.debug("Enabling OpenGL bindings for use...");
        GL.createCapabilities();

        logger.debug("Setting the clear color...");
        glClearColor(0.05f, 0.05f, 0.05f, 0.0f);

        logger.info("Window created.");
    }

    protected void tryCenter() {
        logger.trace("Trying to center the window in the primary monitor...");
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

    public void update() {
        startFrame();
        processFrame();
        endFrame();
    }

    private void startFrame() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    private void processFrame() {

    }

    private void endFrame() {
        GLFW.glfwSwapBuffers(this.id);
        GLFW.glfwPollEvents();
    }

    public long getId() {
        return id;
    }

    public Logger getLogger() {
        return logger;
    }

    public AppConfig getAppConfig() {
        return appConfig;
    }

    protected void dispose() {
        logger.debug("Disposing window...");

        logger.trace("Freeing GLFW callbacks...");
        Callbacks.glfwFreeCallbacks(this.id);
        logger.trace("Destroying GLFW window...");
        GLFW.glfwDestroyWindow(this.id);
        logger.trace("Terminating GLFW...");
        GLFW.glfwTerminate();
        logger.trace("Freeing GLFW error callback...");
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }
}
