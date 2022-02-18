package com.buzz.vpn.utils;

import android.widget.Toast;


import de.blinkt.openvpn.core.App;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class ToastUtil {
    private static volatile ToastUtil ins;
    private Toast toast;

    private ToastUtil() {
    }

    public static ToastUtil ins() {
        if (ins == null) {
            synchronized (ToastUtil.class) {
                if (ins == null) {
                    ins = new ToastUtil();
                }
            }
        }
        return ins;
    }

    public void show(String text) {
        if (StringUtils.isEmpty(text)) {
            return;
        }
        show(text, false);

    }

    public void show(String text, final boolean longTimeShow) {
        Observable.just(text)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        if (toast == null) {
                            toast = Toast.makeText(App.ins(), s, longTimeShow ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
                        } else {
                            toast.setText(s);
                        }
                        toast.show();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Logger.printException(throwable);
                    }
                });

    }
}
