package com.arthur.appupdate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ListPopupWindow;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ListPopupWindow mListPopupWindow;
    private EditText mEdt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mEdt = this.findViewById(R.id.edt);

        mListPopupWindow = new ListPopupWindow(this);
        /*mListPopupWindow.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, list));*/
        mListPopupWindow.setAnchorView(mEdt);
        mListPopupWindow.setModal(true);
        mListPopupWindow.show();
    }

}
