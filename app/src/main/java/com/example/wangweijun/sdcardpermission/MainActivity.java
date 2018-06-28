package com.example.wangweijun.sdcardpermission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends Activity {
    String tag = "MainActivity";
    public static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 121;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void writeSelfCacheDir(View v) {
        // 写自己app生成的外置存储目录不需要申明任何权限(静态与动态申请) kk 19之后
        final File cacheDir = getApplicationContext().getExternalCacheDir();
        Log.i(tag, "cache dir:" + cacheDir);
        new Thread(new Runnable() {
            @Override
            public void run() {
                write2Path(cacheDir, "self.txt", "ccccccccccc");
            }
        }).start();
    }

    public void writeOtherAppCacheDir(View v) {
        // 写sdcard其他的目录需要静态配置，动态申请权限
        if (addPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        } else {
            final File otherAppCacheDir =
                    new File("/storage/emulated/0/Android/data/com.letv.app.appstore/cache");
            Log.i(tag, "otherAppCacheDir dir:" + otherAppCacheDir);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    write2Path(otherAppCacheDir, "other.txt", "oooooooooooo");
                }
            }).start();
        }
    }


    public void write2Path(File cacheDir, String filename, String content) {
        File f = new File(cacheDir, filename);
        try {
            FileOutputStream fileOS = new FileOutputStream(f);
            try {
                fileOS.write(content.getBytes());
                fileOS.flush();
                fileOS.close();
                Log.i(tag, "write success dir:" + cacheDir);
            } catch (IOException e) {
                e.printStackTrace();
                Log.i(tag, "IOException dir:" + cacheDir);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(tag, "write FileNotFoundException dir:" + cacheDir);
        }
    }


    private boolean addPermission(Activity activity, String permission) {
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    @SuppressLint("Override")
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(tag, "request success write sdcard success ");
                }
                break;

        }
    }
}
