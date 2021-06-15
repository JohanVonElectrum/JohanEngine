package com.johanvonelectrum.engine;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import picocli.CommandLine;

@CommandLine.Command(name = "", mixinStandardHelpOptions = true, version = "", description = "")
public class JohanEngine implements Runnable {

    @CommandLine.Option(names = { "-d", "--debug" }, description = "Enables ALL logs.")
    private boolean debug = false;

    @CommandLine.Option(names = { "-s", "--server" }, description = "Start in server mode.")
    private boolean server = false;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new JohanEngine()).execute(args);
        System.exit(exitCode);
    }

    private static void setDebugMode(boolean debug) {
        Configurator.setRootLevel(debug ? Level.ALL : Level.INFO);
    }

    @Override
    public void run() {
        setDebugMode(this.debug);

        Window window = new Window("JohanEngine", 1600, 900);

        while (window.keepRunning()) {
            window.render();
        }
    }
}
