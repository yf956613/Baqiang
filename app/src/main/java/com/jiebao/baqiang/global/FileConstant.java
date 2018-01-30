package com.jiebao.baqiang.global;

import android.os.Environment;

/**
 * Created by Administrator on 2018/1/23 0023.
 */

public interface FileConstant {
    // 巴枪在SDcard中创建的文件目录，用于保存所有文件
    String APP_SDCARD_FILE_NAME = Environment.getExternalStorageDirectory().getPath() +
            "/BaQiang/";
}
