package com.rfid.readerdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button btnUartSettings;
    private Button btnTypeATest;
    private Button btnTypeBTest;
    private Button btnCpuTest;
    private Button btnIso15693Test;
    private Button btnAutoReadTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews() {
        btnUartSettings = (Button) findViewById(R.id.btnUartSettings);
        btnUartSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SerialPortPreferences.class));
            }
        });

        btnTypeATest = (Button) findViewById(R.id.btnTypeATest);
        btnTypeATest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TypeATestActivity.class));
            }
        });

        btnTypeBTest = (Button) findViewById(R.id.btnTypeBTest);
        btnTypeBTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TypeBTestActivity.class));
            }
        });

        btnCpuTest = (Button) findViewById(R.id.btnCpuTest);
        btnCpuTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CpuTestActivity.class));
            }
        });

        btnIso15693Test = (Button) findViewById(R.id.btnIso15693Test);
        btnIso15693Test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Iso15693TestActivity.class));
            }
        });

        btnAutoReadTest = (Button) findViewById(R.id.btnAutoReadTest);
        btnAutoReadTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AutoReadActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((Application) getApplication()).close();
        System.exit(0);
    }
}
