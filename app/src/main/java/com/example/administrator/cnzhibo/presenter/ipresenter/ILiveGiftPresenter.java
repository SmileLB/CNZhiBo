package com.example.administrator.cnzhibo.presenter.ipresenter;

import com.example.administrator.cnzhibo.base.BasePresenter;
import com.example.administrator.cnzhibo.base.BaseView;
import com.example.administrator.cnzhibo.model.GiftInfo;
import com.example.administrator.cnzhibo.model.GiftWithUerInfo;
import com.tencent.imcore.MemberInfo;

import java.util.ArrayList;

/**
 * author : qubian on 2016/12/26 11:08
 * description :
 */

public abstract class ILiveGiftPresenter implements BasePresenter {
    protected ILiveGiftView mBaseView;

    public ILiveGiftPresenter(ILiveGiftView baseView) {
        mBaseView = baseView;
    }


    /**
     * 礼物列表
     *
     * @param userId
     * @param liveId
     */
    public abstract void giftList(String userId, String liveId);

    /**
     * 发送礼物
     *
     * @param sendGiftInfo
     * @param hostId
     * @param liveId
     */
    public abstract void sendGift(GiftInfo sendGiftInfo, String hostId, String liveId);

    /**
     * 获取剩余金币
     *
     * @param userId
     */
    public abstract void coinCount(String userId);

    public interface ILiveGiftView extends BaseView {

        void receiveGift(boolean showGift, GiftWithUerInfo giftWithUerInfo);
        void sendGiftFailed();

        void gotoPay();

        void showSenderInfoCard(MemberInfo currentMember);

        void onCoinCount(int coinCount);

        void onGiftList(ArrayList<GiftInfo> giftList);

        void onGiftListFailed();

    }
}
