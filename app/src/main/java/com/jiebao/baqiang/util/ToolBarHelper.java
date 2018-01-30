package com.jiebao.baqiang.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.activity.BaseActivityWithTitleBar;

/**
 * Created by Administrator on 2015/12/30.
 */
public class ToolBarHelper {
    /*上下文，创建view的时候需要用到*/
    private Context mContext;

    /*base view*/
    private FrameLayout mContentView;

    /*用户定义的view*/
    private View mUserView;

    /*toolbar*/
    private Toolbar mToolBar;
    private int toolBarSize;
    /*视图构造器*/
    private LayoutInflater mInflater;

    /**
     * 加载中视图
     */
    private View loadingView;

    /**
     * 空视图
     */
    private View emptypView;

    /**
     * 错误视图
     */
    private View errorView;

    /*
    * 两个属性
    * 1、toolbar是否悬浮在窗口之上
    * 2、toolbar的高度获取
    * */
    private static int[] ATTRS = {
            R.attr.windowActionBarOverlay,
            R.attr.actionBarSize
    };

    public ToolBarHelper(Context context, int layoutId) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        /*初始化整个内容*/
        initContentView();
        /*初始化用户定义的布局*/
        initUserView(layoutId);
        /*初始化toolbar*/
        initToolBar();
    }

    private void initContentView() {
        /*直接创建一个帧布局，作为视图容器的父容器*/
        mContentView = new FrameLayout(mContext);
        mContentView.setBackgroundResource(R.color.transparent);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContentView.setLayoutParams(params);

    }

    private void initToolBar() {
        /*通过inflater获取toolbar的布局文件*/
        View toolbar = mInflater.inflate(R.layout.title_bar_layout, mContentView);
        mToolBar = (Toolbar) toolbar.findViewById(R.id.toolbar);
    }

    private void initUserView(int id) {
        mUserView = mInflater.inflate(id, null);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        TypedArray typedArray = mContext.getTheme().obtainStyledAttributes(android.support.v7.appcompat.R.styleable.AppCompatTheme);
        /*获取主题中定义的悬浮标志*/
        boolean overly = typedArray.getBoolean(android.support.v7.appcompat.R.styleable.AppCompatTheme_windowActionBarOverlay, false);
        /*获取主题中定义的toolbar的高度*/
        toolBarSize = (int) typedArray.getDimension(android.support.v7.appcompat.R.styleable.AppCompatTheme_actionBarSize, (int) mContext.getResources().getDimension(R.dimen.abc_action_bar_default_height_material));
        typedArray.recycle();
        /*如果是悬浮状态，则不需要设置间距*/
        params.topMargin = overly ? 0 : toolBarSize;
        mContentView.addView(mUserView, params);
    }

    public FrameLayout getContentView() {
        return mContentView;
    }

    public Toolbar getToolBar() {
        return mToolBar;
    }

    /**
     * 显示加载中视图
     *
     * @param loadingViewRes
     */
    public void showLoadingView(int loadingViewRes) {
        if (loadingView == null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            loadingView = mInflater.inflate(R.layout.loading_view, null);
            TextView tvcontent = (TextView) loadingView.findViewById(R.id.dialog_wating_message);
            ImageView imageView = (ImageView) loadingView.findViewById(R.id.dialog_wating_image);
            tvcontent.setText(R.string.loading);
            tvcontent.setTextColor(Color.GRAY);
            Animatable animatable = (Animatable) imageView.getBackground();
            animatable.start();
            loadingView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
            mContentView.addView(loadingView, params);
        } else {
            loadingView.setVisibility(View.VISIBLE);
            mContentView.bringChildToFront(loadingView);
        }
        if (errorView != null) {
            errorView.setVisibility(View.GONE);
        }
        if (emptypView != null) {
            emptypView.setVisibility(View.GONE);
        }
    }

    /**
     * 显示错误视图
     */
//    public void showErrorView(final Context context, final Throwable ex) {
//        if (errorView == null) {
//            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            errorView = mInflater.inflate(R.layout.error_view, null);
//            Button btnError = (Button) errorView.findViewById(R.id.error_detail);
//            Button btnBack = (Button) errorView.findViewById(R.id.back);
//            btnError.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context, ErrorInfoActivity.class);
//                    AppCacheHelper.getInstance().put(Throwable.class.getSimpleName(), ex);
//                    context.startActivity(intent);
//                }
//            });
//            btnBack.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    ((BaseActivityWithTitleBar) context).finish();
//                    context.stopService(new Intent(context, DataSyncService.class));
//                    AppUtils.restartApp();
//                }
//            });
//            mContentView.addView(errorView, params);
//        } else {
//            errorView.setVisibility(View.VISIBLE);
//            mContentView.bringChildToFront(errorView);
//        }
//    }

    /**
     * 显示空视图
     *
     * @param loadingViewRes
     */
    public void showEmptyView(int loadingViewRes) {
        if (emptypView == null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            emptypView = mInflater.inflate(loadingViewRes, null);
            mContentView.addView(emptypView, params);
        } else {
            mContentView.bringChildToFront(emptypView);
        }
    }

    /**
     * 显示内容视图
     */
    public void showContentView() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (emptypView != null) {
            emptypView.setVisibility(View.GONE);
        }
        if (errorView != null) {
            errorView.setVisibility(View.GONE);
        }
    }
}
