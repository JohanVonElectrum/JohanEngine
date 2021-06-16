package com.johanvonelectrum.engine;

import com.johanvonelectrum.engine.config.AppConfig;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiInputTextFlags;
import imgui.type.ImString;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import picocli.CommandLine;

@CommandLine.Command(name = "", mixinStandardHelpOptions = true, version = "", description = "")
public class JohanEngine extends Application {

    public static final Logger LOGGER = LogManager.getLogger("Core");

    public JohanEngine(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    private static void setDebugMode(boolean debug) {
        if (debug) {
            LOGGER.info("Debug mode enabled.");
            Configurator.setRootLevel(Level.ALL);
        } else {
            LOGGER.info("Debug mode disabled.");
            Configurator.setRootLevel(Level.INFO);
        }
    }

    @Override
    protected void initImGui(final AppConfig config) {
        final ImGuiIO io = ImGui.getIO();
        io.setIniFilename(null);                                // We don't want to save .ini file
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);  // Enable Keyboard Controls
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);      // Enable Docking
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);    // Enable Multi-Viewport / Platform Windows
        io.setConfigViewportsNoTaskBarIcon(true);

//        initFonts(io);
    }

    @Override
    protected void preProcess() {

    }

    ImString str = new ImString(5);
    float[] flt = new float[1];
    @Override
    protected void process() {
        ImGui.text("Inspector");
        if (ImGui.button("Close")) {
            LOGGER.debug("Close button pressed...");
        }
        ImGui.sameLine();
        ImGui.text("Info panel");
        ImGui.inputText("name", str, ImGuiInputTextFlags.CallbackResize);
        ImGui.text("Result: " + str.get());
        ImGui.sliderFloat("slider", flt, 0, 1);
        ImGui.separator();
        ImGui.text("description");
    }

    @Override
    protected void postProcess() {

    }

    public static void main(String[] args) {
        Configurator.setRootLevel(Level.ALL);
        LOGGER.info("Starting JohanEngine...");
        AppConfig appConfig = new AppConfig();
        try {
            LOGGER.debug("Parsing CLI args...");
            new CommandLine(appConfig).parseArgs(args);
            setDebugMode(appConfig.isDebug());
            launch(new JohanEngine(appConfig));
            LOGGER.info("Closing as expected...");
            System.exit(0);
        } catch (CommandLine.MissingParameterException exception) {
            LOGGER.fatal(exception);
            System.exit(1);
        }
    }
}
