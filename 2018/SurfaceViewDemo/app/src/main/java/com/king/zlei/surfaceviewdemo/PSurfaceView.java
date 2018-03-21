package com.king.zlei.surfaceviewdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * <b>Create Date:</b> 2018/3/20<br>
 * <b>Author:</b> Zhanglei<br>
 * <b>Description:</b> <br>
 */

public class PSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private SurfaceHolder mSurfaceHolder;
    private Canvas mCanvas;
    private Thread mThread;
    private boolean isRunning;
    //盘块商品名称
    private String[] mStrs = new String[]{"单反相机", "IPAD", "恭喜发财", "IPHONE", "服装一套", "恭喜发财"};
    //盘块商品图片
    private int[] mImags = new int[]{R.drawable.ic_prize_1, R.drawable.ic_prize_2, R.drawable.ic_prize_5, R.drawable.ic_prize_3,
            R.drawable.ic_prize_4, R.drawable.ic_prize_6};

    //盘块商品图片对应的bitmap数组
    private Bitmap[] mBitmaps;
    //盘块对应的颜色
    private int[] mColor = new int[]{0xFFFFC300, 0xFFF17E01, 0xFFFFC300, 0xFFF17E01, 0xFFFFC300, 0xFFF17E01};
    //盘块数量
    private int mCount = 6;
    //盘块的范围
    private RectF mRectF = new RectF();
    //盘块的半径
    private int mRadius;
    //转盘的中心位置
    private int mCenter;
    //padding直接取用户设置的四个padding最小值
    private int mPadding;
    //盘块的画笔
    private Paint mArcPaint;
    //盘块文本的画笔
    private Paint mTextPaint;
    //盘块的背景图
    private Bitmap mBgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_red_pan);
    //位子大小
    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics());
    //盘块的滚动速度
    private double mSpeed;
    //盘块绘制的角度
    private volatile float mStartAngle = 0;//valatile 保证线程间变量的可见性。
    //判断是否点击了暂停按钮
    private boolean isShouldEnd;


    public PSurfaceView(Context context) {
        this(context, null);
    }

    public PSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
        mPadding = getPaddingLeft();
        //半径
        mRadius = width - mPadding * 2;
        //中心点
        mCenter = width / 2;
        Log.e("TAG", "PSurfaceView: " + width);
        setMeasuredDimension(width, width);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //初始化盘块的画笔
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);

        mTextPaint = new Paint();
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setTextSize(mTextSize);

        //初始化盘块的绘制范围
        mRectF = new RectF(mPadding, mPadding, mPadding + mRadius, mPadding + mRadius);
        //初始化图片
        mBitmaps = new Bitmap[mCount];
        for (int i = 0; i < mCount; i++) {
            mBitmaps[i] = BitmapFactory.decodeResource(getResources(), mImags[i]);
        }

        isRunning = true;
        mThread = new Thread(this);
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
    }

    @Override
    public void run() {
        while (isRunning) {
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();
            if (end - start < 50) {
                try {
                    Thread.sleep(50 - (end - start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void draw() {
        try {
            mCanvas = mSurfaceHolder.lockCanvas();
            if (mCanvas != null) {
                //draw
                //绘制背景
                drawBg();
                //绘制盘块
                float tmpAngle = mStartAngle;
                float sweepAngle = 360 / mCount;
                for (int i = 0; i < mCount; i++) {
                    mArcPaint.setColor(mColor[i]);
                    mCanvas.drawArc(mRectF, tmpAngle, sweepAngle, true, mArcPaint);
                    //绘制文本
                    drawText(tmpAngle, sweepAngle, mStrs[i]);
                    //绘制图片
                    drawIcon(tmpAngle, mBitmaps[i]);
                    tmpAngle += sweepAngle;
                }
                mStartAngle += mSpeed;
                if (isShouldEnd) {
                    mSpeed -= 1;
                }

                if (mSpeed <= 0) {
                    mSpeed = 0;
                    isShouldEnd = false;
                }
            }
        } catch (Exception e) {
        } finally {
            if (mCanvas != null && mSurfaceHolder != null) {
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }

    }

    /**
     * 点击启动旋转
     * index控制转盘停止的位置
     */
    public void start(int index) {
        float angle = 360 / mCount;//每一项的角度
        //计算每一项中奖范围（当前index）
        //x轴正坐标开始，0，1，2，盘块
        //1->150 ~210
        //0->210~270

        float from = 270 - (index + 1) * angle;//起始位置
        float end = from + angle;//结束位置
        //设置停下来需要旋转的距离
        float targetFrom = 4 * 360 + from;
        float targetEnd = 4 * 360 + end;

        //数组区间
        /**
         * s->0  s为速度
         * (s+0)*(s+1)/2=targetFrom //等差数列的公式
         * s*s+s-2*targetFrom = 0   求正解
         * s= (-1+Math.sqrt(1+8targetFrom))/2
         */

        float s1 = (float) ((-1 + Math.sqrt(1 + 8 * targetFrom)) / 2);
        float s2 = (float) ((-1 + Math.sqrt(1 + 8 * targetEnd)) / 2);

        mSpeed = s1 + Math.random() * (s2 - s1);
        isShouldEnd = false;
    }

    /**
     * 点击暂停按钮
     */
    public void end() {
        mStartAngle = 0;
        isShouldEnd = true;
    }


    /**
     * 转盘是否在旋转
     *
     * @return
     */
    public boolean isStart() {
        return mSpeed != 0;
    }

    /**
     * 转盘是否停止旋转
     *
     * @return
     */
    public boolean isShouldEnd() {
        return isShouldEnd;
    }

    private void drawIcon(float angle, Bitmap bitmap) {
        int imgWidth = mRadius / 8;//设置图片的宽度
        //起始角度，每个盘块的一般的角度
        float currentAngle = (float) ((angle + 360 / mCount / 2) * Math.PI / 180);
        //图片的中心点位置
        int x = (int) (mCenter + mRadius / 2 / 2 * Math.cos(currentAngle));
        int y = (int) (mCenter + mRadius / 2 / 2 * Math.sin(currentAngle));
        //确定图片的位置
        Rect rect = new Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2);
        mCanvas.drawBitmap(bitmap, null, rect, null);

    }


    /**
     * 绘制每个盘块的文本
     *
     * @param angle
     * @param angle1
     * @param str
     */
    private void drawText(float angle, float angle1, String str) {
        Path path = new Path();
        path.addArc(mRectF, angle, angle1);
        float mTextWidth = mTextPaint.measureText(str);
        int hOffest = (int) (mRadius * Math.PI / mCount / 2 - mTextWidth / 2);
        //水平偏移量，弧度顺时针偏移，垂直偏移量，垂直偏移量沿半径向内的偏移量
        //文本中间位置，弧的一半减去文本长度的一般，垂直随便值都可以。
        int vOffest = mRadius / 2 / 6;
        mCanvas.drawTextOnPath(str, path, hOffest, vOffest, mTextPaint);

    }

    private void drawBg() {
        mCanvas.drawColor(0xffffffff);
        mCanvas.drawBitmap(mBgBitmap, null, new Rect(mPadding / 2, mPadding / 2, getMeasuredWidth() - mPadding / 2, getMeasuredHeight() - mPadding / 2), null);
    }
}
