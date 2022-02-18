package live_library.wechat2.http;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestQueueSingle {

    private static RequestQueueSingle mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private RequestQueueSingle(Context ctx) {
        mCtx = ctx;
    }

    //异步获取单实例
    public static synchronized RequestQueueSingle getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RequestQueueSingle(context);
        }
        return mInstance;
    }

    public RequestQueue getRuquestQueue() {
        if (mRequestQueue == null) {
            //getApplication()方法返回一个当前进程的全局应用上下文，这就意味着
            //它的使用情景为：你需要一个生命周期独立于当前上下文的全局上下文，
            //即就是它的存活时间绑定在进程中而不是当前某个组建。
            mRequestQueue = Volley.newRequestQueue(mCtx);
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRuquestQueue().add(req);
    }

}
