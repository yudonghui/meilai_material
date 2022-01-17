package com.rfid.readerdemo;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rfid.reader.Reader;
import java.io.IOException;

public class TypeATestActivity extends Activity {
    private TextView tvMessage;
    private Button btnGetUid;
    private Button btnRead;
    private Button btnWrite;
    private Reader reader;
    private SoundPool soundPool;
    private int loadId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_typea_test);
        initDatas();
        initViews();

        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 100);
        loadId = soundPool.load(TypeATestActivity.this, R.raw.card, 1);
    }

    private void initDatas() {
        try {
            reader = ((Application) getApplication()).getReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initViews() {
        tvMessage = (TextView) findViewById(R.id.tvMessage);

        btnGetUid = (Button) findViewById(R.id.btnGetUid);
        btnGetUid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] uid = new byte[32];
                byte[] uidLen = new byte[1];
                byte[] errCode = new byte[1];
                int result = reader.Iso14443a_GetUid(uid, uidLen, errCode);
                if (result != 0) {
                    tvMessage.append("GetUid Error, errCode=" + String.format("%02X", (byte) errCode[0]) + "\n");
                } else {
                    String strUid = "";
                    for (int i = 0; i < uidLen[0]; i++) {
                        strUid += String.format("%02X ", uid[i]);
                    }
                    tvMessage.append("UID: " + strUid + "\n");
                }
                soundPool.play(loadId , 15, 15, 1, 0, 1);
            }
        });

        btnRead = (Button) findViewById(R.id.btnRead);
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] key = {(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
                byte[] rData = new byte[32];
                byte[] errCode = new byte[1];
                int result = reader.Iso14443a_Read((byte) 0x01, (byte) 0x00, key, rData, errCode);
                if (result != 0) {
                    tvMessage.append("Read Error, errCode=" + String.format("%02X", (byte) errCode[0]) + "\n");
                } else {
                    String strData = "";
                    for (int i = 0; i < 16; i++) {
                        strData += String.format("%02X ", rData[i]);
                    }
                    tvMessage.append("Response Data: " + strData + "\n");
                }
            }
        });

        btnWrite = (Button) findViewById(R.id.btnWrite);
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] key = {(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
                byte[] wData = {
                        (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07, (byte)0x08,
                        (byte)0x09, (byte)0x0A, (byte)0x0B, (byte)0x0C, (byte)0x0D, (byte)0x0E, (byte)0x0F, (byte)0x10};
                byte[] errCode = new byte[1];
                int result = reader.Iso14443a_Write((byte) 0x01, (byte) 0x00, key, wData, errCode);
                if (result != 0) {
                    tvMessage.append("Mifare_Write Error, errCode=" + String.format("%02X", (byte) errCode[0]) + "\n");
                } else {
                    tvMessage.append("Mifare_Write Success\n");
                }
            }
        });
    }
}
