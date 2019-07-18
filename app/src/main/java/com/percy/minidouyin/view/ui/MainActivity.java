package com.percy.minidouyin.view.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.percy.minidouyin.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private static final int MY_PERMISSIONS_REQUEST_CALL_CAMERA = 2;
    //从登录界面中可以拿到学号姓名
    public String mID;
    public String mName;
    private EditText inputID;
    private EditText inputNAME;
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
        //初始不能够点击登录
        findViewById(R.id.btn_login).setEnabled(false);

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
         * 从EditText拿到学号和姓名信息
         */
        inputID = findViewById(R.id.et_account);
        inputNAME = findViewById(R.id.et_pwd);
        //get ID
        inputID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int number = inputID.getText().toString().length();
                mID = inputID.getText().toString();
                if (number<=0) findViewById(R.id.btn_login).setEnabled(false);
                else if (number >12) {
                    findViewById(R.id.btn_login).setEnabled(false);
                    Toast.makeText(MainActivity.this, "ID too long, no more than 12 char", Toast.LENGTH_LONG).show();
                }
                else if (inputNAME.getText().toString().length()!=0) findViewById(R.id.btn_login).setEnabled(true);
            }
        });
        //get NAME
        inputNAME.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int number = inputNAME.getText().toString().length();
                mName = inputNAME.getText().toString();
                if (number >120) {
                     //findViewById(R.id.btn_login).setEnabled(false);
                    Toast.makeText(MainActivity.this, "NAME too long, no more than 120 char", Toast.LENGTH_LONG).show();
                }

            }
        });
        /**
         * 监听登录按钮，点击登录后同时实现权限的请求
         * 点击登陆按钮进入卡片视图，具有用户名等信息
         */

        findViewById(R.id.btn_login).setOnClickListener(v -> {
           Intent mIntent =  new Intent(MainActivity.this, VideoWFViewActivity.class);
           //向外传出姓名学号
           mIntent.putExtra("ID",mID);
            mIntent.putExtra("NAME",mName);
            startActivity(mIntent);
            mPermissionList.clear();
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);
                }
            }
            if (mPermissionList.isEmpty()) {//未授予的权限为空，表示都授予了
                //Toast.makeText(MainActivity.this, "all permission granted", Toast.LENGTH_LONG).show();
            } else {//请求权限方法
                String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
                ActivityCompat.requestPermissions(MainActivity.this, permissions, MY_PERMISSIONS_REQUEST_CALL_CAMERA);
            }

        });
    }
}
