package com.mylike.keepalive.dialog;


import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.mylike.keepalive.R;

/**
 * @author zhengluping
 * @date 2019/1/11
 * 工具类
 */
public class CommonUtil {

    /**
     * -----------------------------------------------------------toast-------------------------------------------------------
     */
    private static Toast toast;
    //-----------------------------------------------------------加载框-------------------------------------------------------
    public static Dialog dialog;



    /**
     * 加载动态旋转
     *
     * @param context
     * @return
     */
    public static void showLoadProgress(final Context context) {
        if(dialog!=null&&dialog.isShowing()){
            return;
        }
        View itemView = LayoutInflater.from(context).inflate(R.layout.dialog_load, null);
        dialog = new Dialog(context, R.style.LoadDialog);

        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);

        dialog.setContentView(itemView);
        dialog.show();
    }
    /**
     * 加载动态旋转=点击不消失
     *
     * @param context
     * @return
     */
    public static void showLoadProgressOnTouchOutside(final Context context) {
        if(dialog!=null&&dialog.isShowing()){
            return;
        }
        View itemView = LayoutInflater.from(context).inflate(R.layout.dialog_load, null);
        dialog = new Dialog(context, R.style.LoadDialog);

        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(itemView);
        dialog.show();
    }

    //取消弹框
    public static void dismissLoadProgress() {
        try {
            if (dialog != null&&dialog.isShowing()) {
                dialog.dismiss();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
