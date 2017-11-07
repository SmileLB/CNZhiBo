package com.example.administrator.cnzhibo.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.cnzhibo.R;
import com.example.administrator.cnzhibo.activity.LoginActivity;
import com.example.administrator.cnzhibo.base.BaseFragment;
import com.example.administrator.cnzhibo.logic.IMLogin;
import com.example.administrator.cnzhibo.model.UserInfoCache;
import com.example.administrator.cnzhibo.utils.DeviceUtils;
import com.example.administrator.cnzhibo.utils.DialogUtil;
import com.example.administrator.cnzhibo.utils.ImageUtil;

/**
 * description:
 * author: zhm
 * time:2016/12/29 23:35
 */

public class UserInfoFragment extends BaseFragment implements View.OnClickListener {
	private static final String TAG = "UserInfoFragment";
	private ImageView mHeadPic;
	private TextView mNickName;

	public UserInfoFragment() {
	}

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_user_info;
	}

	@Override
	protected void initView(View view) {
		mHeadPic = obtainView(R.id.iv_ui_head);
		mNickName = obtainView(R.id.tv_ui_nickname);
	}

	@Override
	protected void initData() {
		mNickName.setText(UserInfoCache.getNickname(getContext()));
		ImageUtil.showRoundImage(getActivity(), mHeadPic, UserInfoCache.getHeadPic(getContext()), R.drawable.default_head);
	}

	@Override
	protected void setListener(View view) {
		obtainView(R.id.lcv_ui_set).setOnClickListener(this);
		obtainView(R.id.lcv_ui_logout).setOnClickListener(this);
		obtainView(R.id.lcv_ui_version).setOnClickListener(this);
		obtainView(R.id.fanceview).setOnClickListener(this);
		obtainView(R.id.followView).setOnClickListener(this);
		obtainView(R.id.review).setOnClickListener(this);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	private void enterEditUserInfo() {
//		EditUseInfoActivity.invoke(getActivity());
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.lcv_ui_set: //设置用户信息
				enterEditUserInfo();
				break;
			case R.id.lcv_ui_logout: //注销APP
				showLogout();
				break;
			case R.id.lcv_ui_version:
				showAbout();
				break;
			/*case R.id.fanceview:
				FanceActivity.invoke(getContext());
				break;
			case R.id.followView:
				FollowActivity.invoke(getContext());
				break;
			case R.id.review:
				ReviewListActivity.invoke(getContext());
				break;*/
		}
	}

	/**
	 * 退出登录
	 */
	private void showLogout() {
		DialogUtil.showComfirmDialog(getContext(), "你确定要退出当前账号吗？", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				IMLogin.getInstance().logout();
				LoginActivity.invoke(getContext());
				getActivity().finish();
			}
		}, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
	}


	/**
	 * 显示关于信息
	 */
	private void showAbout() {
		DialogUtil.showMsgDialog(getActivity(), getString(R.string.my_about_info, DeviceUtils.getAppVersion(getContext())), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {
			mNickName.setText(UserInfoCache.getNickname(getContext()));
			ImageUtil.showRoundImage(getActivity(), mHeadPic, UserInfoCache.getHeadPic(getContext()), R.drawable.default_head);
		}
	}
}
