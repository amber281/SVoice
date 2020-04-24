package com.amber.svoice.utils;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

public class ResponseErrorListener implements Response.ErrorListener {
    @Override
    public void onErrorResponse(VolleyError error) {
        Log.d("", "IRequestGPData.onErrorResponse: " + error.toString());
    }
}
