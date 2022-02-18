package live_library.wechat2.sns;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

/**
 * Created by Shall on 2015-07-29.
 */
public class StringPostRequest extends StringRequest {
    private Map<String, String> params;
    private String Line = "--------------test---";
    private Map<String, File> mUpImageLists;

    public StringPostRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public StringPostRequest(String url, Response.Listener<String> listener,
                             Response.ErrorListener errorListener) {
        this(Method.POST, url, listener, errorListener);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return mUpImageLists != null ? super.getParams() : params;
    }

    public void setmUpImageLists(Map<String, File> mUpImageLists) {
        this.mUpImageLists = mUpImageLists;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        if (mUpImageLists == null || mUpImageLists.size() == 0) {
            return super.getBody();
        }
        //自定义图片上传
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        StringBuffer sb = new StringBuffer();
        Set keySet = params.keySet();
        //常用的参数数据格式
        for (Object keyName : keySet) {
            sb.append("--" + Line);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;");
            System.out.println("键名：" + keyName);
            sb.append(" name=\"");
            sb.append(keyName);
            sb.append("\"");
            sb.append("\r\n");
            sb.append("\r\n");
            sb.append(params.get(keyName));
            sb.append("\r\n");
        }
        //文件数据
        Set filePath = mUpImageLists.keySet();
        for (Object name : filePath) {
            sb.append("--" + Line);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;");
            sb.append("\r\n");
            sb.append("Content-Type: application/octet-stream");
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;");
            sb.append(" name=\"");
            sb.append(name);
            sb.append("\"");
            sb.append("; filename=\"");
            sb.append(mUpImageLists.get(name));
            sb.append("\"");
            sb.append("\r\n");
            sb.append("\r\n");
            try {
                bos.write(sb.toString().getBytes("utf-8"));
                bos.write(toBytes(new FileInputStream(mUpImageLists.get(name))));
                bos.write("\r\n".getBytes("utf-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //结尾
        String endLine = "--" + Line + "--" + "\r\n";
        try {
            bos.write(endLine.toString().getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public byte[] toBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int ch;
        while ((ch = in.read()) != -1) {
            out.write(ch);
        }
        byte buffer[] = out.toByteArray();
        out.close();
        return buffer;
    }

    @Override
    public String getBodyContentType() {
        return mUpImageLists != null ? "multipart/form-data" + "; boundary=" + Line : super.getBodyContentType();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = (new String(response.data, HttpHeaderParser.parseCharset(response.headers)));
            return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(response));
        }
    }
}
