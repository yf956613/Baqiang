package com.jiebao.baqiang.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.support.v4.view.WindowCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.support.v7.app.AppCompatActivity;

import com.jiebao.baqiang.application.BaqiangApplication;
//import com.jiebao.baqiang.ParamMannager;
import com.jiebao.baqiang.R;
//import com.jiebao.baqiang.beans.TBDParam;
import com.jiebao.baqiang.util.Keyboard;
//import com.jiebao.baqiang.utils.AppUtils;
import com.jiebao.baqiang.util.ToolBarHelper;

/**
 * 带标题栏的acitivity
 *
 * @author shanjiang_gao
 */
public abstract class BaseActivityWithTitleBar extends AppCompatActivity {
    public ToolBarHelper mToolBarHelper;
    public Toolbar toolbar;
    //数量小数位
    public static int decimalPlaceLength = 0;
    //单价金额小数位
    public static int decimalMoneyPlaceLength = 0;
    public boolean isActuQuery = false;//是否在执行即时搜索（在搜索框输入执行查找）
    protected boolean isLoading;

    @Override
    public void setContentView(int layoutResID) {
//        requestWindowFeature(Window.FEATURE_ACTION_MODE_OVERLAY);
        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_MODE_OVERLAY);
        mToolBarHelper = new ToolBarHelper(this, layoutResID);
        toolbar = mToolBarHelper.getToolBar();
        toolbar.setNavigationIcon(R.drawable.icon_back_selector);
        toolbar.setBackgroundResource(R.drawable.bg_tool_bar);
        setContentView(mToolBarHelper.getContentView());
        /*把 toolbar 设置到Activity 中*/
       setSupportActionBar(toolbar);
        /*自定义的一些操作*/
        onCreateCustomToolBar(toolbar);
        //读取数量和金额小数位参数
//        TBDParam decimalPlace = ParamMannager.getmInstance().getSystemParam(ParamMannager.ITEM_DECIMAL_PLACES_FID);
//        TBDParam decimalMoneyPlace = ParamMannager.getmInstance().getSystemParam(ParamMannager.ITEM_MONEY_PLACES_FID);
//        if (decimalPlace != null) {
//            if (!TextUtils.isEmpty(decimalPlace.getFvalue())) {
//                decimalPlaceLength = AppUtils.string2Integer(decimalPlace.getFvalue());
//            }
//        } else {
//            decimalPlaceLength = 2;
//        }
//        if (decimalMoneyPlace != null) {
//            if (!TextUtils.isEmpty(decimalMoneyPlace.getFvalue())) {
//                decimalMoneyPlaceLength = AppUtils.string2Integer(decimalMoneyPlace.getFvalue());
//            }
//        } else {
//            decimalMoneyPlaceLength = 2;
//        }
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void onCreateCustomToolBar(Toolbar toolbar) {
        toolbar.setContentInsetsRelative(0, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       // hidesoftInputBord();
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
              //  hidesoftInputBord();
                finish();
                break;
            case Keyboard.F1:
             //   hidesoftInputBord();
                onKeyBordClicked(Keyboard.F1);
                break;
            case Keyboard.F2:
              //  hidesoftInputBord();
                onKeyBordClicked(Keyboard.F2);
                break;
            case Keyboard.F3:
               // hidesoftInputBord();
                onKeyBordClicked(Keyboard.F3);
                break;
            case Keyboard.F4:
              //  hidesoftInputBord();
                onKeyBordClicked(Keyboard.F4);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 快捷键点击
     *
     * @param keycode 键值
     */
    protected void onKeyBordClicked(int keycode) {

    }

    /**
     * 显示加载中视图
     *
     * @param loadingViewRes
     */
//    public void showLoadingView(final int loadingViewRes) {
//        if (!isActuQuery) {//不是在即时搜索时才调用loading
//            isLoading = true;
//            if (Looper.getMainLooper().getThread().getName().equals(Thread.currentThread().getName())) {
//                if (mToolBarHelper != null) {
//                    mToolBarHelper.showLoadingView(loadingViewRes);
//                }
//            } else {
//                BaqiangApplication.getmMainThreadHandler().removeCallbacks(runnableLoading);
//                BaqiangApplication.getmMainThreadHandler().post(runnableLoading);
//            }
//        }
//    }

    /**
     * 显示加载中视图
     */
//    public void showLoadingView() {
//        if (!isActuQuery) {//不是在即时搜索时才调用loading
//            isLoading = true;
//            if (Looper.getMainLooper().getThread().getName().equals(Thread.currentThread().getName())) {
//                if (mToolBarHelper != null) {
//                    mToolBarHelper.showLoadingView(R.layout.loading_view);
//                }
//            } else {
//                BaqiangApplication.getmMainThreadHandler().removeCallbacks(runnableLoading);
//                BaqiangApplication.getmMainThreadHandler().post(runnableLoading);
//            }
//        }
//    }

    Runnable runnableLoading = new Runnable() {
        @Override
        public void run() {
            if (mToolBarHelper != null) {
                mToolBarHelper.showLoadingView(0);
            }
        }
    };
    Runnable runnableContentView = new Runnable() {
        @Override
        public void run() {
            if (mToolBarHelper != null) {
                mToolBarHelper.showContentView();
            }
        }
    };

    /**
     * 显示错误视图
     */
    public void showErrorView(final Context context, final Throwable ex) {
        isActuQuery = false;
        isLoading = false;
        showContentView();
    }

    /**
     * 显示空视图
     *
     * @param emptyViewRes
     */
    public void showEmptyView(final int emptyViewRes, final String text) {
        isActuQuery = false;
        isLoading = false;
        if (Looper.getMainLooper().getThread().getName().equals(Thread.currentThread().getName())) {
            if (mToolBarHelper != null) {
                mToolBarHelper.showEmptyView(emptyViewRes);
            }
        } else {
            BaqiangApplication.getmMainThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    if (mToolBarHelper != null) {
                        mToolBarHelper.showEmptyView(emptyViewRes);
                    }
                }
            });
        }
    }

    /**
     * 显示内容视图
     */
    public void showContentView() {
        isActuQuery = false;
        isLoading = false;
        //如果在主线程不加handle  by dengyuanming0822
        if ("main".equals(Thread.currentThread().getName())) {
            if (mToolBarHelper != null) {
                mToolBarHelper.showContentView();
            }
        } else {
            BaqiangApplication.getmMainThreadHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    BaqiangApplication.getmMainThreadHandler().removeCallbacks(runnableLoading);
                    BaqiangApplication.getmMainThreadHandler().removeCallbacks(runnableContentView);
                    BaqiangApplication.getmMainThreadHandler().post(runnableContentView);
                }
            }, 200);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //checkServerTime(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaqiangApplication.getmMainThreadHandler().removeCallbacks(runnableLoading);
        BaqiangApplication.getmMainThreadHandler().removeCallbacks(runnableContentView);
    }
}
