package com.percy.helloworld;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.BreakIterator;

public class MainActivity extends AppCompatActivity  {
    private Button button = null;
    private Switch sw1 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 获取界面组件
        sw1 = (Switch) findViewById(R.id.switch1);

        //
        sw1.setOnClickListener(new CompoundButton.OnCheckedChangeListener(){
    @Override
    public void onCheckedchanged(CompoundButton compoundButton,boolean b){
       if (compoundButton.isChecked()) {
           Toast.makeText(MainActivity, this, "open", Toast.LENGTH_SHORT).show();
       }else {
           Toast.makeText(MainActivity, this, "close", Toast.LENGTH_SHORT).show();
       }
       }

        })
    }
}
