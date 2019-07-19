package com.percy.minidouyin.view.ui;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
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
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.percy.minidouyin.R;
import com.percy.minidouyin.api.IMiniDouyinService;
import com.percy.minidouyin.model.GetResponse;
import com.percy.minidouyin.model.PostResponse;
import com.percy.minidouyin.model.Video;
import com.percy.minidouyin.util.ResourceUtils;
import com.percy.minidouyin.util.Utils;
import com.percy.minidouyin.util.saveImage;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class VideoStreamActivity extends AppCompatActivity {
    //瀑布流视图的实现

    /**
    * 在此情况下使用的都为系统相机
    * */
    private static final String TAG = "VideoStreamActivity";
    public Uri mSelectedImage;
    private Uri mSelectedVideo;
    private ImageButton imageButton;
    private VideoView videoView;
    private static final int REQUEST_VIDEO_CAPTURE = 1;

    private static final int REQUEST_EXTERNAL_CAMERA = 101;
    String[] permissions = new String[] {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
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
        //进入界面后第一次自动刷新
        swiperereshlayout.setRefreshing(true);
        fetchFeed();
    }

    private void initBtns() {
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
        imageButton = findViewById(R.id.F_btn);
        imageButton.setOnClickListener(v->{
            if (Utils.isPermissionsReady(this, permissions)) {
                //todo 打开摄像机
                openVideoRecordApp();
            } else {
                //todo 权限检查
                Utils.reuqestPermissions(this,permissions,REQUEST_VIDEO_CAPTURE);
                Utils.reuqestPermissions(this, permissions, REQUEST_EXTERNAL_CAMERA);
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_VIDEO_CAPTURE:{
                Toast.makeText(VideoStreamActivity.this, "已获取相机权限", Toast.LENGTH_LONG).show();
                break;
            }
            case REQUEST_EXTERNAL_CAMERA: {
                //todo 判断权限是否已经授予
                if (Utils.isPermissionsReady(this, permissions)) {
                    //todo 打开摄像机
                    openVideoRecordApp();
                }
                break;
            }
        }
    }
    private void openVideoRecordApp(){
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if(takeVideoIntent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(takeVideoIntent,REQUEST_VIDEO_CAPTURE);
        }
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

            //使用glide来加载
            Glide.with(img.getContext()).load(video.getImageUrl()).into(img);
            //ImageHelper.displayWebImage(video.getImageUrl(), img);
            String text1 = video.getImage_h()+"";
            String text2 = video.getImage_w()+"";
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
        mRv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRv.setAdapter(new RecyclerView.Adapter<VideoStreamActivity.MyViewHolder>() {
            @NonNull
            @Override
            public VideoStreamActivity.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                return new VideoStreamActivity.MyViewHolder(
                        LayoutInflater.from(VideoStreamActivity.this)
                                .inflate(R.layout.staggered_item, viewGroup, false)); }

            @Override
            public void onBindViewHolder(@NonNull VideoStreamActivity.MyViewHolder viewHolder, int i) {
                final Video video = mVideos.get(i);
                viewHolder.bind(VideoStreamActivity.this, video);
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
        //Toast.makeText(VideoStreamActivity.this, "enter result", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onActivityResult() called with: requestCode = ["
                + requestCode
                + "], resultCode = ["
                + resultCode
                + "], data = ["
                + data
                + "]");

        if (resultCode == RESULT_OK && null != data) {
            if (requestCode == PICK_IMAGE) {
                mSelectedVideo = data.getData();
                Log.d(TAG, "mSelectedVideo = " + mSelectedVideo);
                //Toast.makeText(VideoStreamActivity.this, "all video granted", Toast.LENGTH_LONG).show();

                //todo 拿到了video之后马上自动生成封面

                  Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(ResourceUtils.getRealPath(VideoStreamActivity.this, mSelectedVideo), MediaStore.Video.Thumbnails.MINI_KIND);
                    //MediaMetadataRetriever media = new MediaMetadataRetriever();
                   // media.setDataSource(String.valueOf(mSelectedVideo));
                    Log.d(TAG, "mSelectedImage = " + mSelectedImage);
                    //Bitmap bitmap  = media.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC );
                   Log.d(TAG, "mSelectedImage = " + mSelectedImage);
                   // imageView.setImageBitmap(bitmap);//对应的ImageView
                   Date date = new Date(System.currentTimeMillis());
                    if (bitmap==null) Log.d(TAG, "mSelectedImage null = " + mSelectedImage);
                   Uri uri = saveImage.saveImage(bitmap, "minidouyin"+date+".jpeg",VideoStreamActivity.this);
                    mSelectedImage = uri;
                    Log.d(TAG, "mSelectedImage = " + mSelectedImage);
                    postVideo();


            }
        }
    }




    public static Bitmap retriveVideoFrameFromVideo(String videoPath)
            throws Throwable
    {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try
        {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Throwable(
                    "Exception in retriveVideoFrameFromVideo(String videoPath)"
                            + e.getMessage());

        }
        finally
        {
            if (mediaMetadataRetriever != null)
            {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }
    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        File f = new File(ResourceUtils.getRealPath(VideoStreamActivity.this, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    private void postVideo() {
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
                Toast.makeText(VideoStreamActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
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
                Toast.makeText(VideoStreamActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });




    }


}
