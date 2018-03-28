package com.kin.ecosystem.balance.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import com.kin.ecosystem.R;
import com.kin.ecosystem.balance.presenter.BalancePresenter;
import com.kin.ecosystem.base.IBasePresenter;
import com.kin.ecosystem.data.blockchain.BlockchainSource;

public class BalanceView extends ConstraintLayout implements IBalanceView {

    private static final int ANIM_DURATION = 300;

    private IBasePresenter<IBalanceView> balancePresenter;

    private TextView subTitle;
    private TextSwitcher balanceText;

    public BalanceView(Context context) {
        super(context);
        init(context, null);
    }

    public BalanceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BalanceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        inflate(getContext(), R.layout.balance_view, this);
        setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
        int topPadding = getResources().getDimensionPixelOffset(R.dimen.main_medium_margin);
        setPadding(0, topPadding, 0, 0);

        TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.BalanceView, 0, 0);
        boolean showArrow;
        try {
            showArrow = styledAttributes.getBoolean(R.styleable.BalanceView_showArrow, false);
        } finally {
            styledAttributes.recycle();
        }

        this.subTitle = findViewById(R.id.sub_title);
        this.balanceText = findViewById(R.id.balance_text);

        Animation in = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        in.setDuration(ANIM_DURATION);
        out.setDuration(ANIM_DURATION);
        balanceText.setInAnimation(in);
        balanceText.setOutAnimation(out);

        ImageView arrow = findViewById(R.id.arrow);
        arrow.setVisibility(showArrow ? VISIBLE : GONE);

        attachPresenter(new BalancePresenter(BlockchainSource.getInstance()));
    }

    @Override
    public void attachPresenter(IBasePresenter<IBalanceView> presenter) {
        this.balancePresenter = presenter;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.balancePresenter.onAttach(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.balancePresenter.onDetach();
    }

    @Override
    public void updateBalance(final String balance) {
        if (this.balanceText != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    balanceText.setText(balance);
                }
            });
        }
    }

    @Override
    public void updateSubTitle(final String sub_title) {
        if (this.subTitle != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    subTitle.setText(sub_title);
                }
            });
        }
    }
}
