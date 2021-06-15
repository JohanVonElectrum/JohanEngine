package com.johanvonelectrum.engine;

public class JohanEngine implements Runnable {

    public static void main(String[] args) {
        Thread engineThread = new Thread(new JohanEngine());
        engineThread.start();
    }

    @Override
    public void run() {
        Window window = new Window("JohanEngine", 300, 300);

        while (window.keepRunning()) {
            window.render();
        }
    }
}
