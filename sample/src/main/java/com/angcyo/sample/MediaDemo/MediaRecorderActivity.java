package com.angcyo.sample.MediaDemo;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.angcyo.sample.R;

import java.io.IOException;

public class MediaRecorderActivity extends Activity implements SurfaceHolder.Callback {

    SurfaceView surfaceView;
    MediaRecorder mediaRecorder;
    long DELAY_TIME = 60 * 1000;
    Runnable swapFile = new SwapFileRunnable();

    public static String getFileName() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("/sdcard/");
        stringBuilder.append(FileSwapHelper.getTempFileName());
        stringBuilder.append(".mp4");
        return stringBuilder.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_recorder);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(this);
//        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private MediaRecorder initMediaRecorder(Surface surface, String filePath) throws IOException {
        //注意方法调用顺序
        MediaRecorder mediaRecorder = new MediaRecorder();
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);

        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        mediaRecorder.setVideoEncodingBitRate(12 * 1024 * 1024);

        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);

        int video_width = 1920;
        int video_height = 1080;
        mediaRecorder.setVideoSize(video_width, video_height);
        mediaRecorder.setVideoFrameRate(30);

        mediaRecorder.setPreviewDisplay(surface);

        mediaRecorder.setOutputFile(filePath);
        mediaRecorder.prepare();
        mediaRecorder.start();

        return mediaRecorder;
    }

    private void closeMediaRecorder(MediaRecorder mediaRecorder) {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceView.setKeepScreenOn(true);
        surfaceView.postDelayed(swapFile, DELAY_TIME);
        try {
            mediaRecorder = initMediaRecorder(holder.getSurface(), getFileName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceView.setKeepScreenOn(false);
        closeMediaRecorder(mediaRecorder);
    }

    private void resetMediaFileName() {
        if (mediaRecorder != null) {
            String fileName = getFileName();
            mediaRecorder.reset();
            mediaRecorder.setOutputFile(fileName);
            e("重置文件名:" + fileName);
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
                e("mediaRecorder 重置失败:" + e.getMessage());
            }
        }
    }

    private void e(String msg) {
        Log.e("angcyo-->", msg);
    }

    class SwapFileRunnable implements Runnable {
        @Override
        public void run() {
            resetMediaFileName();
            if (surfaceView != null) {
                surfaceView.postDelayed(swapFile, DELAY_TIME);
            }
        }
    }
}
