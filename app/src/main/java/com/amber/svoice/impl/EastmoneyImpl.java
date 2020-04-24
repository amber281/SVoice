package com.amber.svoice.impl;

import android.content.Context;

import com.amber.svoice.api.IHandleGPData;
import com.amber.svoice.api.IRequestGPData;
import com.amber.svoice.utils.ResponseErrorListener;
import com.amber.svoice.utils.ResponseListener;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class EastmoneyImpl implements IRequestGPData {

    private final static String TAG = "EastmoneyImpl";
    private final static String DATA_URL = "http://hqdigi2.eastmoney.com/EM_Quote2010NumericApplication/CompatiblePage.aspx?Type=ZT&jsName=js_fav&fav=?";
    private Context mContext;
    private IHandleGPData mHandleGPData;

    public EastmoneyImpl(Context context, IHandleGPData handleGPData) {
        mContext = context;
        mHandleGPData = handleGPData;
    }

    @Override
    public void readGPData(String code) {
        String URL = DATA_URL.replace("fav=?", "fav=".concat(code));
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new ResponseListener(mHandleGPData), new ResponseErrorListener());
        requestQueue.add(stringRequest);
    }
}
