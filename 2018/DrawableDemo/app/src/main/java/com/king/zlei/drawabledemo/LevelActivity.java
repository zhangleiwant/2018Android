package com.king.zlei.drawabledemo;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

/**
 * 验证popwindow点击返回键没有响应，以及点击外部区域没有响应
 * https://www.jianshu.com/p/825d1cc9fa79
 * 但是验证了一下，没有文章所说的问题，google改了。就有一个问题参见attrs图片
 * 具体的录音功能参考:
 * https://www.jianshu.com/p/06eca50ddda4    码农小阿飞CoderMario
 */

public class LevelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
    }


    /**
     * @param view
     */
    public void onClickShowPop(View view) {
        PopupWindow popupWindow = new PopupWindow(this);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(LayoutInflater.from(this).inflate(R.layout.pop, null));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
    }

    public void onClickShowVoice(View view) {
//        PopupWindow popupWindow = new PopupWindow(this);
//        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
//        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//        popupWindow.setContentView(LayoutInflater.from(this).inflate(R.layout.voice, null));
//        popupWindow.setFocusable(true);
//        popupWindow.setOutsideTouchable(false);
//        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        ImageView imageView = (ImageView) findViewById(R.id.voice_img);
        Drawable drawable = imageView.getDrawable();//getDrawable()空值，布局文件中应写为src
        drawable.setLevel(10000);
    }
}
