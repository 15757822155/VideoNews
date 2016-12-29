package com.zhuoxin.videoplayer.part;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.zhuoxin.videoplayer.R;
import com.zhuoxin.videoplayer.full.VideoViewActivity;

import java.io.IOException;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;

/**
 * Created by Administrator on 2016/12/16.
 * <p>
 * 一个自定义的VideoPlayer，使用MdeiaPlayer+SurfaceView来实现视频的播放
 * MediaPlayer来做视频播放的控制，SurfaceView来显示视频
 * 视图方面（initView初始化）简单实现：一个播放/暂停按钮，一个进度条，一个全屏按钮，一个SurfaceView
 * <p>
 * 结构：
 * 提供setVideoPath方法：设置数据源
 * 提供OnResume方法(在activity的onResume调用)：初始化MediaPlayer，准备MediaPlayer
 * 提供OnPause方法(在activity的onPause调用)：释放mediaplayer，暂停mediaplayer
 */


public class SimpleVideoPlayer extends FrameLayout {
    //进度条最大为1000
    private static final int PROGRESS_MAX = 1000;
    //视频播放路径
    private String videoPath;
    private MediaPlayer mediaPlayer;
    //是否准备好
    private boolean isPrepared;
    //是否正在播放
    private boolean isPlaying;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    //视频预览图
    private ImageView ivPreview;
    //播放暂停按钮
    private ImageView btnToggle;
    //播放进度条
    private ProgressBar progressBar;

    public SimpleVideoPlayer(Context context) {
        this(context, null);
    }

    public SimpleVideoPlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleVideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //视图相关的初始化
    private void init() {
        //vitamio的初始化,必须有
        Vitamio.isInitialized(getContext());
        //填充布局
        LayoutInflater.from(getContext()).inflate(R.layout.view_simple_video_player, this, true);
        //初始化surfaceview
        initSurfaceView();
        //初始化视频播放控制视图
        initControllerViews();
    }

    //设置路径的方法,设置数据源
    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    //提供OnResume方法(在activity的onResume调用)：
    public void onResume() {
        // 初始化MediaPlayer，设置一系列监听器
        initMediaPlayer();
        // 准备MediaPlayer，同时更新UI状态
        prepareVideoPlayer();
    }

    //提供OnPause方法(在activity的onPause调用)：
    public void onPause() {
        // 暂停播放，同时更新UI状态
        pauseMediaPlayer();
        // 释放MediaPlayer，同时更新UI状态
        releaseMediaPlayer();
    }

    //########################################  项目结构完毕  ##################
    //初始化SurfaceView
    private void initSurfaceView() {
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        //注意，vitamio使用surfaceview要设置pixelFormat，否则会花屏
        surfaceHolder.setFormat(PixelFormat.RGBA_8888);
    }

    //初始化视频播放控制视图
    private void initControllerViews() {
        //预览图
        ivPreview = (ImageView) findViewById(R.id.ivPreview);
        //播放，暂停
        btnToggle = (ImageView) findViewById(R.id.btnToggle);
        btnToggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //判断是否在播放
                if (mediaPlayer.isPlaying()) {
                    //如果在播放,点击暂停
                    pauseMediaPlayer();
                } else if (isPrepared) {
                    //如果正在准备,点击开始播放
                    startMediaPlayer();
                } else {
                    //其他情况,toast
                    Toast.makeText(getContext(), "Can't play now！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //设置进度条
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //设置进度条的最大值
        progressBar.setMax(PROGRESS_MAX);
        //全屏播放按钮
        ImageButton btnFullScreen = (ImageButton) findViewById(R.id.btnFullScreen);
        btnFullScreen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                VideoViewActivity.open(getContext(), videoPath);
            }
        });
    }

    // 初始化MediaPlayer，设置一系列监听器
    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer(getContext());
        //mediaplayer与SurfaceHolder绑定
        mediaPlayer.setDisplay(surfaceHolder);
        //准备的监听
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //初始化状态
                isPrepared = true;
                startMediaPlayer();
            }
        });
        //audio处理，info监听(vitamio5.0后必须要做)
        mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if (what == MediaPlayer.MEDIA_INFO_FILE_OPEN_OK) {
                    mediaPlayer.audioInitedOk(mediaPlayer.audioTrackInit());
                    return true;
                }
                return false;
            }
        });
        //视频尺寸大小改变(全屏,半屏)监听
        mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                //获取到视频的宽高
                int layoutWidth = surfaceView.getWidth();
                int layoutHeight = layoutWidth * height / width;
                //更新surfaceview的size
                ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
                params.width = layoutWidth;
                params.height = layoutHeight;
                surfaceView.setLayoutParams(params);
            }
        });
    }

    // 准备MediaPlayer，同时更新UI状态
    private void prepareVideoPlayer() {
        try {
            //重置mediaplayer
            mediaPlayer.reset();
            mediaPlayer.setDataSource(videoPath);
            //设置循环播放
            mediaPlayer.setLooping(true);
            //异步准备
            mediaPlayer.prepareAsync();
            //准备阶段,预览图可见
            ivPreview.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //开始播放
    private void startMediaPlayer() {
        //预览图不可见
        ivPreview.setVisibility(View.INVISIBLE);
        btnToggle.setImageResource(R.drawable.ic_pause);
        mediaPlayer.start();
        isPlaying = true;
        handler.sendEmptyMessage(0);
    }

    //暂停mediaplayer
    private void pauseMediaPlayer() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        isPlaying = false;
        btnToggle.setImageResource(R.drawable.ic_play_arrow);
        handler.removeMessages(0);
    }

    //用handler更新播放进度条
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isPlaying) {
                //每0.2秒更新一次播放进度
                int progress = (int) (mediaPlayer.getCurrentPosition() * PROGRESS_MAX / mediaPlayer.getDuration());
                progressBar.setProgress(progress);
                //发送一个空的延迟消息，不停调用本身，执行内部方法，实现自动更新进度条
                handler.sendEmptyMessageDelayed(0, 200);
            }
        }
    };

    //：释放mediaplayer
    private void releaseMediaPlayer() {
        mediaPlayer.release();
        mediaPlayer = null;
        isPrepared = false;
        progressBar.setProgress(0);
    }
}