package com.jiebao.baqiang.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.util.CompatUtil;

/**
 * Created by Administrator on 2016/1/7.
 */
public class JBItemEdit extends LinearLayout implements TextWatcher, View.OnFocusChangeListener {
    //帮助问号
    private ImageView iconQuestion;
    //左侧，右侧文字大小
    private float leftTextSize, rightInputTextSize;
    //左侧宽度
    private float leftLayoutWidth;
    //左侧、右侧文字颜色
    private int leftTextColor, rightTextColor;
    //是否可以输入
    private boolean isEnable = true;
    //是否单行输入
    private boolean isSingleline = false;
    //标题
    private String title = "";
    //提示
    private String hint = "";

    //右侧文字
    private String rightText;
    //操作图标
    private Drawable actionDrawable;
    //背景
    private Drawable background;
    //左边图标
    private Drawable leftDrawable;//默认是必填标记 星号
    private boolean isLeftDrawableShow;//是否显示左侧图片
    //可输入字符长度
    private int length;
    private TextView leftText;
    //右侧可输入框
    private AutoCompleteTextView rightInputText;
    //右侧显示文本
    private TextView rightDisplayText;
    private ImageView actionButton;
    private ImageView actionDelete;
    //输入类型
    private int inputType;
    //接收的字符串
    private String digest;
    //输入监听
    private TextWatcher textWatcher;
    private TextView tvItemTip;

    private ImageButton increase;
    private ImageButton decrease;

    //焦点获取监听
    private OnFocusChangeListener onFocusChangeListener;

    public JBItemEdit(Context context) {
        this(context, null);
    }

    public JBItemEdit(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JBItemEdit(Context context, AttributeSet attrs,
                      int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.JBItemEdit);
        isEnable = ta.getBoolean(R.styleable.JBItemEdit_android_enabled, true);
        isSingleline = ta.getBoolean(R.styleable.JBItemEdit_android_singleLine, false);
        title = ta.getString(R.styleable.JBItemEdit_android_text);
        hint = ta.getString(R.styleable.JBItemEdit_android_hint);
        background = ta.getDrawable(R.styleable.JBItemEdit_android_background);
        actionDrawable = ta.getDrawable(R.styleable.JBItemEdit_android_src);
        length = ta.getInt(R.styleable.JBItemEdit_lth, 60);
        inputType = ta.getInt(R.styleable.JBItemEdit_android_inputType, 1);
        leftLayoutWidth = ta.getDimension(R.styleable.JBItemEdit_left_width, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80,
                getResources().getDisplayMetrics()));
        digest = ta.getString(R.styleable.JBItemEdit_android_digits);
        leftTextColor = ta.getColor(R.styleable.JBItemEdit_left_text_color, Color.BLACK);
        rightTextColor = ta.getColor(R.styleable.JBItemEdit_right_text_color, 0xAA000000);
        leftTextSize = (int) ta.getDimension(R.styleable.JBItemEdit_left_text_size, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16,
                getResources().getDisplayMetrics()));
        rightInputTextSize = (int) ta.getDimension(R.styleable.JBItemEdit_right_text_size, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14,
                getResources().getDisplayMetrics()));
        /**********note add by xc ***************************************/
        rightText = ta.getString(R.styleable.JBItemEdit_rightText);
        leftDrawable = ta.getDrawable(R.styleable.JBItemEdit_leftDrawable);
        isLeftDrawableShow = ta.getBoolean(R.styleable.JBItemEdit_enabledLeft, false);
        /**********note add by xc ***************************************/
        ta.recycle();
        init();
    }

    private void init() {
        setGravity(Gravity.CENTER_VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.jb_item_edit, this);
        setBackgroundResource(R.drawable.bg_item_column_top);
        if (background != null) {
            CompatUtil.setBackGroudDrawable(this, background);
        }
        increase = (ImageButton) findViewById(R.id.increase);
        decrease = (ImageButton) findViewById(R.id.decrease);
        leftText = (TextView) findViewById(R.id.tv_left_text);
        tvItemTip = (TextView) findViewById(R.id.tv_item_tip);
        rightDisplayText = (TextView) findViewById(R.id.tv_displayt_text);
        rightInputText = (AutoCompleteTextView) findViewById(R.id.et_input_text);
        actionButton = (ImageView) findViewById(R.id.iv_action_choice);
        actionDelete = (ImageView) findViewById(R.id.iv_action_delete);
        ImageView leftImg = (ImageView) findViewById(R.id.iv_action_sure);
        iconQuestion = (ImageView) findViewById(R.id.iv_action_question);
        leftText.setText(title);
        leftText.setMinWidth((int) leftLayoutWidth);
        leftText.setTextColor(leftTextColor);
        leftText.setTextSize(TypedValue.COMPLEX_UNIT_PX, leftTextSize);
        rightInputText.setTextColor(rightTextColor);
        rightDisplayText.setTextColor(rightTextColor);
        rightInputText.setTextSize(TypedValue.COMPLEX_UNIT_PX, rightInputTextSize);
        rightDisplayText.setTextSize(TypedValue.COMPLEX_UNIT_PX, rightInputTextSize);
        rightInputText.setHint(hint);
        rightInputText.setEnabled(isEnable);
        if (!TextUtils.isEmpty(rightText)) {
            rightInputText.setText(rightText);
        }
        //右侧可输入框框
//        if (inputType > 0) {
//            rightInputText.setInputType(inputType);
//            if (!TextUtils.isEmpty(digest)) {
//                rightInputText.setKeyListener(DigitsKeyListener.getInstance(digest));
//            }
//        }
//        rightInputText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(length)/*, new StringFilter()*/});
//        rightInputText.setSelection(rightInputText.getText().length() > 0 ? rightInputText.getText().length() : 0);
//        rightInputText.addTextChangedListener(this);
//        rightInputText.setOnFocusChangeListener(this);
        //右侧显示文本
        rightDisplayText.setSingleLine(isSingleline);
        rightDisplayText.setHint(hint);
        rightInputText.setTextColor(rightTextColor);
        if (actionDrawable != null) {
            actionButton.setImageDrawable(actionDrawable);
        }
        if (isEnable) {
            rightDisplayText.setVisibility(View.GONE);
            rightInputText.setVisibility(View.VISIBLE);
        } else {
            rightDisplayText.setVisibility(View.VISIBLE);
            rightInputText.setVisibility(View.GONE);
        }
        if (leftDrawable != null) {
            leftImg.setImageDrawable(leftDrawable);
        }

        if (isLeftDrawableShow) {
            leftImg.setVisibility(View.VISIBLE);
        } else {
            leftImg.setVisibility(View.INVISIBLE);
        }
    }

    //可以设置为null
    public void setActionButton(Drawable actionDrawable) {
        actionButton.setImageDrawable(actionDrawable);
    }

    public void setActionButton(int actionDrawable) {
        actionButton.setImageResource(actionDrawable);
    }

    public void setActionDeleteVisible(boolean isVisible) {
        actionDelete.setVisibility(isVisible ? VISIBLE : GONE);
    }

    public void setActionDeleteClickListener(OnClickListener onClickListener) {
        actionDelete.setOnClickListener(onClickListener);
    }


    /**
     * 设置输入框改变监听
     *
     * @param textWacher
     */
    public void setTextWacher(TextWatcher textWacher) {
        this.textWatcher = textWacher;
    }

    /**
     * 输入框输入类型
     *
     * @param inputType
     */
    public void setInputType(int inputType) {
        rightInputText.setInputType(inputType);
    }

    /**
     * 从资源获取字体大小
     *
     * @param res
     * @return int
     */
    private float getIntSize(int res) {
        String textSizeStr = String.valueOf(getResources().getDimension(res));
        return Float.parseFloat(textSizeStr.substring(0, textSizeStr.length() - 2));
    }

    /**
     * 从资源获取长度
     */
    private int getResLength(int res) {
        String lengthStr = String.valueOf(getResources().getDimension(res));
        return Integer.parseInt(lengthStr.substring(0, lengthStr.length() - 2));
    }

    public void setIncreaseClickListener(OnClickListener onClickListener) {
        increase.setVisibility(VISIBLE);
        increase.setOnClickListener(onClickListener);
    }

    public void setDecreaseClickListener(OnClickListener onClickListener) {
        decrease.setVisibility(VISIBLE);
        decrease.setOnClickListener(onClickListener);
    }

    public AutoCompleteTextView getRightText() {
        return rightInputText;
    }

    public AutoCompleteTextView getRightInputText() {
        return rightInputText;
    }

    public TextView getLetfTextView() {
        return leftText;
    }

    /**
     * 设置左边文字
     *
     * @param t
     */
    public void setLeftText(String t) {
        leftText.setText(t);
    }

    /**
     * 设置左边文字
     *
     * @param t
     */
    public void setLeftText(int t) {
        leftText.setText(t);
    }

    public String getLeftText() {
        return String.valueOf(leftText.getText());
    }

    /**
     * 设置左边文字宽度
     */
    public void setLeftTextWidth(int width) {
        if (width < 0)
            return;
        leftText.setMinWidth(width);
    }

    /**
     * 设置右边文字
     *
     * @param t
     */
    public void setRightText(String t) {
        if (t != null) {
            rightInputText.setText(t);
            rightInputText.setSelection(rightInputText.getText().length() > 0 ? rightInputText.getText().length() : 0);
            rightDisplayText.setText(t);
        }
    }

    /**
     * 设置右边文字
     *
     * @param t
     */
    public void setRightText(int t) {
        rightInputText.setText(t);
        rightInputText.setSelection(rightInputText.getText().length() > 0 ? rightInputText.getText().length() : 0);
        rightDisplayText.setText(t);
    }


    /**
     * 设置右边文字 超链接
     *
     * @param t
     */
    public void setRightText(SpannableString t) {
        if (t != null) {
            rightInputText.setText(t);
            rightInputText.setSelection(rightInputText.getText().length() > 0 ? rightInputText.getText().length() : 0);
            rightDisplayText.setText(t);
            rightDisplayText.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    /**
     * 设置右边文字
     */
    public void setRightTextColor(int color) {
        rightDisplayText.setTextColor(color);
        rightInputText.setTextColor(color);
    }

    public void setTextIsSelectable() {
        rightDisplayText.setTextIsSelectable(true);
    }

    /**
     * 设置右边hint
     *
     * @param t
     */
    public void setRightTextHint(int t) {
        rightInputText.setHint(t);
    }

    /**
     * 设置右边输入框是否可以输入
     */
    public void setRightEditTextEnable(boolean isEnable) {
        rightInputText.setEnabled(isEnable);
    }

    public String getRightEditText() {
        if (rightInputText != null) {
            return rightInputText.getText().toString();
        }
        return "";
    }

    /**
     * 设置左边文字颜色
     *
     * @param c
     */
    public void setLeftTextColour(int c) {
        leftText.setTextColor(c);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (textWatcher != null) {
            textWatcher.beforeTextChanged(s, start, count, after);
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (textWatcher != null) {
            textWatcher.onTextChanged(s, start, before, count);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (textWatcher != null) {
            textWatcher.afterTextChanged(s);
        }
    }

    public void setHint(String hint) {
        if (!TextUtils.isEmpty(hint)) {
            rightInputText.setHint(hint);
            rightDisplayText.setHint(hint);
        }
    }

    public void setHint(int hint) {
        rightInputText.setHint(hint);
        rightDisplayText.setHint(hint);
    }

    /**
     * 是否可以输入
     *
     * @return
     */
    public boolean isInputEnable() {
        return isEnable;
    }

    /**
     * 这只是否可以输入
     *
     * @param isEnable
     */
    public void setInputEnable(boolean isEnable) {
        this.isEnable = isEnable;
        if (isEnable) {
            rightDisplayText.setVisibility(View.GONE);
            rightInputText.setVisibility(View.VISIBLE);
            rightInputText.setEnabled(true);
        } else {
            rightDisplayText.setVisibility(View.VISIBLE);
            rightInputText.setVisibility(View.GONE);
            rightInputText.setEnabled(false);
        }
    }

    public boolean getInputEnable() {
        return this.isEnable;
    }

    @Override
    public OnFocusChangeListener getOnFocusChangeListener() {
        return onFocusChangeListener;
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        this.onFocusChangeListener = onFocusChangeListener;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (onFocusChangeListener != null) {
            onFocusChangeListener.onFocusChange(v, hasFocus);
        }
    }

    /**
     * 设置右边hint
     *
     * @param text
     */
    public void setRightTextHint(String text) {
        rightDisplayText.setHint(text);
    }

    //右边编辑框掩码
    public void setRightTextMask() {
        rightInputText.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        rightDisplayText.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        rightInputText.setEnabled(false);
    }

    /**
     * 显示/隐藏问号
     *
     * @param isVisible
     */
    public void setIconQuestionVisible(boolean isVisible) {
        if (iconQuestion != null) {
            iconQuestion.setVisibility(isVisible ? VISIBLE : INVISIBLE);
        }
    }

    public ImageView getIconQuestion() {
        return iconQuestion;
    }

    public void setIconQuestionIcon(int res) {
        if (iconQuestion != null) {
            iconQuestion.setImageResource(res);
        }
    }

    /**
     * 问号点击事件
     */
    public void setIconQuestionClickListener(OnClickListener iconQuestionClickListener) {
        if (iconQuestion != null && iconQuestionClickListener != null) {
            iconQuestion.setOnClickListener(iconQuestionClickListener);
        }
    }

    /**
     * 设置右侧文字大小
     */
    public void setRightInputTextSize(int textSize) {
        rightInputTextSize = textSize;
        if (rightInputTextSize > 0) {
            if (rightInputText != null) {
                rightInputText.setTextSize(rightInputTextSize);
            }
            if (rightDisplayText != null) {
                rightDisplayText.setTextSize(rightInputTextSize);
            }
        }
    }

    public void setItemTip(String itemTip) {
        if (TextUtils.isEmpty(itemTip)) {
            itemTip = "";
        }
        tvItemTip.setVisibility(VISIBLE);
        tvItemTip.setText(itemTip);
    }

    public void setItemTip(int itemTip) {
        if (itemTip <= 0) {
            return;
        }
        tvItemTip.setVisibility(VISIBLE);
        tvItemTip.setText(itemTip);
    }

    public void setItemTipClickListener(OnClickListener itemTipClickListener) {
        tvItemTip.setTextColor(isEnable ? CompatUtil.getColor(getContext(), R.color.colorPrimary) : CompatUtil.getColor(getContext(), R.color.gray));
        if (!isEnable) {
            return;
        }
        if (itemTipClickListener == null) {
            return;
        }
        tvItemTip.setOnClickListener(itemTipClickListener);
    }
}
