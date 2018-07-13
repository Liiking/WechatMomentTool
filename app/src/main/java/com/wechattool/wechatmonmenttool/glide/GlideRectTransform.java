package com.wechattool.wechatmonmenttool.glide;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by qwy on 2016/3/14.
 * Glide平行四边形图片转换
 */
public class GlideRectTransform extends BitmapTransformation {

    private static float radius = 0f;

    public GlideRectTransform(Context context) {
        this(context, 2);
    }

    public GlideRectTransform(Context context, int dp) {
        super(context);
        this.radius = Resources.getSystem().getDisplayMetrics().density * dp;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return roundCrop(pool, toTransform);
    }

    private static Bitmap roundCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        int offset = source.getWidth() / 4;
        RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
        Path path = new Path();
        path.moveTo(offset, rectF.left);
        path.lineTo(rectF.left, rectF.bottom);
        path.lineTo(rectF.right - offset, rectF.bottom);
        path.lineTo(rectF.right, 0);
        canvas.drawPath(path, paint);
//        canvas.drawRoundRect(rectF, radius, radius, paint);
        return result;
    }

    @Override
    public String getId() {
        return getClass().getName() + Math.round(radius);
    }
}
