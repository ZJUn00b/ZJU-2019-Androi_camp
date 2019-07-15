package com.percy.mytodolist.write;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.percy.mytodolist.R;
import com.percy.mytodolist.bean.Priority;
import com.percy.mytodolist.db.TodoContract;
import com.percy.mytodolist.db.TodoDbHelper;

public class WriteActivity extends AppCompatActivity {

    private EditText editText;
    private Button addBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        setTitle("write to file");

        editText = findViewById(R.id.edit_text_for_write);
        editText.setFocusable(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.showSoftInput(editText, 0);
        }


        addBtn = findViewById(R.id.btn_add_for_write);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence content = editText.getText();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(com.percy.mytodolist.write.WriteActivity.this,
                            "No content to write", Toast.LENGTH_SHORT).show();
                    return;
                }
                String Text = getText(content.toString().trim());
                //数据是使用Intent返回
                Intent intent = new Intent();
                //把返回数据存入Intent
                intent.putExtra("Text",Text);//按key-value对的形式存入数据


                boolean succeed = true;
                if (succeed) {
                    Toast.makeText(com.percy.mytodolist.write.WriteActivity.this,
                            "write done ", Toast.LENGTH_SHORT).show();
                    setResult(2,intent);
                } else {
                    Toast.makeText(com.percy.mytodolist.write.WriteActivity.this,
                            "Error", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }



    private String getText(String text) {
        return text;
    }
}