package com.jiebao.baqiang.custView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.jiebao.baqiang.util.AppUtil;

public class BatteryView extends ImageView {

    private int BATTERY_RIGHT_MARGIN = 2;
    private int BATTERY_LEFT_MARGIN = 3;
    private int srcWidth = 0;
    private int srcHeight=0;
    private int value = 0;
    private Paint rectPaint = null;
    private Rect rect = null;
    private int powerColor = Color.WHITE;
    private  int lowPowerColor = Color.RED;

    public BatteryView(Context context) {
        this(context, null);
    }
    public BatteryView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public BatteryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        rectPaint = new Paint();
        rectPaint.setAntiAlias(false);
        rectPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        powerColor = Color.parseColor("#ffffffff");
        lowPowerColor= Color.parseColor("#ffff0000");

        DisplayMetrics displayMetrics = AppUtil.getDisplayMetrics(context);
        BATTERY_RIGHT_MARGIN = (int)(displayMetrics.density * BATTERY_RIGHT_MARGIN);
        BATTERY_LEFT_MARGIN  = (int)(displayMetrics.density * BATTERY_LEFT_MARGIN);

        Drawable drawable = this.getDrawable();
        srcWidth = drawable.getIntrinsicWidth();
        srcHeight = drawable.getIntrinsicHeight();

        rect = new Rect();
        rect.top=0;
        rect.bottom=srcHeight;
        rect.left=0;
        rect.right=srcWidth;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int actualWidth = srcWidth -BATTERY_RIGHT_MARGIN - BATTERY_LEFT_MARGIN;
        int valueWidth = value*(actualWidth)/100;
        rect.left= actualWidth-valueWidth + BATTERY_LEFT_MARGIN;

        if(value>15){
            rectPaint.setColor(powerColor);
        }else{
            rectPaint.setColor(lowPowerColor);
        }

        canvas.drawRect(rect, rectPaint);
        this.getDrawable().draw(canvas);
        canvas.save();
    }

    public void updateBattery(int level) {
        value = level;
        this.invalidate();
    }
}