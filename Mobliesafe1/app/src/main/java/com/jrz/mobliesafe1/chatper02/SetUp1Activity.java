package com.jrz.mobliesafe1.chatper02;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.Toast;

import com.jrz.mobliesafe1.R;

public class SetUp1Activity extends BaseSetUpActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
        initView();
    }

    @Override
    public void showPre() {
        Toast.makeText(this,"当前月面已经是第一页",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNext() {
        startActivityAndFinishSelf(SetUp2Activity.class);
    }
    private void initView(){
        //设置第一个小圆点的颜色
        ((RadioButton)findViewById(R.id.rb_first)).setChecked(true);
    }
}
