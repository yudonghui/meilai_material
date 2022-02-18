package com.android.keepalivetest;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.Nullable;

public class LocalService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();

    }

    private final class LocalBinder extends KeepAliveAidl.Stub{
        @Override
        public void wakeUp(String title, String discription, int iconRes) throws RemoteException {
            shouDefNotify();
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void shouDefNotify(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
//            KeepAliveConfig.CONTENT =
        }
    }
}
