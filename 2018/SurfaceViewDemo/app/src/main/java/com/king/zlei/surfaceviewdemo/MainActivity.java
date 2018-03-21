package com.king.zlei.surfaceviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    ImageView start;
    PSurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (ImageView) findViewById(R.id.start);
        mSurfaceView = (PSurfaceView) findViewById(R.id.pan);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mSurfaceView.isStart()){
                    mSurfaceView.start(1);
                    start.setImageResource(R.drawable.stop);
                }else{
                    if(!mSurfaceView.isShouldEnd()){
                        mSurfaceView.end();
                        start.setImageResource(R.drawable.start);
                    }
                }
            }
        });
    }
}
