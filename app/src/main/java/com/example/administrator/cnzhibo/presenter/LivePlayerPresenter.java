package com.example.administrator.cnzhibo.presenter;

import android.os.Bundle;

import com.example.administrator.cnzhibo.presenter.ipresenter.ILivePlayerPresenter;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

/**
 * Created by zhao on 2017/3/11.
 */

public class LivePlayerPresenter extends ILivePlayerPresenter implements ITXLivePlayListener {

    private ILivePlayerView mLivePlayerView;
    private TXCloudVideoView mCloudVideoView;
    private TXLivePlayer mLivePLayer;

    public LivePlayerPresenter(ILivePlayerView baseView) {
        super(baseView);
        mLivePlayerView = baseView;
    }

    @Override
    public void initPlayerView(TXCloudVideoView cloudVideoView, TXLivePlayConfig livePlayConfig) {
        mCloudVideoView = cloudVideoView;
        mLivePLayer = new TXLivePlayer(mLivePlayerView.getContext());
        mLivePLayer.setPlayerView(cloudVideoView);
        mLivePLayer.setPlayListener(this);
        mLivePLayer.setConfig(livePlayConfig);
    }

    @Override
    public void playerPause() {
        mLivePLayer.pause();
    }

    @Override
    public void playerResume() {
        mLivePLayer.resume();
    }

    @Override
    public void startPlay(String playUrl, int playType) {
        mLivePLayer.startPlay(playUrl, playType);
    }

    public void stopPlay(boolean isClearLastImg) {
        mLivePLayer.stopPlay(isClearLastImg);
        mCloudVideoView.onDestroy();
    }

    @Override
    public void start() {

    }

    @Override
    public void finish() {

    }

    @Override
    public void onPlayEvent(int i, Bundle bundle) {

    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }
}
