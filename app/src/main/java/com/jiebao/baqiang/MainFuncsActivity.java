package com.jiebao.baqiang;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Administrator on 2018/1/16.
 */

public class MainFuncsActivity extends Activity {

    private TextView tvLoginResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainfuncsactivity);

        Bundle bundle=this.getIntent().getExtras();
        String response = bundle.getString("response", "Error");
        tvLoginResult = (TextView)findViewById(R.id.tvLoginResult);
        tvLoginResult.setText(response);

    }

}
