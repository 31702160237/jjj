package com.jrz.mobliesafe1.chatper02.receiver;

/**
 * Created by jrz on 2019/3/14.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jrz.mobliesafe1.App;

/**
 * 监听开机启动的广播接收者，主要用于检查SIM卡是否被更换，如果被更换则发送短信给安全号码
 */
public class BootCompleteReciever extends BroadcastReceiver{

    private static final String TAG = BootCompleteReciever.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        ((App)context.getApplicationContext()).correctSIM();//初始化
    }
}
