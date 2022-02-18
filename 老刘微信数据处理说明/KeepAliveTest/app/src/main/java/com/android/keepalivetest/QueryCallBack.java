package com.android.keepalivetest;

import android.app.Dialog;
import android.util.Log;
import android.view.View;

import com.yanzhenjie.kalle.simple.Callback;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;



public abstract class QueryCallBack<S> extends Callback<S, String> {

    private static Dialog mDialog;
    private static View itemView;

    public QueryCallBack() {

    }

    @Override
    public Type getSucceed() {
        Type superClass = getClass().getGenericSuperclass();
        return ((ParameterizedType)superClass).getActualTypeArguments()[0];
    }

    @Override
    public Type getFailed() {
        return String.class;
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onException(Exception e) {
        Log.d("QueryCallBack", "Exception: "+e.getMessage());
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onEnd() {
    }


}
