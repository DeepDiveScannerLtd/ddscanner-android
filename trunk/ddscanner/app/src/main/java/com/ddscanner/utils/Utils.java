package com.ddscanner.utils;

public class Utils {

    /**
     * Stops the given thread
     *
     * @param thread thread to stop
     */
    public static void stopThread(Thread thread) {
        boolean retry = true;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
}
