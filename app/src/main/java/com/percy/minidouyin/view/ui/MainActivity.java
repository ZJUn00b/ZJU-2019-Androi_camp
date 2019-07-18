package com.percy.minidouyin.view.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.percy.minidouyin.R;
import com.percy.minidouyin.api.IMiniDouyinService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private static final int MY_PERMISSIONS_REQUEST_CALL_CAMERA = 2;

    private String[] permissions = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE
    };
    List<String> mPermissionList = new ArrayList<>();


    //Button btn_login = findViewById(R.id.btn_login);
    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 拉起登陆界面
         */
        setContentView(R.layout.activity_login);

        /**
         * 隐藏标题栏
         */
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
            getWindow().setStatusBarColor(R.color.themecolor);
            getWindow().setNavigationBarColor(R.color.themecolor);
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
            );
        }
        /**
         * 监听登录按钮，点击登录后同时实现权限的请求
         *
         */

        findViewById(R.id.btn_login).setOnClickListener(v -> {

            startActivity(new Intent(MainActivity.this, VideoWFViewActivity.class));
            mPermissionList.clear();
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);
                }
            }
            if (mPermissionList.isEmpty()) {//未授予的权限为空，表示都授予了
                Toast.makeText(MainActivity.this, "all permission granted", Toast.LENGTH_LONG).show();
            } else {//请求权限方法
                String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
                ActivityCompat.requestPermissions(MainActivity.this, permissions, MY_PERMISSIONS_REQUEST_CALL_CAMERA);
            }

        });
    }
}
