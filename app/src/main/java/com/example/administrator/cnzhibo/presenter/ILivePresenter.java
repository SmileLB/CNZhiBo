package com.example.administrator.cnzhibo.presenter;

import android.content.Context;

import com.example.administrator.cnzhibo.model.SimpleUserInfo;
import com.example.administrator.cnzhibo.ui.dialog.UserInfoDialog;


/**
 * Created by Andruby on 2017/5/9.
 */

public abstract class ILivePresenter {
    private UserInfoDialog mUserInfoDialog;

    public void showUserInfo(Context context, SimpleUserInfo userInfo) {
        if (mUserInfoDialog == null) {
            mUserInfoDialog = new UserInfoDialog(context, userInfo);
        }
        mUserInfoDialog.show();
    }
}
