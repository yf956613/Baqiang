package com.jiebao.baqiang.custView;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeView extends TextView {
    private Runnable runnable;
    private SimpleDateFormat sdf;
    private Handler handler;

    public TimeView(Context context) {
        this(context, null);
    }
    public TimeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setId(Math.abs(this.hashCode()));
        sdf = new SimpleDateFormat("HH:mm:ss");
        setText(sdf.format(new Date()));
        runnable = new Runnable() {
            @Override
            public void run() {
                setText(sdf.format(new Date()));
                handler.postDelayed(runnable, 1000);
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, 1000);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacks(runnable);

    }
}
