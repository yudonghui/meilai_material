package com.rfid.readerdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rfid.reader.Reader;

import java.io.IOException;

public class CpuTestActivity extends AppCompatActivity {
    private TextView tvMessage;
    private Button btnRats;
    private Button btnApdu;
    private Reader reader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_test);
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

        btnRats = (Button) findViewById(R.id.btnRats);
        btnRats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] Ats = new byte[32];
                byte[] AtsLen = new byte[1];
                byte[] errCode = new byte[1];
                int result = reader.Iso14443a_Rats(Ats, AtsLen, errCode);
                if (result != 0) {
                    tvMessage.append("Cpu_Rats Error, errCode=" + String.format("%02X", (byte) errCode[0]) + "\n");
                } else {
                    String strAts = "";
                    for (int i = 0; i < AtsLen[0]; i++) {
                        strAts += String.format("%02X ", Ats[i]);
                    }
                    tvMessage.append("Ats: " + strAts + "\n");
                }
            }
        });

        btnApdu = (Button) findViewById(R.id.btnApdu);
        btnApdu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] wData = {(byte)0x00, (byte)0x84, (byte)0x00, (byte)0x00, (byte)0x08};
                byte[] rData = new byte[64];
                byte[] rLen = new byte[1];
                byte[] errCode = new byte[1];
                int result = reader.Iso14443a_Apdu(wData, (byte) wData.length, rData, rLen, errCode);
                if (result != 0) {
                    tvMessage.append("Cpu_Apdu Error, errCode=" + String.format("%02X", (byte) errCode[0]) + "\n");
                } else {
                    String strAts = "";
                    for (int i = 0; i < rLen[0]; i++) {
                        strAts += String.format("%02X ", rData[i]);
                    }
                    tvMessage.append("Response Data: " + strAts + "\n");
                }
            }
        });

    }
}
