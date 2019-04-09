package com.jrz.mobliesafe1.chatper02.dIalog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jrz.mobliesafe1.R;

public class InterPasswordDialog extends Dialog implements android.view.View.OnClickListener {

    /**对话框标题*/
    private TextView mTitleTV;
    /**输入密码文本框*/
    private EditText mInterET;
    /**确认按钮*/
    private Button mOKBtn;
    /**取消按钮*/
    private Button mCancleBtn;
    /**回调接口*/
    private MyCallBack myCallBack;
    private Context context;

    public InterPasswordDialog(Context context) {
        super(context,R.style.dialog_custom);
        this.context = context;
    }
    public void setCallBack(MyCallBack myCallBack){
        this.myCallBack = myCallBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inter_password_dialog);
        initView();
    }

    private void initView() {
        mTitleTV = findViewById(R.id.tv_interpwd_title);
        mInterET =  findViewById(R.id.et_inter_password);
        mOKBtn =  findViewById(R.id.btn_comfirm);
        mCancleBtn =  findViewById(R.id.btn_dismiss);
        mOKBtn.setOnClickListener(this);
        mCancleBtn.setOnClickListener(this);

    }

    /***
     * 设置对话框标题
     * @param title
     */
    public void setTitle(String title){
        if(!TextUtils.isEmpty(title)){
            mTitleTV.setText(title);
        }
    }

    public String getPassword(){
        return mInterET.getText().toString();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_comfirm:
                myCallBack.confirm();
                break;
            case R.id.btn_dismiss:
                myCallBack.cancle();
                break;
        }
    }

    /**
     * 回调接口
     * @author admin
     */
    public interface MyCallBack{
        void confirm();
        void cancle();
    }
}
