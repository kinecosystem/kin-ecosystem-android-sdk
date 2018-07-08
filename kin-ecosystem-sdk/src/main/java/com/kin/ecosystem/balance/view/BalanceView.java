package com.kin.ecosystem.balance.view;

import static com.kin.ecosystem.balance.presenter.BalancePresenter.COMPLETED;
import static com.kin.ecosystem.balance.presenter.BalancePresenter.DELAYED;
import static com.kin.ecosystem.balance.presenter.BalancePresenter.FAILED;
import static com.kin.ecosystem.balance.presenter.BalancePresenter.PENDING;
import static com.kin.ecosystem.balance.presenter.BalancePresenter.SPEND;
import static kin.ecosystem.core.util.StringUtil.getAmountFormatted;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;
import com.kin.ecosystem.R;
import com.kin.ecosystem.balance.presenter.BalancePresenter.OrderStatus;
import com.kin.ecosystem.balance.presenter.BalancePresenter.OrderType;
import com.kin.ecosystem.balance.presenter.IBalancePresenter;

public class BalanceView extends ConstraintLayout implements IBalanceView {

	private static final int ANIM_DURATION = 200;
	private static final float FLOAT_1 = 1f;
	private static final float FLOAT_0 = 0f;
	private static final float FLOAT_08 = 0.8f;
	private static int blueColor;
	private static int orangeColor;
	private static int redColor;
	private static int subTitleColor;

	private static float balanceTextSize;
	private static float subTitleTextSize;

	private IBalancePresenter balancePresenter;

	private TextSwitcher subTitleTextView;
	private TextSwitcher balanceText;
	private ImageView arrow;

	private float arrowFromX = 0;
	private float arrowToX = 0;

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
		inflate(context, R.layout.kinecosystem_balance_view, this);
		setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT));
		int topPadding = getResources().getDimensionPixelOffset(R.dimen.kinecosystem_main_medium_margin);
		setPadding(0, topPadding, 0, 0);
		initColors();
		initSizes();

		TypedArray styledAttributes = context.getTheme()
			.obtainStyledAttributes(attrs, R.styleable.KinEcosystemBalanceView, 0, 0);
		String subtitle;
		try {
			subtitle = styledAttributes.getString(R.styleable.KinEcosystemBalanceView_subTitle);

		} finally {
			styledAttributes.recycle();
		}

		this.subTitleTextView = findViewById(R.id.sub_title);
		this.balanceText = findViewById(R.id.balance_text);
		this.arrow = findViewById(R.id.arrow);
		addBalanceViewFactory();
		addSubTitleViewFactory();
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				balancePresenter.balanceClicked();
			}
		});
		this.arrow.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if (arrow.getMeasuredWidth() > 0) {
					arrowFromX = arrow.getPivotX() - getResources().getDimensionPixelOffset(R.dimen.kinecosystem_main_medium_margin);
					arrowToX = arrowFromX + arrow.getWidth();
					arrow.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				}
			}
		});

		this.subTitleTextView.setText(subtitle);
	}

	private void initColors() {
		blueColor = ContextCompat.getColor(getContext(), R.color.kinecosystem_bluePrimary);
		orangeColor = ContextCompat.getColor(getContext(), R.color.kinecosystem_orange);
		redColor = ContextCompat.getColor(getContext(), R.color.kinecosystem_red);
		subTitleColor = ContextCompat.getColor(getContext(), R.color.kinecosystem_subtitle);
	}

	private void initSizes() {
		Resources resources = getResources();
		balanceTextSize = resources.getDimension(R.dimen.kinecosystem_title_xlarge_size);
		subTitleTextSize = resources.getDimension(R.dimen.kinecosystem_sub_title_size);
	}


	private void addBalanceViewFactory() {
		balanceText.setFactory(new ViewFactory() {
			@Override
			public View makeView() {
				TextView balanceText = new TextView(getContext());
				balanceText.setTextSize(TypedValue.COMPLEX_UNIT_PX, balanceTextSize);
				balanceText.setTextColor(blueColor);
				return balanceText;
			}
		});
	}

	private void addSubTitleViewFactory() {
		subTitleTextView.setFactory(new ViewFactory() {
			@Override
			public View makeView() {
				TextView subTitle = new TextView(getContext());
				subTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, subTitleTextSize);
				subTitle.setTextColor(subTitleColor);
				return subTitle;
			}
		});
	}

	@Override
	public void animateArrow(boolean showArrow) {
		if (showArrow) {
			postOnAnimation(new Runnable() {
				@Override
				public void run() {
					balanceText.animate().translationX(arrowFromX).setInterpolator(new LinearInterpolator()).setDuration(ANIM_DURATION).start();
					arrow.animate().scaleX(FLOAT_1).translationX(arrowFromX).alpha(FLOAT_1).setInterpolator(new LinearInterpolator()).setDuration(ANIM_DURATION).start();
				}
			});
		} else {
			postOnAnimation(new Runnable() {
				@Override
				public void run() {
					balanceText.animate().translationX(arrowToX).setInterpolator(new LinearInterpolator()).setDuration(ANIM_DURATION).start();
					arrow.animate().scaleX(FLOAT_08).translationX(arrowToX).alpha(FLOAT_0).setInterpolator(new LinearInterpolator()).setDuration(ANIM_DURATION).start();
				}
			});
		}

	}

	@Override
	public void attachPresenter(IBalancePresenter presenter) {
		this.balancePresenter = presenter;
		this.balancePresenter.onAttach(this);
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
	public void setWelcomeSubtitle() {
		TextView nextTextView = (TextView) subTitleTextView.getNextView();
		nextTextView.setTextColor(subTitleColor);
		nextTextView.setText(R.string.kinecosystem_welcome_to_kin_marketplace);
		subTitleTextView.showNext();
	}

	@Override
	public void updateSubTitle(final int amount, @OrderStatus final int orderStatus, @OrderType final int orderType) {
		if (this.subTitleTextView != null) {
			post(new Runnable() {
				@Override
				public void run() {
					int color;
					String subtitle;
					TextView nextTextView = (TextView) subTitleTextView.getNextView();
					switch (orderStatus) {
						case DELAYED:
							subtitle = getResources().getString(R.string.kinecosystem_sorry_this_may_take_some_time);
							color = orangeColor;
							break;
						case COMPLETED:
							if (isSpend(orderType)) {
								subtitle = getResources().getString(R.string.kinecosystem_spend_completed);
								color = blueColor;
							} else {
								subtitle = getResources()
									.getString(R.string.kinecosystem_earn_completed, getAmountFormatted(amount));
								color = subTitleColor;
							}

							break;
						case FAILED:
							subtitle = getResources().getString(R.string.kinecosystem_something_went_wrong);
							color = redColor;
							break;
						case PENDING:
						default:
							if (isSpend(orderType)) {
								subtitle = getResources().getString(R.string.kinecosystem_spend_pending);
							} else {
								subtitle = getResources()
									.getString(R.string.kinecosystem_earn_pending, getAmountFormatted(amount));
							}
							color = subTitleColor;
							break;

					}

					nextTextView.setTextColor(color);
					nextTextView.setText(subtitle);
					subTitleTextView.showNext();
				}

			});
		}
	}

	private boolean isSpend(int orderType) {
		return orderType == SPEND;
	}

	@Override
	public void clearSubTitle() {
		subTitleTextView.setText("");
	}


}
