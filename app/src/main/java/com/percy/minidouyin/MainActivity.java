package com.percy.minidouyin;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Button btn_login = findViewById(R.id.btn_login);

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
         * 监听登录按钮
         */
        btn_login.setOnClickListener(v -> {
           // startActivity(new Intent(MainActivity.this, VideoInfoActivity.class));
        });


    }
}
