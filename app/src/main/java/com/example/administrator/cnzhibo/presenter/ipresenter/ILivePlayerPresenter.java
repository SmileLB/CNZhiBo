package com.example.administrator.cnzhibo.presenter.ipresenter;

import android.os.Bundle;

import com.example.administrator.cnzhibo.base.BasePresenter;
import com.example.administrator.cnzhibo.base.BaseView;
import com.example.administrator.cnzhibo.model.SimpleUserInfo;
import com.example.administrator.cnzhibo.presenter.ILivePresenter;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.ArrayList;


/**
 * @description: 播放管理
 */
public abstract class ILivePlayerPresenter extends ILivePresenter implements BasePresenter {

    protected ILivePlayerView mBaseView;

    public ILivePlayerPresenter(ILivePlayerView baseView) {
        mBaseView = baseView;
    }

    /**
     * 初始化播放器
     *
     * @param cloudVideoView
     * @param livePlayConfig
     */
    public abstract void initPlayerView(TXCloudVideoView cloudVideoView, TXLivePlayConfig livePlayConfig);

    public abstract void playerPause();

    public abstract void playerResume();

    /**
     * 开始播放
     *
     * @param playUrl
     * @param playType
     */
    public abstract void startPlay(String playUrl,
                                   int playType);

    public abstract void stopPlay(boolean isClearLastImg);

    /**
     * 点赞接口
     *
     * @param userId
     * @param liveId
     * @param hostId
     * @param groupId
     */
    public abstract void doLike(String userId, String liveId, String hostId, String groupId);

    /**
     * 进入直播群
     *
     * @param userId
     * @param liveId
     * @param hostId
     * @param groupId
     */
    public abstract void enterGroup(String userId, String liveId, String hostId, String groupId);

    /**
     * 退出直播群
     *
     * @param userId
     * @param liveId
     * @param hostId
     * @param groupId
     */
    public abstract void quitGroup(String userId, String liveId, String hostId, String groupId);

    /**
     * 当前观看直播的用户列表，限制50个人
     *
     * @param userId
     * @param liveId
     * @param hostId
     * @param groupId
     * @param pageIndex
     * @param pageSize
     */
    public abstract void groupMember(String userId, String liveId, String hostId, String groupId, int pageIndex, int pageSize);

    public interface ILivePlayerView extends BaseView {
        void onPlayEvent(int i, Bundle bundle);

        void onNetStatus(Bundle bundle);

        void doLikeResult(int result);

        /**
         * 获取观众列表结果
         *
         * @param retCode
         * @param totalCount
         * @param membersList
         */
        void onGroupMembersResult(int retCode, int totalCount, ArrayList<SimpleUserInfo> membersList);
    }
}