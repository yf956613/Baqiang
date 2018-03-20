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
import com.jiebao.baqiang.data.arrival.CargoArrivalFileContent;
import com.jiebao.baqiang.data.arrival.CargoArrivalFileName;
import com.jiebao.baqiang.data.arrival.UnloadArrivalFileContent;
import com.jiebao.baqiang.data.arrival.UnloadArrivalFileName;
import com.jiebao.baqiang.data.bean.UploadServerFile;
import com.jiebao.baqiang.data.db.BQDataBaseHelper;
import com.jiebao.baqiang.data.dispatch.ShipmentDispatchFileName;
import com.jiebao.baqiang.data.dispatch.ShipmentFileContent;
import com.jiebao.baqiang.data.stay.StayHouseFileContent;
import com.jiebao.baqiang.data.stay.StayHouseFileName;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCFajianDispatchFileName;
import com.jiebao.baqiang.data.zcfajianmentDispatch.ZCFajianFileContent;
import com.jiebao.baqiang.global.AppManager;
import com.jiebao.baqiang.global.Constant;
import com.jiebao.baqiang.global.Content;
import com.jiebao.baqiang.global.Footer;
import com.jiebao.baqiang.global.Header;
import com.jiebao.baqiang.listener.IHandler;
import com.jiebao.baqiang.scan.ScanHelper;
import com.jiebao.baqiang.scan.ScanListener;
import com.jiebao.baqiang.util.AppUtil;
import com.jiebao.baqiang.util.LogUtil;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.List;

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
                    // BaseActivity中获取F1按键执行上传操作

                    // 全局扫描5张数据表，可用、未上传标记的记录
                    DbManager db = BQDataBaseHelper.getDb();
                    // 1. 上传留仓数据
                    uploadStayHouseData(db);
                    // 2. 上传装车发件数据
                    uploadLoadSendData(db);
                    // 3. 上传卸车到件数据
                    uploadUnloadArrivalData(db);
                    // 4. 上传到件数据
                    uploadCargoArrivalData(db);
                    // 5. 上传发件数据
                    uploadShipmentData(db);
                    // N. 更新UI
                    syncViewAfterUpload();

                    Toast.makeText(this, "数据上传成功", Toast.LENGTH_SHORT).show();
                }
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 提供给子类覆写，用于上传数据后更新UI
     */
    public void syncViewAfterUpload() {

    }

    private void uploadStayHouseData(DbManager db) {
        // 1. 留仓件 上传
        List<StayHouseFileContent> list = null;
        try {
            // FIXME 1. 查询数据库中标识位是“未上传”的记录，且是数据可用
            list = db.selector(StayHouseFileContent.class).where("是否上传", "like", "未上传").and
                    ("是否可用", "=", "可用").findAll();
            if (null != list && list.size() != 0) {
                // 2. 获取随机文件名
                StayHouseFileName mStayHouseFileName = new StayHouseFileName();
                if (mStayHouseFileName.linkToTXTFile()) {
                    // 3. 链接创建的文件和上传功能
                    UploadServerFile mUploadServerFile = new UploadServerFile(mStayHouseFileName
                            .getFileInstance());
                    for (int index = 0; index < list.size(); index++) {
                        // 4. 创建写入文本的字符串，并写入文本
                        StayHouseFileContent javaBean = list.get(index);
                        String content = javaBean.getmCurrentValue() + "\r\n";
                        if (mUploadServerFile.writeContentToFile(content, true)) {
                            // 不能删除数据，应该是否上传标志位为：已上传
                            WhereBuilder whereBuilder = WhereBuilder.b();
                            whereBuilder.and("运单编号", "=", javaBean.getShipmentNumber());
                            // 5. 将当前数据库中对应数据“是否上传”标志置为：已上传
                            db.update(StayHouseFileContent.class, whereBuilder, new KeyValue
                                    ("是否上传", "已上传"));
                        } else {
                            // TODO 写入文件失败
                            LogUtil.trace("写入文件失败");
                        }
                    }

                    // 6. 文件上传服务器
                    mUploadServerFile.uploadFile();
                } else {
                    // TODO 创建文件失败
                    LogUtil.trace("创建文件失败");
                }
            } else {
                LogUtil.trace("当前数据库没有需要上传数据");
            }
        } catch (DbException e) {
            LogUtil.d(TAG, "崩溃信息:" + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private void uploadLoadSendData(DbManager db) {
        List<ZCFajianFileContent> list = null;
        try {
            // FIXME 1. 查询数据库中标识位是“未上传”的记录，且是数据可用
            list = db.selector(ZCFajianFileContent.class).where("是否上传", "like", "未上传").and
                    ("是否可用", "=", "可用").findAll();
            if (null != list && list.size() != 0) {
                // 2. 获取随机文件名
                ZCFajianDispatchFileName mZcFajianDispatchFileName = new ZCFajianDispatchFileName();
                if (mZcFajianDispatchFileName.linkToTXTFile()) {
                    // 3. 链接创建的文件和上传功能
                    UploadServerFile mZcfajianUploadFile = new UploadServerFile
                            (mZcFajianDispatchFileName.getFileInstance());
                    for (int index = 0; index < list.size(); index++) {
                        // 4. 创建写入文本的字符串，并写入文本
                        ZCFajianFileContent javaBean = list.get(index);
                        String content = javaBean.getmCurrentValue() + "\r\n";
                        if (mZcfajianUploadFile.writeContentToFile(content, true)) {
                            // 不能删除数据，应该是否上传标志位为：已上传
                            WhereBuilder whereBuilder = WhereBuilder.b();
                            whereBuilder.and("运单编号", "=", javaBean.getShipmentNumber());
                            // 5. 将当前数据库中对应数据“是否上传”标志置为：已上传
                            db.update(ZCFajianFileContent.class, whereBuilder, new KeyValue
                                    ("是否上传", "已上传"));
                        } else {
                            // TODO 写入文件失败
                            LogUtil.trace("写入文件失败");
                        }
                    }

                    // 6. 文件上传服务器
                    mZcfajianUploadFile.uploadFile();
                } else {
                    // TODO 创建文件失败
                    LogUtil.trace("创建文件失败");
                }
            } else {
                LogUtil.trace("当前数据库没有需要上传数据");
            }
        } catch (DbException e) {
            LogUtil.d(TAG, "崩溃信息:" + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private void uploadUnloadArrivalData(DbManager db) {
        List<UnloadArrivalFileContent> list = null;
        try {
            // FIXME 1. 查询数据库中标识位是“未上传”的记录，且是数据可用
            list = db.selector(UnloadArrivalFileContent.class).where("是否上传", "like", "未上传").and
                    ("是否可用", "=", "可用").findAll();
            if (null != list && list.size() != 0) {
                // 2. 获取随机文件名
                UnloadArrivalFileName mUnloadArrivalFileName = new UnloadArrivalFileName();
                if (mUnloadArrivalFileName.linkToTXTFile()) {
                    // 3. 链接创建的文件和上传功能
                    UploadServerFile mUploadServerFile = new UploadServerFile
                            (mUnloadArrivalFileName.getFileInstance());
                    for (int index = 0; index < list.size(); index++) {
                        // 4. 创建写入文本的字符串，并写入文本
                        UnloadArrivalFileContent javaBean = list.get(index);
                        String content = javaBean.getmCurrentValue() + "\r\n";
                        if (mUploadServerFile.writeContentToFile(content, true)) {
                            // 不能删除数据，应该是否上传标志位为：已上传
                            WhereBuilder whereBuilder = WhereBuilder.b();
                            whereBuilder.and("运单编号", "=", javaBean.getShipmentNumber());
                            // 5. 将当前数据库中对应数据“是否上传”标志置为：已上传
                            db.update(UnloadArrivalFileContent.class, whereBuilder, new KeyValue
                                    ("是否上传", "已上传"));
                        } else {
                            // TODO 写入文件失败
                            LogUtil.trace("写入文件失败");
                        }
                    }

                    // 6. 文件上传服务器
                    mUploadServerFile.uploadFile();
                } else {
                    // TODO 创建文件失败
                    LogUtil.trace("创建文件失败");
                }
            } else {
                LogUtil.trace("当前数据库没有需要上传数据");
            }
        } catch (DbException e) {
            LogUtil.d(TAG, "崩溃信息:" + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private void uploadCargoArrivalData(DbManager db) {
        List<CargoArrivalFileContent> list = null;
        try {
            // FIXME 1. 查询数据库中标识位是“未上传”的记录，且是数据可用
            list = db.selector(CargoArrivalFileContent.class).where("是否上传", "like", "未上传").and
                    ("是否可用", "=", "可用").findAll();
            if (null != list && list.size() != 0) {
                // 2. 获取随机文件名
                CargoArrivalFileName mCargoArrivalFileName = new CargoArrivalFileName();
                if (mCargoArrivalFileName.linkToTXTFile()) {
                    // 3. 链接创建的文件和上传功能
                    UploadServerFile mUploadServerFile = new UploadServerFile
                            (mCargoArrivalFileName.getFileInstance());
                    for (int index = 0; index < list.size(); index++) {
                        // 4. 创建写入文本的字符串，并写入文本
                        CargoArrivalFileContent javaBean = list.get(index);
                        String content = javaBean.getmCurrentValue() + "\r\n";
                        if (mUploadServerFile.writeContentToFile(content, true)) {
                            // 不能删除数据，应该是否上传标志位为：已上传
                            WhereBuilder whereBuilder = WhereBuilder.b();
                            whereBuilder.and("运单编号", "=", javaBean.getShipmentNumber());
                            // 5. 将当前数据库中对应数据“是否上传”标志置为：已上传
                            db.update(CargoArrivalFileContent.class, whereBuilder, new KeyValue
                                    ("是否上传", "已上传"));
                        } else {
                            // TODO 写入文件失败
                            LogUtil.trace("写入文件失败");
                        }
                    }

                    // 6. 文件上传服务器
                    mUploadServerFile.uploadFile();
                } else {
                    // TODO 创建文件失败
                    LogUtil.trace("创建文件失败");
                }
            } else {
                LogUtil.trace("当前数据库没有需要上传数据");
            }
        } catch (DbException e) {
            LogUtil.d(TAG, "崩溃信息:" + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public void uploadShipmentData(DbManager db) {
        List<ShipmentFileContent> list = null;
        try {
            // FIXME 1. 查询数据库中标识位是“未上传”的记录，且是数据可用
            list = db.selector(ShipmentFileContent.class).where("是否上传", "like", "未上传").and
                    ("是否可用", "=", "可用").findAll();
            if (null != list && list.size() != 0) {
                // 2. 获取随机文件名
                ShipmentDispatchFileName mShipmentDispatchFileName = new ShipmentDispatchFileName();
                if (mShipmentDispatchFileName.linkToTXTFile()) {
                    // 3. 链接创建的文件和上传功能
                    UploadServerFile mShipmentUploadFile = new UploadServerFile
                            (mShipmentDispatchFileName.getFileInstance());
                    for (int index = 0; index < list.size(); index++) {
                        // 4. 创建写入文本的字符串，并写入文本
                        ShipmentFileContent javaBean = list.get(index);
                        String content = javaBean.getmCurrentValue() + "\r\n";
                        if (mShipmentUploadFile.writeContentToFile(content, true)) {
                            // 不能删除数据，应该是否上传标志位为：已上传
                            WhereBuilder whereBuilder = WhereBuilder.b();
                            whereBuilder.and("运单编号", "=", javaBean.getShipmentNumber());
                            // 5. 将当前数据库中对应数据“是否上传”标志置为：已上传
                            db.update(ShipmentFileContent.class, whereBuilder, new KeyValue
                                    ("是否上传", "已上传"));
                        } else {
                            // TODO 写入文件失败
                            LogUtil.trace("写入文件失败");
                        }
                    }

                    // 6. 文件上传服务器
                    mShipmentUploadFile.uploadFile();
                } else {
                    // TODO 创建文件失败
                    LogUtil.trace("创建文件失败");
                }
            } else {
                LogUtil.trace("当前数据库没有需要上传数据");
            }
        } catch (DbException e) {
            LogUtil.d(TAG, "崩溃信息:" + e.getLocalizedMessage());
            e.printStackTrace();
        }
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

        setHeaderRightViewText("未上传：" + searchUnloadData());

        // BaqiangApplication.mTopActivity = this;
        if (isSupportScan()) {
            LogUtil.trace("This Device is support Scanner function...");

            ScanHelper.getInstance().Open_Barcode(this);
            boolean isActivityNeedFocus = isActivityNeedFocus();
            ScanHelper.getInstance().setScanListener(this.getClass().getName(), mScanListener,
                    isActivityNeedFocus);

            LogUtil.trace();
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

    protected void fillCode(String barcode) {

    }

    protected void dspStat(String barcode) {

    }

    protected void timeout(long timeout) {

    }

    /**
     * BaseActivity中查询所有表的未上传数据
     */
    public int searchUnloadData() {
        int unloadDataRecords = 0;

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
        }

        return unloadDataRecords;
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
