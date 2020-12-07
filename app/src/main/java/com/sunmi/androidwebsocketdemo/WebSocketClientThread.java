package com.sunmi.androidwebsocketdemo;

public class WebSocketClientThread extends Thread{

    private static final String TAG = "WebClient";

    public static volatile Thread self;

    @Override
    public synchronized void start() {
        if (self != null) {
            return;
        }
        super.start();
        self = this;
    }

    public synchronized void stopSelf() {
        if (self != null) {
            self.interrupt();
            self = null;
        }
    }

    @Override
    public void run() {
        super.run();

        LogUtil.e(TAG, "run() start");

        while (!isInterrupted()) {

        }

        self = null;
        LogUtil.e(TAG, "run() end");

    }

}
