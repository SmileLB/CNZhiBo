package com.example.administrator.cnzhibo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.administrator.cnzhibo.R;
import com.example.administrator.cnzhibo.base.BaseAdapter;
import com.example.administrator.cnzhibo.model.LiveInfo;

import java.util.List;


/**
 * @Description: 直播列表的Adapter
 * 列表项布局格式: R.layout.listview_video_item
 * 列表项数据格式: LiveInfo
 */
public class LiveListAdapter extends BaseAdapter<LiveInfo> {

	public LiveListAdapter(Context context, List<LiveInfo> dataList) {
		super(context, dataList);
	}

	@Override
	protected int getViewLayoutId() {
		return R.layout.live_item_view;
	}

	@Override
	protected void initData(ViewHolder viewHolder, LiveInfo data, int position) {
		ImageView ivCover = viewHolder.getView(R.id.cover);
		TextView tvTitle = viewHolder.getView(R.id.live_title);
		TextView tvHostName = viewHolder.getView(R.id.host_name);
		TextView tvMembers = viewHolder.getView(R.id.live_members);
		TextView tvPraise = viewHolder.getView(R.id.praises);
		TextView tvLbs = viewHolder.getView(R.id.live_lbs);
		ImageView ivAvatar = viewHolder.getView(R.id.avatar);
		ImageView tvLogo = viewHolder.getView(R.id.live_logo);

		if (TextUtils.isEmpty(data.liveCover)) {
			ivCover.setImageResource(R.drawable.bg);
		} else {
			RequestManager requestManager = Glide.with(getContext());
			requestManager.load(data.liveCover).placeholder(R.drawable.bg).into(ivCover);
		}

		if (TextUtils.isEmpty(data.userInfo.headPic)) {
			ivAvatar.setImageResource(R.drawable.face);
		} else {
			RequestManager requestManager = Glide.with(getContext());
			requestManager.load(data.userInfo.headPic).placeholder(R.drawable.face).into(ivCover);
		}

		if (TextUtils.isEmpty(data.userInfo.nickname)) {
			tvHostName.setText("" + data.userInfo.userId);
		} else {
			tvHostName.setText(data.userInfo.nickname);
		}

		if (TextUtils.isEmpty(data.userInfo.location)) {
			tvLbs.setText("不显示位置");
		} else {
			tvLbs.setText(data.userInfo.location);
		}

		tvTitle.setText(data.title);
		tvMembers.setText("" + data.viewCount);
		tvPraise.setText("" + data.likeCount);


	}
}
