package com.kin.ecosystem.marketplace.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.kin.ecosystem.R;


public class OffersEmptyView extends ConstraintLayout {

    private ImageView ovalTop;
    private ImageView ovalCenter;
    private ImageView ovalBottom;

    private Animation animation;

    public OffersEmptyView(@NonNull Context context) {
        super(context);
        init();
    }

    public OffersEmptyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OffersEmptyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.offers_empty_view, this);
        setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT));
        ovalTop = findViewById(R.id.oval_top);
        ovalCenter = findViewById(R.id.oval_center);
        ovalBottom = findViewById(R.id.oval_bottom);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startSpaceShipAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopSpaceShipAnimation();
    }

    private void startSpaceShipAnimation() {
        Animation animation = getSpaceAnimation();
        ovalTop.startAnimation(animation);
        ovalCenter.startAnimation(animation);
        ovalBottom.startAnimation(animation);
    }

    private void stopSpaceShipAnimation() {
        ovalTop.clearAnimation();
        ovalCenter.clearAnimation();
        ovalBottom.clearAnimation();
        animation.cancel();
        animation = null;
    }

    private Animation getSpaceAnimation() {
        if (animation == null) {
            animation = AnimationUtils.loadAnimation(getContext(), R.anim.oval_spaceship_slide_top);
        }
        return animation;
    }
}
