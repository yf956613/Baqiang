package com.jiebao.baqiang.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.global.AppManager;
import com.jiebao.baqiang.global.Content;
import com.jiebao.baqiang.global.Footer;
import com.jiebao.baqiang.global.Header;
import com.jiebao.baqiang.listener.IHandler;
import com.jiebao.baqiang.scan.ScanHelper;
import com.jiebao.baqiang.scan.ScanListener;
import com.jiebao.baqiang.util.AppUtil;
import com.jiebao.baqiang.util.LogUtil;

/**
 * Created by Administrator on 2018/3/13 0013.
 */

public abstract class BaseActivityWithTitleAndNumber extends FragmentActivity implements Header,
        Footer, Content, View.OnTouchListener {
    public final int REQUEST_CAMARA_CODE = 100;
    private int FOOTER_BUTTONS_NUM = 4;
    private float mHeaderLeftViewTextSize = 20;
    private int mHeaderLeftViewTextColor = Color.BLACK;
    private float mHeaderRightViewTextSize = 20;
    private int mHeaderRightViewTextColor = Color.BLACK;

    // TODO 头部
    private FrameLayout mHeaderLayout;
    private TextView mHeaderLeftView;
    private TextView mHeaderRightView;
    private RelativeLayout.LayoutParams mHeaderLayoutParams;

    // TODO 中部内容布局
    private RelativeLayout mContentLayout;
    private View mContent;
    private RelativeLayout.LayoutParams mContentViewParams;
    private ViewGroup contentLayout;
    private RelativeLayout mGlobalLayout;
    private RelativeLayout.LayoutParams mFooterLayoutParams;

    // TODO 底部
    protected LinearLayout mFooterLayout;
    private RelativeLayout.LayoutParams mContentLayoutParams;

    private boolean mIsDestroyed = false;
    public AlertDialog loadingBulider;

    private void initAttributes() {
        mHeaderLayout = (FrameLayout) View.inflate(this, R.layout.header_layout_un_upload, null);
        mHeaderLeftView = (TextView) mHeaderLayout.findViewById(R.id.headerLeftView);
        mHeaderRightView = (TextView) mHeaderLayout.findViewById(R.id.headerRithtView);
        mHeaderLayout.setId(R.id.HEADER_LAYOUT_ID);

        mContentLayout = new RelativeLayout(this);
        mContent = new View(this);
        mContentViewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        contentLayout = (ViewGroup) View.inflate(this, R.layout.activity_base_content, null);
        mGlobalLayout = (RelativeLayout) contentLayout.findViewById(R.id.global_layout);

        mFooterLayout = (LinearLayout) View.inflate(this, R.layout.footer_layout, null);
        mFooterLayout.setId(R.id.FOOTER_LAYOUT_ID);

        mHeaderLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                .MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mFooterLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                .MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mContentLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams
                .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        mFooterLayout.setLayoutParams(mFooterLayoutParams);
        mHeaderLayout.setLayoutParams(mHeaderLayoutParams);

        mFooterLayout.setVisibility(View.GONE);

        // TODO 默认情况：显示状态栏，包括WIFI、电量、时间等
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                .LayoutParams.FLAG_FULLSCREEN);*/
    }

    public abstract void initView();

    public abstract void initData();

    private void initHandler() {
        BaqiangApplication.handler.setHandler(new IHandler() {

            @Override
            public void handleMessage(Message msg) {
                handler(msg);// 有消息就提交给子类实现的方法
            }
        });
    }

    // 让子类处理消息
    protected void handler(Message msg) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initAttributes();
        initView();

        initData();
        initHandler();

        int footTxtNum = getFootTxtNum();
        String[] footTxtStr = getFootTxtStr();
        if (footTxtNum > 0 && footTxtStr != null) {
            for (int index = 0; index < FOOTER_BUTTONS_NUM; index++) {
                TextView footTxt = (TextView) mFooterLayout.getChildAt(index);
                String[] suffStrs = getFootTxtPrefix();
                if (index < footTxtNum) {
                    if (BaqiangApplication.getOperateType() == 1 && suffStrs != null)
                        footTxt.setText(suffStrs[index] + footTxtStr[index]);
                    else footTxt.setText(footTxtStr[index]);

                    if (index == footTxtNum - 1) {
                        footTxt.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                    }
                } else {
                    footTxt.setVisibility(View.GONE);
                }
            }
        }

        // Activity管理类
        AppManager.getAppManager().addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // BaqiangApplication.mTopActivity = this;
        if (isSupportScan()) {
            LogUtil.trace("This Device is support Scanner function...");

            ScanHelper.getInstance().Open_Barcode(this);
            boolean isActivityNeedFocus = isActivityNeedFocus();
            ScanHelper.getInstance().setScanListener(this.getClass().getName(), scanListener,
                    isActivityNeedFocus);
        }
    }

    protected boolean isSupportScan() {
        return true;
    }

    protected boolean isActivityNeedFocus() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mIsDestroyed = true;
        closeLoadinDialog();

        AppManager.getAppManager().finishActivity(this);
    }

    protected int getFootTxtNum() {
        return 0;
    }

    protected String[] getFootTxtStr() {
        return null;
    }

    protected String[] getFootTxtPrefix() {
        return null;
    }

    public void closeLoadinDialog() {
        if (!isFinishing()) {
            if (null != loadingBulider) {
                if (loadingBulider.isShowing()) {
                    loadingBulider.dismiss();
                    loadingBulider = null;
                }
            }
        }
    }

    @Override
    public void setHeaderLeftViewText(String leftText) {
        setHeaderLeftViewText(leftText, mHeaderLeftViewTextColor);
    }

    @Override
    public void setHeaderLeftViewText(String leftText, int color) {
        setHeaderLeftViewText(leftText, color, mHeaderLeftViewTextSize);
    }

    @Override
    public void setHeaderLeftViewText(String leftText, int color, float size) {
        mHeaderLeftView.setText(leftText);
        mHeaderLeftViewTextColor = color;
        mHeaderLeftViewTextSize = size;
    }

    @Override
    public void setHeaderRightViewText(String rightText) {
        setHeaderRightViewText(rightText, mHeaderRightViewTextColor);
    }

    @Override
    public void setHeaderRightViewText(String rightText, int color) {
        setHeaderRightViewText(rightText, color, mHeaderRightViewTextSize);
    }

    @Override
    public void setHeaderRightViewText(String rightText, int color, float size) {
        mHeaderRightView.setText(rightText);
        mHeaderRightViewTextColor = color;
        mHeaderRightViewTextSize = size;
    }

    @Override
    public void setContent(int layoutId) {
        View contentView = View.inflate(this, layoutId, null);
        mContentLayout.removeView(mContent);
        mContentLayout.addView(contentView, mContentViewParams);

        mHeaderLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mGlobalLayout.addView(mHeaderLayout, mHeaderLayoutParams);

        mFooterLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mGlobalLayout.addView(mFooterLayout, mFooterLayoutParams);

        mContentLayoutParams.addRule(RelativeLayout.BELOW, R.id.HEADER_LAYOUT_ID);
        mContentLayoutParams.addRule(RelativeLayout.ABOVE, R.id.FOOTER_LAYOUT_ID);
        mGlobalLayout.addView(mContentLayout, mContentLayoutParams);

        setContentView(contentLayout);
    }

    private int mLastX = 0;
    private int mLastY = 0;
    private int initialTouchX = 0;

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialTouchX = mLastX = (int) event.getRawX();
                mLastY = (int) event.getRawY();
                v.setBackgroundResource(R.drawable.float_button_pressed);
                break;

            case MotionEvent.ACTION_MOVE: {
                int dx = (int) event.getRawX() - mLastX;
                int dy = (int) event.getRawY() - mLastY;
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) v.getLayoutParams();
                int lastLeftMargin = lp.leftMargin;
                int lastTopMargin = lp.topMargin;

                int rawLeftMargin = lastLeftMargin + dx;
                int rawTopMargin = lastTopMargin + dy;

                if (rawLeftMargin < 0) {
                    rawLeftMargin = 0;
                }
                if (rawLeftMargin + v.getWidth() > AppUtil.getWindowWidthSize()) {
                    rawLeftMargin = AppUtil.getWindowWidthSize() - v.getWidth();
                }
                if (rawTopMargin < 0) {
                    rawTopMargin = 0;
                }
                if (rawTopMargin + v.getHeight() > AppUtil.getWindowHeightSize()) {
                    rawTopMargin = AppUtil.getWindowHeightSize() - v.getHeight();
                }
                lp.setMargins(rawLeftMargin, rawTopMargin, 0, 0);
                v.setLayoutParams(lp);
                mLastX = (int) event.getRawX();
                mLastY = (int) event.getRawY();
                break;
            }
            case MotionEvent.ACTION_UP:
                v.setBackgroundResource(R.drawable.float_button_normal);
                if (Math.abs(event.getRawX() - initialTouchX) <= 2) {
                    v.performClick();
                    return false;
                }
        }
        return true;
    }

    private ScanListener scanListener = new ScanListener() {

        @Override
        public void fillCode(String barcode) {
            BaseActivityWithTitleAndNumber.this.fillCode(barcode);
        }

        @Override
        public void dspStat(String content) {
            BaseActivityWithTitleAndNumber.this.dspStat(content);
        }
    };

    protected void fillCode(String barcode) {

    }

    protected void dspStat(String barcode) {

    }

    @Override
    public void setFooterBtnVisible(int tvId, int visableState) {
        mFooterLayout.getChildAt(tvId).setVisibility(visableState);
    }

    public void setFootLayout(LinearLayout footLayout) {
        mFooterLayout = footLayout;
    }

    @Override
    public TextView getFooterTextView(int index) {
        if (index < 0 || index > FOOTER_BUTTONS_NUM - 1) {
            System.err.println("Footer button index is out of bound.");
        } else {
            return (TextView) mFooterLayout.getChildAt(index);
        }
        return null;
    }

    @Override
    public void setFooterTVText(int tvId, String text) {
        TextView footTv = (TextView) mFooterLayout.getChildAt(tvId);
        footTv.setTextColor(0xffffffff);
        footTv.setText(text);
    }

    /**
     * 如何交互？
     */
    public void showLoadinDialog() {
        if (mIsDestroyed) {
            return;
        }

        if (null == loadingBulider) {
            View view = LayoutInflater.from(this).inflate(R.layout.layout_widget_loading, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R
                    .style.myLoadingTheme));
            loadingBulider = builder.create();
            loadingBulider.setCancelable(false);
            loadingBulider.setView(view, 0, 0, 0, 0);
            loadingBulider.setOnKeyListener(new DialogInterface.OnKeyListener() {

                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    // TODO 屏蔽Back按键
                    /*if (keyCode == KeyEvent.KEYCODE_BACK) {
                        closeLoadinDialog();
                        loadingBulider = null;
                    }*/
                    return false;
                }
            });
            loadingBulider.setCanceledOnTouchOutside(false);
        }
        if (!loadingBulider.isShowing()) {
            loadingBulider.show();
        }
    }

}
