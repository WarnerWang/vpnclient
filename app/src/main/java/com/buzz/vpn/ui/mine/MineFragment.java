package com.buzz.vpn.ui.mine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.buzz.vpn.R;
import com.buzz.vpn.api.bean.commonBean.user.UserInfo;
import com.buzz.vpn.base.BaseFragment;
import com.buzz.vpn.databinding.FragmentMineBinding;
import com.buzz.vpn.manager.UserManage;
import com.buzz.vpn.widge.WJTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MineFragment extends BaseFragment<FragmentMineBinding> {

    ImageView userHeaderIcon;
    TextView userName;
    TextView userPhone;
    View cellProtocol;
    View cellProvacy;
    WJTextView logoutBtn;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        userHeaderIcon = viewBinding.userHeaderIcon;
        userName = viewBinding.userName;
        userPhone = viewBinding.userPhone;
        logoutBtn = viewBinding.logoutBtn;
        TextView protocolName = viewBinding.cellProtocol.cellName;
        ImageView protocolIcon = viewBinding.cellProtocol.cellIcon;
        TextView provacyName = viewBinding.cellProvacy.cellName;
        ImageView provacyIcon = viewBinding.cellProvacy.cellIcon;
        protocolName.setText("用户协议");
        protocolIcon.setImageResource(R.mipmap.protocol_icon);
        provacyName.setText("隐私条款");
        provacyIcon.setImageResource(R.mipmap.provacy_icon);
        setUserInfoData();
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserManage.ins().logout();
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void setUserInfoData(){
        UserInfo userInfo = UserManage.ins().getUserInfo();
        if (userInfo == null) {
            UserManage.ins().logout();
            return;
        }
        userName.setText(userInfo.getUserName());
        userPhone.setText(userInfo.getPhone());
    }
}
