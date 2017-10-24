package com.example.administrator.cnzhibo.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.cnzhibo.R;
import com.example.administrator.cnzhibo.adapter.ChatMsgListAdapter;
import com.example.administrator.cnzhibo.logic.UserInfoMgr;
import com.example.administrator.cnzhibo.model.ChatEntity;
import com.example.administrator.cnzhibo.model.SimpleUserInfo;
import com.example.administrator.cnzhibo.presenter.IMChatPresenter;
import com.example.administrator.cnzhibo.presenter.PusherPresenter;
import com.example.administrator.cnzhibo.presenter.SwipeAnimationController;
import com.example.administrator.cnzhibo.presenter.ipresenter.IIMChatPresenter;
import com.example.administrator.cnzhibo.presenter.ipresenter.IPusherPresenter;
import com.example.administrator.cnzhibo.ui.customviews.HeartLayout;
import com.example.administrator.cnzhibo.ui.customviews.InputTextMsgDialog;
import com.example.administrator.cnzhibo.utils.AsimpleCache.ACache;
import com.example.administrator.cnzhibo.utils.Constants;
import com.example.administrator.cnzhibo.utils.HWSupportList;
import com.example.administrator.cnzhibo.utils.LogUtil;
import com.example.administrator.cnzhibo.utils.OtherUtils;
import com.example.administrator.cnzhibo.utils.ToastUtils;
import com.tencent.TIMMessage;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePushConfig;
import com.tencent.rtmp.audio.TXAudioPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class LivePublisherActivity extends IMBaseActivity implements View.OnClickListener,
		IPusherPresenter.IPusherView, IIMChatPresenter.IIMChatView, InputTextMsgDialog.OnTextSendListener {
	private static final String TAG = LivePublisherActivity.class.getSimpleName();


	private TXCloudVideoView mTXCloudVideoView;

	private TXLivePushConfig mTXPushConfig = new TXLivePushConfig();

	private Handler mHandler = new Handler();

	private boolean mFlashOn = false;
	private boolean mPasuing = false;

	private String mPushUrl;
	private String mRoomId;
	private String mUserId;
	private String mTitle;
	private String mCoverPicUrl;
	private String mHeadPicUrl;
	private String mNickName;
	private String mLocation;
	private boolean mIsRecord;


	private LinearLayout mAudioPluginLayout;
	private Button mBtnAudioEffect;
	private Button mBtnAudioClose;
	private TXAudioPlayer mAudioPlayer;
	private RelativeLayout mControllLayer;
	private SwipeAnimationController mTCSwipeAnimationController;

	private PusherPresenter mPusherPresenter;
	private int[] mSettingLocation = new int[2];
	private View btnSettingView;

	private IMChatPresenter mIMChatPresenter;

	//主播相关信息，头像、观众数
	private ImageView ivHeadIcon;
	private ImageView ivRecordBall;
	private TextView tvMemberCount;
	//播放信息：时间、红点
	private long mSecond = 0;
	private TextView tvBroadcastTime;
	private Timer mBroadcastTimer;
	private BroadcastTimerTask mBroadcastTimerTask;
	private ObjectAnimator mObjAnim;

	private InputTextMsgDialog mInputTextMsgDialog;
	//消息列表
	private ArrayList<ChatEntity> mArrayListChatEntity = new ArrayList<>();
	private ChatMsgListAdapter mChatMsgListAdapter;
	private ListView mListViewMsg;

	/**
	 * 点赞动画
	 */
	private HeartLayout mHeartLayout;
	@Override
	protected void setBeforeLayout() {
		super.setBeforeLayout();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

	}

	private void getDataFormIntent() {
		Intent intent = getIntent();
		mUserId = intent.getStringExtra(Constants.USER_ID);
		mPushUrl = intent.getStringExtra(Constants.PUBLISH_URL);
		mTitle = intent.getStringExtra(Constants.ROOM_TITLE);
		mCoverPicUrl = intent.getStringExtra(Constants.COVER_PIC);
		mHeadPicUrl = intent.getStringExtra(Constants.USER_HEADPIC);
		mNickName = intent.getStringExtra(Constants.USER_NICK);
		mLocation = intent.getStringExtra(Constants.USER_LOC);
		mIsRecord = intent.getBooleanExtra(Constants.IS_RECORD, false);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_live_publisher;
	}

	@Override
	public void onReceiveExitMsg() {
		super.onReceiveExitMsg();

		LogUtil.e(TAG, "publisher broadcastReceiver receive exit app msg");
		//在被踢下线的情况下，执行退出前的处理操作：停止推流、关闭群组
		mTXCloudVideoView.onPause();
		stopPublish();
	}

	@Override
	protected void initView() {
		getDataFormIntent();

		mTXCloudVideoView = obtainView(R.id.video_view);
		btnSettingView = obtainView(R.id.btn_setting);

		mTCSwipeAnimationController = new SwipeAnimationController(this);
		mTCSwipeAnimationController.setAnimationView(mControllLayer);


		//主播信息
		tvBroadcastTime = obtainView(R.id.tv_broadcasting_time);
		tvBroadcastTime.setText(String.format(Locale.US, "%s", "00:00:00"));
		ivRecordBall = obtainView(R.id.iv_record_ball);
		ivHeadIcon = obtainView(R.id.iv_head_icon);
		OtherUtils.showPicWithUrl(this, ivHeadIcon, ACache.get(this).getAsString("head_pic_small"), R.drawable.default_head);
		tvMemberCount = obtainView(R.id.tv_member_counts);
		tvMemberCount.setText("0");

		mPusherPresenter = new PusherPresenter(this);
		mIMChatPresenter = new IMChatPresenter(this);
		recordAnmination();


		mInputTextMsgDialog = new InputTextMsgDialog(this, R.style.InputDialog);
		mInputTextMsgDialog.setmOnTextSendListener(this);

		mListViewMsg = obtainView(R.id.im_msg_listview);
		mChatMsgListAdapter = new ChatMsgListAdapter(this, mListViewMsg, mArrayListChatEntity);
		mListViewMsg.setAdapter(mChatMsgListAdapter);

		mHeartLayout = obtainView(R.id.heart_layout);
	}

	private void recordAnmination() {
		mObjAnim = ObjectAnimator.ofFloat(ivRecordBall, "alpha", 1.0f, 0f, 1.0f);
		mObjAnim.setDuration(1000);
		mObjAnim.setRepeatCount(-1);
		mObjAnim.start();
	}

	@Override
	protected void initData() {
		if (mTXCloudVideoView != null) {
			mTXCloudVideoView.disableLog(false);
		}
		mIMChatPresenter.createGroup();
	}

	@Override
	protected void setListener() {

	}

	private void startPublish() {
		mTXPushConfig.setAutoAdjustBitrate(false);
		mTXPushConfig.setVideoResolution(TXLiveConstants.VIDEO_RESOLUTION_TYPE_540_960);
		mTXPushConfig.setVideoBitrate(1000);
		mTXPushConfig.setVideoFPS(20);
		Log.i(TAG, "startPublish: MANUFACTURER " + Build.MANUFACTURER + " model:" + Build.MODEL);
		if (HWSupportList.isHWVideoEncodeSupport()) {
			mTXPushConfig.setHardwareAcceleration(true);
			Log.i(TAG, "startPublish: 手机型号硬编码设置成功！！！");
		} else {
			Log.i(TAG, "startPublish: 手机型号不支持硬编码！！！");
		}

		mTXPushConfig.setWatermark(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher), 50, 50);
		//切后台推流图片
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pause_publish, options);
		mTXPushConfig.setPauseImg(bitmap);
		mPusherPresenter.startPusher(mTXCloudVideoView, mTXPushConfig, mPushUrl);

		if (mBroadcastTimer == null) {
			mBroadcastTimer = new Timer(true);
			mBroadcastTimerTask = new BroadcastTimerTask();
			mBroadcastTimer.schedule(mBroadcastTimerTask, 1000, 1000);
		}
	}

	@Override
	public void onJoinGroupResult(int code, String msg) {
		if (0 == code) {
			//获取推流地址
			LogUtil.e(TAG, "onJoin group success" + msg);
			mRoomId = msg;
			mPusherPresenter.getPusherUrl(mUserId, msg, mTitle, mCoverPicUrl, mNickName, mHeadPicUrl, mLocation);
		} else if (Constants.NO_LOGIN_CACHE == code) {
			LogUtil.e(TAG, "onJoin group failed" + msg);
		} else {
			LogUtil.e(TAG, "onJoin group failed" + msg);
		}
	}

	@Override
	public void onGroupDeleteResult() {

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
//        ChatEntity entity = new ChatEntity();
//        entity.setSenderName(userInfo.nickname + ":");
//        entity.setContext("点亮了桃心");
//        entity.setType(Constants.AVIMCMD_TEXT_TYPE);
//        notifyMsg(entity);
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

	private void stopPublish() {
		mPusherPresenter.stopPusher();
		if (mAudioPlayer != null) {
			mAudioPlayer.stop();
			mAudioPlayer = null;
		}
	}


	@Override
	protected void onResume() {
		super.onResume();
		mTXCloudVideoView.onResume();

		if (mPasuing) {
			mPasuing = false;
			mPusherPresenter.resumePusher();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mTXCloudVideoView.onPause();
		mPusherPresenter.pausePusher();

	}

	@Override
	protected void onStop() {
		super.onStop();
		mPasuing = true;
		mPusherPresenter.stopPusher();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mTXCloudVideoView.onDestroy();
		stopPublish();
		mIMChatPresenter.deleteGroup();
		if (mObjAnim != null) {
			mObjAnim.cancel();
		}
		if (mBroadcastTimerTask != null) {
			mBroadcastTimerTask.cancel();
			mBroadcastTimer.cancel();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_close:
				stopPublish();
				finish();
				break;
			case R.id.btn_message_input:
				showInputMsgDialog();
				break;
			case R.id.btn_setting:
				//setting坐标
				mPusherPresenter.showSettingPopupWindow(btnSettingView, mSettingLocation);
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
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (mSettingLocation[0] == 0 && mSettingLocation[1] == 0) {
			btnSettingView.getLocationOnScreen(mSettingLocation);
		}
	}

	public static void invoke(Activity activity, String roomTitle, String location, boolean isRecord, int bitrateType) {
		Intent intent = new Intent(activity, LivePublisherActivity.class);
		intent.putExtra(Constants.ROOM_TITLE,
				TextUtils.isEmpty(roomTitle) ? UserInfoMgr.getInstance().getNickname() : roomTitle);
		intent.putExtra(Constants.USER_ID, ACache.get(activity).getAsString("user_id"));
		intent.putExtra(Constants.USER_NICK, ACache.get(activity).getAsString("nickname"));
		intent.putExtra(Constants.USER_HEADPIC, ACache.get(activity).getAsString("head_pic_small"));
		intent.putExtra(Constants.COVER_PIC, ACache.get(activity).getAsString("head_pic"));
		intent.putExtra(Constants.USER_LOC, location);
		intent.putExtra(Constants.IS_RECORD, isRecord);
		intent.putExtra(Constants.BITRATE, bitrateType);
		activity.startActivity(intent);
	}

	@Override
	public void onGetPushUrl(String pushUrl, int errorCode) {
		mPushUrl = pushUrl;
		if (errorCode == 0) {
			startPublish();
		} else {
			ToastUtils.showShort(this, "push url is empty");
			finish();
		}
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
	public FragmentManager getFragmentMgr() {
		return getFragmentManager();
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


	class BroadcastTimerTask extends TimerTask {

		@Override
		public void run() {
			mSecond++;
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					tvBroadcastTime.setText(OtherUtils.formattedTime(mSecond));
				}
			});
		}
	}
}
