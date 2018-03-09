package com.jiebao.baqiang.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.custView.BatteryView;
import com.jiebao.baqiang.global.AppManager;
import com.jiebao.baqiang.global.Content;
import com.jiebao.baqiang.global.Footer;
import com.jiebao.baqiang.global.Header;
import com.jiebao.baqiang.listener.IHandler;
import com.jiebao.baqiang.scan.ScanHelper;
import com.jiebao.baqiang.scan.ScanListener;
import com.jiebao.baqiang.util.AppUtil;
import com.jiebao.baqiang.util.ListUtil;
import com.jiebao.baqiang.util.LogUtil;

import java.util.List;

//import com.jiebao.baqiang.zxing.CaptureActivity;

public abstract class BaseActivity extends FragmentActivity implements Header, Footer, Content,
        View.OnTouchListener {
    public final int REQUEST_CAMARA_CODE = 100;

    private ViewGroup contentLayout;
    private RelativeLayout mGlobalLayout;
    private RelativeLayout.LayoutParams mHeaderLayoutParams;
    private RelativeLayout.LayoutParams mFooterLayoutParams;
    private RelativeLayout.LayoutParams mContentLayoutParams;

    protected FrameLayout mHeaderLayout;

    private TextView mHeaderCenterView;
    private BatteryView mBatteryView;

    private float mHeaderCenterViewTextSize = 20;

    private int mHeaderCenterViewTextColor = Color.WHITE;

    protected LinearLayout mFooterLayout;

    private int FOOTER_BUTTONS_NUM = 4;

    private RelativeLayout mContentLayout;
    private View mContent;
    private RelativeLayout.LayoutParams mContentViewParams;

    public final static String TABLE_NAME = "tableName";

    public int currentSelectIndex = 0;

    protected Button floatButton;
    private int mLastX = 0;
    private int mLastY = 0;
    private int initialTouchX = 0;

    public abstract void initView();

    public abstract void initData();

    public AlertDialog loadingBulider;
    private boolean mIsDestroyed = false;

    private void initAttributes() {
        mHeaderLayout = (FrameLayout) View.inflate(this, R.layout.header_layout, null);
        mHeaderCenterView = (TextView) mHeaderLayout.findViewById(R.id.headerCenterView);
        mBatteryView = (BatteryView) mHeaderLayout.findViewById(R.id.batteryView);
        mHeaderLayout.setId(R.id.HEADER_LAYOUT_ID);

        mFooterLayout = (LinearLayout) View.inflate(this, R.layout.footer_layout, null);
        mFooterLayout.setId(R.id.FOOTER_LAYOUT_ID);

        mContentLayout = new RelativeLayout(this);
        mContent = new View(this);
        mContentViewParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);

        contentLayout = (ViewGroup) View.inflate(this, R.layout.activity_base_content, null);
        mGlobalLayout = (RelativeLayout) contentLayout.findViewById(R.id.global_layout);
        floatButton = (Button) contentLayout.findViewById(R.id.floatButton);

        mHeaderLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        mFooterLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        mContentLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);

        mFooterLayout.setLayoutParams(mFooterLayoutParams);
        mHeaderLayout.setLayoutParams(mHeaderLayoutParams);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                .LayoutParams.FLAG_FULLSCREEN);
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
//                    }else if(index == 0 && LanguageUtil.getLanguageEnv().equals("en")){
//                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)footTxt
// .getLayoutParams();
//                        lp.weight = 0.75f;
//                        footTxt.setLayoutParams(lp);
//                    }
                } else {
                    footTxt.setVisibility(View.GONE);
                }
            }
        }

        AppManager.getAppManager().addActivity(this);
        registerStatusTnfo();
    }

//    public void openCameraScan() {
//        Intent openCameraIntent = new Intent(this, CaptureActivity.class);
//        startActivityForResult(openCameraIntent, 0);
//    }

    public void setCameraScanVisible() {
        floatButton.setVisibility(View.VISIBLE);
        floatButton.setOnTouchListener(this);
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openCameraScan();
            }
        });
    }

    public void setCameraScanPisition() {
        final FrameLayout.LayoutParams floatLp = new FrameLayout.LayoutParams(FrameLayout
                .LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        floatLp.leftMargin = AppUtil.getWindowWidthSize() / 2 - floatButton.getBackground()
                .getIntrinsicWidth() / 2;
        floatLp.topMargin = AppUtil.getWindowHeightSize() / 2 - floatButton.getBackground()
                .getIntrinsicHeight() / 2;
        floatButton.setLayoutParams(floatLp);
    }

    private void initHandler() {
        BaqiangApplication.handler.setHandler(new IHandler() {
            public void handleMessage(Message msg) {
                handler(msg);// 有消息就提交给子类实现的方法
            }
        });
    }

    public void back() {
        finish();
    }

    public void refreshData() {

    }

//    public void gotoHelp(String helpInfo) {
//        Intent intent = new Intent(this, HelpActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putString(Constant.HTLP_EXTRA_TAG,helpInfo);
//        intent.putExtras(bundle);
//        startActivity(intent);
//    }

    // 让子类处理消息
    protected void handler(Message msg) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mIsDestroyed = true;
        closeLoadinDialog();

        unregisterStatusTnfo();
        AppManager.getAppManager().finishActivity(this);

    }

    @Override
    public void setHeaderCenterViewText(String centerText) {
        setHeaderCenterViewText(centerText, mHeaderCenterViewTextColor);
    }

    @Override
    public void setHeaderCenterViewText(String centerText, int color) {
        setHeaderCenterViewText(centerText, color, mHeaderCenterViewTextSize);
    }

    @Override
    public void setHeaderCenterViewText(String centerText, int color, float size) {
        mHeaderCenterView.setText(centerText);
        mHeaderCenterViewTextColor = color;
        mHeaderCenterViewTextSize = size;
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

    public void registerStatusTnfo() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBroadcastReceiver, filter);
    }

    public void unregisterStatusTnfo() {
        unregisterReceiver(mBroadcastReceiver);
    }

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                int level = intent.getIntExtra("level", 0);
                mBatteryView.updateBattery(level);
            }
        }
    };

    public <T> boolean checkSelectStatusValid(List<T> datas) {
        if (ListUtil.isEmpty(datas) || currentSelectIndex > datas.size() - 1) return false;
        return true;
    }

    public void showQuizWin() {
        String title = getString(R.string.txt_system_hint);
        String message = getString(R.string.hint_whether_quiz);
//        DialogUtil.showCustomDialog(this, title, message, false, "", new ICustomDialogListener() {
//
//            @Override
//            public void onDialogNegativeClick(int requestCode) {
//
//            }
//
//            @Override
//            public void onDialogPositiveClick(int requestCode, String inputArg, Object object) {
//                finish();
//                System.exit(0);
//            }
//        });
    }

    public void showCheckAdmin() {
        String title = getString(R.string.title_system_admin_check);
        String numberHint = getString(R.string.txt_passward);
        String message = getString(R.string.hint_admin_input_passward);
//        DialogUtil.showInputNumberDialog(this, title, numberHint, "",message, true, new
// ICustomDialogListener() {
//            @Override
//            public void onDialogPositiveClick(int requestCode, String inputArg, Object object) {
////                if (inputArg.equals(CacheManager.INIT_PASSWARD)) {
////                    finish();
////                    System.exit(0);
////                } else
////                    DialogUtil.showAlertDialog(BaseActivity.this, getString(R.string
/// .msg_passward_no_correct));
//            }
//
//            @Override
//            public void onDialogNegativeClick(int requestCode) {
//
//            }
//        });
    }

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

    protected int getFootTxtNum() {
        return 0;
    }

    protected String[] getFootTxtStr() {
        return null;
    }

    protected String[] getFootTxtPrefix() {
        return null;
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
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        closeLoadinDialog();
                        loadingBulider = null;
                    }
                    return false;
                }
            });
            loadingBulider.setCanceledOnTouchOutside(false);
        }
        if (!loadingBulider.isShowing()) {
            loadingBulider.show();
        }
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

    private ScanListener scanListener = new ScanListener() {

        @Override
        public void fillCode(String barcode) {
            BaseActivity.this.fillCode(barcode);
        }

        @Override
        public void dspStat(String content) {
            BaseActivity.this.dspStat(content);
        }
    };

    protected void fillCode(String barcode) {

    }

    protected void dspStat(String barcode) {

    }

    protected boolean isSupportScan() {
        return true;
    }

    protected boolean isActivityNeedFocus() {
        return true;
    }
}