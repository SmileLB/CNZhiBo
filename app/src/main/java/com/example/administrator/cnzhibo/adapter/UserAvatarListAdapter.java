package com.example.administrator.cnzhibo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.administrator.cnzhibo.R;
import com.example.administrator.cnzhibo.model.SimpleUserInfo;
import com.example.administrator.cnzhibo.utils.OtherUtils;

import java.util.LinkedList;


/**
 * @Description: 直播头像列表Adapter
 * @author: Andruby
 * @date: 2016年7月15日
 */
public class UserAvatarListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    LinkedList<SimpleUserInfo> mUserAvatarList;
    Context mContext;
    //主播id
    private String mPusherId;
    //最大容纳量
    private final static int TOP_STORGE_MEMBER = 50;
    private OnItemClickListener mItemClickListener;


    public UserAvatarListAdapter(Context context, String pusherId) {
        this.mContext = context;
        this.mPusherId = pusherId;
        this.mUserAvatarList = new LinkedList<>();
    }

    /**
     * 添加用户信息
     *
     * @param userInfo 用户基本信息
     * @return 存在重复或头像为主播则返回false
     */
    public boolean addItem(SimpleUserInfo userInfo) {

        //去除主播头像
        if (userInfo.userId.equals(mPusherId))
            return false;

        //去重操作
        for (SimpleUserInfo tcSimpleUserInfo : mUserAvatarList) {
            if (tcSimpleUserInfo.userId.equals(userInfo.userId))
                return false;
        }

        //始终显示新加入item为第一位
        mUserAvatarList.add(0, userInfo);
        //超出时删除末尾项
        if (mUserAvatarList.size() > TOP_STORGE_MEMBER)
            mUserAvatarList.remove(TOP_STORGE_MEMBER);
        notifyItemInserted(0);
        return true;
    }

    public void removeItem(String userId) {
        SimpleUserInfo tempUserInfo = null;

        for (SimpleUserInfo userInfo : mUserAvatarList)
            if (userInfo.userId.equals(userId))
                tempUserInfo = userInfo;


        if (null != tempUserInfo) {
            mUserAvatarList.remove(tempUserInfo);
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_user_avatar, parent, false);

        final AvatarViewHolder avatarViewHolder = new AvatarViewHolder(view);
        avatarViewHolder.ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleUserInfo userInfo = mUserAvatarList.get(avatarViewHolder.getAdapterPosition());
//                Toast.makeText(mContext.getApplicationContext(),"当前点击用户： " + userInfo.userId, Toast.LENGTH_SHORT).show();
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClickListener(userInfo);
                }
            }
        });

        return avatarViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        OtherUtils.showPicWithUrl(mContext, ((AvatarViewHolder) holder).ivAvatar, mUserAvatarList.get(position).headPic,
                R.drawable.default_head);

    }

    @Override
    public int getItemCount() {
        return mUserAvatarList != null ? mUserAvatarList.size() : 0;
    }

    private class AvatarViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAvatar;

        public AvatarViewHolder(View itemView) {
            super(itemView);

            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mItemClickListener = onItemClickListener;
    }


    public interface OnItemClickListener {
        void onItemClickListener(SimpleUserInfo userInfo);
    }
}
