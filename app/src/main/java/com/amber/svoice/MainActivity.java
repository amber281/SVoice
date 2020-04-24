package com.amber.svoice;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amber.svoice.api.IHandleGPData;
import com.amber.svoice.api.IRequestGPData;
import com.amber.svoice.impl.EastmoneyImpl;
import com.amber.svoice.utils.GPInfo;
import com.amber.svoice.utils.GPInfoAdapter;
import com.amber.svoice.utils.SQLiteAssist;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener, IHandleGPData {

    public final static String TAG = "MainActivity";

    private List<GPInfo> list;
    private GPInfoAdapter adapter;
    private Button button1;
    private Button settingButton;
    private EditText editText;
    private IRequestGPData requestGPData;
    private SQLiteAssist sqLiteAssist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button1 = findViewById(R.id.add_btn);
        button1.setOnClickListener(this);
        settingButton = findViewById(R.id.setting_btn);
        settingButton.setOnClickListener(this);
        editText = findViewById(R.id.gpcode_edit);
        sqLiteAssist = new SQLiteAssist(new WeakReference<>(this).get());
        requestGPData = new EastmoneyImpl(new WeakReference<>(this).get(), this);

        startService(new Intent(this, GPSchedulerService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        list = new LinkedList<>();
        Cursor cursor = sqLiteAssist.query(new String[]{SQLiteAssist.COLUMN_2, SQLiteAssist.COLUMN_3}, null, null, null);
        while (cursor.moveToNext()) {
            String code = cursor.getString(0);
            String name = cursor.getString(1);
            list.add(new GPInfo(code, name));
        }
        cursor.close();
        adapter = new GPInfoAdapter(this, R.layout.gpinfo_layout, list);
        ListView listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (list != null) {
            list = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, GPSchedulerService.class));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_btn) {
            CharSequence code = editText.getText();
            if (code == null || code.length() == 0) {
                Toast.makeText(MainActivity.this, "请输入有效的代码.", Toast.LENGTH_LONG).show();
                return;
            }
            requestGPData.readGPData(code.toString());
            editText.setText("");
        } else if (view.getId() == R.id.setting_btn) {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
        }
    }

    @Override
    public void handleData(String data) {
        Log.d(TAG, "handleData: " + data);
        if (data != null && data.indexOf(",") > 0) {
            String[] array = data.split(",");
            String code = array[1], name = array[2];
            ContentValues contentValues = new ContentValues();
            contentValues.put(SQLiteAssist.COLUMN_2, code);
            contentValues.put(SQLiteAssist.COLUMN_3, name);
            if (sqLiteAssist.getName(array[1]) == null) {
                sqLiteAssist.insert(contentValues);
                list.add(new GPInfo(code, name));
                adapter.notifyDataSetChanged();
            }
        }
    }
}
