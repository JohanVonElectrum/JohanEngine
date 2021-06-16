package com.johanvonelectrum.engine.config;

import picocli.CommandLine;

public class AppConfig {

    @CommandLine.Option(names = { "-t", "--title" }, required = true, description = "Start in server mode.")
    private String title = null;

    @CommandLine.Option(names = { "-d", "--debug" }, description = "Enables ALL logs.")
    private boolean debug = false;

    @CommandLine.Option(names = { "-s", "--server" }, description = "Start in server mode.")
    private boolean server = false;

    @Override
    public String toString() {
        return "AppConfig{" +
                "title='" + title + '\'' +
                ", debug=" + debug +
                ", server=" + server +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isServer() {
        return server;
    }
}
