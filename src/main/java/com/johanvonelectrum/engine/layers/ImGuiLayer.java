package com.johanvonelectrum.engine.layers;

import com.johanvonelectrum.engine.JohanEngine;
import com.johanvonelectrum.engine.events.Event;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImString;
import org.lwjgl.glfw.GLFW;

public class ImGuiLayer extends Layer {

    private final ImGuiImplGlfw implGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 implGl3 = new ImGuiImplGl3();

    ImString str = new ImString(5);
    float[] flt = new float[1];

    public ImGuiLayer(String name) {
        super(name);
    }

    @Override
    public Layer init() {
        logger.info("Initializing ImGui...");

        logger.debug("Creating ImGui context...");
        ImGui.createContext();

        logger.debug("Initializing ImGui interface components...");
        final ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);                                // We don't want to save .ini file
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);  // Enable Keyboard Controls
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);      // Enable Docking
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);    // Enable Multi-Viewport / Platform Windows
        io.setConfigViewportsNoTaskBarIcon(true);

//        initFonts(io);

        logger.debug("Initializing ImGui GLFW implementation...");
        implGlfw.init(JohanEngine.getWindow().getId(), true);
        logger.debug("Initializing ImGui GL3 implementation...");
        implGl3.init();

        return this;
    }

    @Override
    public void begin() {
        implGlfw.newFrame();
        ImGui.newFrame();

        setupDockSpace();
    }

    @Override
    public void render(float deltaTime) {
        ImGui.begin("Inspector");

        if (ImGui.button("Close")) {
            JohanEngine.LOGGER.debug("Close button pressed...");
        }
        ImGui.sameLine();
        ImGui.text("Info panel");
        ImGui.inputText("name", str, ImGuiInputTextFlags.CallbackResize);
        ImGui.text("Result: " + str.get());
        ImGui.sliderFloat("slider", flt, 0, 1);
        ImGui.separator();
        ImGui.text("Game metrics");
        ImGui.text("Frame time: " + deltaTime);
        ImGui.text("FPS: " + 1 / deltaTime);

        ImGui.end();
    }

    @Override
    public void end() {
        ImGui.render();
        implGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupWindowPtr);
        }
    }

    @Override
    public void onEvent(Event event) {
        event.setHandled();
    }

    @Override
    public void dispose() {
        logger.debug("Disposing GL3 implementation...");
        implGl3.dispose();
        logger.debug("Disposing GLFW implementation...");
        implGlfw.dispose();
        logger.debug("Disposing ImGui...");
        logger.trace("Destroying ImGui context...");
        ImGui.destroyContext();
    }

    private void setupDockSpace() {
        ImGui.setNextWindowPos(JohanEngine.getWindow().getX(), JohanEngine.getWindow().getY(), ImGuiCond.Always);
        ImGui.setNextWindowSize(JohanEngine.getWindow().getWidth(), JohanEngine.getWindow().getHeight());
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);

        ImGui.begin("Dockspace Demo", ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus);
        ImGui.popStyleVar(3);

        // Dockspace
        ImGui.dockSpace(ImGui.getID("Dockspace"));

        ImGui.beginMainMenuBar();

        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("Close", "Alt+F4"))
                JohanEngine.getWindow().shouldClose = true;
            ImGui.endMenu();
        }

        ImGui.endMainMenuBar();

        ImGui.end();
    }
}
