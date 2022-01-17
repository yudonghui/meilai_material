package com.rfid.readerdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rfid.reader.Reader;

import java.io.IOException;

public class Iso15693TestActivity extends Activity {
    private TextView tvMessage;
    private Button btnInventory;
    private Button btnReadBlock;
    private Button btnWriteBlock;
    private Button btnLockBlock;
    private Reader reader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iso15693_test);
        initDatas();
        initViews();
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

        btnInventory = (Button) findViewById(R.id.btnInventory);
        btnInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] numOfCard = new byte[1];
                byte[] flags = new byte[1];
                byte[] dsfid = new byte[1];
                byte[] uid = new byte[32];
                byte[] uidLen = new byte[1];
                byte[] errCode = new byte[1];
                int result = reader.Iso15693_Inventory((byte)0x06, (byte)0x00, numOfCard, flags, dsfid, uid, uidLen, errCode);
                if (result != 0) {
                    tvMessage.append("Inventory Error, errCode=" + String.format("%02X", (byte) errCode[0]) + "\n");
                } else {
                    String strUid = "";
                    for (int i = 0; i < uidLen[0]; i++) {
                        strUid += String.format("%02X ", uid[i]);
                    }
                    tvMessage.append("UID: " + strUid + "\n");
                }
            }
        });

        btnReadBlock = (Button) findViewById(R.id.btnReadBlock);
        btnReadBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] flags = new byte[1];
                byte[] rData = new byte[32];
                byte[] rLen = new byte[1];
                byte[] errCode = new byte[1];
                int result = reader.Iso15693_ReadBlock((byte) 0x42, (byte) 0x01, (byte) 0x01, null, flags, rData, rLen, errCode);
                if (result != 0) {
                    tvMessage.append("ReadBlock Error, errCode=" + String.format("%02X", (byte) errCode[0]) + "\n");
                } else {
                    String strData = "";
                    for (int i = 0; i < rLen[0]; i++) {
                        strData += String.format("%02X ", rData[i]);
                    }
                    tvMessage.append("Response Data: " + strData + "\n");
                }
            }
        });

        btnWriteBlock = (Button) findViewById(R.id.btnWriteBlock);
        btnWriteBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] key = {(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
                byte[] wData = {(byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04};
                byte[] errCode = new byte[1];
                int result = reader.Iso15693_WriteBlock((byte) 0x42, (byte) 0x01, (byte) 0x01, null, wData, (byte) wData.length, errCode);
                if (result != 0) {
                    tvMessage.append("WriteBlock Error, errCode=" + String.format("%02X", (byte) errCode[0]) + "\n");
                } else {
                    tvMessage.append("WriteBlock Success\n");
                }
            }
        });

        btnLockBlock = (Button) findViewById(R.id.btnLockBlock);
        btnLockBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] key = {(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
                byte[] wData = {
                        (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07, (byte)0x08,
                        (byte)0x09, (byte)0x0A, (byte)0x0B, (byte)0x0C, (byte)0x0D, (byte)0x0E, (byte)0x0F, (byte)0x10};
                byte[] errCode = new byte[1];
                int result = reader.Iso15693_LockBlock((byte) 0x42, (byte) 0x05, null, errCode);
                if (result != 0) {
                    tvMessage.append("LockBlock Error, errCode=" + String.format("%02X", (byte) errCode[0]) + "\n");
                } else {
                    tvMessage.append("LockBlock Success\n");
                }
            }
        });

    }
}
