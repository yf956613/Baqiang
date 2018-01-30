package com.jiebao.baqiang.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

//import com.jiebao.baqiang.ParamMannager;
import com.jiebao.baqiang.util.StringFilter;

public class HideSoftBoardEditText extends android.support.v7.widget.AppCompatEditText implements View.OnFocusChangeListener, View.OnTouchListener {
    private OnFocusChangeListener mOnFocusChangeListener;

    public HideSoftBoardEditText(Context context) {
        super(context, null);
        init();
    }

    public HideSoftBoardEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public HideSoftBoardEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public void setmOnFocusChangeListener(OnFocusChangeListener listener){
        this.mOnFocusChangeListener = listener;
    }
    private void init() {
        setFocusableInTouchMode(true);
        setOnTouchListener(this);
        setOnFocusChangeListener(this);
        //过滤单引号'和/ ，这里两个符号会引起sql异常
        setFilters(new InputFilter[]{new StringFilter()});
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setOnFocusChangeListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN /*&& cLoseSoftBoard()*/) {
            v.setFocusableInTouchMode(true);
            v.setFocusable(true);
            v.requestFocus();
            String text = getText().toString();
            if (text.length() > 0) {
                setSelection(text.length());
                setSelectAllOnFocus(true);
            }
            return true;
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onFocusChange(View v, boolean hasFocus) {
        //boolean isCloseSoftBoard = cLoseSoftBoard();
        boolean isCloseSoftBoard =true;
        if (isCloseSoftBoard) {
            if (!hasFocus) {
                setShowSoftInputOnFocus(true);
            } else {
                InputMethodManager inputManager = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(inputManager.isActive())
                    inputManager.hideSoftInputFromWindow(getWindowToken(),0);
                setShowSoftInputOnFocus(false);
            }
        }
        Log.e("HideSoftBoardEditText", "onFocusChange--" + hasFocus);
        if(mOnFocusChangeListener != null){
            mOnFocusChangeListener.onFocusChange(v,hasFocus);
        }
    }

//    private boolean cLoseSoftBoard() {
//        int inputType = getInputType();
//        if ((inputType == InputType.TYPE_CLASS_NUMBER
//                || inputType == InputType.TYPE_NUMBER_FLAG_DECIMAL
//                || inputType == InputType.TYPE_CLASS_PHONE
//                || inputType == (InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER))) {
//            return ParamMannager.getmInstance().isCloseSoftBoard();
//        }
//        return false;
//    }

}
