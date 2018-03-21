package com.king.zlei.drawabledemo;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

/**
 * <b>Create Date:</b> 2018/3/12<br>
 * <b>Author:</b> Zhanglei<br>
 * <b>Description:</b> 不需要自定义view实现圆角图片<br>
 *     参考内容：
 *     http://blog.csdn.net/lmj623565791/article/details/43752383
 *     自定义操作：
 *     http://blog.csdn.net/lmj623565791/article/details/41967509
 */

public class RoundImageDrawable extends Drawable {
    private Paint mPaint;
    private Bitmap mBitmap;
    private RectF mRect;
    private int x;
    private int y;

    public RoundImageDrawable(Bitmap bitmap, int x, int y) {
        mBitmap = bitmap;
        this.x = x;
        this.y = y;
        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setShader(bitmapShader);
    }

    //设置绘制的范围
    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        mRect = new RectF(left, top, right, bottom);
    }

    //核心代码
    @Override
    public void draw(Canvas canvas) {
        canvas.drawRoundRect(mRect, x, y, mPaint);
    }

    //view使用wrap_content的时候提供一下尺寸
    @Override
    public int getIntrinsicHeight() {
        return mBitmap.getHeight();
    }

    //view使用wrap_content的时候提供一下尺寸
    @Override
    public int getIntrinsicWidth() {
        return mBitmap.getWidth();
    }

    //必须实现的方法
    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    //必须实现的方法
    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    //必须实现的方法
    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }
}
