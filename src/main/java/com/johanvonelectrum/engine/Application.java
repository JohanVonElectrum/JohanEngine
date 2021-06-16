package com.johanvonelectrum.engine;

public abstract class Application extends Window {
    /**
     * Method called before window creation. Could be used to provide basic window information, like title name etc.
     */
    protected void configure() {
    }

    /**
     * Method called once, before application run loop.
     */
    protected void preRun() {
    }

    /**
     * Method called once, after application run loop.
     */
    protected void postRun() {
    }

    /**
     * Entry point of any ImGui application. Use it to start the application loop.
     *
     * @param app application instance to run
     */
    public static void launch(final Application app) {
        JohanEngine.LOGGER.info("Launching JohanEngine application...");

        JohanEngine.LOGGER.debug("Initializing JohanEngine application...");
        initialize(app);
        JohanEngine.LOGGER.debug("Pre-running JohanEngine application...");
        app.preRun();
        JohanEngine.LOGGER.debug("Running JohanEngine application...");
        app.run();
        JohanEngine.LOGGER.debug("Post-running JohanEngine application...");
        app.postRun();
        JohanEngine.LOGGER.debug("Disposing JohanEngine application...");
        app.dispose();
    }

    private static void initialize(final Application app) {
        app.init();
    }
}
