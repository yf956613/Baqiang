package com.jiebao.baqiang.custView;

import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;

import com.jiebao.baqiang.util.LogUtil;

/**
 * Created by Administrator on 2018/3/19 0019.
 */

public class TestAutoView extends android.support.v7.widget.AppCompatAutoCompleteTextView {
    private static final String TAG = TestAutoView.class.getSimpleName();

    public TestAutoView(Context context) {
        super(context);
    }

    public TestAutoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TestAutoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void replaceText(CharSequence text) {
        clearComposingText();

        LogUtil.trace("text:" + text);
        String[] arr = text.toString().split("  ");
        if (arr != null && arr.length >= 2) {
            setText(arr[1]);
        } else {
            setText(text);
        }

        // make sure we keep the caret at the end of the text view
        Editable spannable = getText();
        Selection.setSelection(spannable, spannable.length());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            // case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_ENTER: {
                if (TextUtils.isEmpty(getText().toString())) {
                    showDropDown();
                }
            }
        }

        return super.onKeyDown(keyCode, event);
    }
}
