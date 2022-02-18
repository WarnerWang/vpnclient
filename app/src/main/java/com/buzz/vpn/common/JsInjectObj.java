package com.buzz.vpn.common;

import android.app.Activity;
import android.webkit.JavascriptInterface;

import com.buzz.vpn.utils.Logger;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.SoftReference;

public class JsInjectObj {

    private SoftReference<Activity> activitySoftReference;

    public JsInjectObj(Activity activity) {
        this.activitySoftReference = new SoftReference<>(activity);
    }

    /*分享 shareAction  {shareTitle: … ,shareContent:…, shareImgUrl:… shareUrl:… , actId:…}

    更新用户信息 updateUserInfo {userId:… recommendId:… secretKey:… token:… phone:…}

    购买会员 buyVip {}*/

    /**
     * 分享
     * @param shareTitle
     * @param shareContent
     * @param shareImgUrl
     * @param shareUrl
     * @param actId
     * @param actJoinId
     */
    @JavascriptInterface
    public void shareAction(String shareTitle, String shareContent, String shareImgUrl, String shareUrl, String actId, String actJoinId) {
        Logger.i("shareAction,shareTitle=" + shareTitle + ",shareContent=" + shareContent + ",shareImgUrl=" + shareImgUrl + ",shareUrl=" + shareUrl + ",actId=" + actId + ",actJoinId=" + actJoinId);
        Activity context = activitySoftReference.get();
//        ShareActivity.startMe(context, shareTitle, shareContent, shareUrl, shareImgUrl, actId, actJoinId, fromActivity);
    }

    /**
     * 更新用户信息
     * @param userId
     * @param recommendId
     * @param secretKey
     * @param token
     * @param phone
     */
    @JavascriptInterface
    public void updateUserInfo(String userId, String recommendId, String secretKey, String token, String phone) {
        Logger.i("updateUserInfo，userId=" + userId + ",recommendId=" + recommendId + ",secretKey=" + secretKey + ",token=" + token + ",phone=" + phone);
    }
}
