package com.percy.minidouyin.view.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.percy.minidouyin.R;
import com.percy.minidouyin.api.IMiniDouyinService;
import com.percy.minidouyin.camera.CustomCameraActivity;
import com.percy.minidouyin.model.GetResponse;
import com.percy.minidouyin.model.PostResponse;
import com.percy.minidouyin.model.Video;
import com.percy.minidouyin.util.Utils;
import com.percy.minidouyin.util.saveImage;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * 在此情况下使用的都为自定义相机
 */
public class VideoWFViewActivity extends AppCompatActivity {
    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final int REQUEST_EXTERNAL_CAMERA = 101;
    private static final int REQUEST_CUSTOM_VIDEO_CAPTURE = 4;
    String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private static final String TAG = "VideoWFViewActivity";
    public Uri mSelectedImage;
    private Uri mSelectedVideo;
    //接收到来自登录界面的姓名和学号
    private String mID, mNAME;
    //分别设置取封面和取视频的信号
    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;
    //My RecyclerView
    private RecyclerView mRv;
    private List<Video> mVideos = new ArrayList<>();

    //下拉刷新组件
    private SwipeRefreshLayout swiperereshlayout;

    static private String thumbTextNum = (int) (1 + Math.random() * (10 - 1 + 1)) + 10 + "";
    private ImageButton imageButton;
    Button mBtnRefresh;

    //------------------------登录界面后解决网路请求
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(IMiniDouyinService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private IMiniDouyinService miniDouyinService = retrofit.create(IMiniDouyinService.class);

    private IMiniDouyinService getMiniDouyinService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(IMiniDouyinService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        if (miniDouyinService == null) {
            miniDouyinService = retrofit.create(IMiniDouyinService.class);
        }

        return miniDouyinService;
    }
//---------------------------------------------------------------------------------------


    /**
     * 下拉刷新
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stream);
        Intent mIntent = getIntent();
        mID = mIntent.getStringExtra("ID");
        mNAME = mIntent.getStringExtra("NAME");
        //Toast.makeText(VideoWFViewActivity.this, ""+mNAME+mID, Toast.LENGTH_LONG).show();
        initRecyclerView();
        initBtns();
        //进入界面后第一次自动刷新
        swiperereshlayout.setRefreshing(true);
        fetchFeed();
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


        /*swiperereshlayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light);*/
        //给swipeRefreshLayout绑定刷新监听
        swiperereshlayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchFeed();
            }
        });

        imageButton = findViewById(R.id.F_btn);
        imageButton.setOnClickListener(v -> {
            if (Utils.isPermissionsReady(this, permissions)) {
                //todo 拉起自定义摄像机
                startActivityForResult(new Intent(VideoWFViewActivity.this, CustomCameraActivity.class), REQUEST_CUSTOM_VIDEO_CAPTURE);
            } else {
                //todo 权限检查
                Utils.reuqestPermissions(this, permissions, REQUEST_VIDEO_CAPTURE);
                Utils.reuqestPermissions(this, permissions, REQUEST_EXTERNAL_CAMERA);
            }
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;
        public TextView idText, authorText, updateText, thumbText;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //和item中的图片控件进行绑定
            img = itemView.findViewById(R.id.coverView);
            //对文字控件也进行绑定
            //学号
            idText = itemView.findViewById(R.id.idText);
            //姓名
            authorText = itemView.findViewById(R.id.authorText);
            //时间戳
            updateText = itemView.findViewById(R.id.updateText);
            //点赞数
            thumbText = itemView.findViewById(R.id.thumbText);
        }

        public void bind(final Activity activity, final Video video) {
            //对item中的元素进行绑定(用glide实现绑定)
            //封面
            Glide.with(img.getContext()).load(video.getImageUrl()).into(img);
            idText.setText(video.getStudentId());
            authorText.setText(video.getUserName());
            updateText.setText(video.getupdatedAt().toLocaleString());
            //随机点赞数

            thumbText.setText(thumbTextNum);


            String text1 = video.getImage_h() + "";
            String text2 = video.getImage_w() + "";
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
                                .inflate(R.layout.stream_item, viewGroup, false));
            }

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
            if (requestCode == REQUEST_CUSTOM_VIDEO_CAPTURE) {
                Uri mVideoUri = data.getData();
                mSelectedVideo = mVideoUri;

                Log.d(TAG, "mSelectedVideo = " + mSelectedVideo);
                // mBtn.setText(R.string.post_it);
                //todo 拿到了video之后马上自动生成封面
                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(mSelectedVideo.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
                Date date = new Date(System.currentTimeMillis());
                if (bitmap != null) Log.d(TAG, "mSelectedImage null = " + mSelectedImage);
                Uri uri = saveImage.saveImage(bitmap, "minidouyin" + date + ".jpeg", VideoWFViewActivity.this);
                mSelectedImage = uri;
                Log.d(TAG, "mSelectedImage = " + mSelectedImage);
                postVideo(mSelectedImage, mSelectedVideo);
            }
        }
    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        File f = new File(uri.getPath());
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    private void postVideo(Uri mSelectedImage, Uri mSelectedVideo) {
        MultipartBody.Part coverImagePart = getMultipartFromUri("cover_image", mSelectedImage);
        MultipartBody.Part videoPart = getMultipartFromUri("video", mSelectedVideo);
        Log.d(TAG, "post");
        // TODO 9: post video & update buttons
        Call<PostResponse> call = getMiniDouyinService().postVideo(mID + "11", mNAME + "11", coverImagePart, videoPart);
        call.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if (response.body() != null && response.body().getSuccess()) {
                    //Toast.makeText(VideoWFViewActivity.this,"uploaded",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                //Toast.makeText(VideoWFViewActivity.this,"upload fail",Toast.LENGTH_SHORT).show();
            }
        });

    }

    //get videos & update recycler list
    public void fetchFeed() {
        Call<GetResponse> call = getMiniDouyinService().getVideos();
        call.enqueue(new Callback<GetResponse>() {
            @Override
            public void onResponse(Call<GetResponse> call, Response<GetResponse> response) {
                if (response.body() != null && response.body().getSuccess()) {
                    mVideos = response.body().getVideos();
                    initRecyclerView();
                    swiperereshlayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<GetResponse> call, Throwable t) {
                Toast.makeText(VideoWFViewActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}




