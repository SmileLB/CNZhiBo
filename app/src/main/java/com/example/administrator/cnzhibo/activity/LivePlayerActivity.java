package com.example.administrator.cnzhibo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.cnzhibo.LiveApp;
import com.example.administrator.cnzhibo.R;
import com.example.administrator.cnzhibo.adapter.ChatMsgListAdapter;
import com.example.administrator.cnzhibo.adapter.UserAvatarListAdapter;
import com.example.administrator.cnzhibo.logic.FrequeMgr;
import com.example.administrator.cnzhibo.logic.IMLogin;
import com.example.administrator.cnzhibo.model.ChatEntity;
import com.example.administrator.cnzhibo.model.GiftInfo;
import com.example.administrator.cnzhibo.model.GiftWithUerInfo;
import com.example.administrator.cnzhibo.model.LiveInfo;
import com.example.administrator.cnzhibo.model.SimpleUserInfo;
import com.example.administrator.cnzhibo.presenter.IMChatPresenter;
import com.example.administrator.cnzhibo.presenter.LiveGiftPresenter;
import com.example.administrator.cnzhibo.presenter.LivePlayerPresenter;
import com.example.administrator.cnzhibo.presenter.ipresenter.IIMChatPresenter;
import com.example.administrator.cnzhibo.presenter.ipresenter.ILiveGiftPresenter;
import com.example.administrator.cnzhibo.presenter.ipresenter.ILivePlayerPresenter;
import com.example.administrator.cnzhibo.service.LiveGiftServices;
import com.example.administrator.cnzhibo.ui.customviews.EndDetailFragment;
import com.example.administrator.cnzhibo.ui.customviews.HeartLayout;
import com.example.administrator.cnzhibo.ui.customviews.InputTextMsgDialog;
import com.example.administrator.cnzhibo.ui.gift.LiveGiftView;
import com.example.administrator.cnzhibo.utils.AsimpleCache.ACache;
import com.example.administrator.cnzhibo.utils.Constants;
import com.example.administrator.cnzhibo.utils.LogUtil;
import com.example.administrator.cnzhibo.utils.OtherUtils;
import com.example.administrator.cnzhibo.utils.ToastUtils;
import com.google.gson.Gson;
import com.tencent.TIMMessage;
import com.tencent.imcore.MemberInfo;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * @Description: 观众 观看播放页面  (  页面元素空)
 */
public class LivePlayerActivity extends IMBaseActivity implements View.OnClickListener,
        ILivePlayerPresenter.ILivePlayerView, IIMChatPresenter.IIMChatView, InputTextMsgDialog.OnTextSendListener
        ,UserAvatarListAdapter.OnItemClickListener,ILiveGiftPresenter.ILiveGiftView,LiveGiftView.LiveGiftViewListener{

    private static final String TAG = LivePlayerActivity.class.getSimpleName();
    public final static int LIVE_PLAYER_REQUEST_CODE = 1000;

    private TXCloudVideoView mTXCloudVideoView;
    private TXLivePlayConfig mTXPlayConfig = new TXLivePlayConfig();
    private boolean mPausing = false;
    private String mPlayUrl = "";
    private boolean mPlaying = false;
    private LiveInfo mLiveInfo;

    private LivePlayerPresenter mLivePlayerPresenter;
    private IMChatPresenter mIMChatPresenter;
    //主播信息
    private ImageView ivHeadIcon;
    private ImageView ivRecordBall;
    private TextView tvPuserName;
    private TextView tvMemberCount;
    private long mMemberCount = 0;

    private int mTotalCount = 0; //总观众人数
    private int mPraiseCount = 0;
    private long mLiveStartTime = 0;

    private InputTextMsgDialog mInputTextMsgDialog;

    //消息列表
    private ArrayList<ChatEntity> mArrayListChatEntity = new ArrayList<>();
    private ChatMsgListAdapter mChatMsgListAdapter;
    private ListView mListViewMsg;

    //点赞频率控制
    private FrequeMgr mLikeFrequeControl;
    private HeartLayout mHeartLayout;

    //观众列表
    private RecyclerView mUserAvatarList;
    private UserAvatarListAdapter mAvatarListAdapter;

    private int mJoinCount = 0;
    private boolean mOfficialMsgSended = false;
    //背景
    private ImageView ivLiveBg;

    //礼物
    private LiveGiftView mLiveGiftView;
    private LiveGiftPresenter mLiveGiftPresenter;
    private Gson mGson = new Gson();
    private boolean isGifViewShowing;
    //礼物服务
    private FrameLayout mGiftRootView;
    private LiveGiftServices.LiveGiftShowBinder mLiveGiftShowBinder;
    private ServiceConnection mGiftConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mLiveGiftShowBinder = (LiveGiftServices.LiveGiftShowBinder) service;
            mLiveGiftShowBinder.initGiftShowManager(mContext, mGiftRootView);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void setBeforeLayout() {
        super.setBeforeLayout();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_live_player;
    }

    @Override
    protected void initView() {
        getDataFormIntent();
        initLiveView();

        //mPlayerView即step1中添加的界面view
        mTXCloudVideoView = obtainView(R.id.video_view);
        mLivePlayerPresenter = new LivePlayerPresenter(this);
        mLivePlayerPresenter.initPlayerView(mTXCloudVideoView, mTXPlayConfig);
        mIMChatPresenter = new IMChatPresenter(this);

        //主播信息
        tvPuserName = obtainView(R.id.tv_broadcasting_time);
        tvPuserName.setText(OtherUtils.getLimitString(mLiveInfo.userInfo.nickname, 10));
        ivRecordBall = obtainView(R.id.iv_record_ball);
        ivRecordBall.setVisibility(View.GONE);
        ivHeadIcon = obtainView(R.id.iv_head_icon);
        OtherUtils.showPicWithUrl(this, ivHeadIcon, mLiveInfo.userInfo.headPic, R.drawable.default_head);
        tvMemberCount = obtainView(R.id.tv_member_counts);

        mInputTextMsgDialog = new InputTextMsgDialog(this, R.style.InputDialog);
        mInputTextMsgDialog.setmOnTextSendListener(this);

        mMemberCount++;
        tvMemberCount.setText(String.format(Locale.CHINA, "%d", mMemberCount));
        if (mPlayUrl != null) {
            mLivePlayerPresenter.startPlay(mPlayUrl, TXLivePlayer.PLAY_TYPE_LIVE_FLV); //推荐FLV
        } else {
            showToast("play url is empty");
        }

        mIMChatPresenter.joinGroup(mLiveInfo.groupId);

        mListViewMsg = obtainView(R.id.im_msg_listview);
        mChatMsgListAdapter = new ChatMsgListAdapter(this, mListViewMsg, mArrayListChatEntity);
        mListViewMsg.setAdapter(mChatMsgListAdapter);

        mHeartLayout = obtainView(R.id.heart_layout);

        //观众列表
        mUserAvatarList = obtainView(R.id.rv_user_avatar);
        mUserAvatarList.setVisibility(View.VISIBLE);
        mAvatarListAdapter = new UserAvatarListAdapter(this, IMLogin.getInstance().getLastUserInfo().identifier);
        mUserAvatarList.setAdapter(mAvatarListAdapter);
        mAvatarListAdapter.setOnItemClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mUserAvatarList.setLayoutManager(linearLayoutManager);

        ivLiveBg = obtainView(R.id.iv_live_bg);

        initGift();

    }

    private void initGift() {
        mLiveGiftView = obtainView(R.id.live_gift_view);
        mGiftRootView = obtainView(R.id.liveGiftLayout);

        mLiveGiftView.initLiveGiftView(this, (RelativeLayout) obtainView(R.id.gift_item_layout));
        mLiveGiftView.setCoinCount(100000);

        mLiveGiftPresenter = new LiveGiftPresenter(this);
        mLiveGiftPresenter.giftList(ACache.get(this).getAsString("user_id"), mLiveInfo.liveId);
        mLiveGiftPresenter.coinCount(ACache.get(this).getAsString("user_id"));

        Intent intent = new Intent(this, LiveGiftServices.class);
        bindService(intent, mGiftConn, BIND_AUTO_CREATE);

    }

    @Override
    protected void initData() {
        String headPic = ACache.get(this).getAsString("head_pic");
        if (!TextUtils.isEmpty(headPic)) {
            OtherUtils.blurBgPic(this, ivLiveBg, ACache.get(this).getAsString("head_pic"), R.drawable.bg);
        }
    }

    private void getDataFormIntent() {
        Intent intent = getIntent();
        mLiveInfo = (LiveInfo) intent.getSerializableExtra(Constants.LIVE_INFO);
        mPlayUrl = mLiveInfo.playUrl;
        LogUtil.e(TAG, "mPlayUrl:" + mPlayUrl);
    }

    @Override
    protected void setListener() {

    }


    /**
     * 初始化观看直播界面
     */
    private void initLiveView() {
        mTXCloudVideoView = obtainView(R.id.video_view);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                showComfirmDialog(getString(R.string.msg_stop_watch), false);
                break;
            case R.id.btn_message_input:
                showInputMsgDialog();
                break;
            case R.id.btn_like:
                if (mLikeFrequeControl == null) {
                    mLikeFrequeControl = new FrequeMgr();
                    mLikeFrequeControl.init(2, 1);
                }
                if (mLikeFrequeControl.canTrigger()) {
                    if (!"1".equals(ACache.get(this).getAsString(mLiveInfo.liveId + "_first_praise"))) {
                        mIMChatPresenter.sendPraiseFirstMessage();
                        ACache.get(this).put(mLiveInfo.liveId + "_first_praise", "1");
                        mLivePlayerPresenter.doLike(ACache.get(this).getAsString("user_id"), mLiveInfo.liveId,  mLiveInfo.userInfo.userId, mLiveInfo.groupId);
                        mHeartLayout.addFavor();
                    } else {
                        mLivePlayerPresenter.doLike(ACache.get(this).getAsString("user_id"), mLiveInfo.liveId, mLiveInfo.userInfo.userId, mLiveInfo.groupId);
                        mIMChatPresenter.sendPraiseMessage();
                        mHeartLayout.addFavor();
                    }
                }
                Log.i(TAG, "onClick: sendPraiseMessage");
                break;
            case R.id.btn_gift:
                mLiveGiftView.show();
                break;
            case R.id.iv_head_icon:
                SimpleUserInfo userInfo = new SimpleUserInfo(mLiveInfo.userInfo.userId, mLiveInfo.userInfo.nickname, mLiveInfo.userInfo.headPic);
                mLivePlayerPresenter.showUserInfo(this, userInfo);
                break;

            default:
                break;
        }
    }

    private void showInputMsgDialog() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = mInputTextMsgDialog.getWindow().getAttributes();

        lp.width = display.getWidth(); //设置宽度
        mInputTextMsgDialog.getWindow().setAttributes(lp);
        mInputTextMsgDialog.setCancelable(true);
        mInputTextMsgDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mInputTextMsgDialog.show();
    }

    @Override
    public void onBackPressed() {
        showComfirmDialog(getString(R.string.msg_stop_watch), false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLivePlayerPresenter.playerPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLivePlayerPresenter.playerResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGiftConn != null) {
            unbindService(mGiftConn);
        }
        mLivePlayerPresenter.stopPlay(true);
        mTXCloudVideoView.onDestroy();
        mIMChatPresenter.quitGroup(mLiveInfo.groupId);
        ACache.get(this).put(mLiveInfo.liveId + "_first_praise", "0");
    }

    public static void invoke(Activity activity, LiveInfo liveInfo) {
        Intent intent = new Intent(activity, LivePlayerActivity.class);
        intent.putExtra(Constants.LIVE_INFO, liveInfo);
        activity.startActivityForResult(intent, LIVE_PLAYER_REQUEST_CODE);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void showMsg(String msg) {
        ToastUtils.makeText(this, msg, Toast.LENGTH_SHORT);
    }

    @Override
    public void showMsg(int msg) {
        ToastUtils.makeText(this, msg, Toast.LENGTH_SHORT);
    }


    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onPlayEvent(int event, Bundle bundle) {
        //播放相关事件
        if (event == TXLiveConstants.PLAY_EVT_PLAY_BEGIN) {
            mLiveStartTime = System.currentTimeMillis();
            ivLiveBg.setVisibility(View.GONE);
            //可以上传播放状态
            if (!mOfficialMsgSended) {
                refreshMsg("", getString(R.string.live_system_name), getString(R.string.live_system_notify), Constants.AVIMCMD_TEXT_TYPE);
                mOfficialMsgSended = true;
            }
        }

        if (event < 0) {
            if (event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {
                showComfirmDialog("请检查网络", true);
            }
        }
        if (event == TXLiveConstants.PLAY_WARNING_HW_ACCELERATION_FAIL) {
            mLivePlayerPresenter.enableHardwareDecode(false);
            stopPlay();
            mLivePlayerPresenter.startPlay(mPlayUrl, TXLivePlayer.PLAY_TYPE_LIVE_FLV);
        }
    }

    /**
     * 显示确认消息
     *
     * @param msg     消息内容
     * @param isError true错误消息（必须退出） false提示消息（可选择是否退出）
     */
    public void showComfirmDialog(String msg, Boolean isError) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(msg);

        if (!isError) {
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    stopPlay();
                    showEndDetail();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else {
            //当情况为错误的时候，直接停止推流
            stopPlay();
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
        }
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }

    @Override
    public void doLikeResult(int result) {

    }

    @Override
    public void onGroupMembersResult(int retCode, int totalCount, ArrayList<SimpleUserInfo> membersList) {
        if (retCode == 0 && totalCount > 0) {
            mTotalCount += totalCount;
            mMemberCount += totalCount;
            tvMemberCount.setText("" + mMemberCount);
            if (membersList != null) {
                for (SimpleUserInfo userInfo : membersList) {
                    mAvatarListAdapter.addItem(userInfo);
                }
            }
        } else {
            LogUtil.e(TAG, "onGroupMembersResult failed");
        }
    }

    @Override
    public void onJoinGroupResult(int code, String msg) {
        if (code != 0) {
            mLivePlayerPresenter.enterGroup(ACache.get(this).getAsString("user_id"),
                    mLiveInfo.liveId, mLiveInfo.userInfo.userId, mLiveInfo.groupId);
            if (Constants.ERROR_GROUP_NOT_EXIT == code) {
                showErrorAndQuit(Constants.ERROR_MSG_GROUP_NOT_EXIT);
            } else if (Constants.ERROR_QALSDK_NOT_INIT == code) {
                mJoinCount++;
                ((LiveApp) getApplication()).initSDK();
                if (mJoinCount > 1) {
                    showErrorAndQuit(Constants.ERROR_MSG_JOIN_GROUP_FAILED);
                } else {
                    mIMChatPresenter.joinGroup(mLiveInfo.groupId);
                }
            } else {
                showErrorAndQuit(Constants.ERROR_MSG_JOIN_GROUP_FAILED + code);
            }
        } else {
            // 进入房间成功  获取 成员数据
            mLivePlayerPresenter.groupMember(ACache.get(this).getAsString("user_id"), mLiveInfo.liveId,
                    mLiveInfo.userInfo.userId, mLiveInfo.groupId, 1, 20);
        }
    }

    @Override
    public void onGroupDeleteResult() {
        stopPlay();
        showEndDetail();
    }

    private void stopPlay() {
        mLivePlayerPresenter.quitGroup(ACache.get(this).getAsString("user_id"),
                mLiveInfo.liveId, mLiveInfo.userInfo.userId, mLiveInfo.groupId);
        mLivePlayerPresenter.stopPlay(true);
        mTXCloudVideoView.onDestroy();
        mIMChatPresenter.quitGroup(mLiveInfo.groupId);
        ACache.get(this).put(mLiveInfo.liveId + "_first_praise", "0");
    }

    private void showEndDetail() {
        long second = 0;
        if (mLiveStartTime != 0) {
            second = (System.currentTimeMillis() - mLiveStartTime) / 1000;
        }
        EndDetailFragment.invoke(getFragmentManager(), second, mPraiseCount, mTotalCount);
    }

    @Override
    public void handleTextMsg(SimpleUserInfo userInfo, String text) {
        ChatEntity entity = new ChatEntity();
        entity.setSenderName(userInfo.nickname + ":");
        entity.setContext(text);
        entity.setType(Constants.AVIMCMD_TEXT_TYPE);
        notifyMsg(entity);
    }

    @Override
    public void handlePraiseMsg(SimpleUserInfo userInfo) {
        mPraiseCount++;
        mHeartLayout.addFavor();
    }

    @Override
    public void handlePraiseFirstMsg(SimpleUserInfo userInfo) {
        ChatEntity entity = new ChatEntity();
        entity.setSenderName(userInfo.nickname + ":");
        entity.setContext("点亮了桃心");
        entity.setType(Constants.AVIMCMD_TEXT_TYPE);
        notifyMsg(entity);
        mHeartLayout.addFavor();
    }

    @Override
    public void onSendMsgResult(int code, TIMMessage timMessage) {

    }

    @Override
    public void handleEnterLiveMsg(SimpleUserInfo userInfo) {
        Log.i(TAG, "handleEnterLiveMsg: ");
        //更新观众列表，观众进入显示
        if (!mAvatarListAdapter.addItem(userInfo))
            return;

        mMemberCount++;
        mTotalCount++;
        tvMemberCount.setText(String.format(Locale.CHINA, "%d", mMemberCount));

        refreshMsg(userInfo.userId, TextUtils.isEmpty(userInfo.nickname) ? userInfo.userId : userInfo.nickname, "进入直播", Constants.AVIMCMD_ENTER_LIVE);
    }

    @Override
    public void handleExitLiveMsg(SimpleUserInfo userInfo) {
        Log.i(TAG, "handleExitLiveMsg: ");
        //更新观众列表，观众退出显示
        if (mMemberCount > 0)
            mMemberCount--;
        tvMemberCount.setText(String.format(Locale.CHINA, "%d", mMemberCount));

        mAvatarListAdapter.removeItem(userInfo.userId);
        refreshMsg(userInfo.userId, TextUtils.isEmpty(userInfo.nickname) ? userInfo.userId : userInfo.nickname, "退出直播", Constants.AVIMCMD_EXIT_LIVE);
    }

    @Override
    public void handleLiveEnd(SimpleUserInfo userInfo) {
        stopPlay();
    }

    @Override
    public void handleGift(GiftWithUerInfo giftWithUerInfo) {
//        if (giftWithUerInfo != null) {
//            mLiveGiftShowBinder.dispatchGift(giftWithUerInfo);
//            refreshMsg(giftWithUerInfo.getUserInfo().getUserId(), giftWithUerInfo.getUserInfo().getNickname(),
//                    "送出" + giftWithUerInfo.getGiftInfo().getGiftCount() + giftWithUerInfo.getGiftInfo().getGiftName(), Constants.AVIMCMD_GIFT);
//        }
    }

    /**
     * 消息刷新显示
     *
     * @param name    发送者
     * @param context 内容
     * @param type    类型 （上线线消息和 聊天消息）
     */
    public void refreshMsg(String id, String name, String context, int type) {
        ChatEntity entity = new ChatEntity();
        name = TextUtils.isEmpty(name) ? getString(R.string.live_tourist) : name;
        entity.setId(id);
        entity.setSenderName(name);
        entity.setContext(context);
        entity.setType(type);
        notifyMsg(entity);
    }

    /**
     * 刷新消息列表
     *
     * @param entity
     */
    private void notifyMsg(final ChatEntity entity) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mArrayListChatEntity.add(entity);
                mChatMsgListAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onTextSend(String msg, boolean tanmuOpen) {
        mIMChatPresenter.sendTextMsg(msg);
        ChatEntity entity = new ChatEntity();
        entity.setSenderName("我:");
        entity.setContext(msg);
        entity.setType(Constants.AVIMCMD_TEXT_TYPE);
        notifyMsg(entity);
    }

    @Override
    public void onItemClickListener(SimpleUserInfo userInfo) {
        mLivePlayerPresenter.showUserInfo(this, userInfo);
    }

    @Override
    public void receiveGift(boolean showGift, GiftWithUerInfo giftWithUerInfo) {
        mLiveGiftShowBinder.dispatchGift(giftWithUerInfo);
        mIMChatPresenter.sendGiftMessage(mGson.toJson(giftWithUerInfo));
    }

    @Override
    public void sendGiftFailed() {
        ToastUtils.makeText(this, "gift send failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void gotoPay() {
        ToastUtils.makeText(this, "跳转充值界面", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSenderInfoCard(MemberInfo currentMember) {

    }

    @Override
    public void onCoinCount(int coinCount) {
        if (coinCount > 0) {
            mLiveGiftView.setCoinCount(coinCount);
        }
    }

    @Override
    public void onGiftList(ArrayList<GiftInfo> giftList) {
        mLiveGiftView.setGiftPagerList(giftList);
    }

    @Override
    public void onGiftListFailed() {
        ToastUtils.makeText(this, "get gift list error", Toast.LENGTH_SHORT);
    }


    @Override
    public void isShowing(boolean isShowing) {
        isGifViewShowing = isShowing;
    }

    @Override
    public void showPayDialog() {
        mLiveGiftPresenter.showPayDialog();
    }

    @Override
    public void sendGift(GiftInfo giftInfo) {
        mLiveGiftPresenter.sendGift(giftInfo, ACache.get(this).getAsString("user_id"), mLiveInfo.liveId);
    }


}
