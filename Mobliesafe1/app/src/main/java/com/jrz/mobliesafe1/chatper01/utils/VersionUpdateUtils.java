package com.jrz.mobliesafe1.chatper01.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.jrz.mobliesafe1.HomeAcitvity;
import com.jrz.mobliesafe1.R;
import com.jrz.mobliesafe1.chatper01.entity.VersionEntity;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2019/3/12.
 */

public class VersionUpdateUtils {
    private static final int MESSAGE_NET_EEOR = 101;
    private static final int MESSAGE_IO_EEOR = 102;
    private static final int MESSAGE_JSON_EEOR = 103;
    private static final int MESSAGE_SHOEW_DIALOG = 104;
    protected static final int MESSAGE_ENTERHOME = 105;

    /**
     * 更新UI
     * 导入的是OS包
     */
    private Handler handler = new Handler(){ //注意：导入的是OS包

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_IO_EEOR:
                    Toast.makeText(context,"IO异常",Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case MESSAGE_JSON_EEOR:
                    Toast.makeText(context,"JSON异常",Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case MESSAGE_NET_EEOR:
                    Toast.makeText(context,"网络异常",Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case MESSAGE_SHOEW_DIALOG:
                    //弹出对话框
                    showUpdateDialog(versionEntity);
                    break;
                case MESSAGE_ENTERHOME:
                    //使用意图进入主页面
                    Intent intent = new Intent(context, HomeAcitvity.class);
                    context.startActivity(intent);
                    context.finish();
                    break;
            }
        }
    };

    /**
     * 本地版本号
     */
    private String mVersion;
    private Activity context;
    private ProgressDialog mProgressDialog;
    private VersionEntity versionEntity;

    public VersionUpdateUtils(String Version, Activity activity) {
        mVersion = Version;
        context = activity;
    }

    /**
     * 弹出更新提示对话框
     */
    private void showUpdateDialog(final VersionEntity versionEntity){
        //创建dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("检查到新版本:"+versionEntity.versioncode);//设置标题
        builder.setMessage(versionEntity.description);
        //根据服务器返回描述，设置升级描述信息
        builder.setCancelable(false);//设置不能点击手机返回按钮隐藏对话框
        builder.setIcon(R.mipmap.ic_launch2);//设置对话框图标
        //设置立即升级按钮点击事件
        builder.setPositiveButton("立即升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                initProgressDialog();
                downloadNewApk(versionEntity.apkurl);
            }
        });
        //设置暂不升级按钮点击事件
        builder.setNegativeButton("暂不升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                enterHome();
            }
        });
        builder.show();
    }



    /**
     * 网络请求获取服务器版本号
     * 1、创建网络请求对象
     * 2、网络连接超时和请求超时的设置
     * 3、访问成功，得到服务器返回的数据
     * 4、对数据进行解析（json解析）
     */
    public void getCloudVersion(){
        try {
            HttpClient client = new DefaultHttpClient();
             /*连接超时*/
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 5000);
            /*请求超时*/
            HttpConnectionParams.setSoTimeout(client.getParams(), 5000);
            HttpGet httpGet = new HttpGet(
                    "http://172.26.41.152:8080/updateinfo.html");
            //向服务器发送请求后，得到响应后的结果
            HttpResponse execute = client.execute(httpGet);
            if (execute.getStatusLine().getStatusCode() == 200) {//200访问成功
                // 请求和响应都成功了
                //得到服务器的实体内容
                HttpEntity entity = execute.getEntity();
                //对实体内容的字符串进行json解析
                String result = EntityUtils.toString(entity, "gbk");
                // 创建jsonObject对象
                JSONObject jsonObject = new JSONObject(result);
                versionEntity = new VersionEntity();
                String code = jsonObject.getString("code");
                versionEntity.versioncode = code;
                String des = jsonObject.getString("des");
                versionEntity.description = des;
                String apkurl = jsonObject.getString("apkurl");
                versionEntity.apkurl = apkurl;
                if (!mVersion.equals(versionEntity.versioncode)) {
                    // 版本号不一致
                    handler.sendEmptyMessage(MESSAGE_SHOEW_DIALOG);//更新UI发送的消息
                }
            }
        } catch (ClientProtocolException e) {
            handler.sendEmptyMessage(MESSAGE_NET_EEOR);
            e.printStackTrace();
        } catch (IOException e) {
            handler.sendEmptyMessage(MESSAGE_IO_EEOR);
            e.printStackTrace();
        }catch (JSONException e) {
            handler.sendEmptyMessage(MESSAGE_JSON_EEOR);
            e.printStackTrace();
        }
    }

    /**
     * 初始化进度对话框
     */
    private void initProgressDialog() {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("准备下载...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.show();
    }

    /**
     * 下载新版本apk
     * @param apkurl
     */

    private void downloadNewApk(String apkurl) {
        DownLoadUtils downLoadUtils = new DownLoadUtils();
        downLoadUtils.downapk(apkurl, "/mnt/sdcard/mobilesafe2.0.apk", new DownLoadUtils.MyCallBack() {
            @Override
            public void onSuccess(ResponseInfo<File> arg0) {
                mProgressDialog.dismiss();
                MyUtils.installApk(context);
            }

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                mProgressDialog.setMessage("下载失败...");
                mProgressDialog.dismiss();
                enterHome();
            }

            @Override
            public void onLoadding(long total, long current, boolean isUploading) {
                mProgressDialog.setMax((int)total);
                mProgressDialog.setMessage("正在下载...");
                mProgressDialog.setProgress((int)current);//进度值
            }
        });
    }
    private void enterHome(){

        handler.sendEmptyMessageDelayed(MESSAGE_ENTERHOME,2000);
    }
}
