package com.jiebao.baqiang.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;

/**
 * 更新APP
 */
public class DownLoadApkFileService extends Service {
    private static final String TAG = DownLoadApkFileService.class.getSimpleName();

    private DownloadManager mDownloadManager;
    private DownloadCompleteReceiver mDownloadCompleteReceiver;
    private String mDownloadUrl;
    private String mDownloadApkName;
    private String DOWNLOADPATH = "/bqapk/";

    class DownloadCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);


                Intent apkintent = new Intent();
                apkintent.setAction("com.jiebao.baqinag.download");
                if (mDownloadManager.getUriForDownloadedFile(downId) != null) {
                    installAPK(context, getRealFilePath(context, mDownloadManager.getUriForDownloadedFile
                            (downId)));
                    apkintent.putExtra("downloadstate", true);
                } else {
                    Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
                    apkintent.putExtra("downloadstate", false);
                }

                sendBroadcast(apkintent);
                DownLoadApkFileService.this.stopSelf();
            }
        }

        private void installAPK(Context context, String path) {
            File file = new File(path);
            if (file.exists()) {
                openFile(file, context);
            } else {
                Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mDownloadUrl = intent.getStringExtra("downloadurl");
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + DOWNLOADPATH
                + "baqiang.apk";
        File file = new File(path);
        if (file.exists()) {
            deleteFileWithPath(path);
        }
        try {
            initDownManager();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Uri uri = Uri.parse("market://details?id=" + getPackageName());
                Intent intent0 = new Intent(Intent.ACTION_VIEW, uri);
                intent0.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent0);
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT).show();
            }
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (mDownloadCompleteReceiver != null) unregisterReceiver(mDownloadCompleteReceiver);
        super.onDestroy();
    }

    private void initDownManager() {
        mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        mDownloadCompleteReceiver = new DownloadCompleteReceiver();
        DownloadManager.Request down = new DownloadManager.Request(Uri.parse(mDownloadUrl));
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager
                .Request.NETWORK_WIFI);
        down.setAllowedOverRoaming(false);
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap
                .getFileExtensionFromUrl(mDownloadUrl));
        down.setMimeType(mimeString);
        down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        down.setVisibleInDownloadsUi(true);
        down.setDestinationInExternalPublicDir(DOWNLOADPATH, "baqiang.apk");
        down.setTitle("巴枪安装文件下载");
        mDownloadManager.enqueue(down);
        registerReceiver(mDownloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    /**
     * 获取apk路径
     *
     * @param context
     * @param uri
     * @return
     */
    public String getRealFilePath(Context context, Uri uri) {
        if (null == uri) {
            return null;
        }

        final String scheme = uri.getScheme();
        String data = null;

        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore
                    .Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    public void openFile(File var0, Context var1) {
        Intent var2 = new Intent();
        var2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        var2.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri uriForFile = FileProvider.getUriForFile(var1, var1.getApplicationContext()
                    .getPackageName() + ".provider", var0);
            var2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            var2.setDataAndType(uriForFile, var1.getContentResolver().getType(uriForFile));
        } else {
            var2.setDataAndType(Uri.fromFile(var0), getMIMEType(var0));
        }

        try {
            var1.startActivity(var2);
        } catch (Exception var5) {
            var5.printStackTrace();
            Toast.makeText(var1, "没有找到打开此类文件的程序", Toast.LENGTH_SHORT).show();
        }
    }

    public String getMIMEType(File var0) {
        String var1 = "";
        String var2 = var0.getName();
        String var3 = var2.substring(var2.lastIndexOf(".") + 1, var2.length()).toLowerCase();
        var1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3);
        return var1;
    }

    public static boolean deleteFileWithPath(String filePath) {
        SecurityManager checker = new SecurityManager();
        File f = new File(filePath);
        checker.checkDelete(filePath);
        if (f.isFile()) {
            f.delete();
            return true;
        }
        return false;
    }

}
