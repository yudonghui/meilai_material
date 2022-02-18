package com.example.rfidmod;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

//串口
import android_serialport_api.ComBean;
import android_serialport_api.SerialPortFinder;
import android_serialport_api.SerialHelper;
import android_serialport_api.MyFunc;
import android_serialport_api.MyDes;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import java.util.HashMap;
import java.util.Map;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.View.OnClickListener;

import java.io.File;
import java.io.FileInputStream;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    Spinner SpinnerCOMA;
    Spinner SpinnerBaudRateCOMA;
    ToggleButton toggleButtonCOMA;
    Button ButtonClear,btnSendCMD,btnGetInfo,btnReboot,btnWorkMode;
    EditText editTextRecDisp,editTextCOMA;

    Spinner spinnerSector,spinnerBlock,spinnerKeyGroup,spinnerWorkMode;
    EditText editTextKey;
    Button btnReqA,btnAntiColl,btnSelect,btnAnthKey,btnReadBlock,btnWriteBlock,btnHaltA;
    EditText editApduCmd;
    Button btnReqAntiSel,btnRats,btnApduSend;
    Button btnOpenFile,btnUpdata;
    ProgressBar barUpdata;

    SerialControl serialCom;//串口
    DispQueueThread DispQueue;//刷新显示线程
    SimpleDateFormat m_sdfDate = new SimpleDateFormat("HH:mm:ss ");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); // 国际化标志时间格式类

    SerialPortFinder mSerialPortFinder;//串口设备搜索

    private byte[] gcardnum = new byte[4];  //防冲突的时候缓存卡片uid

    private byte[] recvdata = new byte[1500];   //发送接收缓存
    private int recvlen=0;

    private String PASSWORD_CRYPT_KEY = new String("1234567812345679");
    private String PASSWORD_IV = new String("00000000");
    private String str = new String("asas534534534");

    static private int openfileDialogId = 0;
    static private String openfilepath;

    static public int currentbar=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //DES加解密测试
        Log.d(TAG, "KEY:"+MyFunc.ByteArrToHex(PASSWORD_CRYPT_KEY.getBytes()));
        Log.d(TAG, "IV:"+MyFunc.ByteArrToHex(PASSWORD_IV.getBytes()));
        Log.d(TAG, "DATA:"+MyFunc.ByteArrToHex(str.getBytes()));

        byte[] encdata = MyDes.ecbEncrypt(PASSWORD_CRYPT_KEY.getBytes(), str.getBytes());
        Log.d(TAG, "ecbEncrypt:"+MyFunc.ByteArrToHex(encdata));
        byte[] decdata = MyDes.ecbDecrypt(PASSWORD_CRYPT_KEY.getBytes(), encdata);
        Log.d(TAG, "ecbDecrypt:"+MyFunc.ByteArrToHex(decdata));

        byte[] cbcencdata = MyDes.cbcEncrypt(PASSWORD_CRYPT_KEY.getBytes(), PASSWORD_IV.getBytes(), str.getBytes());
        Log.d(TAG, "cbcEncrypt:"+MyFunc.ByteArrToHex(cbcencdata));
        byte[] cbcdecdata = MyDes.cbcDecrypt(PASSWORD_CRYPT_KEY.getBytes(), PASSWORD_IV.getBytes(), cbcencdata);
        Log.d(TAG, "cbcDecrypt:"+MyFunc.ByteArrToHex(cbcdecdata));

        initView();
        initReadCardView();
        initApduView();
        DispQueue = new DispQueueThread();
        DispQueue.start();

    }

    @Override
    public void onDestroy(){
        CloseComPort(serialCom);
        super.onDestroy();
    }

    private void initReadCardView() {
        try{
            spinnerSector=(Spinner)findViewById(R.id.spinnerSector);
            spinnerBlock=(Spinner)findViewById(R.id.spinnerBlock);
            spinnerKeyGroup=(Spinner)findViewById(R.id.spinnerKeyGroup);

            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.sector_name,
                    android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSector.setAdapter(adapter);
            spinnerSector.setSelection(0);

            adapter = ArrayAdapter.createFromResource(this,
                    R.array.block_name,
                    android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerBlock.setAdapter(adapter);
            spinnerBlock.setSelection(0);

            adapter = ArrayAdapter.createFromResource(this,
                    R.array.keygroup_name,
                    android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerKeyGroup.setAdapter(adapter);
            spinnerKeyGroup.setSelection(0);

            editTextKey=(EditText)findViewById(R.id.editTextKey);
            editTextKey.setText("FF FF FF FF FF FF");

            btnReqA=(Button)findViewById(R.id.btnReqA);
            btnAntiColl=(Button)findViewById(R.id.btnAntiColl);
            btnSelect=(Button)findViewById(R.id.btnSelect);
            btnAnthKey =(Button)findViewById(R.id.btnAnthKey);
            btnReadBlock =(Button)findViewById(R.id.btnReadBlock);
            btnWriteBlock =(Button)findViewById(R.id.btnWriteBlock);
            btnHaltA =(Button)findViewById(R.id.btnHaltA);

            btnReqA.setOnClickListener(new ButtonClickEvent());
            btnAntiColl.setOnClickListener(new ButtonClickEvent());
            btnSelect.setOnClickListener(new ButtonClickEvent());
            btnAnthKey.setOnClickListener(new ButtonClickEvent());
            btnReadBlock.setOnClickListener(new ButtonClickEvent());
            btnWriteBlock.setOnClickListener(new ButtonClickEvent());
            btnHaltA.setOnClickListener(new ButtonClickEvent());
        }
        catch(Exception e)
        {
            Log.d(TAG,"setControls:"+e.getMessage());
        }
    }

    private void initView() {
        try{
            serialCom = new SerialControl();

            SpinnerCOMA=(Spinner)findViewById(R.id.SpinnerCOMA);
            SpinnerBaudRateCOMA=(Spinner)findViewById(R.id.SpinnerBaudRateCOMA);
            spinnerWorkMode=(Spinner)findViewById(R.id.spinnerWorkMode);

            mSerialPortFinder= new SerialPortFinder();
            String[] entryValues = mSerialPortFinder.getAllDevicesPath();
            List<String> allDevices = new ArrayList<String>();
            for (int i = 0; i < entryValues.length; i++) {
                allDevices.add(entryValues[i]);
            }
            ArrayAdapter<String> aspnDevices = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, allDevices);
            aspnDevices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            SpinnerCOMA.setAdapter(aspnDevices);
            if (allDevices.size()>1)
            {
                SpinnerCOMA.setSelection(0);
            }
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                    R.array.baudrates_value,
                    android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            SpinnerBaudRateCOMA.setAdapter(adapter);
            SpinnerBaudRateCOMA.setSelection(4);

            adapter = ArrayAdapter.createFromResource(this,
                    R.array.workmode_name,
                    android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerWorkMode.setAdapter(adapter);
            spinnerWorkMode.setSelection(0);

            editTextRecDisp=(EditText)findViewById(R.id.editTextRecDisp);
            editTextCOMA=(EditText)findViewById(R.id.editTextCOMA);


            editTextCOMA.setText("AB000000010B0ABA");

            toggleButtonCOMA=(ToggleButton)findViewById(R.id.toggleButtonCOMA);
            ButtonClear=(Button)findViewById(R.id.ButtonClear);
            btnGetInfo=(Button)findViewById(R.id.btnGetInfo);
            btnReboot=(Button)findViewById(R.id.btnReboot);
            btnWorkMode =(Button)findViewById(R.id.btnWorkMode);
            btnSendCMD =(Button)findViewById(R.id.btnSendCMD);
            btnOpenFile =(Button)findViewById(R.id.btnOpenFile);
            btnUpdata =(Button)findViewById(R.id.btnUpdata);
            barUpdata = (ProgressBar)findViewById(R.id.barUpdata);

            toggleButtonCOMA.setOnCheckedChangeListener(new ToggleButtonCheckedChangeEvent());
            ButtonClear.setOnClickListener(new ButtonClickEvent());
            btnGetInfo.setOnClickListener(new ButtonClickEvent());
            btnReboot.setOnClickListener(new ButtonClickEvent());
            btnWorkMode.setOnClickListener(new ButtonClickEvent());
            btnSendCMD.setOnClickListener(new ButtonClickEvent());
            btnOpenFile.setOnClickListener(new ButtonClickEvent());
            btnUpdata.setOnClickListener(new ButtonClickEvent());
        }
        catch(Exception e)
        {
            Log.d(TAG,"setControls:"+e.getMessage());
        }
    }

    private void initApduView() {
        try{

            editApduCmd=(EditText)findViewById(R.id.editApduCmd);
            editApduCmd.setText("00A40000023F00");

            btnReqAntiSel=(Button)findViewById(R.id.btnReqAntiSel);
            btnRats=(Button)findViewById(R.id.btnRats);
            btnApduSend=(Button)findViewById(R.id.btnApduSend);

            btnReqAntiSel.setOnClickListener(new ButtonClickEvent());
            btnRats.setOnClickListener(new ButtonClickEvent());
            btnApduSend.setOnClickListener(new ButtonClickEvent());
        }
        catch(Exception e)
        {
            Log.d(TAG,"setControls:"+e.getMessage());
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if(id==openfileDialogId){
            Map<String, Integer> images = new HashMap<String, Integer>();
            // 下面几句设置各文件类型的图标， 需要你先把图标添加到资源文件夹
            images.put(OpenFileDialog.sRoot, R.mipmap.filedialog_root);	// 根目录图标
            images.put(OpenFileDialog.sParent, R.mipmap.filedialog_folder_up);	//返回上一层的图标
            images.put(OpenFileDialog.sFolder, R.mipmap.filedialog_folder);	//文件夹图标
            images.put("wav", R.mipmap.filedialog_wavfile);	//wav文件图标
            images.put(OpenFileDialog.sEmpty, R.mipmap.filedialog_root);
            Dialog dialog = OpenFileDialog.createDialog(id, this, "打开文件", new CallbackBundle() {
                        @Override
                        public void callback(Bundle bundle) {
                            openfilepath = bundle.getString("path");
//                            setTitle(openfilepath); // 把文件路径显示在标题上
                            ShowMessage(openfilepath);
                        }
                    },
                    ".bin;.gwt;",
                    images);
            return dialog;
        }
        return null;
    }

    //----------------------------------------------------打开关闭串口
    class ToggleButtonCheckedChangeEvent implements ToggleButton.OnCheckedChangeListener {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if(SpinnerCOMA.getCount()<=0){
                return;
            }

            try {
                if (buttonView == toggleButtonCOMA) {

                    SpinnerCOMA.setEnabled(!isChecked);
                    SpinnerBaudRateCOMA.setEnabled(!isChecked);

                    if (isChecked) {

                        serialCom.setPort(SpinnerCOMA.getSelectedItem().toString());
                        serialCom.setBaudRate(Integer.parseInt(
                                                SpinnerBaudRateCOMA.getSelectedItem().toString()));
                        OpenComPort(serialCom);
                    } else {

                        CloseComPort(serialCom);
                    }
                }
            } catch (Exception se) {
                ShowMessage(se.getMessage());
            }
        }
    }

    //----------------------------------------------------按钮事件
    class ButtonClickEvent implements View.OnClickListener {
        public void onClick(View v) {
            //检查串口是否已经打开
            if (!serialCom.isOpen()) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.Serial_connect_failed),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (v == ButtonClear) {
                editTextRecDisp.setText("");
            }
            else if (v == btnSendCMD){
                serialCom.send(MyFunc.HexToByteArr(editTextCOMA.getText().toString().replace(" ", "")));
            }
            else if (v == btnGetInfo) {
                recvlen = serialCom.sendSocket((byte)0x00, (byte)0x0B, (short) 0, recvdata, recvdata, 200);
                Log.d(TAG,"setControls:"+recvlen);
                if(recvlen>0) {
                    StringBuilder sMsg = new StringBuilder();
                    try {
                        sMsg.append(new String(recvdata));

                        ShowMessage(sMsg.toString());

                    } catch (Exception ex) {
                        Log.d(TAG, ex.getMessage());
                    }
                }
            }
            else if (v == btnReboot) {
                serialCom.send(MyFunc.HexToByteArr("AB000000011716BA"));
            }
            else if (v == btnWorkMode) {
                recvdata[0] = (byte)spinnerWorkMode.getSelectedItemId();
                recvlen = serialCom.sendSocket((byte)0x00, (byte)0x16, (short) 1, recvdata, recvdata, 100);
                Log.d(TAG,"btnWorkMode:"+recvlen);
                if(recvlen>0) {
                }
            }
            else if (v == btnReqA) {
                recvdata[0] = (byte)0x52;
                recvlen = serialCom.sendSocket((byte)0x00, (byte)0x40, (short) 1, recvdata, recvdata, 100);
                Log.d(TAG,"btnReqA:"+recvlen);
                if(recvlen>0) {
                    StringBuilder sMsg = new StringBuilder();
                    try {
                        sMsg.append(MyFunc.ByteArrToHex(recvdata, 0, recvlen));

                        ShowMessage(sMsg.toString());

                    } catch (Exception ex) {
                        Log.d(TAG, ex.getMessage());
                    }
                }

            }
            else if (v == btnAntiColl) {
                recvdata[0] = (byte)0x93;
                recvlen = serialCom.sendSocket((byte)0x00, (byte)0x41, (short) 1, recvdata, recvdata, 100);
                Log.d(TAG,"btnAntiColl:"+recvlen);
                if(recvlen>0) {
                    StringBuilder sMsg = new StringBuilder();
                    try {
                        Log.d(TAG, MyFunc.ByteArrToHex(recvdata, 0, recvlen));
                        sMsg.append(MyFunc.ByteArrToHex(recvdata, 0, recvlen));
                        System.arraycopy( recvdata, 0 , gcardnum, 0, 4);

                        ShowMessage(sMsg.toString());
                    } catch (Exception ex) {
                        Log.d(TAG, ex.getMessage());
                    }
                }
            }
            else if (v == btnSelect) {
                recvdata[0] = (byte)0x93;
                System.arraycopy( gcardnum,0 , recvdata, 1, 4);
                Log.d(TAG, MyFunc.ByteArrToHex(recvdata, 0, 5));
                recvlen = serialCom.sendSocket((byte)0x00, (byte)0x42, (short) 5, recvdata, recvdata, 100);
                Log.d(TAG,"btnSelect:"+recvlen);
                if(recvlen>0) {
                    StringBuilder sMsg = new StringBuilder();
                    try {
                        sMsg.append(MyFunc.ByteArrToHex(recvdata, 0, recvlen));

                        ShowMessage(sMsg.toString());

                    } catch (Exception ex) {
                        Log.d(TAG, ex.getMessage());
                    }
                }
            }
            else if (v == btnAnthKey) {
                recvdata[0] = 0x60;
                recvdata[0] += (byte)spinnerKeyGroup.getSelectedItemId();
                recvdata[1] = (byte)spinnerSector.getSelectedItemId();
                recvdata[1] *= 4;
                recvdata[1] += (byte)spinnerBlock.getSelectedItemId();

                byte[] key = MyFunc.HexToByteArr(editTextKey.getText().toString().replace(" ", ""));

                System.arraycopy( key,0 , recvdata, 2, 6);
                System.arraycopy( gcardnum,0 , recvdata, 8, 4);
                Log.d("recv：", MyFunc.ByteArrToHex(recvdata, 0, 12));

                recvlen = serialCom.sendSocket((byte)0x00, (byte)0x50, (short) 12, recvdata, recvdata, 300);
                Log.d(TAG,"btnAnthKey:"+recvlen);
                if(recvlen>0) {
                    StringBuilder sMsg = new StringBuilder();
                    try {
                        sMsg.append(MyFunc.ByteArrToHex(recvdata, 0, recvlen));

                        ShowMessage(sMsg.toString());

                    } catch (Exception ex) {
                        Log.d(TAG, ex.getMessage());
                    }
                }
            }
            else if (v == btnReadBlock) {
                recvdata[0] = (byte)spinnerSector.getSelectedItemId();
                recvdata[0] *= 4;
                recvdata[0] += (byte)spinnerBlock.getSelectedItemId();
                recvlen = serialCom.sendSocket((byte)0x00, (byte)0x51, (short) 1, recvdata, recvdata, 100);
                Log.d(TAG,"btnReadBlock:"+recvlen);
                if(recvlen>0) {
                    StringBuilder sMsg = new StringBuilder();
                    try {
                        sMsg.append(MyFunc.ByteArrToHex(recvdata, 0, recvlen));
                        editTextCOMA.setText(sMsg);
                        ShowMessage(sMsg.toString());

                    } catch (Exception ex) {
                        Log.d(TAG, ex.getMessage());
                    }
                }
            }
            else if (v == btnWriteBlock) {
                recvdata[0] = (byte)spinnerSector.getSelectedItemId();
                recvdata[0] *= 4;
                recvdata[0] += (byte)spinnerBlock.getSelectedItemId();

                byte[] blockdata = MyFunc.HexToByteArr(editTextCOMA.getText().toString().replace(" ", ""));
                if(blockdata.length<=16) {
                    ShowMessage("Plese input 16 bytes");
                    return;
                }
                System.arraycopy( blockdata, 0 , recvdata, 1, 16);

                recvlen = serialCom.sendSocket((byte)0x00, (byte)0x52, (short) 17, recvdata, recvdata, 100);
                Log.d(TAG,"btnWriteBlock:"+recvlen);
                if(recvlen>0) {
                    StringBuilder sMsg = new StringBuilder();
                    try {
                        sMsg.append(MyFunc.ByteArrToHex(recvdata, 0, recvlen));

                        ShowMessage(sMsg.toString());

                    } catch (Exception ex) {
                        Log.d(TAG, ex.getMessage());
                    }
                }
            }
            else if (v == btnHaltA) {
                recvlen = serialCom.sendSocket((byte)0x00, (byte)0x44, (short) 0, recvdata, recvdata, 100);
                Log.d(TAG,"btnHaltA:"+recvlen);
                if(recvlen>0) {
                }
            }
            else if (v == btnReqAntiSel) {
                recvdata[0] = (byte)0x52;
                recvlen = serialCom.sendSocket((byte)0x00, (byte)0x43, (short) 1, recvdata, recvdata, 100);
                Log.d(TAG,"btnReqA:"+recvlen);
                if(recvlen>0) {
                    StringBuilder sMsg = new StringBuilder();
                    try {
                        sMsg.append(MyFunc.ByteArrToHex(recvdata, 0, recvlen));

                        ShowMessage(sMsg.toString());

                    } catch (Exception ex) {
                        Log.d(TAG, ex.getMessage());
                    }
                }
            }
            else if (v == btnRats) {
                recvdata[0] = (byte)0x00;
                recvlen = serialCom.sendSocket((byte)0x00, (byte)0x49, (short) 1, recvdata, recvdata, 100);
                Log.d(TAG,"btnReqA:"+recvlen);
                if(recvlen>0) {
                    StringBuilder sMsg = new StringBuilder();
                    try {
                        sMsg.append(MyFunc.ByteArrToHex(recvdata, 0, recvlen));

                        ShowMessage(sMsg.toString());

                    } catch (Exception ex) {
                        Log.d(TAG, ex.getMessage());
                    }
                }
            }
            else if (v == btnApduSend) {
                byte[] apdu = MyFunc.HexToByteArr(editApduCmd.getText().toString().replace(" ", ""));

                recvlen = serialCom.sendSocket((byte)0x00, (byte)0x4B, (short) apdu.length, apdu, recvdata, 200);
                Log.d(TAG,"btnReqA:"+recvlen);
                if(recvlen>0) {
                    StringBuilder sMsg = new StringBuilder();
                    try {
                        sMsg.append(MyFunc.ByteArrToHex(recvdata, 0, recvlen));

                        ShowMessage(sMsg.toString());

                    } catch (Exception ex) {
                        Log.d(TAG, ex.getMessage());
                    }
                }
            }
            else if (v == btnOpenFile){
                showDialog(openfileDialogId);
            }
            else if (v == btnUpdata){
                updateFirmware(openfilepath);
            }

        }
    }

    //------------------------------------------显示消息
    private void ShowMessage(String sMsg) {
        StringBuilder sbMsg = new StringBuilder();
        sbMsg.append(editTextRecDisp.getText());
//        sbMsg.append(m_sdfDate.format(new Date()));
        sbMsg.append(sMsg);
        sbMsg.append("\r\n");
        editTextRecDisp.setText(sbMsg);
        editTextRecDisp.setSelection(sbMsg.length(), sbMsg.length());
    }

    //----------------------------------------------------串口控制类
    private class SerialControl extends SerialHelper {
        public SerialControl() {
        }

        @Override
        protected void onDataReceived(final ComBean ComRecData) {
            DispQueue.AddQueue(ComRecData);// 线程定时刷新显示(推荐)
        }
    }

    //----------------------------------------------------关闭串口
    private void CloseComPort(SerialHelper ComPort) {
        if (ComPort != null) {
            ComPort.stopSend();
            ComPort.close();
        }
    }

    //----------------------------------------------------打开串口
    private void OpenComPort(SerialHelper ComPort) {
        try {
            ComPort.open();
        } catch (SecurityException e) {
            ShowMessage(getString(R.string.No_read_or_write_permissions));
        } catch (IOException e) {
            ShowMessage(getString(R.string.Unknown_error));
        } catch (InvalidParameterException e) {
            ShowMessage(getString(R.string.Parameter_error));
        }
    }

    //----------------------------------------------------刷新显示线程
    private class DispQueueThread extends Thread {
        private Queue<ComBean> QueueList = new LinkedList<ComBean>();
        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                final ComBean ComData;
                while ((ComData = QueueList.poll()) != null) {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            DispRecData(ComData);
                        }
                    });

                    try {
                        Thread.sleep(10);// 显示性能高的话，可以把此数值调小。
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        public synchronized void AddQueue(ComBean ComData) {
            QueueList.add(ComData);
        }
    }

    //----------------------------------------------------显示接收数据
    private void DispRecData(ComBean ComRecData) {
        StringBuilder sMsg = new StringBuilder();
        byte[] temp = new byte[20];
        long cardint=0;
        long wg26_1=0;
        long wg26_2=0;
        long wg34_1=0;
        long wg34_2=0;
        try {
            sMsg.append("recv: "+MyFunc.ByteArrToHex(ComRecData.bRec));
            ShowMessage(sMsg.toString());

            if(solveRecv(ComRecData.bRec, temp)==0){    //主动刷卡的数据处理
                int len = temp[0];
                byte[] cardnum = new byte[4];
                System.arraycopy( temp,1, cardnum, 0, 4);   //只保留前面4个字节卡号

                sMsg.append("\n原始卡号："+MyFunc.ByteArrToHex(cardnum)+"\n");

                cardint = Long.parseLong(MyFunc.ByteArrToHex(cardnum, 0, 4), 16);
                sMsg.append("正码转十进制："+String.format("%010d", cardint)+"\n");

                wg26_1 = Long.parseLong(MyFunc.ByteArrToHex(cardnum,1,1), 16);
                wg26_2 = Long.parseLong(MyFunc.ByteArrToHex(cardnum,2,2), 16);
                sMsg.append("正码转韦根26："+String.format("%03d,%05d", wg26_1, wg26_2)+"\n");

                wg34_1 = Long.parseLong(MyFunc.ByteArrToHex(cardnum,0,2), 16);
                wg34_2 = Long.parseLong(MyFunc.ByteArrToHex(cardnum,2,2), 16);
                sMsg.append("正码转韦根34："+String.format("%05d,%05d", wg34_1, wg34_2)+"\n");

                MyFunc.reverseByte(cardnum);
                cardint = Long.parseLong(MyFunc.ByteArrToHex(cardnum, 0, 4), 16);
                sMsg.append("反码转十进制："+String.format("%010d", cardint)+"\n");

                wg26_1 = Long.parseLong(MyFunc.ByteArrToHex(cardnum,1,1), 16);
                wg26_2 = Long.parseLong(MyFunc.ByteArrToHex(cardnum,2,2), 16);
                sMsg.append("反码转韦根26："+String.format("%03d,%05d", wg26_1, wg26_2)+"\n");

                wg34_1 = Long.parseLong(MyFunc.ByteArrToHex(cardnum,0,2), 16);
                wg34_2 = Long.parseLong(MyFunc.ByteArrToHex(cardnum,2,2), 16);
                sMsg.append("反码转韦根34："+String.format("%05d,%05d", wg34_1, wg34_2)+"\n");


                runOnUiThread(new Runnable() {
                    public void run() {
                        String data = getMifareSertorData();

                        //立刻切换回读卡
                        serialCom.sendSocket((byte)0x00, (byte)0x2E, (short) 0, recvdata, recvdata, 100);

                        sMsg.append(data);
                        ShowMessage(sMsg.toString());
                    }
                });

            }

        } catch (Exception ex) {
            Log.d(TAG,ex.getMessage());
        }
    }

    //识别主动刷卡数据
    private int solveRecv(byte[] bRec, byte[] retRec) {
        int sta = -1;

        if((byte)bRec[0] == 0x02){

            byte len = bRec[1];
            if(len <= bRec.length){

                byte result = MyFunc.bccCalc(bRec, 1, len-3);
                if((byte)bRec[len-2] == (byte)result){
                    retRec[0] = (byte) (len-5);
                    System.arraycopy( bRec,3,retRec, 1,len-5);
                    sta = 0;
                }
            }
        }

        return sta;
    }

    //读Mifare扇区
    private String getMifareSertorData(){
        StringBuilder sMsg = new StringBuilder();
        byte[] rdata = new byte[500];
        byte[] sdata = new byte[500];
        int sendlen=0;
        byte[] snr = new byte[4];
//
//        //寻卡
//        sendlen = 0;
//        sdata[sendlen] = (byte)0x52;  sendlen++;
//        recvlen = serialCom.sendSocket((byte)0x00, (byte)0x40, (short) sendlen, sdata, rdata, 100);
//        if(recvlen<0)
//            return "";
//
//        //反冲突
//        sendlen = 0;
//        sdata[sendlen] = (byte)0x93;  sendlen++;
//        recvlen = serialCom.sendSocket((byte)0x00, (byte)0x41, (short) sendlen, sdata, rdata, 100);
//        if(recvlen<0)
//            return "";
//
//        System.arraycopy( rdata, 0 , snr, 0, 4);
//
//        //选卡
//        sendlen = 0;
//        sdata[sendlen] = (byte)0x93;  sendlen++;
//        System.arraycopy( snr, 0 , sdata, sendlen, 4);    sendlen += 4;
//        recvlen = serialCom.sendSocket((byte)0x00, (byte)0x42, (short) sendlen, sdata, rdata, 100);
//        if(recvlen<0)
//            return "";

//        //寻卡+防冲突+选卡
//        sendlen = 0;
//        sdata[sendlen] = (byte)0x52;  sendlen++;
//        recvlen = serialCom.sendSocket((byte)0x00, (byte)0x43, (short) sendlen, sdata, rdata, 200);
//        if(recvlen<0)
//            return "";
//
//        System.arraycopy( rdata, 4 , snr, 0, 4);
//        Log.d(TAG, "snr:"+MyFunc.ByteArrToHex(snr, 0, 4));
//
//        //验证卡片密码
//        sendlen = 0;
//        sdata[sendlen] = (byte)0x60;   sendlen++;//keyA
//        sdata[sendlen] = (byte)0x00;   sendlen++;//block0
//        sdata[sendlen] = (byte)0xFF;   sendlen++;//key
//        sdata[sendlen] = (byte)0xFF;   sendlen++;
//        sdata[sendlen] = (byte)0xFF;   sendlen++;
//        sdata[sendlen] = (byte)0xFF;   sendlen++;
//        sdata[sendlen] = (byte)0xFF;   sendlen++;
//        sdata[sendlen] = (byte)0xFF;   sendlen++;
//        System.arraycopy( snr, 0 , sdata, sendlen, 4);    sendlen += 4;
//        recvlen = serialCom.sendSocket((byte)0x00, (byte)0x50, (short) sendlen, sdata, rdata, 300);
//        if(recvlen<0)
//            return "";
//
//        //读块
//        sendlen = 0;
//        sdata[sendlen] = (byte)0x00;   sendlen++;//block0
//        recvlen = serialCom.sendSocket((byte)0x00, (byte)0x51, (short) sendlen, sdata, rdata, 100);
//        if(recvlen<0)
//            return "";
//        sMsg.append("block0:"+MyFunc.ByteArrToHex(rdata, 0, recvlen)+"\n");
//        Log.d(TAG, "block0:"+MyFunc.ByteArrToHex(rdata, 0, recvlen));
//
//        //读块
//        sendlen = 0;
//        sdata[sendlen] = (byte)0x01;   sendlen++;//block1
//        recvlen = serialCom.sendSocket((byte)0x00, (byte)0x51, (short) sendlen, sdata, rdata, 100);
//        if(recvlen<0)
//            return "";
//        sMsg.append("block1:"+MyFunc.ByteArrToHex(rdata, 0, recvlen)+"\n");
//        Log.d(TAG, "block1:"+MyFunc.ByteArrToHex(rdata, 0, recvlen));
//
//        //读块
//        sendlen = 0;
//        sdata[sendlen] = (byte)0x02;   sendlen++;//block2
//        recvlen = serialCom.sendSocket((byte)0x00, (byte)0x51, (short) sendlen, sdata, rdata, 100);
//        if(recvlen<0)
//            return "";
//        sMsg.append("block2:"+MyFunc.ByteArrToHex(rdata, 0, recvlen)+"\n");
//        Log.d(TAG, "block2:"+MyFunc.ByteArrToHex(rdata, 0, recvlen));
//
//        //读块
//        sendlen = 0;
//        sdata[sendlen] = (byte)0x03;   sendlen++;//block3
//        recvlen = serialCom.sendSocket((byte)0x00, (byte)0x51, (short) sendlen, sdata, rdata, 100);
//        if(recvlen<0)
//            return "";
//        sMsg.append( "block3:"+MyFunc.ByteArrToHex(rdata, 0, recvlen)+"\n");
//        Log.d(TAG, "block3:"+MyFunc.ByteArrToHex(rdata, 0, recvlen));

        //寻卡+反冲突+选卡+验证密码+读块
        sendlen = 0;
        sdata[sendlen] = (byte)0x60;   sendlen++;//keyA
        sdata[sendlen] = (byte)0x00;   sendlen++;//从0块开始读
        sdata[sendlen] = (byte)0x03;   sendlen++;//一共读3块
        sdata[sendlen] = (byte)0xFF;   sendlen++;//key
        sdata[sendlen] = (byte)0xFF;   sendlen++;
        sdata[sendlen] = (byte)0xFF;   sendlen++;
        sdata[sendlen] = (byte)0xFF;   sendlen++;
        sdata[sendlen] = (byte)0xFF;   sendlen++;
        sdata[sendlen] = (byte)0xFF;   sendlen++;
        recvlen = serialCom.sendSocket((byte)0x00, (byte)0x54, (short) sendlen, sdata, rdata, 500);
        Log.d(TAG, ""+recvlen);
        if(recvlen<=0)
            return "";
        sMsg.append( "block0:"+MyFunc.ByteArrToHex(rdata, 0, 16)+"\n");
        sMsg.append( "block1:"+MyFunc.ByteArrToHex(rdata, 16, 16)+"\n");
        sMsg.append( "block2:"+MyFunc.ByteArrToHex(rdata, 32, 16)+"\n");

        Log.d(TAG, MyFunc.ByteArrToHex(rdata, 0, recvlen));
        return sMsg.toString();
    }

    //升级模块固件线程
    private void updateFirmware(String filepath){

        if(filepath.isEmpty())
            return ;

        Thread newThread;        //声明一个子线程
        new Thread() {
            @Override
            public void run() {
                try {
                    byte[] filetemp = new byte[1100];
                    byte[] rdata = new byte[500];
                    byte[] sdata = new byte[1100];
                    int sendlen=0;
                    int fileoffset=0;
                    int readlen = 0;
                    boolean downFlag = true;

                    //进入IAP命令
                    recvlen = serialCom.sendSocket((byte)0x00, (byte)0x0E, (short) 0, sdata, rdata, 100);

                    //等待
                    int timecount = 500;
                    while(timecount>0){
//            MyFunc.delayMs(1);
                        try{
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        timecount--;
                        if(timecount==0){
                            break;
                        }
                    }

                    //查询信息
                    recvlen = serialCom.sendSocket((byte)0x00, (byte)0x0B, (short) 0, sdata, rdata, 300);
                    if(recvlen<0)
                        return ;

                    //打开升级文件
                    File f = new File(filepath);
                    FileInputStream fis = null;
                    fis = new FileInputStream(f);

                    short maxpack = (short)(f.length()/1024);
                    if((f.length()%1024)>0){
                        maxpack++;
                    }
                    short currentpack=0;
                    int trycount=0;
                    Log.d(TAG,"filesize:"+f.length());
                    Log.d(TAG,"maxpack:"+maxpack);
                    barUpdata.setMax(maxpack);
                    barUpdata.setProgress(currentpack);

                    while ((downFlag) && (currentpack<maxpack)) {

                        readlen = fis.read(filetemp, 0, 1024);
                        if(readlen<0) {
                            break;
                        }

                        trycount = 0;

                        Log.d(TAG,"readlen:"+readlen);

                        do{
                            trycount++;
                            sendlen = 0;
                            sdata[sendlen] = (byte) (maxpack>>8); sendlen++;
                            sdata[sendlen] = (byte) maxpack;    sendlen++;
                            sdata[sendlen] = (byte) (currentpack>>8); sendlen++;
                            sdata[sendlen] = (byte) currentpack;    sendlen++;
                            System.arraycopy( filetemp, 0 , sdata, 4, readlen); sendlen+=readlen;
                            //升级固件
                            recvlen = serialCom.sendSocket((byte) 0x00, (byte) 0x0C, (short) sendlen, sdata, rdata, 500);
                            Log.d(TAG, "recv:" +recvlen);
                            if (recvlen < 0) {
                                continue;
                            }
                            else
                                break;
                        }while(trycount<=3);    //超过3次失败

                        if(trycount==3)
                        {
                            break;
                        }

                        fileoffset += readlen;
                        currentpack++;
                        Log.d(TAG, "currentpack:" +currentpack);
                        Log.d(TAG,"fileoffset:"+fileoffset);
                        currentbar = currentpack;

                        runOnUiThread(new Runnable() {
                            public void run() {
                                barUpdata.setProgress(currentbar);
                            }
                        });

                    }

                    if(currentpack==maxpack){   //发送成功
                        Log.d(TAG, "send done ok\n");
                        runOnUiThread(new Runnable() {
                            public void run() {
                                ShowMessage("send done ok");
                            }
                        });

                        //等待
                        timecount = 200;
                        while(timecount>0){
                            try{
                                Thread.sleep(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            timecount--;
                            if(timecount==0){
                                break;
                            }
                        }

                        //重启命令
                        recvlen = serialCom.sendSocket((byte)0x00, (byte)0x17, (short) 0, sdata, rdata, 100);
                    }
                    else{
                        Log.d(TAG, "send err\n");
                        runOnUiThread(new Runnable() {
                            public void run() {
                                ShowMessage("send err!\n");
                            }
                        });
                    }

                }
                catch(Exception e)
                {
                    Log.d(TAG,"setControls:"+e.getMessage());
                }
            }
        }.start();

    }

}