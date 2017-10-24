package com.example.administrator.cnzhibo.presenter.ipresenter;

import android.os.Bundle;

import com.example.administrator.cnzhibo.base.BasePresenter;
import com.example.administrator.cnzhibo.base.BaseView;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.ui.TXCloudVideoView;


/**
 * @description: 播放管理
 */
public abstract class ILivePlayerPresenter implements BasePresenter {

    protected ILivePlayerView mBaseView;

    public ILivePlayerPresenter(ILivePlayerView baseView) {
        mBaseView = baseView;
    }

    /**
     * 初始化播放器
     * @param cloudVideoView
     * @param livePlayConfig
     */
    public abstract void initPlayerView(TXCloudVideoView cloudVideoView, TXLivePlayConfig livePlayConfig);

    public abstract void playerPause();

    public abstract void playerResume();

    /**
     * 开始播放
     * @param playUrl
     * @param playType
     */
    public abstract void startPlay(String playUrl,
                                   int playType);

    public abstract void stopPlay(boolean isClearLastImg);

    public abstract void doLike(String userId, String liveId, String hostId, String groupId);


    public interface ILivePlayerView extends BaseView {
        void onPlayEvent(int i, Bundle bundle);

        void onNetStatus(Bundle bundle);

        void doLikeResult(int result);
    }
}