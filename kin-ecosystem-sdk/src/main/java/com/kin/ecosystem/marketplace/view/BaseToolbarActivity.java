package com.kin.ecosystem.marketplace.view;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.kin.ecosystem.R;


public abstract class BaseToolbarActivity extends AppCompatActivity {

    abstract @LayoutRes int getLayoutRes();

    abstract @DrawableRes int getNavigationIcon();

    abstract View.OnClickListener getNavigationClickListener();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());
        setupToolbar();
    }

    private void setupToolbar() {
        setTitle(null);
        Toolbar topToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        topToolBar.setNavigationIcon(getNavigationIcon());
        topToolBar.setNavigationOnClickListener(getNavigationClickListener());
    }

}
