package com.hxjg.vpn.eventbus;


import com.hxjg.vpn.manager.UserManage;

/**
 * eventBus 传递的事件
 */
public interface IEvent {

    class LoginSuccess {

        public boolean notStartMain = false;

        public LoginSuccess(){


        }
    }

    class Logout {
        public Logout(boolean reLogin) {
            UserManage.ins().setUserInfo(null);
            UserManage.ins().setSecretKey(null);
        }

        public boolean reLogin = true;
    }
}
