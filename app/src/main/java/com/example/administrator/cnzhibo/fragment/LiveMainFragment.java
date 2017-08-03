package com.example.administrator.cnzhibo.fragment;

import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.example.administrator.cnzhibo.R;
import com.example.administrator.cnzhibo.base.BaseActivity;
import com.example.administrator.cnzhibo.base.BaseFragment;
import com.example.administrator.cnzhibo.presenter.LiveMainPresenter;
import com.example.administrator.cnzhibo.ui.pagersliding.PagerSlidingTabStrip;

/**
 * description:
 */

public class LiveMainFragment extends BaseFragment {

	private LiveMainPresenter mMainPresenter;
	private ViewPager mViewPager;
	private PagerSlidingTabStrip mTabStrip;

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_live_main;
	}

	@Override
	protected void initView(View view) {
		mViewPager = obtainView(R.id.viewpager);
		mTabStrip = obtainView(R.id.tab_strip);

		mTabStrip.setTextColorResource(R.color.white);
		mTabStrip.setIndicatorColorResource(R.color.white);
		mTabStrip.setDividerColor(Color.TRANSPARENT);
		mTabStrip.setTextSelectedColorResource(R.color.white);
		mTabStrip.setTextSize(getResources().getDimensionPixelSize(R.dimen.h6));
		mTabStrip.setTextSelectedSize(getResources().getDimensionPixelSize(R.dimen.h10));
		mTabStrip.setUnderlineHeight(1);
	}

	@Override
	protected void initData() {
		mMainPresenter = new LiveMainPresenter((BaseActivity) getActivity());
		mViewPager.setAdapter(mMainPresenter.getAdapter());
		mTabStrip.setViewPager(mViewPager);
	}

	@Override
	protected void setListener(View view) {

	}
}
