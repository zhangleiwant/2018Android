package com.king.zlei.spandemo;

import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

/**
 * 图文并排的使用
 * 1.Compound Drawable 属性
 * 2.Html支持
 * 3.Span
 * http://www.jianshu.com/p/72d31b7da85b
 */
public class MainActivity extends AppCompatActivity {

    TextView mTextView;
    TextView htmlTxt;
    TextView SpanTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = (TextView) findViewById(R.id.txt);
        htmlTxt = (TextView) findViewById(R.id.html_text);
        SpanTxt = (TextView) findViewById(R.id.span_text);
        startAnimation(mTextView);
        userHtml();
        userSpan();
        Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                return false;
            }
        })
    }

    /**
     * span实现图文并排
     *  http://blog.csdn.net/jiashuai94/article/details/78742281
     */
    private void userSpan() {
        SpannableString s = SpannableString.valueOf("我爱你爱着你就像老鼠爱大米 ");

        s.setSpan(new ForegroundColorSpan(Color.RED),0,8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpanTxt.setText(s);

    }

    private void userHtml() {
        /**
         * 基本用法1
         */
//        String str = "恭喜您！您的手机跑分为<font color='#F50057'><big><big><big>888888分</big></big></big></font>，已经超过全国<font color='#00E676'><big><big><big>99%</big></big></big></font>的Android手机。";
        //        String html = getString(R.string.html);
//        view.setMovementMethod(LinkMovementMethod.getInstance());//让链接可以点击
//        view.setText(html);
//        htmlTxt.setText(Html.fromHtml(str));
        /**
         * 基本用法2 达不到预期效果 getString将删除所有样式文本信息，看3的解决方法
         */
//        htmlTxt.setText(Html.fromHtml(getString(R.string.test_string)));
        /**
         * 基础用法3
         */
        htmlTxt.setText(Html.fromHtml(getString(R.string.html)));


    }

    private void startAnimation(TextView view) {
        Drawable[] drawables = view.getCompoundDrawables();
        for (Drawable d : drawables) {

            if (d != null && d instanceof Animatable)
                ((Animatable) d).start();
        }

    }
}
