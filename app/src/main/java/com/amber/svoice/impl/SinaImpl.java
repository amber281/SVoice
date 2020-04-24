package com.amber.svoice.impl;

import android.content.Context;
import android.util.Log;

import com.amber.svoice.api.IHandleGPData;
import com.amber.svoice.api.IRequestGPData;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class SinaImpl implements IRequestGPData {

    private final static String TAG = "SinaImpl";
    private final static String DATA_URL = "http://hq.sinajs.cn/list=?";
    private Context mContext;
    private IHandleGPData mHandleGPData;

    public SinaImpl(Context context, IHandleGPData handleGPData) {
        mContext = context;
        mHandleGPData = handleGPData;
    }

    @Override
    public void readGPData(String code) {
        if (code.startsWith("00")) {
            code = "sz".concat(code);
        } else if (code.startsWith("60")) {
            code = "sh".concat(code);
        } else {
            Log.d(TAG, "invalid code");
            return;
        }
        String URL = DATA_URL.replace("?", code);
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new com.amber.svoice.utils.ResponseListener(mHandleGPData), new com.amber.svoice.utils.ResponseErrorListener());
        requestQueue.add(stringRequest);
    }
}
