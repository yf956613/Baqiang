package com.jiebao.baqiang.data.zcfajianmentDispatch;

import com.jiebao.baqiang.global.FileConstant;
import com.jiebao.baqiang.util.FileUtils;
import com.jiebao.baqiang.util.LogUtil;
import com.jiebao.baqiang.util.TextStringUtil;

import java.io.File;

/**
 * Created by Administrator on 2018/3/23 0023.
 */

public class TestFileName {

    private static final String TAG = TestFileName.class.getSimpleName();

    private static final String FILE_PREFIX = "zc";

    private String mTime;
    private String mNoRepeatString;

    private String mCurrentFileName;

    public String getmCurrentFileName() {
        return mCurrentFileName;
    }

    public File getFileInstance() {
        LogUtil.d(TAG, "mCurrentFileName: " + mCurrentFileName);
        // TODO 创建文件
        linkToTXTFile();

        return FileUtils.getFileByPath(mCurrentFileName);
    }

    private static final String FILE_SUFFIX = ".txt";

    public TestFileName() {
        this.mTime = TextStringUtil.getFormatTimeString();
        this.mNoRepeatString = TextStringUtil.generateShortUuid();
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
