package com.jiebao.baqiang.activity;

import android.content.Context;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.view.ClearEditText;
import com.jiebao.baqiang.view.JBSearchView;

//import rx.Subscription;


/**
 * 标题栏带搜索框的activity基类
 * Created by shanjiang_gao on 2015/12/30.
 */
public abstract class BaseActivityWithSearchTitle extends BaseActivityWithTitleBar implements SearchView.OnQueryTextListener {
    public abstract int setSearchViewHint();

    public MenuItem searchMenuItem;
   // protected Subscription searchSubscription;
    public JBSearchView searchView;
    //搜索条件
    public String serachCondition = "";

    //菜单资源文件
    protected int getInflateId() {
        return R.menu.menu_base_list;
    }

    /**
     * 搜索按钮展开
     *
     * @param item
     */
    public void onActionMenuItemActionExpand(MenuItem item) {
    }

    /**
     * 搜索按钮收起
     *
     * @param item
     */
    public void onActionMenuItemActionCollapse(MenuItem item) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(getInflateId(), menu);
        searchView = (JBSearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setBackgroundResource(R.drawable.bg_item_column);
        searchView.setOnQueryTextListener(this);
        if (setSearchViewHint() > 0) {
            searchView.setSearchHint(setSearchViewHint());
            searchMenuItem = menu.findItem(R.id.action_search);
            //搜索框展开/收起监听器
            MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    ClearEditText clearEditText = searchView.getClearEditText();
                    if (clearEditText != null) {
                        clearEditText.setFocusable(true);
                        clearEditText.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    onActionMenuItemActionExpand(item);
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    if (!TextUtils.isEmpty(searchView.getSearchKeyWord())) {
                        searchView.setSearchKeyWord("");
                    }
                   // hidesoftInputBord(searchView.getClearEditText());
                    onActionMenuItemActionCollapse(item);
                    return true;
                }
            });
        }

        return super.onCreateOptionsMenu(menu);
    }


}
