package com.jiebao.baqiang.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jiebao.baqiang.R;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.TextStringUtil;


public class SetServerInfoActivity extends Activity {

    private static final String TAG = "SetServerInfoActivity";

    private EditText mEtIp;
    private EditText mEtPort;
    private Button mBtnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setserverinfo);

        mEtIp = (EditText) findViewById(R.id.etIp);
        mEtPort = (EditText) findViewById(R.id.etPort);
        mBtnOk = (Button) findViewById(R.id.btnOk);

        mBtnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String ip = mEtIp.getText().toString();
                String port = mEtPort.getText().toString();

                if (!(TextUtils.isEmpty(ip) || TextUtils.isEmpty(port))) {
                    if (TextStringUtil.isIpAddressAvailable(ip) &&
                            TextStringUtil.isPortCheckAvailable(port)) {
                        SharedPreferences sp = getSharedPreferences
                                ("ServerInfo",
                                        Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("Ip", ip);
                        editor.putString("Port", port);
                        editor.commit();
                        LogUtil.e(TAG, "save new server info ip " + ip + " " +
                                "port "
                                + port);
                        finish();
                    } else {
                        Toast.makeText(SetServerInfoActivity.this,
                                "IP地址和端口号不符合规范，请修改！", Toast.LENGTH_SHORT)
                                .show();
                    }
                }

            }
        });

        SharedPreferences sp = getSharedPreferences("ServerInfo", Context
                .MODE_PRIVATE);
        if (sp != null) {
            String ip = sp.getString("Ip", "");
            String port = sp.getString("Port", "");
            mEtIp.setText(ip);
            mEtPort.setText(port);
        }

    }

}
