package com.jrz.mobliesafe1.chatper01.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.io.File;

/**
 * Created by Administrator on 2019/3/12.
 */

public class MyUtils {

    public static String getVersion(Context context){
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(),0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
    public static void installApk(Activity activity){
        Intent intent = new Intent("android.intent.action.VIEW");
        //添加默认分类
        intent.addCategory("android.intent.category.DEFAULT");
        //设置数据和类型
        intent.setDataAndType(Uri.fromFile(new File("/mnt/sdcard/mobilesafe2.0apk")),"app;ocatopm/vnd.android.package-archive");
        //如果开启的Activity退出时会回调当前Activity的onActivityResult
        activity.startActivityForResult(intent,0);
    }
}
