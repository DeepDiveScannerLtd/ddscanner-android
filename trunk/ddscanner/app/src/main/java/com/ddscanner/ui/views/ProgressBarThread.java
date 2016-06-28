package com.ddscanner.ui.views;

import android.graphics.Canvas;
import android.view.View;

/**
 * Created by lashket on 28.6.16.
 */
public class ProgressBarThread extends Thread {

    static final long FPS = 60;
    private DDProgressBarView view;
    private boolean running = false;

    public ProgressBarThread(DDProgressBarView view) {
        this.view = view;
    }

    public void setRunning(boolean run) {
        running = run;
    }

    @Override
    public void run() {
        long ticksPS = 1000 / FPS;
        long startTime;
        long sleepTime;
        while (running) {
            Canvas c = null;
            startTime = System.currentTimeMillis();
//            try {
//                c = view.getHolder().lockCanvas();
//                synchronized (view.getHolder()) {
//                    view.onDraw(c);
//                }
//            } finally {
//                if (c != null) {
//                    view.getHolder().unlockCanvasAndPost(c);
//                }
//            }
            sleepTime = ticksPS-(System.currentTimeMillis() - startTime);
            try {
                if (sleepTime > 0)
                    sleep(sleepTime);
                else
                    sleep(10);
            } catch (Exception e) {}
        }
    }

}
