package com.jiebao.baqiang.data.dispatch;

import com.jiebao.baqiang.global.FileConstant;
import com.jiebao.baqiang.util.FileUtils;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.TextStringUtil;

import java.io.File;

/**
 * 发件功能中：根据指定字段内容创建文件
 */

public class ShipmentDispatchFileName {
    private static final String TAG = ShipmentDispatchFileName.class
            .getSimpleName();

    private static final String FILE_PREFIX = "fj";

    private String mTime;
    private String mNoRepeatString;

    private String mCurrentFileName;

    public String getmCurrentFileName() {
        return mCurrentFileName;
    }

    public File getFileInstance() {
        LogUtil.d(TAG, "mCurrentFileName: " + mCurrentFileName);

        return FileUtils.getFileByPath(mCurrentFileName);
    }

    private static final String FILE_SUFFIX = ".txt";

    public ShipmentDispatchFileName() {
        this.mTime = TextStringUtil.getFormatTimeString();
        this.mNoRepeatString = TextStringUtil.generateShortUuid();
    }

    public ShipmentDispatchFileName(String time, String noRepeatString) {
        this.mTime = time;
        this.mNoRepeatString = noRepeatString;
    }

    /**
     * 根据当前JavaBean字段内容，在SD卡的指定目录创建指定名称的文本
     *
     * @return
     */
    public boolean linkToTXTFile() {
        String currentFileName = FILE_PREFIX + this.mTime + this
                .mNoRepeatString + FILE_SUFFIX;
        final String fileName = FileConstant.APP_SDCARD_FILE_NAME +
                currentFileName;

        // return FileUtils.createOrExistsFile(fileName) ? true : false; 创建文件
        if (FileUtils.createOrExistsFile(fileName)) {
            mCurrentFileName = fileName;
            return true;
        }

        return false;
    }

}
