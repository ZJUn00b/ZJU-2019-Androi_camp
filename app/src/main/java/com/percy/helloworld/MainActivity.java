package com.percy.helloworld;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.percy.helloworld.recyclerview.recyclerview_activity;
import java.text.BreakIterator;

public class MainActivity extends AppCompatActivity {
    private SeekBar testSeekBar;
    private TextView text2;
    public String tag = "mainactivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //log检测

        super.onCreate(savedInstanceState);
        Log.i(tag,"onCreate");
        setContentView(R.layout.activity_main);
        Log.i(tag,"setContentView");
        // 获取界面组件
        //根据ID值取得SeekBar对象和TextView对象
        testSeekBar = (SeekBar)findViewById(R.id.seekBar);
        text2 = (TextView)findViewById(R.id.textView2);
        testSeekBar.setMax(100); //设置进度条的最大值是100
        testSeekBar.setProgress(30); //设置初始位置
        Log.i(tag,"根据ID值取得SeekBar对象和TextView对象");
        //获取工具条
        android.support.v7.widget.Toolbar toolbar1 = findViewById(R.id.toolbar);
        toolbar1.setLogo(R.mipmap.ic_launcher);
        //setSupportActionBar(toolbar1);
        //按钮1,并为按钮1在Activity中定义一个内部类继承监听器接口（这里是OnClickListener）
        Button bt1 = (Button) findViewById(R.id.button1);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"go to list view",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, recyclerview_activity.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
        });
        //按钮2
        Button bt2 = (Button) findViewById(R.id.button3);
        //为按钮2创建监听器
        bt2.setOnClickListener(listener);
        //switch组件
        Switch  mswitch = (Switch)findViewById(R.id.switch1);
        mswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TextView textView1 = (TextView)findViewById(R.id.textView1);
                if (isChecked)
                {
                    //打印调试信息
                    Log.d("mainactivity","in  checkedchanged");
                    textView1.setText("Mode: innormal");

                }else {
                    textView1.setText("Mode: normal");
                }
            }
        });
        //为seekbar写监听事件
        testSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

            //在进度开始改变时执行
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                int i= seekBar.getProgress(); //获取进度条的进度值
                text2.setText("Start value is "+i); //在TextView上显示进度
            }

            //当进度发生改变时执行
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if(fromUser) //判断是谁修改的进度值?
                    text2.setText("Current value is "+progress+"");
                else
                    text2.setText("Current value is "+progress+"");
            }

            //在停止拖动时执行
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int i= seekBar.getProgress();  //获取进度条的进度值
                text2.setText("Alpha Value "+i);
            }
        });


    }
    Button.OnClickListener listener = new Button.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, Main2Activity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }
    };

}
