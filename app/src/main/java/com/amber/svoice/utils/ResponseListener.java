package com.amber.svoice.utils;

import com.amber.svoice.api.IHandleGPData;
import com.android.volley.Response;

public class ResponseListener implements Response.Listener<String> {

    private IHandleGPData handleData;

    public ResponseListener(IHandleGPData data) {
        this.handleData = data;
    }

    @Override
    public void onResponse(String response) {
        handleData.handleData(response);
    }
}
