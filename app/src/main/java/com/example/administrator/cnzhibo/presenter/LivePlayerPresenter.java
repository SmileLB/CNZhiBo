package com.example.administrator.cnzhibo.presenter;

import android.os.Bundle;
import android.util.Log;

import com.example.administrator.cnzhibo.http.AsyncHttp;
import com.example.administrator.cnzhibo.http.request.GroupMemberReuest;
import com.example.administrator.cnzhibo.http.request.LiveLikeRequest;
import com.example.administrator.cnzhibo.http.request.RequestComm;
import com.example.administrator.cnzhibo.http.response.ResList;
import com.example.administrator.cnzhibo.http.response.Response;
import com.example.administrator.cnzhibo.model.SimpleUserInfo;
import com.example.administrator.cnzhibo.presenter.ipresenter.ILivePlayerPresenter;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.util.ArrayList;



public class LivePlayerPresenter extends ILivePlayerPresenter implements ITXLivePlayListener {

    private ILivePlayerView mLivePlayerView;
    private TXCloudVideoView mCloudVideoView;
    private TXLivePlayer mLivePLayer;

    public LivePlayerPresenter(ILivePlayerView baseView) {
        super(baseView);
        mLivePlayerView = baseView;
    }

    public void  enableHardwareDecode(boolean decode){
        mLivePLayer.enableHardwareDecode(false);
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
    public void doLike(String userId, String liveId, String hostId, String groupId) {
        LiveLikeRequest req = new LiveLikeRequest(1000, userId, liveId, hostId, groupId);
        AsyncHttp.instance().postJson(req, new AsyncHttp.IHttpListener() {
            @Override
            public void onStart(int requestId) {

            }

            @Override
            public void onSuccess(int requestId, Response response) {
                if (response.status == RequestComm.SUCCESS) {
                    mLivePlayerView.doLikeResult(0);
                } else {
                    mLivePlayerView.doLikeResult(1);
                }
                Log.i("log", "onSuccess: doLike");
            }

            @Override
            public void onFailure(int requestId, int httpStatus, Throwable error) {
                mLivePlayerView.doLikeResult(1);
                Log.i("log", "onFailure: doLike");
            }
        });
    }

    @Override
    public void enterGroup(String userId, String liveId, String hostId, String groupId) {

    }

    @Override
    public void quitGroup(String userId, String liveId, String hostId, String groupId) {

    }

    @Override
    public void groupMember(String userId, String liveId, String hostId, String groupId, int pageIndex, int pageSize) {
        GroupMemberReuest quiteRequest = new GroupMemberReuest(RequestComm.memberList, userId, liveId, hostId, groupId, 1, 20);
        AsyncHttp.instance().postJson(quiteRequest, new AsyncHttp.IHttpListener() {
            @Override
            public void onStart(int requestId) {

            }

            @Override
            public void onSuccess(int requestId, Response response) {
                if (response != null && response.status == RequestComm.SUCCESS) {
                    ResList resList = (ResList) response.data;
                    if (resList != null && resList.items != null) {
                        mLivePlayerView.onGroupMembersResult(0, resList.totalCount, (ArrayList<SimpleUserInfo>) resList.items);
                    } else {
                        mLivePlayerView.onGroupMembersResult(1, 0, null);
                    }
                } else {
                    mLivePlayerView.onGroupMembersResult(1, 0, null);
                }
            }

            @Override
            public void onFailure(int requestId, int httpStatus, Throwable error) {
                mLivePlayerView.onGroupMembersResult(1, 0, null);
            }
        });
    }


    @Override
    public void start() {

    }

    @Override
    public void finish() {

    }

    @Override
    public void onPlayEvent(int event, Bundle bundle) {
        mLivePlayerView.onPlayEvent(event,bundle);
    }

    @Override
    public void onNetStatus(Bundle bundle) {
        mLivePlayerView.onNetStatus(bundle);
    }
}
