package com.buzz.vpn.ui.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.buzz.vpn.R;
import com.buzz.vpn.base.BaseActivity;
import com.buzz.vpn.databinding.ActivityLoginBinding;
import com.buzz.vpn.utils.StringUtils;
import com.buzz.vpn.utils.ToastUtil;
import com.buzz.vpn.widge.WJTextView;

import androidx.annotation.Nullable;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {

    EditText phoneEdit;
    WJTextView sendCodeBtn;
    ImageButton chooseBtn;
    TextView descText;

    public static void startMe(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
        ((Activity) context).startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding.navBar.ibToolbarBack.setVisibility(View.GONE);
        phoneEdit = viewBinding.phoneEdit;
//        phoneEdit.setText("15508659017");
        descText = viewBinding.descText;
        setStatusBarColor(R.color.backgroundColor);
        viewBinding.sendCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = phoneEdit.getText().toString();
                if (StringUtils.isEmpty(phoneNumber) || phoneNumber.length() != 11) {
                    ToastUtil.ins().show("请输入正确手机号");
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString(LoginCodeActivity.KEY_LOGIN_MOBILE, phoneNumber);
                startMe(LoginActivity.this, LoginCodeActivity.class, bundle);
            }
        });
        viewBinding.chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
