package com.jiebao.baqiang;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jiebao.baqiang.util.LogUtil;

/**
 * Created by Administrator on 2018/1/16.
 */

public class SetServerInfoActivity extends Activity {

    private static final String TAG = "SetServerInfoActivity";

    private EditText etIp;
    private EditText etPort;
    private Button btnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setserverinfo);

        etIp = (EditText) findViewById(R.id.etIp);
        etPort = (EditText) findViewById(R.id.etPort);
        btnOk = (Button) findViewById(R.id.btnOk);

        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String ip = etIp.getText().toString();
                String port = etPort.getText().toString();

                if (!(TextUtils.isEmpty(ip) || TextUtils.isEmpty(port))) {
                    SharedPreferences sp = getSharedPreferences("ServerInfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("Ip", ip);
                    editor.putString("Port", port);
                    editor.commit();
                    LogUtil.d(TAG, "save new server info ip " + ip + " port " + port);
                    finish();
                }

            }
        });

        SharedPreferences sp = getSharedPreferences("ServerInfo", Context.MODE_PRIVATE);
        if (sp != null) {
            String ip = sp.getString("Ip", "");
            String port = sp.getString("Port", "");
            etIp.setText(ip);
            etPort.setText(port);
        }

    }

}
