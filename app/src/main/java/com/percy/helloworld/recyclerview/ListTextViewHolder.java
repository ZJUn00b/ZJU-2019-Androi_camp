package com.percy.helloworld.recyclerview;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v7.widget.AppCompatImageView;
import com.percy.helloworld.R;

public class ListTextViewHolder extends RecyclerView.ViewHolder {

    private TextView mTextView;
    private TextView mTextView1;
    private TextView mTextView2;
    private android.support.v7.widget.AppCompatImageView mysymbol;


    public static ListTextViewHolder create(Context context, ViewGroup root) {
        View v = LayoutInflater.from(context).inflate(R.layout.layout_item_text_list, root, false);
        return new ListTextViewHolder(v);
    }

    public ListTextViewHolder(@NonNull View itemView) {
        super(itemView);
        mTextView = itemView.findViewById(R.id.index);
        mTextView1 = itemView.findViewById(R.id.Title);
        mTextView2 = itemView.findViewById(R.id.HotValue);
        mysymbol  = itemView.findViewById(R.id.image2);
    }

    @SuppressLint("ResourceAsColor")
    public void bind(Data data) {
        if (null == data) return;

        mTextView.setText(data.getInfo() + "");
        if (data.getInfo()!="1."&&data.getInfo()!="2."&&data.getInfo()!="3.") {
            mTextView.setTextColor(Color.argb(153,255,255,255));

        }
        else mTextView.setTextColor(Color.argb(230,250,206,21));
        mTextView1.setText(data.gettitle() + "");
        mTextView2.setText(data.getHotvalue() + "");
        if(data.getInfo()!="1.") mTextView2.setText("\uD83D\uDD25"+data.getHotvalue() + "");
       // if (data.getInfo()!="1.")
          //  mysymbol.setVisibility(View.INVISIBLE);
    }


}
