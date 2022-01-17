package com.rfid.readerdemo;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceActivity;
import android.widget.TextView;

import com.rfid.reader.Reader;

import java.io.IOException;

/**
 * Created by Administrator on 2017-8-26.
 */
public class AutoReadActivity extends Activity {
    private TextView tvMessage;
    private Reader reader;
    private SoundPool soundPool;
    private int loadId;
    private boolean isRunning = true;
    private String message = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autoread_test);
        initDatas();
        initViews();

        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 100);
        loadId = soundPool.load(AutoReadActivity.this, R.raw.card, 1);
    }

    private void initViews() {
        tvMessage = (TextView) findViewById(R.id.tvMessage);
    }

    private void initDatas() {
        try {
            reader = ((Application) getApplication()).getReader();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new ReadThread().start();
    }

    @Override
    protected void onDestroy() {
        isRunning = false;
        super.onDestroy();
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            while (isRunning) {
                byte[] uid = new byte[32];
                byte[] uidLen = new byte[1];
                byte[] errCode = new byte[1];
                int result = reader.Iso14443a_GetUid(uid, uidLen, errCode);
                if (result != 0) {
                    message = "GetUid Error, errCode=" + String.format("%02X", (byte) errCode[0]) + "\n";
                } else {
                    String strUid = "";
                    for (int i = 0; i < uidLen[0]; i++) {
                        strUid += String.format("%02X ", uid[i]);
                    }
                    message = "UID: " + strUid + "\n";
                    soundPool.play(loadId , 15, 15, 1, 0, 1);
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                }

                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                tvMessage.append(message);
            }
        }
    };

}
