package com.johanvonelectrum.engine;

import com.johanvonelectrum.engine.config.AppConfig;
import com.johanvonelectrum.engine.events.EventSystem;
import com.johanvonelectrum.engine.layers.ImGuiLayer;
import com.johanvonelectrum.engine.layers.LayerStack;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import picocli.CommandLine;

@CommandLine.Command(name = "", mixinStandardHelpOptions = true, version = "", description = "")
public class JohanEngine extends Application {

    public static final Logger LOGGER = LogManager.getLogger("Core");
    private static Window window;

    private final EventSystem eventSystem = new EventSystem();
    private final LayerStack layerStack = new LayerStack();

    protected JohanEngine(AppConfig appConfig) {
        super(appConfig);
    }

    private static void setDebugMode(boolean debug) {
        if (debug) {
            Configurator.setRootLevel(Level.ALL);
            LOGGER.debug("Debug mode is enabled.");
        } else {
            Configurator.setRootLevel(Level.INFO);
            LOGGER.warn("Debug mode is disabled.");
        }
    }

    @Override
    protected void init() {
        window = new Window(this.appConfig);
    }

    @Override
    protected void preRun() {
        layerStack.add(new ImGuiLayer("ImGuiLayer").init());
    }

    @Override
    public void run() {
        while (window.keepRunning()) {
            window.update(layerStack);
            layerStack.handle(eventSystem);
        }
    }

    @Override
    protected void postRun() {

    }

    @Override
    protected void dispose() {
        layerStack.dispose();
        eventSystem.dispose();
        window.dispose();
    }

    public static Window getWindow() {
        return window;
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
