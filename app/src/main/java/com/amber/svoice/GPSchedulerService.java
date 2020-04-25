package com.amber.svoice;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import com.amber.svoice.api.IHandleGPData;
import com.amber.svoice.api.IRequestGPData;
import com.amber.svoice.impl.EastmoneyImpl;
import com.amber.svoice.impl.SinaImpl;
import com.amber.svoice.utils.SQLiteAssist;

import java.lang.ref.WeakReference;
import java.util.Locale;

public class GPSchedulerService extends JobService implements IHandleGPData {

    public final static String TAG = "GPSchedulerService";

    private JobScheduler mJobScheduler;
    private TextToSpeech mTextToSpeech;
    private IRequestGPData requestGPData;
    private SQLiteAssist sqLiteAssist;
    /**
     * 数据来源
     * 0：新浪
     * 1：东方财富
     */
    private int dataSource = 0;


    @Override
    public void onCreate() {
        super.onCreate();
        mJobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        mJobScheduler.schedule(getJobInfo());

        sqLiteAssist = new SQLiteAssist(new WeakReference<>(this).get());
        requestGPData = new EastmoneyImpl(new WeakReference<>(this).get(), this);
        requestGPData = new SinaImpl(new WeakReference<>(this).get(), this);
        mTextToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTextToSpeech.setLanguage(Locale.CHINA);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.d(TAG, "onInit: 数据丢失或不支持");
                    }
                }
            }
        });

        mTextToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {
            }

            @Override
            public void onDone(String s) {
            }

            @Override
            public void onError(String s) {
            }
        });
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.i(TAG, "--------------------------------------------------------------------------------------------------");
        Cursor cursor = sqLiteAssist.query(new String[]{SQLiteAssist.COLUMN_2}, null, null, null);
        while (cursor.moveToNext()) {
            String code = cursor.getString(0);
            if (code != null && !code.equals("")) {
                requestGPData.readGPData(code);
            }
        }
        cursor.close();
        mJobScheduler.cancelAll();
        mJobScheduler.schedule(getJobInfo());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mJobScheduler != null) {
            mJobScheduler.cancelAll();
            mJobScheduler = null;
        }
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
            mTextToSpeech = null;
        }
        if (requestGPData != null) {
            requestGPData = null;
        }
        if (sqLiteAssist != null) {
            sqLiteAssist = null;
        }
    }

    @Override
    public void handleData(String data) {
        String message = null;
        switch (dataSource) {
            case 0:
                if (data != null && data.indexOf(",") > 0 && data.indexOf("\"") > 0) {
                    String[] array = data.split("\"")[1].split(",");
                    String name = array[0];
                    String price = array[3];
                    if (price.endsWith("0")) {
                        price = price.substring(0, price.length() - 1);
                    }
                    message = name.concat("当前价格").concat(price).concat("元");
                }
                break;
            case 1:
                if (data != null && data.indexOf(",") > 0) {
                    String[] array = data.split(",");
                    String code = array[1], name = array[2], price = array[3];
                    message = name.concat("当前价格").concat(price).concat("元");
                }
                break;
            default:
                throw new IllegalStateException("Unexpected DataSource: " + dataSource);
        }
        while (mTextToSpeech.isSpeaking()) {
        }
        if (message != null && mTextToSpeech != null) {
            // 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
            mTextToSpeech.setPitch(1f);
            //设定语速 ，默认1.0正常语速
            mTextToSpeech.setSpeechRate(1.0f);
            mTextToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, "speech");
        }
    }


    private JobInfo getJobInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("time", Activity.MODE_PRIVATE);
        long TIME = sharedPreferences.getLong("time", 10) * 1000;
        ComponentName componentName = new ComponentName(getPackageName(), GPSchedulerService.class.getName());
        JobInfo.Builder builder = new JobInfo.Builder(1, componentName);
        if (Build.VERSION.SDK_INT >= 24) {
            builder.setMinimumLatency(TIME); //执行的最小延迟时间
            builder.setOverrideDeadline(TIME);  //执行的最长延时时间
            builder.setBackoffCriteria(TIME, JobInfo.BACKOFF_POLICY_LINEAR);//线性重试方案
        } else {
            builder.setPeriodic(TIME);
        }
        builder.setPersisted(true);  // 设备重启后，任务还会继续执行
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);//执行的网络条件
        builder.setRequiresCharging(false); // true : 当插入充电器，才会执行该任务
        return builder.build();
    }
}
