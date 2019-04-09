package com.jrz.mobliesafe1.chatper02.receiver;

/**
 * Created by jrz on 2019/3/15.
 */

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 定于特殊的广播接收者，系统超级管理员的广播接收者
 */
public class MyDeviceAdminReciever extends DeviceAdminReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
