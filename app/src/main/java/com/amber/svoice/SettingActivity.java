package com.amber.svoice;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import static com.amber.svoice.R.layout.activity_setting;

public class SettingActivity extends Activity implements AdapterView.OnItemSelectedListener {

    public final static String TAG = "SettingActivity";
    private Spinner timeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        setContentView(activity_setting);

        timeSpinner = findViewById(R.id.time_spinner);
        timeSpinner.setOnItemSelectedListener(this);

        SharedPreferences sharedPreferences = getSharedPreferences("time", Activity.MODE_PRIVATE);
        timeSpinner.setSelection(sharedPreferences.getInt("position", 0));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            finish();
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        String[] settings = getResources().getStringArray(R.array.settings);
        String text = settings[pos];
        long time = 10;
        try {
            if (text.indexOf("秒") > 0) {
                time = Long.valueOf(text.replace("秒", ""));
            } else if (text.indexOf("分钟") > 0) {
                time = Long.valueOf(text.replace("分钟", "")) * 60;
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "number format exception: ", e);
        }
        if (time > 0) {
            SharedPreferences sharedPreferences = getSharedPreferences("time", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("time", time);
            editor.putInt("position", pos);
            editor.apply();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}
