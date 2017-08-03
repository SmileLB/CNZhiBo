package com.example.administrator.cnzhibo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.example.administrator.cnzhibo.R;
import com.example.administrator.cnzhibo.base.BaseActivity;

import java.lang.ref.WeakReference;

/**
 * Created by LiBing
 * on 2017/7/25 0025
 * describe:
 */

public class SplashActivity extends BaseActivity {


    private RelativeLayout rlSplash;
    private static final int START_LOGIN = 2873;
    private final MyHandler mHandler = new MyHandler(this);

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
        rlSplash = obtainView(R.id.rl_splash);
    }

    @Override
    protected void initData() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Message msg = Message.obtain();
        msg.arg1 = START_LOGIN;
        mHandler.sendMessageDelayed(msg, 1000);
    }

    @Override
    protected void setListener() {
//		SplashActivity.invoke(this);
    }

    public static void invoke(Context context){
        Intent intent = new Intent(context,SplashActivity.class);
        context.startActivity(intent);
    }

    private void jumpToLoginActivity() {
        LoginActivity.invoke(this);
        finish();
    }

    private static class MyHandler extends Handler {
        private final WeakReference<SplashActivity> mActivity;

        public MyHandler(SplashActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            SplashActivity activity = mActivity.get();
            if (activity != null) {
                activity.jumpToLoginActivity();
            }
        }
    }
}
