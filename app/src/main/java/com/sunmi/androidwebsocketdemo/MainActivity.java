package com.sunmi.androidwebsocketdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        getPermission();
    }


    private void getPermission(){
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
        if(serverThread.self != null){
            serverThread.send("ok I understand, there is nothing I can do with it".getBytes());
            return;
        }
        if(clientThread.self != null){
            clientThread.send("this is my september again".getBytes());
            return;
        }
        uiLog("no socket ready");
    }

    private void clickClient() {
        clientThread.start();
    }

    private void clickServer(){
        serverThread.start();
    }

}