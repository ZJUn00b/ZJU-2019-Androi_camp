package com.percy.helloworld;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Main2Activity extends AppCompatActivity {
    private String tag = "Main2Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(tag,"onCreate");
        setContentView(R.layout.activity_main2);
        ImageView imgView = (ImageView) this.findViewById(R.id.imageView3);
        final int[] imageItems = new int[] { R.drawable.symbol };
        imgView.setImageResource(imageItems[0]);
        Button bt3 = (Button) findViewById(R.id.button4);
        //创建监听器
        bt3.setOnClickListener(listener);
        Log.i(tag,"listener");
    }
    Button.OnClickListener listener = new Button.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(Main2Activity.this, MainActivity.class);
            startActivity(intent);
            Main2Activity.this.finish();
        }
    };
}