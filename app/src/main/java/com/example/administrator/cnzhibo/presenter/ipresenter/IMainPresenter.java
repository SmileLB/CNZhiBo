package com.example.administrator.cnzhibo.presenter.ipresenter;


import com.example.administrator.cnzhibo.base.BasePresenter;
import com.example.administrator.cnzhibo.base.BaseView;

/**
 * @description: 主页面管理
 */
public abstract class IMainPresenter implements BasePresenter {
    protected BaseView mBaseView;

    public IMainPresenter(BaseView baseView) {
        mBaseView = baseView;
    }

    /**
     * 监测缓存和登陆
     */
    protected abstract void checkCacheAndLogin();

    public interface IMainView extends BaseView {
    }
}