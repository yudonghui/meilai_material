package com.pl.testscan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnScan ;
    private Button btnClear ;
    private EditText editData ;
    private CheckBox chAuto ;
    private Handler handler = new Handler() ;


    private Timer timer ;

    private ScanReader scanReader ;
    //接收返回数据
    private BroadcastReceiver resultReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            byte[] barcode = intent.getByteArrayExtra(ScanReader.SCAN_RESULT);
            Log.e("MainActivity", "barcode = " + new String(barcode)) ;
            if (barcode != null) {
                editData.append(new String(barcode));
                editData.append("\n");
                String test = editData.getText().toString() ;
                if(test.length() > 2000){
                    editData.setText("");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnScan = findViewById(R.id.button_scan) ;
        btnClear = findViewById(R.id.button_clear) ;
        editData = findViewById(R.id.editText)  ;
        chAuto = findViewById(R.id.checkBox_auto) ;

        btnScan.setOnClickListener(this);
        btnClear.setOnClickListener(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ScanReader.ACTION_SCAN_RESULT);
        registerReceiver(resultReceiver, filter);

        scanReader = new ScanReader(this);
        scanReader.init();

        chAuto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    timer = new Timer() ;
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            scanReader.startScan();
                        }
                    }, 10, 200);
                }else{
                    if(timer != null){
                        timer.cancel();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(resultReceiver);
    }

    long exitTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(System.currentTimeMillis() - exitTime < 2000){
                if(timer != null){
                    timer.cancel();
                }
                scanReader.closeScan();
                finish();
            }else{
                exitTime = System.currentTimeMillis() ;
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                return false ;
            }
        }

        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_scan:
                scanReader.startScan();
                break ;

            case R.id.button_clear:
                editData.setText("");
                break ;
        }
    }
}
