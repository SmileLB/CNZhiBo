package com.example.administrator.cnzhibo.presenter.ipresenter;


import com.example.administrator.cnzhibo.base.BasePresenter;
import com.example.administrator.cnzhibo.base.BaseView;

/**
 * @description: IM聊天管理
 */
public abstract class IIMChatPresenter implements BasePresenter {
    protected IIMChatView mIMChatView;

    public IIMChatPresenter(IIMChatView baseView) {
        mIMChatView = baseView;
    }

    /**
     * 创建群
     */
    public abstract void createGroup();

    /**
     * 删除群
     */
    public abstract void deleteGroup();


    /**
     * 加入群
     *
     * @param roomId
     */
    public abstract void joinGroup(final String roomId);

    /**
     * 退出群
     *
     * @param roomId
     */
    public abstract void quitGroup(final String roomId);

    public interface IIMChatView extends BaseView {
        /**
         * 加入群组回调
         *
         * @param code 错误码，成功时返回0，失败时返回相应错误码
         * @param msg  返回信息，成功时返回群组Id，失败时返回相应错误信息
         */
        void onJoinGroupResult(int code, String msg);

        /**
         * 群组删除回调，在主播群组解散时被调用
         */
        void onGroupDeleteResult();
    }
}