package com.jiebao.baqiang.custView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.HorizontalScrollView;

public class HorizontalTableView extends HorizontalScrollView {

    private float mLastMotionX = 0;
    private float mLastMotionY = 0;
    private Button floatingBtn;

    public HorizontalTableView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public HorizontalTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HorizontalTableView(Context context) {
        super(context);
        init();
    }

    private void init() {
//        View view = View.inflate(getContext(), R.layout.wrap_form_content, null);
//        this.removeAllViews();
//        this.addView(view);

//        floatingBtn = new Button(getContext());
//        final FrameLayout.LayoutParams floatLp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
//        floatingBtn.setBackgroundResource(R.drawable.float_button_selector);
//        //floatLp.gravity = Gravity.CENTER;
//        floatingBtn.setLayoutParams(floatLp);
//        this.addView(floatingBtn, floatLp);
//
//        floatingBtn.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int X = (int) event.getRawX();
//                int Y = (int) event.getRawY();
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
////                        LastX = X;
////                        LastY = Y;
//                        break;
//                    case MotionEvent.ACTION_MOVE: {
//
//                        if (X < 0 || Y < 0)
//                            return false;
//
//                        if (X + floatingBtn.getWidth() > getWidth())
//                            X = getWidth() - floatingBtn.getWidth();
//
//                        if (Y + floatingBtn.getHeight() > getHeight())
//                            Y = getHeight() - floatingBtn.getHeight();
//
//                        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) floatingBtn.getLayoutParams();
//                        lp.leftMargin = X;
//                        lp.topMargin = Y;
//                        floatingBtn.setLayoutParams(lp);
//                        break;
//                    }
//                    default:
//                        break;
//                }
//                return true;
//            }
//        });
    }



    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

//        int action = ev.getAction();
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                mLastMotionX = ev.getX();
//                mLastMotionY = ev.getY();
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                float deltaX = ev.getX() - mLastMotionX;
//                float deltaY = ev.getY() - mLastMotionY;
//                if (deltaX > deltaY)
//                    return true;
//                else
//                    return false;
//            case MotionEvent.ACTION_UP:
//                break;
//        }
       // return super.onInterceptTouchEvent(ev);
        return false;
    }


   
}
