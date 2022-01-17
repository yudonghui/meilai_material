package com.rfid.readerdemo;

import android.content.SharedPreferences;

import com.rfid.reader.Reader;
import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * Created by Administrator on 2017-6-22.
 */
public class Application extends android.app.Application {
    public Reader reader = null;

    public Reader getReader() throws SecurityException, IOException, InvalidParameterException {
        if (reader == null) {
			/* Read serial port parameters from com.rfid.readerdemo_preferences.xml */
            SharedPreferences sp = getSharedPreferences("com.rfid.readerdemo_preferences", MODE_PRIVATE);
            String path = sp.getString("DEVICE", "");
            if (path.length() == 0) {
            	throw new InvalidParameterException();
            }

			/* Open the serial port */
            reader = Reader.getInstance(path, 9600);
        }
        return reader;
    }

    public void close() {
        if (reader != null) {
            reader.close();
            reader = null;
        }
    }
}