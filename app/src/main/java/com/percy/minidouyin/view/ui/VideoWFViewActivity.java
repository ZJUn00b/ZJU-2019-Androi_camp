package com.percy.minidouyin.view.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bytedance.androidcamp.network.lib.util.ImageHelper;
import com.percy.minidouyin.R;
import com.percy.minidouyin.api.IMiniDouyinService;
import com.percy.minidouyin.model.GetResponse;
import com.percy.minidouyin.model.PostResponse;
import com.percy.minidouyin.model.Video;
import com.percy.minidouyin.util.ResourceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VideoWFViewActivity extends AppCompatActivity {
    private static final String TAG = "VideoWFViewActivity";
    public Uri mSelectedImage;
    private Uri mSelectedVideo;
    //分别设置取封面和取视频的信号
    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;
    //My RecyclerView
    private RecyclerView mRv;
    private List<Video> mVideos = new ArrayList<>();

    //下拉刷新组件
    private SwipeRefreshLayout swiperereshlayout ;

    Button mBtnRefresh;

    //------------------------登录界面后解决网路请求
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(IMiniDouyinService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private IMiniDouyinService miniDouyinService = retrofit.create(IMiniDouyinService.class);

    private IMiniDouyinService getMiniDouyinService()
    {
        if(retrofit == null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl(IMiniDouyinService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        if(miniDouyinService == null)
        {
            miniDouyinService = retrofit.create(IMiniDouyinService.class);
        }

        return miniDouyinService;
    }
//---------------------------------------------------------------------------------------

    //实现瀑布流布局的页面

    /**
    *下拉刷新
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stream);
        initRecyclerView();
        initBtns();

    }

    private void initBtns() {
      //  mBtn = findViewById(R.id.btn);
      /*  mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mBtn.getText().toString();
                if (getString(R.string.select_an_image).equals(s)) {
                    chooseImage();
                } else if (getString(R.string.select_a_video).equals(s)) {
                    chooseVideo();
                } else if (getString(R.string.post_it).equals(s)) {
                    if (mSelectedVideo != null && mSelectedImage != null) {
                        postVideo();
                    } else {
                        throw new IllegalArgumentException("error data uri, mSelectedVideo = "
                                + mSelectedVideo
                                + ", mSelectedImage = "
                                + mSelectedImage);
                    }
                } else if ((getString(R.string.success_try_refresh).equals(s))) {
                    mBtn.setText(R.string.select_an_image);
                }
            }
        });*/

        //mBtnRefresh = findViewById(R.id.btn_refresh);

        swiperereshlayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        mRv = findViewById(R.id.streamList);


        swiperereshlayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light);
        //给swipeRefreshLayout绑定刷新监听
        swiperereshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchFeed();
            }
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;
        public TextView t1,t2;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //和item中的图片控件进行绑定
            img = itemView.findViewById(R.id.coverView);


        }

        public void bind(final Activity activity, final Video video) {
            ImageHelper.displayWebImage(video.getImageUrl(), img);
            String text1 = video.getImage_h()+"";
            String text2 = video.getImage_w()+"";
           // t1.setText(text1);
            //t2.setText(text2);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoActivity.launch(activity, video.getVideoUrl());
                }
            });
        }
    }

    private void initRecyclerView() {
        mRv = findViewById(R.id.streamList);
        //if
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(new RecyclerView.Adapter<MyViewHolder>() {
            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new MyViewHolder(
                        LayoutInflater.from(VideoWFViewActivity.this)
                                .inflate(R.layout.stream_item, viewGroup, false)); }

            @Override
            public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
                final Video video = mVideos.get(i);
                viewHolder.bind(VideoWFViewActivity.this, video);
            }

            @Override
            public int getItemCount() {
                return mVideos.size();
            }
        });
    }

    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE);
    }

    public void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult() called with: requestCode = ["
                + requestCode
                + "], resultCode = ["
                + resultCode
                + "], data = ["
                + data
                + "]");

        if (resultCode == RESULT_OK && null != data) {
            if (requestCode == PICK_IMAGE) {
                mSelectedImage = data.getData();
                Log.d(TAG, "selectedImage = " + mSelectedImage);
               // mBtn.setText(R.string.select_a_video);
            } else if (requestCode == PICK_VIDEO) {
                mSelectedVideo = data.getData();
                Log.d(TAG, "mSelectedVideo = " + mSelectedVideo);
               // mBtn.setText(R.string.post_it);
            }
        }
    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        File f = new File(ResourceUtils.getRealPath(VideoWFViewActivity.this, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    private void postVideo() {
        //mBtn.setText("POSTING...");
        //mBtn.setEnabled(false);
        MultipartBody.Part coverImagePart = getMultipartFromUri("cover_image", mSelectedImage);
        MultipartBody.Part videoPart = getMultipartFromUri("video", mSelectedVideo);
        // TODO 9: post video & update buttons
        Call<PostResponse> call = getMiniDouyinService().postVideo("3170102492","hootch",coverImagePart,videoPart);
        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if(response.body() != null && response.body().getSuccess())
                {
                    //mBtn.setEnabled(true);
                    //mBtn.setText(R.string.select_an_image);
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
               // mBtn.setEnabled(true);
               // mBtn.setText(R.string.select_an_image);
               // Toast.makeText(MainActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    //get videos & update recycler list
    public void fetchFeed() {
        Call<GetResponse> call = getMiniDouyinService().getVideos();
        call.enqueue(new Callback<GetResponse>() {
            @Override
            public void onResponse(Call<GetResponse> call, Response<GetResponse> response) {
                if(response.body() != null && response.body().getSuccess())
                {
                    mVideos = response.body().getVideos();
                    initRecyclerView();
                    swiperereshlayout.setRefreshing(false);
                }
            }
            @Override
            public void onFailure(Call<GetResponse> call, Throwable t) {
                Toast.makeText(VideoWFViewActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });


    }
}




