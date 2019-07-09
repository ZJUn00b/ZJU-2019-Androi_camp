package com.percy.helloworld.recyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;


import com.percy.helloworld.Main2Activity;
import com.percy.helloworld.MainActivity;
import com.percy.helloworld.R;

import java.util.Arrays;
import java.util.List;

public class recyclerview_activity extends AppCompatActivity {
    private String tag = "recyclerview_activity";
    private RecyclerView mListView;
    private ListAdapter mAdapter;
    private Button mInsertButton, mInsertRangeButton, mDeleteButton, mDeleteRangeButton, mChangeButton, mChangeRangeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        mListView = findViewById(R.id.rl_list);
        mListView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ListAdapter();
        mListView.setAdapter(mAdapter);
        mAdapter.setList(DataFactory.getData(30));
        mAdapter.notifyDataSetChanged();
        //添加自定义分割线
        DividerItemDecoration divider = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this,R.drawable.line));
        mListView.addItemDecoration(divider);
        Button btback = (Button)findViewById(R.id.bt_back);
        btback.setOnClickListener(listener);

        mInsertButton = findViewById(R.id.bt_insert);
        mInsertButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
              Data data = new Data("1.","insert one","insert one");
                insert(data, 5);
            }
       });
        mInsertRangeButton = findViewById(R.id.bt_insert_range);
        mInsertRangeButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Data data1 = new Data("1.","insert one","insert one");
                Data data2 = new Data("2.","insert two","insert two");
                Data data3 = new Data("3.","insert three","insert three");
               insertRange(Arrays.asList(data1, data2, data3), 5);
            }
        });
        mDeleteButton = findViewById(R.id.bt_delete);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                delete( 5);
            }
        });
        mDeleteRangeButton = findViewById(R.id.bt_delete_range);
        mDeleteRangeButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                deleteRange( 5, 3);
            }
        });
        mChangeButton = findViewById(R.id.bt_change);
        mChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Data data = new Data("1.","changed","changed");
                change( data,5);
            }
        });
    }
    Button.OnClickListener listener = new Button.OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent(recyclerview_activity.this, MainActivity.class);
            startActivity(intent);
            recyclerview_activity.this.finish();
        }
    };

    public void insert(Data data, int index) {
        mAdapter.insert(data, index);
        mAdapter.notifyItemInserted(index);
    }

    public void insertRange(List<Data> list, int startIndex) {
        mAdapter.insertRange(list, startIndex);
        mAdapter.notifyItemRangeInserted(startIndex, list.size());
    }

    public void delete(int index) {
        mAdapter.delete(index);
        mAdapter.notifyItemRemoved(index);
    }

    public void deleteRange(int index, int count) {
        mAdapter.deleteRange(index, count);
        mAdapter.notifyItemRangeRemoved(index, count);
    }

    public void change(Data data, int index) {
        mAdapter.change(data, index);
        mAdapter.notifyItemChanged(index);
    }
}
