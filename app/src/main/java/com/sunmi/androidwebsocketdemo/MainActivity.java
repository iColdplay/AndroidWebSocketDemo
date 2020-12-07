package com.sunmi.androidwebsocketdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    // ui log
    private TextView tvLog;
    private static final int MAX_LENGTH = 4096 * 2;
    private static final StringBuilder stringBuilderLog = new StringBuilder();

    private void uiLog(String content) {
        LogUtil.e(TAG, content);
        stringBuilderLog.insert(0, DateFormat.format("hh-mm-ss", new Date()).toString() + ": " + content + "\n");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (stringBuilderLog.length() > MAX_LENGTH) {
                    int totalLength = stringBuilderLog.length();
                    stringBuilderLog.delete(MAX_LENGTH, totalLength);
                }
                tvLog.setText(stringBuilderLog.toString());
            }
        });
    }

    private WebSocketServerThread serverThread;
    private WebSocketClientThread clientThread;

    private boolean isServer = false;
    private WebSocketClient client;
    private TestServer testServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        getPermission();
    }


    private void getPermission() {
        ArrayList<String> requestPermissionArr = new ArrayList<>();

        int p1 = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        if (p1 != PackageManager.PERMISSION_GRANTED) {
            requestPermissionArr.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        int p2 = checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE);
        if (p2 != PackageManager.PERMISSION_GRANTED) {
            requestPermissionArr.add(Manifest.permission.ACCESS_WIFI_STATE);
        }

        int p3 = checkSelfPermission(Manifest.permission.INTERNET);
        if (p3 != PackageManager.PERMISSION_GRANTED) {
            requestPermissionArr.add(Manifest.permission.INTERNET);
        }

        int p4 = checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE);
        if (p4 != PackageManager.PERMISSION_GRANTED) {
            requestPermissionArr.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }

        if (requestPermissionArr.size() >= 1) {
            String[] requestArray = new String[requestPermissionArr.size()];
            for (int i = 0; i < requestArray.length; i++) {
                requestArray[i] = requestPermissionArr.get(i);
            }
            requestPermissions(requestArray, 99);
        }

    }

    private void initView() {
        tvLog = findViewById(R.id.tv_log);
        Button btnServer = findViewById(R.id.server);
        btnServer.setOnClickListener(this);
        Button btnClient = findViewById(R.id.client);
        btnClient.setOnClickListener(this);
        Button btnSend = findViewById(R.id.send);
        btnSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        final int id = v.getId();
        switch (id) {
            case R.id.server:
                clickServer();
                break;
            case R.id.client:
                clickClient();
                break;
            case R.id.send:
                clickSend();
                break;
        }
    }

    private void clickSend() {
//        if(serverThread.self != null){
//            serverThread.send("ok I understand, there is nothing I can do with it".getBytes());
//            return;
//        }
//        if(clientThread.self != null){
//            clientThread.send("this is my september again".getBytes());
//            return;
//        }
//        uiLog("no socket ready");
        if(isServer){
            testServer.sendMessage(null, null);
        }else {
            client.send("I wanna something just like this");
        }
    }

    private void clickClient() {
//        clientThread.start();
        try {
            client = new WebSocketClient(new URI("ws://192.168.3.13:11011")) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    uiLog("onOpen");
                }

                @Override
                public void onMessage(String message) {
                    uiLog("onMessage: " + message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    uiLog("onClose, code-->" + code + " reason-->" + reason);
                }

                @Override
                public void onError(Exception ex) {
                    uiLog("onError: " + ex.toString());
                }
            };
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        isServer = false;
    }


    private void clickServer() {
//        serverThread.start();
        // 192.168.3.13
        testServer = new TestServer(11011);
        testServer.start();
        isServer = true;
    }

    public class TestServer extends WebSocketServer {

        private TestServer serverSocket;

        private WebSocket webSocketTheOne;

        private static final String TAG = "TestServer";

        public TestServer(int port) {
            super(new InetSocketAddress(port));
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            //开始连接
            uiLog("onOpen");
            webSocketTheOne = conn;
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            //服务器关闭
            uiLog("onClose");
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            //接收消息，做逻辑处理，这里我直接重新返回消息
            uiLog("onMessage: " + message);
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            //异常
            uiLog("onError: " + ex.toString());
        }

        @Override
        public void onStart() {
            uiLog("onStart");
        }

        public void sendMessage(WebSocket socket, String message) {
            webSocketTheOne.send("She said where do you wanna go?");
        }

    }

}