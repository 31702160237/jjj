package com.jrz.mobliesafe1;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.jrz.mobliesafe1.chatper01.adapter.HomeAdapter;
import com.jrz.mobliesafe1.chatper02.LostFindActivity;
import com.jrz.mobliesafe1.chatper02.dIalog.SetupPasswordDialog;
import com.jrz.mobliesafe1.chatper02.dIalog.InterPasswordDialog;
import com.jrz.mobliesafe1.chatper02.receiver.MyDeviceAdminReciever;
import com.jrz.mobliesafe1.chatper02.utils.MD5Utils;

public class HomeAcitvity extends Activity {

    //声明GridView该控件类似ListView
    private GridView gv_home;
    //存储手机防盗密码的sp
    private SharedPreferences msharedPreferences;
    //设备管理员
    private DevicePolicyManager policyManager;
    //申请权限
    private ComponentName componentName;
    private long mExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化布局
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home);
        msharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
        gv_home = findViewById(R.id.gv_home);
        //初始化GridView
        gv_home.setAdapter(new HomeAdapter(HomeAcitvity.this));
        //设置条目的点击事件
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //parten代表gridView，view代表每个条目的view对象，position代表每个条目的位置
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0: //手机防盗
                        if(isSetUpPassword()){
                            //弹出输入密码对话框
                            showInterPswdDialog();
                        }else {
                            //弹出设置密码对话框
                            showSetUpPswdDialog();
                        }
                        break;
                }
            }
        });
        //1.获取设备管理员
        policyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        //2.申请权限，MyDeviceAdminReciever继承自DeviceAdminReceiver
        componentName = new ComponentName(this, MyDeviceAdminReciever.class);
        //3.判断，如果没有权限则申请权限
        boolean active = policyManager.isAdminActive(componentName);
        if(!active){
            //没有管理员权限，则获取管理员的权限
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"获取超级管理员权限，用于远程锁屏和清除数据");
            startActivity(intent);
        }
    }
    /**
     * 弹出设置密码对话框
     */
    private void showSetUpPswdDialog(){
        final SetupPasswordDialog setUpPawwordDialog = new SetupPasswordDialog(HomeAcitvity.this);
        setUpPawwordDialog.setMyCallBack(new SetupPasswordDialog.MyCallBack() {
            @Override
            public void ok() {
                String firstPwsd = setUpPawwordDialog.mFirstPWDET.getText().toString().trim();
                String affirmPwsd = setUpPawwordDialog.mAffirmEF.getText().toString().trim();
                if(!TextUtils.isEmpty(firstPwsd) && !TextUtils.isEmpty(affirmPwsd)){
                    if(firstPwsd.equals(affirmPwsd)){
                        //两次密码一致，存储密码
                        savePswd(affirmPwsd);
                        setUpPawwordDialog.dismiss();
                        //显示输入密码对话框
                        showInterPswdDialog();
                    }else {
                        Toast.makeText(HomeAcitvity.this,"两次密码不一致！",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(HomeAcitvity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void cancle() {
                setUpPawwordDialog.dismiss();
            }
        });
        setUpPawwordDialog.setCancelable(true);
        setUpPawwordDialog.show();
    }
    /**
     * 弹出输入密码框
     */
    private void showInterPswdDialog(){
        final String password = getPassword();
        final InterPasswordDialog mInPswdDialog = new InterPasswordDialog(HomeAcitvity.this);
        mInPswdDialog.setCallBack(new InterPasswordDialog.MyCallBack() {
            @Override
            public void confirm() {
                if(TextUtils.isEmpty(mInPswdDialog.getPassword())){
                    Toast.makeText(HomeAcitvity.this,"密码不能为空!",Toast.LENGTH_SHORT).show();
                }else if(password.equals(MD5Utils.encode(mInPswdDialog.getPassword()))){
                    //进入防盗界面
                    mInPswdDialog.dismiss();
                    startActivity(LostFindActivity.class);
                }else {
                    //对话框消失，弹出土司
                    mInPswdDialog.dismiss();
                    Toast.makeText(HomeAcitvity.this,"密码有误，请重新输入!",Toast.LENGTH_SHORT).show();
                }
            }


            /**
             * 开启新的activity不关闭自己
             * @param cls
             */
            private void startActivity(Class<?> cls) {
                Intent intent = new Intent(HomeAcitvity.this,cls);
                HomeAcitvity.this.startActivity(intent);
            }

            /**
             * 按两次返回键退出程序
             */


            @Override
            public void cancle() {
                mInPswdDialog.dismiss();
            }
        });
        mInPswdDialog.setCancelable(true);
        //对话框显示
        mInPswdDialog.show();
    }

    private void savePswd(String affirmPwsd){
        SharedPreferences.Editor edit = msharedPreferences.edit();
        //为了防止用户隐私被泄露，因此需要加密密码
        edit.putString("PhoneAntiTheftPWD", MD5Utils.encode(affirmPwsd));
        edit.commit();
    }

    private boolean isSetUpPassword(){
        String password = msharedPreferences.getString("PhoneAntiTheftPWD",null);
        if(TextUtils.isEmpty(password)){
            return false;
        }
        return true;
    }

    private String getPassword(){
        String password = msharedPreferences.getString("PhoneAntiTheftPWD",null);
        if(TextUtils.isEmpty(password)){
            return "";
        }
        return password;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if((System.currentTimeMillis()-mExitTime)>2000){
                Toast.makeText(this,"再按一次退出程序",Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            }else {
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
