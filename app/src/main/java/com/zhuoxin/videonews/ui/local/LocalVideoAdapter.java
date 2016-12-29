package com.zhuoxin.videonews.ui.local;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016/12/28.
 */

public class LocalVideoAdapter extends CursorAdapter {
    // 用来加载视频预览图的线程池
    private final ExecutorService mExecutorService = Executors.newFixedThreadPool(3);

    //用来缓存加载过的图像
    private LruCache<String, Bitmap> mLruCache = new LruCache<String, Bitmap>(5 * 1024 * 1024) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getHeight() * value.getWidth();
        }
    };

    public LocalVideoAdapter(Context context) {
        super(context, null, true);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return new LocalVideoItem(context);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final LocalVideoItem item = (LocalVideoItem) view;
        item.bind(cursor);

        //拿到文件路径
        final String filePath = item.getFilePath();
        //先查看是否缓存
        Bitmap bitmap = mLruCache.get(filePath);
        if (bitmap != null) {
            item.setIvPreview(bitmap);
            return;
        }
        //后台线程获取视频预览图
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                //加载预览图像
                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND);
                //缓存当前的预览图,文件路径为key
                mLruCache.put(filePath, bitmap);
                //将预览图设置到控件上
                item.setIvPreview(filePath, bitmap);
            }
        });
    }

    //关闭线程池
    public void release() {
        mExecutorService.shutdown();
    }
}
