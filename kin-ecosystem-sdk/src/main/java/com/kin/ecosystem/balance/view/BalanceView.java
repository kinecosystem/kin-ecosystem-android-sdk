package com.kin.ecosystem.balance.view;

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
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;
import com.kin.ecosystem.R;
import com.kin.ecosystem.balance.presenter.BalancePresenter;
import com.kin.ecosystem.base.IBasePresenter;
import com.kin.ecosystem.data.blockchain.BlockchainSourceImpl;
import com.kin.ecosystem.data.offer.OfferRepository;
import com.kin.ecosystem.data.order.OrderRepository;
import com.kin.ecosystem.network.model.Order.Status;

public class BalanceView extends ConstraintLayout implements IBalanceView {

	private static int blueColor;
	private static int subTitleColor;
	private static float balanceTextSize;
	private static float subTitleTextSize;

	private IBasePresenter<IBalanceView> balancePresenter;

	private TextSwitcher subTitle;
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
		inflate(getContext(), R.layout.kinecosystem_balance_view, this);
		setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT));
		int topPadding = getResources().getDimensionPixelOffset(R.dimen.kinecosystem_main_medium_margin);
		setPadding(0, topPadding, 0, 0);
		initColors();
		initSizes();

		TypedArray styledAttributes = context.getTheme()
			.obtainStyledAttributes(attrs, R.styleable.KinEcosystemBalanceView, 0, 0);
		boolean showArrow;
		String subTitleText;
		try {
			showArrow = styledAttributes.getBoolean(R.styleable.KinEcosystemBalanceView_showArrow, false);
			subTitleText = styledAttributes.getString(R.styleable.KinEcosystemBalanceView_subTitle);

		} finally {
			styledAttributes.recycle();
		}

		this.subTitle = findViewById(R.id.sub_title);
		this.balanceText = findViewById(R.id.balance_text);
		setArrowVisibility(showArrow);
		addBalanceViewFactory();
		addSubTitleViewFactory();
		updateSubTitle(subTitleText, null);

		attachPresenter(new BalancePresenter(BlockchainSourceImpl.getInstance(), OfferRepository.getInstance(),
			OrderRepository.getInstance()));
	}

	private void initColors() {
		blueColor = ContextCompat.getColor(getContext(), R.color.kinecosystem_bluePrimary);
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
		subTitle.setFactory(new ViewFactory() {
			@Override
			public View makeView() {
				TextView subTitle = new TextView(getContext());
				subTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, subTitleTextSize);
				subTitle.setTextColor(subTitleColor);
				return subTitle;
			}
		});
	}

	private void setArrowVisibility(boolean showArrow) {
		ImageView arrow = findViewById(R.id.arrow);
		arrow.setVisibility(showArrow ? VISIBLE : GONE);
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
	public void updateSubTitle(final String sub_title, final Status status) {
		if (this.subTitle != null) {
			post(new Runnable() {
				@Override
				public void run() {
					if (sub_title != null) {
						if (status != null) {
							//TODO change colors based on status
							TextView textView = (TextView) subTitle.getNextView();

						}
						subTitle.setText(sub_title);
					}
				}
			});
		}
	}
}
