package com.rfid.readerdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rfid.reader.Reader;

import java.io.IOException;

public class TypeBTestActivity extends Activity {
    private TextView tvMessage;
    private Button btnGetUid;
    private Reader reader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_typeb_test);
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

        btnGetUid = (Button) findViewById(R.id.btnGetUid);
        btnGetUid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] uid = new byte[32];
                byte[] uidLen = new byte[1];
                byte[] errCode = new byte[1];
                int result = reader.Iso14443b_GetUid(uid, uidLen, errCode);
                if (result != 0) {
                    tvMessage.append("GetUid Error, errCode=" + String.format("%02X", (byte) errCode[0]) + "\n");
                } else {
                    String strUid = "";
                    for (int i = 0; i < uidLen[0]; i++) {
                        strUid += String.format("%02X ", uid[i]);
                    }
                    tvMessage.append("UID: " + strUid + "\n");
                }
            }
        });

    }
}
