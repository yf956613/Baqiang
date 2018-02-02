package com.jiebao.baqiang.data.arrival;

import com.jiebao.baqiang.global.FileConstant;
import com.jiebao.baqiang.util.FileUtils;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.TextStringUtil;

import java.io.File;

/**
 * 封装卸车到件功能中 待上传文件名
 * <p>
 * 文件名参考：卸车说明文档，具体为：dc + yyyyMMddHHmmss + 序号保证文件名不重复 + .txt
 */

public class UnloadArrivalFileName {
    private static final String TAG = UnloadArrivalFileName.class.getSimpleName();
    private static final String FILE_PREFIX = "xc";

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

    public UnloadArrivalFileName() {
        this.mTime = TextStringUtil.getFormatTimeString();
        this.mNoRepeatString = TextStringUtil.generateShortUuid();
    }

    public UnloadArrivalFileName(String time, String noRepeatString) {
        this.mTime = time;
        this.mNoRepeatString = noRepeatString;
    }

    /**
     * 根据当前JavaBean字段内容，在SD卡的指定目录创建指定名称的文本
     *
     * @return
     */
    public boolean linkToTXTFile() {
        String currentFileName = FILE_PREFIX + this.mTime + this.mNoRepeatString + FILE_SUFFIX;
        final String fileName = FileConstant.APP_SDCARD_FILE_NAME + currentFileName;

        // return FileUtils.createOrExistsFile(fileName) ? true : false;
        if (FileUtils.createOrExistsFile(fileName)) {
            mCurrentFileName = fileName;
            return true;
        }

        return false;
    }
}
