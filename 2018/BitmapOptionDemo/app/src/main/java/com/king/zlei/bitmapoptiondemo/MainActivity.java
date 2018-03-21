package com.king.zlei.bitmapoptiondemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;

/**
 * 图片压缩
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Bitmap bitmap = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img);

        doOption();
//        doOption1(10);
//       doOption2();
//        doOption3();
//        doOption4();
        LRU();
        Glide.with(this).load("sfd").asBitmap().into();

    }

    private void LRU() {
        long l = Runtime.getRuntime().maxMemory();
        Log.d(TAG, "LRU: "+l/1024/1024);

    }

    /**
     * RGB565法
     * 图片尺寸不变，但是图片大小小了一半
     */
    private void doOption4() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img, options);
        Log.d(TAG, "RGB565压缩: " + "压缩后的图片大小" + bitmap.getByteCount() / 1024 + "kb宽度为" + bitmap.getWidth() + "高度为" + bitmap.getHeight());
    }

    /**
     * 缩放压缩
     */
    private void doOption3() {
        Matrix matrix = new Matrix();
        matrix.setScale(0.5f, 0.5f);
        Bitmap bitmap = Bitmap.createBitmap(this.bitmap, 0, 0, this.bitmap.getWidth(), this.bitmap.getHeight(), matrix, true);
        Log.d(TAG, "缩放压缩: " + "压缩后的图片大小" + bitmap.getByteCount() / 1024 + "kb宽度为" + bitmap.getWidth() + "高度为" + bitmap.getHeight());
    }

    /**
     * 采样率压缩
     */
    private void doOption2() {
        BitmapFactory.Options bf = new BitmapFactory.Options();
        bf.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img, bf);
        Log.d(TAG, "采样率压缩: " + "压缩后的图片大小" + bitmap.getByteCount() / 1024 + "kb宽度为" + bitmap.getWidth() + "高度为" + bitmap.getHeight());

    }


    /**
     * 质量压缩 quality 0 -100  图片大小没有改变，长宽像素都不变，那么bitmap占用的内存是不变的
     * 我们看到二进制图片的数据随着quelity变小而变小。适合传递二进制图片数据，微信分享图片（32KB内）。
     * 质量压缩在保证像素的前提下，改变图片的位深和透明度等。
     * png格式的图片不能进行压缩，因为png是无损的。
     * WENP  google推出的格式。
     */
    private void doOption1(int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        byte[] bytes = baos.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        Log.d(TAG, "质量压缩: " + "压缩后的图片大小" + bitmap.getByteCount() / 1024 / 1024 + "M宽度为" + bitmap.getWidth()
                + "高度为" + bitmap.getHeight() + "bytes.length = " + bytes.length / 1024 + "kb" + "quality = " + quality);

    }

    /**
     * 压缩前图片大小
     */
    private void doOption() {
        Log.d(TAG, "压缩前: " + "压缩前的图片大小" + bitmap.getByteCount() / 1024 + "kb宽度为" + bitmap.getWidth() + "高度为" + bitmap.getHeight());
    }
}
