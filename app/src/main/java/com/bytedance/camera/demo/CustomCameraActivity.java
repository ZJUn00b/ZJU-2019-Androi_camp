package com.bytedance.camera.demo;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.ExifInterface;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.bytedance.camera.demo.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static com.bytedance.camera.demo.utils.Utils.MEDIA_TYPE_IMAGE;
import static com.bytedance.camera.demo.utils.Utils.MEDIA_TYPE_VIDEO;
import static com.bytedance.camera.demo.utils.Utils.getOutputMediaFile;

public class CustomCameraActivity extends AppCompatActivity {

    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Button mpause, mcontinue;
    private int CAMERA_TYPE = Camera.CameraInfo.CAMERA_FACING_BACK;

    private boolean isRecording = false;

    private int rotationDegree = 0;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_custom_camera);

        mSurfaceView = findViewById(R.id.img);
        //暂停和继续录制键
        mpause = findViewById(R.id.video_pro1);
        mcontinue = findViewById(R.id.video_pro2);
        mCamera = getCamera(CAMERA_TYPE);
        mCamera.setDisplayOrientation(getCameraDisplayOrientation(CAMERA_TYPE));
        //todo 给SurfaceHolder添加Callback
        mHolder = mSurfaceView.getHolder();
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (mCamera != null)
                        releaseCameraAndPreview();
                    mCamera = getCamera(CAMERA_TYPE);
                    mCamera.setPreviewDisplay(holder);
                    mCamera.startPreview();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        });

        findViewById(R.id.btn_picture).setOnClickListener(v -> {
            //todo 拍一张照片
            mCamera.takePicture(null, null, mPicture);
        });

        findViewById(R.id.btn_record).setOnClickListener(v -> {
            //todo 录制，第一次点击是start，第二次点击是stop
            if (isRecording) {
                //todo 停止录制
                releaseMediaRecorder();
                isRecording = false;
                findViewById(R.id.video_pro1).setVisibility(View.INVISIBLE);
                findViewById(R.id.video_pro2).setVisibility(View.INVISIBLE);
                Toast.makeText(getApplicationContext(), "结束录制", Toast.LENGTH_SHORT).show();
            } else {
                //todo 录制
                prepareVideoRecorder();
                isRecording = true;
                //录制时暂停键和继续键都可见
                findViewById(R.id.video_pro1).setVisibility(View.VISIBLE);
                findViewById(R.id.video_pro2).setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "开始录制", Toast.LENGTH_SHORT).show();
            }
        });


        findViewById(R.id.btn_facing).setOnClickListener(v -> {
            //todo 切换前后摄像头
            if (CAMERA_TYPE == Camera.CameraInfo.CAMERA_FACING_BACK) {
                rotationDegree = getCameraDisplayOrientation(Camera.CameraInfo.CAMERA_FACING_FRONT);
                try {
                    mCamera = getCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    mCamera.setDisplayOrientation(rotationDegree);
                    startPreview(mHolder);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                rotationDegree = getCameraDisplayOrientation(Camera.CameraInfo.CAMERA_FACING_BACK);
                try {
                    mCamera = getCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
                    mCamera.setDisplayOrientation(rotationDegree);
                    startPreview(mHolder);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.flash).setOnClickListener(v -> {
            //todo extra 监听闪光灯
            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters.getFlashMode().equals(android.hardware.Camera.Parameters.FLASH_MODE_TORCH)) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
            } else {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
            }
        });
        findViewById(R.id.btn_zoom).setOnClickListener(v -> {
            //todo 调焦，需要判断手机是否支持
            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters.isZoomSupported()) {
                int zoom = parameters.getZoom() + 5;
                if (zoom < parameters.getMaxZoom()) {
                    parameters.setZoom(zoom);
                    mCamera.setParameters(parameters);
                }
            }
        });
        findViewById(R.id.video_pro1).setOnClickListener(v -> {
            //暂停
            if (isRecording) {
                pauseVideoRecorder(mMediaRecorder);
                isRecording = false;
            }
        });
        findViewById(R.id.video_pro2).setOnClickListener(v -> {
            //继续
            if (isRecording == false) {
                resumeVideoRecorder(mMediaRecorder);
                isRecording = true;
            }
        });
    }


    public Camera getCamera(int position) {
        CAMERA_TYPE = position;
        if (mCamera != null) {
            releaseCameraAndPreview();
        }
        Camera cam = Camera.open(position);
        //todo 摄像头添加属性，例是否自动对焦，设置旋转方向等
        rotationDegree = getCameraDisplayOrientation(position);
        cam.setDisplayOrientation(rotationDegree);
        //done 自动对焦
        Camera.Parameters parameters = cam.getParameters();
        //只有后置相机支持自动对焦
        if (CAMERA_TYPE == Camera.CameraInfo.CAMERA_FACING_BACK)
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        if (mCamera != null && CAMERA_TYPE == Camera.CameraInfo.CAMERA_FACING_BACK) {

            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {
                    if (b) {
                        camera.cancelAutoFocus();

                        Toast.makeText(CustomCameraActivity.this, "auto focus success", Toast.LENGTH_LONG).show();
                    } else {
                        mCamera.autoFocus(this);//如果失败，自动聚焦
                    }
                }
            });
        }
        cam.setParameters(parameters);
        return cam;
    }


    private static final int DEGREE_90 = 90;
    private static final int DEGREE_180 = 180;
    private static final int DEGREE_270 = 270;
    private static final int DEGREE_360 = 360;

    private int getCameraDisplayOrientation(int cameraId) {
        Camera.CameraInfo info =
                new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = DEGREE_90;
                break;
            case Surface.ROTATION_180:
                degrees = DEGREE_180;
                break;
            case Surface.ROTATION_270:
                degrees = DEGREE_270;
                break;
            default:
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % DEGREE_360;
            result = (DEGREE_360 - result) % DEGREE_360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + DEGREE_360) % DEGREE_360;
        }
        return result;
    }


    private void releaseCameraAndPreview() {
        //todo 释放camera资源
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    Camera.Size size;

    private void startPreview(SurfaceHolder holder) {
        //todo 开始预览
        Camera.Parameters param = mCamera.getParameters();
        Camera.Size size = getOptimalPreviewSize(param.getSupportedPictureSizes(), mSurfaceView.getWidth(), mSurfaceView.getHeight());
        param.setPictureSize(size.width, size.height);
        mCamera.setParameters(param);

        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private MediaRecorder mMediaRecorder;

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean pauseVideoRecorder(MediaRecorder mMediaRecorder) {
        mMediaRecorder.pause();
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean resumeVideoRecorder(MediaRecorder mMediaRecorder) {
        mMediaRecorder.resume();
        return true;
    }



    private boolean prepareVideoRecorder() {
        //todo 准备MediaRecorder
        mMediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());
        //mMediaRecorder.setOutputFile(videoFile.getAbsolutePath());
        mMediaRecorder.setPreviewDisplay(mHolder.getSurface());
        mMediaRecorder.setOrientationHint(rotationDegree);

        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IOException e) {
            releaseMediaRecorder();
            e.printStackTrace();
        }
        return true;
    }


    private void releaseMediaRecorder() {
        //todo 释放MediaRecorder
        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder = null;
        mCamera.lock();

    }


    private Camera.PictureCallback mPicture = (data, camera) -> {
        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (pictureFile == null) {
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
            Toast.makeText(CustomCameraActivity.this, "save to " + pictureFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.d("mPicture", "Error accessing file: " + e.getMessage());
        }
        setPictureDegreeZero(pictureFile.getAbsolutePath());
        Utils.insertIntoGallery(pictureFile, this);
        mCamera.startPreview();
    };


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = Math.min(w, h);

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }


    //done 自动旋转
    private static final int[] ORIENTATION_MAP = new int[]{
            ExifInterface.ORIENTATION_NORMAL, ExifInterface.ORIENTATION_ROTATE_90,
            ExifInterface.ORIENTATION_ROTATE_180, ExifInterface.ORIENTATION_ROTATE_270
    };

    public void setPictureDegreeZero(String path) {
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = ORIENTATION_MAP[getCameraDisplayOrientation(CAMERA_TYPE) / 90];
            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION,
                    String.valueOf(orientation));
            exifInterface.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




