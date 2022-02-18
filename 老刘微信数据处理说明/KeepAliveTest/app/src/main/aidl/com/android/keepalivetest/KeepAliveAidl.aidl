// KeepAliveAidl.aidl
package com.android.keepalivetest;

// Declare any non-default types here with import statements

interface KeepAliveAidl {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void wakeUp(String title,String discription,int iconRes);
}
