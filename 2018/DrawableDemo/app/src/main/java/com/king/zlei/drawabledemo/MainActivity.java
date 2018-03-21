package com.king.zlei.drawabledemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

/**
 * 很全面的Drawable实例
 * 参考文章：
 * https://juejin.im/post/5a28b2d0f265da431c703153
 * 关于shape讲解比较清楚的文章：
 * http://keeganlee.me/post/android/20150830
 */
public class MainActivity extends AppCompatActivity {

    private ImageView mView;
    private ImageView mUnread;
    private ImageView roundImg;
    private ImageView circleImg;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mView = (ImageView) findViewById(R.id.img);
        mUnread = (ImageView) findViewById(R.id.unread);
        roundImg = (ImageView) findViewById(R.id.round_img);
        circleImg = (ImageView) findViewById(R.id.circle_img);
        /**
         * 也可以通过代码设置图片背景色
         */
//        GradientDrawable gradientDrawable = new GradientDrawable();
//        gradientDrawable.setColor(getResources().getColor(R.color.colorPrimary));
//        mView.setBackground(gradientDrawable);
        mUnread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUnread.setImageLevel(0);
            }
        });

        createRoundImg();
        createCircleImg();
    }

    private void createCircleImg() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hourse);
        circleImg.setImageDrawable(new CircleImageDrawable(bitmap));
    }

    private void createRoundImg() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hourse);
        roundImg.setImageDrawable(new RoundImageDrawable(bitmap, 30, 30));
    }

    public void onLevelClick(View view) {
        startActivity(new Intent(this, LevelActivity.class));
    }
}
