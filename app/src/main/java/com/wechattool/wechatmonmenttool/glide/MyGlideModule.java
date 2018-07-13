package com.wechattool.wechatmonmenttool.glide;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;
import com.wechattool.wechatmonmenttool.util.FileUtil;
import java.io.File;
import java.io.InputStream;

/**
 * Created by qwy on 2016/3/15.
 * glide
 */
public class MyGlideModule implements GlideModule {
    @Override
    public void applyOptions(final Context context, GlideBuilder builder) {
        builder.setDiskCache(new DiskCache.Factory() {
            @Override
            public DiskCache build() {
                File file = new File(FileUtil.getImageCachePath(context));
                return DiskLruCacheWrapper.get(file, 50 * 1024 * 1024);
            }
        });
        builder.setMemoryCache(new LruResourceCache(200 * 1024 * 1024));
        builder.setBitmapPool(new LruBitmapPool(200 * 2014 * 1024));

    }

    @Override
    public void registerComponents(Context context, Glide glide) {
//        glide.register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
    }
}
