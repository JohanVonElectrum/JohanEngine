package com.johanvonelectrum.engine;

import com.johanvonelectrum.engine.config.AppConfig;
import com.johanvonelectrum.engine.io.KeyboardInput;
import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
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

public abstract class Window implements Runnable {

    public static final int[] DEFAULT_WINDOW_SIZE = new int[] { 1600, 900 };

    private long id;
    private Logger logger;
    protected AppConfig appConfig;

    private final ImGuiImplGlfw implGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 implGl3 = new ImGuiImplGl3();

    protected void init() {
        this.logger = LogManager.getLogger("Window (" + this.appConfig.getTitle() + ")");

        logger.trace(this.appConfig);

        initWindow(this.appConfig);
        initImGui();
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

    protected abstract void initImGui(AppConfig appConfig);

    private void initImGui() {
        logger.info("Initializing ImGui...");

        logger.debug("Creating ImGui context...");
        ImGui.createContext();

        logger.debug("Initializing ImGui interface components...");
        initImGui(this.appConfig);

        logger.debug("Initializing ImGui GLFW implementation...");
        implGlfw.init(this.id, true);
        logger.debug("Initializing ImGui GL3 implementation...");
        implGl3.init();
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

    public void run() {
        while (keepRunning()) {
            startFrame();
            preProcess();
            process();
            postProcess();
            endFrame();
        }
    }

    protected void startFrame() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        implGlfw.newFrame();
        ImGui.newFrame();
    }

    protected abstract void preProcess();

    protected abstract void process();

    protected abstract void postProcess();

    protected void endFrame() {
        ImGui.render();
        implGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupWindowPtr);
        }

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
        logger.debug("Disposing GL3 implementation...");
        implGl3.dispose();
        logger.debug("Disposing GLFW implementation...");
        implGlfw.dispose();
        logger.debug("Disposing ImGui...");
        disposeImGui();
        logger.debug("Disposing window...");
        disposeWindow();
    }

    protected void disposeImGui() {
        logger.trace("Destroying ImGui context...");
        ImGui.destroyContext();
    }

    protected void disposeWindow() {
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
