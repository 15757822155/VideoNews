package com.zhuoxin.videoplayer.full;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.zhuoxin.videoplayer.R;

import io.vov.vitamio.widget.MediaController;

/**
 * Created by Administrator on 2016/12/19.
 */

public class CustomMediaController extends MediaController {
    //自定义视频控制器
    private MediaPlayerControl mediaPlayerControl;
    //音频管理
    private AudioManager audioManager;
    //视频亮度管理
    private Window window;
    //最大音量
    private int maxVolume;
    //当前音量
    private int currentVolume;
    //当前电量
    private float currentBrightness;

    public CustomMediaController(Context context) {
        super(context);
        //音频管理
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //获得最大音量
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //用于视频亮度管理
        window = ((Activity) context).getWindow();

    }

    //通过从写此方法，来自定义layout
    @Override
    protected View makeControllerView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_custom_video_controller, this);
        initView(view);
        return view;
    }

    //拿到自定义视频控制器
    @Override
    public void setMediaPlayer(MediaPlayerControl player) {
        super.setMediaPlayer(player);
        mediaPlayerControl = player;
    }

    //初始化视图,设置一些监听
    private void initView(View view) {
        //开进快退的监听
        ImageButton btnFastForward = (ImageButton) view.findViewById(R.id.btnFastForward);
        btnFastForward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取当前位置
                long position = mediaPlayerControl.getCurrentPosition();
                //位置加10秒
                position += 10000;
                //如果加10秒后,大于视频的总长度
                if (position > mediaPlayerControl.getDuration()) {
                    position = mediaPlayerControl.getDuration();
                }
                //跳转到设定好的位置
                mediaPlayerControl.seekTo(position);
            }
        });

        ImageButton btnFastRewind = (ImageButton) view.findViewById(R.id.btnFastRewind);
        btnFastRewind.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                long position = mediaPlayerControl.getCurrentPosition();
                position -= 10000;
                if (position < 0) {
                    position = 0;
                }
                mediaPlayerControl.seekTo(position);
            }
        });
        //屏幕亮度，音量的控制
        final View adjustView = view.findViewById(R.id.adjustView);
        final GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                //开始位置的x的位置
                float startX = e1.getX();
                //开始位置的y的位置
                float startY = e1.getY();
                //结束位置的x的位置
                float endX = e2.getX();
                //结束位置的y的位置
                float endY = e2.getY();
                //获取屏幕的宽度高度
                float height = adjustView.getHeight();
                float width = adjustView.getWidth();
                //获取移动的百分比
                float percentage = (startY - endY) / height;
                //电量调整
                if (startX < width / 5) {
                    //调整电量的方法
                    adjustBrightness(percentage);
                }
                //音频调整
                if (startX > 4 * width / 5) {
                    //调整音量的方法
                    adjustVolume(percentage);
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });
        //对adjustView(调整试图)进行一个touch监听
        //但是不判断touch动用了什么,都让gestureDetector进行操作;
        adjustView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //当用户按下的时候
                if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                    //获得当前音量
                    currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    //获得当前亮度
                    currentBrightness = window.getAttributes().screenBrightness;
                }
                //交给gestureDetector去做
                gestureDetector.onTouchEvent(event);
                //在调整过程中一直显示(比如音量调整条,亮度调整条)
                show();
                return true;
            }
        });
    }

    private void adjustVolume(float percentage) {
        //最终音量=最大音量*改变百分比+当前音量
        int volume = (int) (maxVolume * percentage + currentVolume);
        //如果最终音量大于最大音量,就等于最大音量
        volume = volume > maxVolume ? maxVolume : volume;
        //如果最终音量小于最小音量,就等于最小音量
        volume = volume < 0 ? 0 : volume;
        //设置音量
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, AudioManager.FLAG_SHOW_UI);

    }

    //最小亮度=0.最大亮度=1.0f
    private void adjustBrightness(float percentage) {
        //最终亮度=亮度百分比+当前亮度
        float brightness = percentage + currentBrightness;
        brightness = brightness > 1.0f ? 1.0f : brightness;
        brightness = brightness < 0 ? 0 : brightness;
        //设置亮度
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = brightness;
        window.setAttributes(layoutParams);
    }
}
