package com.kin.ecosystem.splash.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.kin.ecosystem.R;


public class SplashScreenButton extends FrameLayout {

    private Button letsGetStartedBtn;
    private ImageView centerImage;
    private View circleDash;
    private GradientDrawable bgDrawable;
    private LoadAnimationListener animationListener;

    private RotateAnimation circleRotateAnimation;
    private AnimationSet imageShowAnimation;
    private Animation imageHideAnimation;
    private AnimatorSet buttonAnimation;

    private int image_index = 0;
    private final int[] imageResIDs = new int[]{R.drawable.kinecosystem_diamond_1, R.drawable.kinecosystem_diamond_2, R.drawable.kinecosystem_diamond_3};

    private final int initialWidth = getResources().getDimensionPixelSize(R.dimen.kinecosystem_round_button_width);
    private final int toWidth = getResources().getDimensionPixelSize(R.dimen.kinecosystem_round_button_height);
    private final int startColor = ContextCompat.getColor(getContext(), R.color.kinecosystem_bluePrimary);

    private final float startRadius = getResources().getDimension(R.dimen.kinecosystem_button_corners_radius);

    private static final int BUTTON_ANIM_DURATION = 500;
    private static final int IMAGE_SHOW_DURATION = 400;
    private static final int IMAGE_HIDE_DURATION = 300;
    private static final int IMAGE_DELAY_DURATION = 500;
    private static final int CIRCLE_ROTATION_DURATION = 5000;
    private static final float ONE_FLOAT = 1f;
    private static final float ZERO_FLOAT = 0f;
    private static final float CENTER_PIVOT = 0.5f;
    private static final float FINAL_DEGREE = 360f;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public SplashScreenButton(@NonNull Context context) {
        super(context);
        init();
    }

    public SplashScreenButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SplashScreenButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.kinecosystem_splash_loader, this);
        setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        letsGetStartedBtn = findViewById(R.id.lets_get_started_button);
        centerImage = findViewById(R.id.center_image);
        circleDash = findViewById(R.id.circle_dash);
        bgDrawable = (GradientDrawable) letsGetStartedBtn.getBackground();

        setUpButtonAnimation();
        setUpRotateAnimation();
        setUpImageShowAnimation();
        setUpImageHideAnimation();
    }

    private void setUpButtonAnimation() {

        ValueAnimator widthAnimation = ValueAnimator.ofInt(initialWidth, toWidth);
        widthAnimation.setDuration(BUTTON_ANIM_DURATION);
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = letsGetStartedBtn.getLayoutParams();
                layoutParams.width = val;
                letsGetStartedBtn.setLayoutParams(layoutParams);
            }
        });

        ObjectAnimator cornerAnimation = ObjectAnimator
            .ofFloat(bgDrawable, "cornerRadius", startRadius, startRadius * 2);
        cornerAnimation.setDuration(BUTTON_ANIM_DURATION);

        ObjectAnimator buttonTextFadeAnimation = ObjectAnimator
            .ofInt(letsGetStartedBtn, "textColor", startColor, Color.TRANSPARENT);
        buttonTextFadeAnimation.setEvaluator(new ArgbEvaluator());
        buttonTextFadeAnimation.setRepeatMode(ValueAnimator.REVERSE);
        buttonTextFadeAnimation.setDuration(BUTTON_ANIM_DURATION / 2);

        buttonAnimation = new AnimatorSet();
        buttonAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                letsGetStartedBtn.setVisibility(INVISIBLE);
                startDashAnimation();
                startShowImageAnimation();
            }
        });
        buttonAnimation.playTogether(widthAnimation, cornerAnimation, buttonTextFadeAnimation);
    }

    private void startButtonAnimation() {
        buttonAnimation.start();
    }

    private void setUpRotateAnimation() {
        circleRotateAnimation = new RotateAnimation(ZERO_FLOAT, FINAL_DEGREE,
            Animation.RELATIVE_TO_SELF, CENTER_PIVOT,
            Animation.RELATIVE_TO_SELF, CENTER_PIVOT);
        circleRotateAnimation.setDuration(CIRCLE_ROTATION_DURATION);
        circleRotateAnimation.setRepeatCount(Animation.INFINITE);
        circleRotateAnimation.setInterpolator(new LinearInterpolator());
    }

    private void setUpImageShowAnimation() {
        Animation scaleUpImageAnimation = new ScaleAnimation(ZERO_FLOAT, ONE_FLOAT, ZERO_FLOAT, ONE_FLOAT,
            Animation.RELATIVE_TO_SELF, CENTER_PIVOT,
            Animation.RELATIVE_TO_SELF, CENTER_PIVOT);
        scaleUpImageAnimation.setInterpolator(new OvershootInterpolator(ONE_FLOAT));
        scaleUpImageAnimation.setDuration(IMAGE_SHOW_DURATION);
        scaleUpImageAnimation.setFillAfter(true);

        Animation fadeInImageAnimation = new AlphaAnimation(ZERO_FLOAT, ONE_FLOAT);
        fadeInImageAnimation.setInterpolator(new LinearInterpolator());
        fadeInImageAnimation.setDuration(IMAGE_SHOW_DURATION);
        fadeInImageAnimation.setFillAfter(true);

        imageShowAnimation = new AnimationSet(false);
        imageShowAnimation.addAnimation(scaleUpImageAnimation);
        imageShowAnimation.addAnimation(fadeInImageAnimation);
        imageShowAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startHideImageAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void setUpImageHideAnimation() {
        imageHideAnimation = new AlphaAnimation(ONE_FLOAT, ZERO_FLOAT);
        imageHideAnimation.setInterpolator(new LinearInterpolator());
        imageHideAnimation.setDuration(IMAGE_HIDE_DURATION);
        imageHideAnimation.setFillAfter(true);
        imageHideAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startShowImageAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void startDashAnimation() {
        circleDash.setVisibility(VISIBLE);
        circleDash.startAnimation(circleRotateAnimation);
    }

    private void startShowImageAnimation() {
        centerImage.setVisibility(VISIBLE);
        centerImage.setImageResource(imageResIDs[image_index]);
        centerImage.startAnimation(imageShowAnimation);
    }

    private void startHideImageAnimation() {
        incrementImageIndex();
        mainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                centerImage.startAnimation(imageHideAnimation);
            }
        }, IMAGE_DELAY_DURATION);
    }

    private void incrementImageIndex() {
        image_index++;
        if (image_index >= imageResIDs.length) {
            image_index = 0;
            animationListener.onAnimationEnd();
        }
    }

    public void animateLoading() {
        startButtonAnimation();
    }

    public void stopLoading(boolean reset) {
        cancelAndResetAnimations();
        if (reset) {
            resetButton();
            letsGetStartedBtn.setVisibility(VISIBLE);
        } else {
            letsGetStartedBtn.setVisibility(INVISIBLE);
        }
        circleDash.setVisibility(GONE);
        centerImage.setVisibility(GONE);
    }

    private void cancelAndResetAnimations() {
        circleRotateAnimation.cancel();
        imageShowAnimation.cancel();
        imageHideAnimation.cancel();
        buttonAnimation.cancel();

        circleRotateAnimation.reset();
        imageShowAnimation.reset();
        imageHideAnimation.reset();
    }

    private void resetButton() {
        ViewGroup.LayoutParams layoutParams = letsGetStartedBtn.getLayoutParams();
        layoutParams.width = initialWidth;
        letsGetStartedBtn.setLayoutParams(layoutParams);
        letsGetStartedBtn.setTextColor(startColor);
    }

    public void setButtonListener(OnClickListener onClickListener) {
        letsGetStartedBtn.setOnClickListener(onClickListener);
    }

    public void setLoadAnimationListener(LoadAnimationListener animationListener) {
        this.animationListener = animationListener;
    }

    public interface LoadAnimationListener {

        void onAnimationEnd();
    }
}
