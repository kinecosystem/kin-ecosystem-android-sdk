package com.kin.ecosystem.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.kin.ecosystem.R;


public abstract class BaseToolbarActivity extends AppCompatActivity {

    protected static final int EMPTY_TITLE = -1;

    protected abstract @LayoutRes int getLayoutRes();

    protected abstract @StringRes int getTitleRes();

    protected abstract @DrawableRes int getNavigationIcon();

    protected abstract View.OnClickListener getNavigationClickListener();

    protected abstract void initViews();
    private Toolbar topToolBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());
        setupToolbar();
        initViews();
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

    protected void navigateToActivity(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.kinecosystem_slide_in_right, R.anim.kinecosystem_slide_out_left);
    }

    public Toolbar getToolbar() {
        return topToolBar;
    }
}
