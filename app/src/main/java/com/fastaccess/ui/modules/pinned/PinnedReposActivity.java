package com.fastaccess.ui.modules.pinned;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fastaccess.ui.modules.main.MainActivity;
import com.google.android.material.tabs.TabLayout;

import com.fastaccess.R;
import com.fastaccess.data.dao.FragmentPagerAdapterModel;
import com.fastaccess.ui.adapter.FragmentsPagerAdapter;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.widgets.ViewPagerView;

import net.grandcentrix.thirtyinch.TiPresenter;

import butterknife.BindView;

/**
 * Created by Kosh on 25 Mar 2017, 11:14 PM
 */

public class PinnedReposActivity extends BaseActivity {

    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.tabbedPager)
    ViewPagerView tabbedPager;

    public static void startActivity(@NonNull Context context) {
        context.startActivity(new Intent(context, PinnedReposActivity.class));
    }

    @Override
    protected int layout() {
        return R.layout.tabbed_pager_layout;
    }

    @Override
    protected boolean isTransparent() {
        return true;
    }

    @Override
    protected boolean canBack() {
        return true;
    }

    @Override
    protected boolean isSecured() {
        return false;
    }

    @NonNull
    @Override
    public TiPresenter providePresenter() {
        return new BasePresenter();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectPinned();
        tabbedPager.setAdapter(new FragmentsPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapterModel.buildForPinned(this)));
        tabs.setupWithViewPager(tabbedPager);
        tabs.setPadding(0, 0, 0, 0);
        tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            MainActivity.launchMainActivity(this, true);
            finish();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public void onNavBack() {
        if (isTaskRoot()) {
            MainActivity.launchMainActivity(this, true);
        }
        finish();
    }
}
