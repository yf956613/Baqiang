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
import android.widget.Toast;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.data.db.DaojianDBHelper;
import com.jiebao.baqiang.data.db.FajianDBHelper;
import com.jiebao.baqiang.data.db.LiucangDBHelper;
import com.jiebao.baqiang.data.db.XcdjDBHelper;
import com.jiebao.baqiang.data.db.ZcFajianDBHelper;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCfajianUploadFile;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.global.Content;
import com.jiebao.baqiang.global.Footer;
import com.jiebao.baqiang.global.Header;
import com.jiebao.baqiang.listener.IHandler;
import com.jiebao.baqiang.scan.ScanHelper;
import com.jiebao.baqiang.scan.ScanListener;
import com.jiebao.baqiang.util.AppUtil;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.SharedUtil;

/**
 * Created by Administrator on 2018/3/13 0013.
 */

public abstract class BaseActivityWithTitleAndNumber extends FragmentActivity implements Header,
        Footer, Content, View.OnTouchListener {
    private static final String TAG = BaseActivityWithTitleAndNumber.class.getSimpleName();

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

    private ScanListener mScanListener = new ScanListener() {

        @Override
        public void fillCode(String barcode) {
            BaseActivityWithTitleAndNumber.this.fillCode(barcode);
        }

        @Override
        public void dspStat(String content) {
            BaseActivityWithTitleAndNumber.this.dspStat(content);
        }

        @Override
        public void timeout(long timeout) {
            BaseActivityWithTitleAndNumber.this.timeout(timeout);
        }
    };

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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtil.trace("BaseActivity onKeyDown: " + keyCode);

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case Constant.F1_KEY_CODE: {
                    ZCfajianUploadFile.uploadZcfjUnloadRecords();
                    XcdjDBHelper.uploadXcdjUnloadRecords();
                    DaojianDBHelper.uploadDaojianUnloadRecords();
                    FajianDBHelper.uploadFajianUnloadRecords();
                    LiucangDBHelper.uploadLiucangUnloadRecords();

                    Toast.makeText(this, "数据上传成功", Toast.LENGTH_SHORT).show();
                    // F1事件，传递给Activity更新UI
                    syncViewAfterUpload();

                }
            }
        }

        return super.onKeyDown(keyCode, event);
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
    }

    @Override
    protected void onResume() {
        super.onResume();

        // FIXME
        setHeaderRightViewText("未上传：" + searchUnloadDataForUpdate(Constant
                .SYNC_UNLOAD_DATA_TYPE_ALL));

        // BaqiangApplication.mTopActivity = this;
        if (isSupportScan()) {
            ScanHelper.getInstance().Open_Barcode(this);
            boolean isActivityNeedFocus = isActivityNeedFocus();
            ScanHelper.getInstance().setScanListener(this.getClass().getName(), mScanListener,
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

    protected void fillCode(String barcode) {

    }

    protected void dspStat(String barcode) {

    }

    protected void timeout(long timeout) {

    }

    /**
     * 提供给子类覆写，用于上传数据后更新UI
     */
    public void syncViewAfterUpload() {
        setHeaderRightViewText("未上传：" + searchUnloadDataForUpdate(Constant
                .SYNC_UNLOAD_DATA_TYPE_ALL));
        // do child other update view steps
    }

    /**
     * 根据Type值，查询对应未上传数，更新TextView内容
     *
     * @param updateType
     * @return
     */
    public int searchUnloadDataForUpdate(int updateType) {
        int unloadRecords = 0;

        unloadRecords += SharedUtil.getInt(this, Constant.PREFERENCE_NAME_ZCFJ);

        unloadRecords += SharedUtil.getInt(this, Constant.PREFERENCE_NAME_XCDJ);

        unloadRecords += SharedUtil.getInt(this, Constant.PREFERENCE_NAME_DJ);

        unloadRecords += SharedUtil.getInt(this, Constant.PREFERENCE_NAME_FJ);

        unloadRecords += SharedUtil.getInt(this, Constant.PREFERENCE_NAME_LCJ);

        /*DbManager db = BQDataBaseHelper.getDb();
        switch (updateType) {
            case Constant.SYNC_UNLOAD_DATA_TYPE_ALL: {
                break;
            }

            case Constant.SYNC_UNLOAD_DATA_TYPE_ZCFJ: {
                try {
                    // 装车发件：查询数据库中标识位“未上传”的记录
                    List<ZCFajianFileContent> zCFajianData = db.selector(ZCFajianFileContent
                            .class).where("是否上传", "like", "未上传").and("是否可用", "=", "可用").findAll();
                    if (zCFajianData != null && zCFajianData.size() != 0) {
                        SharedUtil.putInt(this, Constant.PREFERENCE_NAME_ZCFJ, zCFajianData.size());
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }

                break;
            }

            case Constant.SYNC_UNLOAD_DATA_TYPE_XCDJ: {
                break;
            }

            case Constant.SYNC_UNLOAD_DATA_TYPE_DJ: {
                break;
            }

            case Constant.SYNC_UNLOAD_DATA_TYPE_FJ: {
                break;
            }

            case Constant.SYNC_UNLOAD_DATA_TYPE_LCJ: {
                break;
            }

            default:
                break;
        }*/

        /*int unloadDataRecords = 0;

        DbManager db = BQDataBaseHelper.getDb();
        try {
            // 留仓件：查询数据库中标识位“未上传”，且数据可用的记录
            List<StayHouseFileContent> stayHouseData = db.selector(StayHouseFileContent.class)
                    .where("是否上传", "=", "未上传").and("是否可用", "=", "可用").findAll();
            if (stayHouseData != null && stayHouseData.size() != 0) {
                unloadDataRecords += stayHouseData.size();
            }

            // 装车发件：查询数据库中标识位“未上传”的记录
            List<ZCFajianFileContent> zCFajianData = db.selector(ZCFajianFileContent.class).where
                    ("是否上传", "like", "未上传").and("是否可用", "=", "可用").findAll();
            if (zCFajianData != null && zCFajianData.size() != 0) {
                unloadDataRecords += zCFajianData.size();
            }

            // 卸车到件：查询数据库中标识位“未上传”的记录
            List<UnloadArrivalFileContent> unloadArrivalData = db.selector
                    (UnloadArrivalFileContent.class).where("是否上传", "like", "未上传").and("是否可用",
                    "=", "可用").findAll();
            if (unloadArrivalData != null && unloadArrivalData.size() != 0) {
                unloadDataRecords += unloadArrivalData.size();
            }

            // 到件：查询数据库中标识位“未上传”的记录
            List<CargoArrivalFileContent> cargoArrivalData = db.selector(CargoArrivalFileContent
                    .class).where("是否上传", "like", "未上传").and("是否可用", "=", "可用").findAll();
            if (cargoArrivalData != null && cargoArrivalData.size() != 0) {
                unloadDataRecords += cargoArrivalData.size();
            }

            // 发件：查询数据库中标识位“未上传”的记录
            List<ShipmentFileContent> shipmentData = db.selector(ShipmentFileContent.class).where
                    ("是否上传", "=", "未上传").and("是否可用", "=", "可用").findAll();
            if (shipmentData != null && shipmentData.size() != 0) {
                unloadDataRecords += shipmentData.size();
            }
        } catch (DbException e) {
            e.printStackTrace();
        }*/

        LogUtil.trace("unloadRecords:" + unloadRecords);
        return unloadRecords;
    }
}
