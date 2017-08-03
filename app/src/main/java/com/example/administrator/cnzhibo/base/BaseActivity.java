package com.example.administrator.cnzhibo.base;

import android.app.ActionBar;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

/**
 * Created by LiBing
 * on 2017/7/25 0025
 * describe:界面的抽取，包括标题、网络、图片加载
 */

public abstract class BaseActivity extends FragmentActivity {
    protected Context mContext;
    protected Handler mHandler = new Handler();
    /**
     * 图片加载
     */
    public ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBeforeLayout();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mContext=this;
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        }
        initView();
        initData();
        setListener();
    }

    protected void setBeforeLayout(){}
    /**
     * 返回当前界面布局文件
     */
    protected abstract int getLayoutId();

    /**
     * 此方法描述的是： 初始化所有view
     */
    protected abstract void initView();

    /**
     * 此方法描述的是： 初始化所有数据的方法
     */
    protected abstract void initData();

    /**
     * 此方法描述的是： 设置所有事件监听 linqiang
     */
    protected abstract void setListener();

    @Override
    protected void onResume() {
        super.onResume();
    }


    public <T extends View> T obtainView(int resId) {
        return (T) findViewById(resId);
    }

    /**
     * 显示toast
     */
    public void showToast(final int resId) {
        showToast(getString(resId));
    }

    /**
     * 显示toast
     */
    public Toast showToast(final String resStr) {

        if (TextUtils.isEmpty(resStr)) {
            return null;
        }

        Toast toast = null;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(BaseActivity.this, resStr,
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        return toast;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
