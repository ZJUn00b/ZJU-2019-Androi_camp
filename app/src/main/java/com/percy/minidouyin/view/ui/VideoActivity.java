package com.percy.minidouyin.view.ui;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.percy.minidouyin.R;
import com.percy.minidouyin.anim.DYLoadingView;

public class VideoActivity extends AppCompatActivity {
    private DYLoadingView dy1;
    private DYLoadingView dy2;
    private boolean isPrepared = false;
    private boolean isPlaying = false;
    public static void launch(Activity activity, String url) {
        Intent intent = new Intent(activity, VideoActivity.class);
        intent.putExtra("url", url);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        dy1= (DYLoadingView) findViewById(R.id.dy1);
        dy2= (DYLoadingView) findViewById(R.id.dy2);
        String url = getIntent().getStringExtra("url");
        VideoView videoView = findViewById(R.id.video_view);
        MediaController mc = new MediaController(this);
        mc.setVisibility(View.INVISIBLE);
        videoView.setMediaController(mc);
        //final ProgressBar progressBar = findViewById(R.id.progress_bar);
        //videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(Uri.parse(url));
        videoView.requestFocus();
        videoView.start();
        //监听加号按键，可以实现点击加号进入内建相机
        findViewById(R.id.add_post_cover).setOnClickListener(v->{
            //监听加号点击行为
            startActivity(new Intent(VideoActivity.this, CameraActivity.class));
        });

        //缓冲的时候显示动画
        dy1.start();
        videoView.getBufferPercentage();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //TODO 在prepared的时候关闭并隐藏动画
                isPrepared = true;
                isPlaying =true;
                dy1.stop();
                dy1.setVisibility(View.GONE);
                //设置为循环播放
                mp.setLooping(true);
                //TODO 在加载完成后需要对整个屏幕进行进行监听，如果有相关的点击事件，则暂停
                if (isPrepared) {
                    findViewById(R.id.surface).setOnClickListener(v->{
                        //监听对整个页的点击事件
                        if (isPlaying){
                            //控制播放暂停
                            mp.pause();isPlaying = false;
                            findViewById(R.id.btn_play).setVisibility(View.VISIBLE);
                        }
                        else {
                            //控制播放继续
                            mp.start();isPlaying = true;
                            findViewById(R.id.btn_play).setVisibility(View.INVISIBLE);
                        }
                    });
                }
            // progressBar.setVisibility(View.GONE);
            }

        });


       // progressBar.setVisibility(View.VISIBLE);
    }
}