package com.jiebao.baqiang.global;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;

import com.jiebao.baqiang.listener.ICustomDialogListener;
//import com.jiebao.baqiang.util.DialogUtil;

/**
 * 无法打开权限 时 ，需要调到应用的权限设置界面 打开相关权限（允许，或 询问）
 */
public class PermissionSettingManager {
    public static void showPermissionSetting(final boolean close, final FragmentActivity activity, String tipMsg, final String content){
//           DialogUtil.showCustomDialog(activity, tipMsg, content, false, "", new ICustomDialogListener() {
//               @Override
//               public void onDialogPositiveClick(int requestCode, String inputArg, Object object) {
//                   Intent intent = new Intent();
//                   intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                   intent.setData(Uri.fromParts("package","com.jiebao.baqiang", null));
//                   activity.startActivity(intent);
//               }
//
//               @Override
//               public void onDialogNegativeClick(int requestCode) {
//                   Intent intent = new Intent();
//                   intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                   intent.setData(Uri.fromParts("package","com.jiebao.baqiang", null));
//                   activity.startActivity(intent);
//               }
//           });
    }
}
