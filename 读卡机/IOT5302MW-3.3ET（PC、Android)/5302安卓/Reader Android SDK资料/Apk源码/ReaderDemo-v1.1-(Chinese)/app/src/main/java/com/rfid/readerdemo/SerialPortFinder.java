package com.rfid.readerdemo;

import android.util.Log;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by Administrator on 2017-6-22.
 */
public class SerialPortFinder {
    private static final String TAG = SerialPortFinder.class.getSimpleName();
    private Vector<Driver> mDrivers = null;

    public class Driver {
        private String mDriverName;
        private String mDeviceRoot;
        Vector<File> mDevices = null;

        public Driver(String name, String root) {
            mDriverName = name;
            mDeviceRoot = root;
        }

        public Vector<File> getDevices() {
            if (mDevices == null) {
                mDevices = new Vector<>();
                File dev = new File("/dev");
                File[] files = dev.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].getAbsolutePath().startsWith(mDeviceRoot)) {
                        Log.d(TAG, "Found new device: " + files[i]);
                        mDevices.add(files[i]);
                    }
                }
            }
            return mDevices;
        }

        public String getName() {
            return mDriverName;
        }
    }

    Vector<Driver> getDrivers() throws IOException {
        if (mDrivers == null) {
            mDrivers = new Vector<>();
            LineNumberReader lnReader = new LineNumberReader(new FileReader("/proc/tty/drivers"));
            String str;
            while ( (str = lnReader.readLine()) != null) {
                String[] w = str.split(" +");
                if ((w.length == 5) && (w[4].equals("serial"))) {
                    Log.d(TAG, "Found new driver: " + w[1]);
                    mDrivers.add(new Driver(w[0], w[1]));
                }
            }
            lnReader.close();
        }
        return mDrivers;
    }

    public String[] getAllDevices() {
        Vector<String> devices = new Vector<>();
        Iterator<Driver> itdriv;

        try {
            itdriv = getDrivers().iterator();
            while (itdriv.hasNext()) {
                Driver driver = itdriv.next();
                Iterator<File> itdev = driver.getDevices().iterator();
                while(itdev.hasNext()) {
                    String device = itdev.next().getName();
                    String value = String.format("%s (%s)", device, driver.getName());
                    devices.add(value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return devices.toArray(new String[devices.size()]);
    }

    public String[] getAllDevicesPath() {
        Vector<String> devices = new Vector<>();
        Iterator<Driver> itdriv;

        try {
            itdriv = getDrivers().iterator();
            while (itdriv.hasNext()) {
                Driver driver = itdriv.next();
                Iterator<File> itdev = driver.getDevices().iterator();
                while(itdev.hasNext()) {
                    String device = itdev.next().getAbsolutePath();
                    devices.add(device);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return devices.toArray(new String[devices.size()]);
    }
}
