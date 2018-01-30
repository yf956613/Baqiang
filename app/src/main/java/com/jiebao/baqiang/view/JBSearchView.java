package com.jiebao.baqiang.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.jiebao.baqiang.application.BaqiangApplication;
import com.jiebao.baqiang.R;

/**
 * 搜索视图
 * Created by Administrator on 2016/3/3.
 */
public class JBSearchView extends LinearLayout {
    private ClearEditText clearEditText;
    private android.support.v7.widget.SearchView.OnQueryTextListener onQueryTextListener;

    public android.support.v7.widget.SearchView.OnQueryTextListener getOnQueryTextListener() {
        return onQueryTextListener;
    }

    public void setOnQueryTextListener(android.support.v7.widget.SearchView.OnQueryTextListener onQueryTextListener) {
        this.onQueryTextListener = onQueryTextListener;
    }

    public JBSearchView(Context context) {
        this(context, null);
    }

    public JBSearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JBSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.search_view, this);
        clearEditText = (ClearEditText) findViewById(R.id.et_input_text);
        clearEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = s == null?null:s.toString();
                if(content != null && content.contains("\'")){
                    clearEditText.setText(content.replaceAll("\'","\""));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!iscanQuery){
                    return;
                }
                String content = s.toString();
//                if (onQueryTextListener != null) {
//                    onQueryTextListener.onQueryTextChange(content.replaceAll("\'","\""));
//                }
                 BaqiangApplication.getmMainThreadHandler().removeCallbacks(mTextChangeTask);
                if(mTextChangeTask == null){
                    mTextChangeTask = new TextChangeTask(content);
                }
                mTextChangeTask.setContent(content);
                BaqiangApplication.getmMainThreadHandler().postDelayed(mTextChangeTask,500);//解决快速输入加载重复数据bug
            }
        });
    }
    private boolean iscanQuery = true;
    public void setCanQuery(boolean iscanQuery){
        this.iscanQuery = iscanQuery;
    }

   /**********note add by xc ***************************************/
    private TextChangeTask mTextChangeTask;
    private class TextChangeTask implements Runnable {
        private String content;

        public TextChangeTask(String content){
            this.content = content;
        }
        public void setContent(String content){
            this.content = content;
        }
        @Override
        public void run() {
            if (onQueryTextListener != null) {
                onQueryTextListener.onQueryTextChange(content.replaceAll("\'","\""));
            }
        }
    }
   /**********note add by xc ***************************************/

    public void setSearchKeyWord(String keyWord) {
        clearEditText.setText(keyWord);
    }
    public String getSearchKeyWord(){
        if(clearEditText != null){
            return clearEditText.getText().toString();
        }
        return "";
    }

    /**
     * 设置搜索提示
     */
    public void setSearchHint(String hint) {
        clearEditText.setHint(hint);
    }

    /**
     * 设置搜索提示
     */
    public void setSearchHint(int hint) {
        clearEditText.setHint(hint);
    }

    public ClearEditText getClearEditText() {
        return clearEditText;
    }
}
