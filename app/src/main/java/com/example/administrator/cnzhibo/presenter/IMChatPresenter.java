package com.example.administrator.cnzhibo.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.example.administrator.cnzhibo.logic.IMLogin;
import com.example.administrator.cnzhibo.model.GiftWithUerInfo;
import com.example.administrator.cnzhibo.model.SimpleUserInfo;
import com.example.administrator.cnzhibo.presenter.ipresenter.IIMChatPresenter;
import com.example.administrator.cnzhibo.utils.AsimpleCache.ACache;
import com.example.administrator.cnzhibo.utils.Constants;
import com.example.administrator.cnzhibo.utils.LogUtil;
import com.google.gson.Gson;
import com.tencent.TIMCallBack;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMGroupManager;
import com.tencent.TIMGroupSystemElem;
import com.tencent.TIMGroupSystemElemType;
import com.tencent.TIMManager;
import com.tencent.TIMMessage;
import com.tencent.TIMMessageListener;
import com.tencent.TIMTextElem;
import com.tencent.TIMValueCallBack;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.List;

/**
 * Created by zhao on 2017/3/13.
 */

public class IMChatPresenter extends IIMChatPresenter implements TIMMessageListener {

    private final static String TAG = "IMChatPresenter------";
    private String mRoomId;

    private TIMConversation mGroupConversation;
    private IIMChatView mIMChatView;
    private Gson mGson = new Gson();

    public IMChatPresenter(IIMChatView baseView) {
        super(baseView);
        mIMChatView = baseView;
    }

    @Override
    public void start() {

    }

    @Override
    public void finish() {
        TIMManager.getInstance().removeMessageListener(this);
        mGroupConversation = null;
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
                        mGroupConversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, mRoomId);
                        TIMManager.getInstance().addMessageListener(IMChatPresenter.this);
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
        sendMessage(Constants.AVIMCMD_LIVE_END, "");
        TIMGroupManager.getInstance().deleteGroup(mRoomId, new TIMCallBack() {
            @Override
            public void onError(int code, String msg) {
                Log.e(TAG, String.format("delete group error code = %d,msg = %s", code, msg));
            }

            @Override
            public void onSuccess() {
                Log.e(TAG, "delete group success");
                finish();
            }
        });
    }

    @Override
    public void joinGroup(String roomId) {
        TIMGroupManager.getInstance().applyJoinGroup(roomId, "", new TIMCallBack() {
            @Override
            public void onError(int code, String msg) {
                Log.e(TAG, String.format("join group error code = %d,msg = %s", code, msg));
            }

            @Override
            public void onSuccess() {
                Log.e(TAG, "join group success");
                mGroupConversation = TIMManager.getInstance().getConversation(TIMConversationType.Group, mRoomId);
                TIMManager.getInstance().addMessageListener(IMChatPresenter.this);
                mIMChatView.onJoinGroupResult(0, mRoomId);
                sendMessage(Constants.AVIMCMD_ENTER_LIVE, "");
            }
        });
    }

    @Override
    public void quitGroup(String roomId) {
        sendMessage(Constants.AVIMCMD_EXIT_LIVE, "");
        TIMGroupManager.getInstance().quitGroup(roomId, new TIMCallBack() {
            @Override
            public void onError(int code, String msg) {
                Log.e(TAG, String.format("quit group error code = %d,msg = %s", code, msg));
            }

            @Override
            public void onSuccess() {
                Log.e(TAG, "quit group success");
                finish();
            }
        });
    }

    @Override
    public void sendTextMsg(final String msg) {
        Log.e(TAG, "-----1-----"+msg);
        sendMessage(Constants.AVIMCMD_TEXT_TYPE, msg);
    }

    @Override
    public void sendPraiseMessage() {
        sendMessage(Constants.AVIMCMD_PRAISE, null);
    }

    @Override
    public void sendPraiseFirstMessage() {
        sendMessage(Constants.AVIMCMD_PRAISE_FIRST, null);
    }


    public void sendGiftMessage(String msg) {
        sendMessage(Constants.AVIMCMD_GIFT, msg);
    }

    @Override
    public boolean onNewMessages(List<TIMMessage> list) {
        Log.e(TAG, "-----5-----");
        parserMessage(list);
        return false;
    }

    private void parserMessage(List<TIMMessage> list) {
        for (TIMMessage msg : list) {
            Log.e(TAG, "-----6-----");
            TIMElem elem = msg.getElement(0);
            if (elem.getType() == TIMElemType.Text) {
                Log.e(TAG, "-----7-----");
                TIMTextElem text = (TIMTextElem) elem;
                handleCustomTextMsg(text.getText());
                Log.e(TAG, "onNewMessages: msg = " + text.getText());
            }else if (elem.getType() == TIMElemType.GroupSystem) {
                TIMGroupSystemElem systemElem = (TIMGroupSystemElem) elem;
                Log.i(TAG, "parserMessage: group msg");
                if (systemElem.getSubtype() == TIMGroupSystemElemType.TIM_GROUP_SYSTEM_DELETE_GROUP_TYPE) {
                    Log.i(TAG, "parserMessage: delete group msg");
                    //观众 退出直播观看
                    mIMChatView.onGroupDeleteResult();
                }
            }
        }
    }

    private void handleCustomTextMsg(String jsonMsg) {
        JSONTokener jsonTokener = new JSONTokener(jsonMsg);
        try {
            JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
            int userAction = jsonObject.getInt("userAction");
            String nickname = null;
            String userId = null;
            String headPic = null;
            String msg = null;
            if (jsonObject.has("userId")) {
                userId = jsonObject.getString("userId");
            }
            if (jsonObject.has("nickname")) {
                nickname = jsonObject.getString("nickname");
            }
            if (jsonObject.has("headPic")) {
                headPic = jsonObject.getString("headPic");
            }
            if (jsonObject.has("msg")) {
                msg = jsonObject.getString("msg");
            }
            switch (userAction) {
                case Constants.AVIMCMD_TEXT_TYPE:
                    mIMChatView.handleTextMsg(new SimpleUserInfo(userId, nickname, headPic), msg);
                    break;
                case Constants.AVIMCMD_PRAISE_FIRST:
                    mIMChatView.handlePraiseFirstMsg(new SimpleUserInfo(userId, nickname, headPic));
                    break;
                case Constants.AVIMCMD_PRAISE:
                    mIMChatView.handlePraiseMsg(new SimpleUserInfo(userId, nickname, headPic));
                    break;
                case Constants.AVIMCMD_ENTER_LIVE:
                    mIMChatView.handleEnterLiveMsg(new SimpleUserInfo(userId, nickname, headPic));
                    break;
                case Constants.AVIMCMD_EXIT_LIVE:
                    mIMChatView.handleExitLiveMsg(new SimpleUserInfo(userId, nickname, headPic));
                    break;
                case Constants.AVIMCMD_HOST_LEAVE:
                    Log.i(TAG, "handleCustomTextMsg: AVIMCMD_HOST_LEAVE");
                    break;
                case Constants.AVIMCMD_HOST_BACK:
                    Log.i(TAG, "handleCustomTextMsg: AVIMCMD_HOST_BACK");
                    break;
                case Constants.AVIMCMD_LIVE_END:
                    Log.i(TAG, "handleCustomTextMsg: AVIMCMD_LIVE_END");
                    mIMChatView.handleExitLiveMsg(new SimpleUserInfo(userId, nickname, headPic));
                    break;
                case Constants.AVIMCMD_GIFT:
                    GiftWithUerInfo giftWithUerInfo = mGson.fromJson(jsonObject.getString("params"), GiftWithUerInfo.class);
                    mIMChatView.handleGift(giftWithUerInfo);
                    break;
            }
        } catch (JSONException e) {
            Log.e(TAG, "-----8-----");
            e.printStackTrace();
        }
    }

    public void sendMessage(int userAction, String msg) {
        JSONObject json = new JSONObject();
        try {
            json.put("userAction", userAction);
            json.put("userId", ACache.get(mIMChatView.getContext()).getAsString("user_id"));
            json.put("nickname", ACache.get(mIMChatView.getContext()).getAsString("nickname"));
            json.put("headPic", ACache.get(mIMChatView.getContext()).getAsString("head_pic"));
            if (msg != null) {
                json.put("msg", msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonMsg = json.toString();
        TIMMessage message = new TIMMessage();
        TIMTextElem textElem = new TIMTextElem();
        textElem.setText(jsonMsg);
        if (message.addElement(textElem) != 0) {
            return;
        }
        Log.e(TAG, "-----2-----"+jsonMsg);
        sendTIMMessage(message, new TIMValueCallBack() {
            @Override
            public void onError(int code, String msg) {
                Log.e(TAG, "-----3-----"+msg);
            }

            @Override
            public void onSuccess(Object o) {
                Log.e(TAG, "-----4-----");
            }
        });
    }

    private void sendTIMMessage(TIMMessage message, TIMValueCallBack callBack) {
        if (mGroupConversation != null) {
            mGroupConversation.sendMessage(message, callBack);
        }
    }
}
