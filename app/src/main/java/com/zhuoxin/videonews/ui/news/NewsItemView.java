package com.zhuoxin.videonews.ui.news;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zhuoxin.videonews.R;
import com.zhuoxin.videonews.UserManager;
import com.zhuoxin.videonews.bombapi.entity.NewsEntity;
import com.zhuoxin.videonews.commons.CommonUtils;
import com.zhuoxin.videonews.ui.base.BaseItemView;
import com.zhuoxin.videonews.ui.news.comments.CommentsActivity;
import com.zhuoxin.videoplayer.list.MediaPlayerManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/12/26.
 * 新闻列表的单项试图,将使用MediaPlayer播放视频，TextureView来显示视频
 */

public class NewsItemView
        extends BaseItemView<NewsEntity>
        implements TextureView.SurfaceTextureListener, MediaPlayerManager.OnPlaybackListener {

    @BindView(R.id.textureView)
    TextureView textureView; // 用来展现视频的TextureView
    @BindView(R.id.ivPreview)
    ImageView ivPreview;
    @BindView(R.id.tvNewsTitle)
    TextView tvNewsTitle;
    @BindView(R.id.tvCreatedAt)
    TextView tvCreatedAt;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.ivPlay)
    ImageView ivPlay;


    private NewsEntity newsEntity;
    private MediaPlayerManager mediaPlayerManager;
    private Surface surface;

    public NewsItemView(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.item_news, this, true);
        ButterKnife.bind(this);
        //添加列表视频播放控制相关监听
        mediaPlayerManager = MediaPlayerManager.getsInstance(getContext());
        mediaPlayerManager.addPlayerBackListener(this);
        //textureView -> surface相关监听
        textureView.setSurfaceTextureListener(this);
    }

    @Override
    protected void bindModel(NewsEntity newsEntity) {
        this.newsEntity = newsEntity;
        //初始化视图
        tvNewsTitle.setVisibility(View.VISIBLE);//标题
        ivPreview.setVisibility(View.VISIBLE);//缩略图
        progressBar.setVisibility(View.VISIBLE);
        ivPlay.setVisibility(View.VISIBLE);//播放按钮
        //设置标题,创建时间,预览图的内容
        tvNewsTitle.setText(newsEntity.getNewsTitle());
        tvCreatedAt.setText(CommonUtils.format(newsEntity.getCreatedAt()));//用工具包进行格式转换
        //设置预览图像(Picasso),因为服务器返回的是带中文的地址,所有需要通过工具包转换
        String url = CommonUtils.encodeUrl(newsEntity.getPreviewUrl());
        Picasso.with(getContext())
                .load(url)
                .into(ivPreview);
    }

    //点击事件，跳转到评论页面
    @OnClick(R.id.tvCreatedAt)
    public void navigateToComments() {
        // TODO: 2016/12/26 0026 跳转到评论页面
        CommentsActivity.open(getContext(), newsEntity);
    }

    //点击预览图，开始播放
    @OnClick(R.id.ivPreview)
    public void startPlayer() {
        if (surface == null) {
            return;
        }
        //因为viewpager有缓存机制，需要控制视频的停止
        UserManager.getInstance().setPlay(false);
        String path = newsEntity.getVideoUrl();
        String videoId = newsEntity.getObjectId();
        mediaPlayerManager.startPlayer(surface, path, videoId);
    }

    //点击视频,停止播放
    @OnClick(R.id.textureView)
    public void stopPlayer() {
        mediaPlayerManager.stopPlayer();
        //因为viewpager有缓存机制,需要控制食品的停止
        UserManager.getInstance().setPlay(false);
    }

    // 判断是否操作当前的视频
    private boolean isCurrentVideo(String videoId) {
        if (videoId == null || newsEntity == null) {
            return false;
        }
        return videoId.equals(newsEntity.getObjectId());
    }


    //############### playbackListener  start  ####################

    @Override
    public void onStartBuffering(String videoId) {
        if (isCurrentVideo(videoId)) {
            //将当前视频的prb显示出来
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStopBuffering(String videoId) {
        if (isCurrentVideo(videoId)) {
            //将当前视频的prb隐藏
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onStartPlay(String videoId) {
        if (isCurrentVideo(videoId)) {
            tvNewsTitle.setVisibility(View.INVISIBLE);
            ivPreview.setVisibility(View.INVISIBLE);
            ivPlay.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onStopPlay(String videoId) {
        if (isCurrentVideo(videoId)) {
            tvNewsTitle.setVisibility(View.VISIBLE);
            ivPreview.setVisibility(View.VISIBLE);
            ivPlay.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onSizeMeasured(String videoId, int width, int height) {
        if (isCurrentVideo(videoId)) {
            //无需求，不做处理
        }
    }

    //############### playbackListener  end  ####################

    //############### Texture  start  ####################
    //textureView -> surface相关监听
    //拿到Surface
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        this.surface = new Surface(surface);
    }

    //当surface
    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    //当surface销毁时，停止播放
    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        this.surface.release();
        this.surface = null;
        // 停止自己
        if (newsEntity.getObjectId().equals(mediaPlayerManager.getVideoId())) {
            mediaPlayerManager.stopPlayer();
        }
        return false;

    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
    //############### Texture  end  ####################
}
