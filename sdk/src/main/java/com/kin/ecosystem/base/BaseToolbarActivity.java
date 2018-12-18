package com.kin.ecosystem.base;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.kin.ecosystem.R;


public abstract class BaseToolbarActivity extends KinEcosystemBaseActivity {

    protected static final int EMPTY_TITLE = -1;

    protected abstract @StringRes int getTitleRes();

    protected abstract @DrawableRes int getNavigationIcon();

    protected abstract View.OnClickListener getNavigationClickListener();

    private Toolbar topToolBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar();
    }

    private void setupToolbar() {
        topToolBar = findViewById(R.id.toolbar);
        if (getTitleRes() != EMPTY_TITLE) {
            topToolBar.setTitle(getTitleRes());
        } else {
            topToolBar.setTitle("");
        }
        setSupportActionBar(topToolBar);
        topToolBar.setNavigationIcon(getNavigationIcon());
        topToolBar.setNavigationOnClickListener(getNavigationClickListener());
    }

    public Toolbar getToolbar() {
        return topToolBar;
    }
}
