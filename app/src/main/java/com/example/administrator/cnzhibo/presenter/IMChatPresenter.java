package com.example.administrator.cnzhibo.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.example.administrator.cnzhibo.logic.IMLogin;
import com.example.administrator.cnzhibo.presenter.ipresenter.IIMChatPresenter;
import com.example.administrator.cnzhibo.utils.LogUtil;
import com.tencent.TIMCallBack;
import com.tencent.TIMGroupManager;
import com.tencent.TIMManager;
import com.tencent.TIMValueCallBack;

/**
 * Created by zhao on 2017/3/13.
 */

public class IMChatPresenter extends IIMChatPresenter {

    private final static String TAG = IMChatPresenter.class.getSimpleName();
    private String mRoomId;

    public IMChatPresenter(IIMChatView baseView) {
        super(baseView);
    }

    @Override
    public void start() {

    }

    @Override
    public void finish() {

    }

    @Override
    public void createGroup() {
        //在特殊情况下未接收到kick out消息下会导致创建群组失败，在登录前做监测
        checkLoginState(new IMLogin.IMLoginListener() {
            @Override
            public void onSuccess() {
                IMLogin.getInstance().removeIMLoginListener();
                //用户登录，创建直播间
                TIMGroupManager.getInstance().createAVChatroomGroup("cniaow_live", new TIMValueCallBack<String>() {
                    @Override
                    public void onError(int code, String msg) {
                        LogUtil.e(TAG, "create group failed. code: " + code + " errmsg: " + msg);
                    }

                    @Override
                    public void onSuccess(String roomId) {
                        LogUtil.e(TAG, "create group succ, groupId:" + roomId);
                        mRoomId = roomId;
                        mIMChatView.onJoinGroupResult(0, roomId);
                    }
                });
            }

            @Override
            public void onFailure(int code, String msg) {
                IMLogin.getInstance().removeIMLoginListener();
            }
        });
    }

    private void checkLoginState(IMLogin.IMLoginListener loginListener) {

        IMLogin imLogin = IMLogin.getInstance();
        if (TextUtils.isEmpty(TIMManager.getInstance().getLoginUser())) {
            imLogin.setIMLoginListener(loginListener);
            imLogin.checkCacheAndLogin();
        } else {
            //已经处于登录态直接进行回调
            if (null != loginListener)
                loginListener.onSuccess();
        }
    }

    @Override
    public void deleteGroup() {
        TIMGroupManager.getInstance().deleteGroup(mRoomId, new TIMCallBack() {
            @Override
            public void onError(int code, String msg) {
                Log.i(TAG, String.format("delete group error code = %d,msg = %s", code, msg));
            }

            @Override
            public void onSuccess() {
                Log.i(TAG, "delete group success");
            }
        });
    }

    @Override
    public void joinGroup(String roomId) {
        TIMGroupManager.getInstance().applyJoinGroup(roomId, "", new TIMCallBack() {
            @Override
            public void onError(int code, String msg) {
                Log.i(TAG, String.format("join group error code = %d,msg = %s", code, msg));
            }

            @Override
            public void onSuccess() {
                Log.i(TAG, "join group success");
            }
        });
    }

    @Override
    public void quitGroup(String roomId) {
        TIMGroupManager.getInstance().quitGroup(roomId, new TIMCallBack() {
            @Override
            public void onError(int code, String msg) {
                Log.i(TAG, String.format("quit group error code = %d,msg = %s", code, msg));
            }

            @Override
            public void onSuccess() {
                Log.i(TAG, "quit group success");
            }
        });
    }
}
