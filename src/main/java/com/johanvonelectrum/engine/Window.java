package com.johanvonelectrum.engine;

import com.johanvonelectrum.engine.config.AppConfig;
import com.johanvonelectrum.engine.io.KeyboardInput;
import com.johanvonelectrum.engine.io.resources.ResourceLoader;
import com.johanvonelectrum.engine.io.resources.Texture;
import com.johanvonelectrum.engine.layers.LayerStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.*;
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

    private int[] x = new int[1], y = new int[1];
    private int[] width = new int[1], height = new int[1];
    public boolean shouldClose;

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

        trySetIcon();

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

    private void trySetIcon() {
        try {
            GLFWImage image = GLFWImage.malloc();
            GLFWImage.Buffer buffer = GLFWImage.malloc(1);
            Texture icon = ResourceLoader.loadResourceTexture("icon/JohanEngine");
            image.set(icon.getWidth(), icon.getHeight(), icon.getBuffer());
            buffer.put(0, image);
            glfwSetWindowIcon(this.id, buffer);
        } catch (Exception e) {
            logger.error(e);
        }
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
        return !(glfwWindowShouldClose(this.id) || shouldClose);
    }

    public void update(float deltaTime, LayerStack layerStack) {
        glfwGetWindowPos(this.id, this.x, this.y);
        glfwGetWindowSize(this.id, this.width, this.height);

        startFrame(layerStack);
        processFrame(deltaTime, layerStack);
        endFrame(layerStack);
    }

    private void startFrame(LayerStack layerStack) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    private void processFrame(float deltaTime, LayerStack layerStack) {
        layerStack.render(deltaTime);
    }

    private void endFrame(LayerStack layerStack) {
        GLFW.glfwSwapBuffers(this.id);
        GLFW.glfwPollEvents();
    }

    public void dispose() {
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

    public long getId() {
        return id;
    }

    public Logger getLogger() {
        return logger;
    }

    public AppConfig getAppConfig() {
        return appConfig;
    }

    public int getX() {
        return x[0];
    }

    public int getY() {
        return y[0];
    }

    public int getWidth() {
        return width[0];
    }

    public int getHeight() {
        return height[0];
    }
}
