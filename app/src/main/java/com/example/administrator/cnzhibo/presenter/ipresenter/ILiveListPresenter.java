package com.example.administrator.cnzhibo.presenter.ipresenter;


import com.example.administrator.cnzhibo.base.BasePresenter;
import com.example.administrator.cnzhibo.base.BaseView;
import com.example.administrator.cnzhibo.model.LiveInfo;

import java.util.ArrayList;


/**
 * @description: 列表数据管理
 *
 */
public abstract class ILiveListPresenter implements BasePresenter {
    protected BaseView mBaseView;

    public ILiveListPresenter(BaseView baseView) {
        mBaseView = baseView;
    }

    /**
     * 获取缓存列表
     * @return
     */
    public abstract ArrayList<LiveInfo> getLiveListFormCache();

    /**
     * 重新加载列表
     * @return
     */
    public abstract boolean reloadLiveList();

    public abstract boolean loadDataMore() ;

    public interface ILiveListView extends BaseView {
        /**
         * @param retCode 获取结果，0表示成功
         * @param result  列表数据
         * @param refresh 是否需要刷新界面，首页需要刷新
         */
        public void onLiveList(int retCode, final ArrayList<LiveInfo> result, boolean refresh);
    }
}