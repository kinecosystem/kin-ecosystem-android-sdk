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
        initToolbar();
        setupToolbar();
        initViews();
    }

    private void initToolbar() {
        topToolBar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        setSupportActionBar(topToolBar);
        topToolBar.setTitle(getTitleRes());
        topToolBar.setNavigationIcon(getNavigationIcon());
        topToolBar.setNavigationOnClickListener(getNavigationClickListener());
    }

    protected void navigateToActivity(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public View getToolbar(){
        return topToolBar;
    }
}
