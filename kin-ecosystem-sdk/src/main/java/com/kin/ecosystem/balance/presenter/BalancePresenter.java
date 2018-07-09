package com.kin.ecosystem.balance.presenter;

import static com.kin.ecosystem.main.ScreenId.MARKETPLACE;
import static com.kin.ecosystem.main.ScreenId.ORDER_HISTORY;
import static kin.ecosystem.core.util.StringUtil.getAmountFormatted;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import com.kin.ecosystem.balance.view.IBalanceView;
import com.kin.ecosystem.base.BasePresenter;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.bi.EventLogger;
import com.kin.ecosystem.bi.events.BalanceTapped;
import com.kin.ecosystem.data.blockchain.BlockchainSource;
import com.kin.ecosystem.data.model.Balance;
import com.kin.ecosystem.data.order.OrderDataSource;
import com.kin.ecosystem.main.ScreenId;
import com.kin.ecosystem.network.model.Offer.OfferType;
import com.kin.ecosystem.network.model.Order;
import com.kin.ecosystem.network.model.Order.Origin;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class BalancePresenter extends BasePresenter<IBalanceView> implements IBalancePresenter {

	private static final String BALANCE_ZERO_TEXT = "0.00";

	private final EventLogger eventLogger;
	private final BlockchainSource blockchainSource;
	private final OrderDataSource orderRepository;

	private Observer<Balance> balanceObserver;
	private Observer<Order> orderObserver;
	private BalanceClickListener balanceClickListener;

	private int pendingOrderCount;
	private Order currentPendingOrder;
	private @OrderStatus
	int status;
	private @ScreenId
	int currentScreen = MARKETPLACE;

	public static final int PENDING = 0x00000001;
	public static final int DELAYED = 0x00000002;
	public static final int COMPLETED = 0x00000003;
	public static final int FAILED = 0x00000004;

	@IntDef({PENDING, DELAYED, COMPLETED, FAILED})
	@Retention(RetentionPolicy.SOURCE)
	public @interface OrderStatus {

	}

	public static final int EARN = 0x00000001;
	public static final int SPEND = 0x00000002;

	@IntDef({EARN, SPEND})
	@Retention(RetentionPolicy.SOURCE)
	public @interface OrderType {

	}


	public BalancePresenter(@NonNull IBalanceView view,
		@NonNull final EventLogger eventLogger,
		@NonNull final BlockchainSource blockchainSource,
		@NonNull final OrderDataSource orderRepository) {
		this.view = view;
		this.eventLogger = eventLogger;
		this.blockchainSource = blockchainSource;
		this.orderRepository = orderRepository;
		createBalanceObserver();
		createOrderObserver();

		this.view.attachPresenter(this);
	}

	private void createBalanceObserver() {
		balanceObserver = new Observer<Balance>() {
			@Override
			public void onChanged(Balance balance) {
				updateBalance(balance);
			}
		};
	}

	private void updateBalance(Balance balance) {
		int balanceValue = balance.getAmount().intValue();
		String balanceString;
		if (balanceValue == 0) {
			balanceString = BALANCE_ZERO_TEXT;
		} else {
			balanceString = getAmountFormatted(balanceValue);
		}
		if (view != null) {
			view.updateBalance(balanceString);
		}
	}

	private int getType(OfferType offerType) {
		switch (offerType) {
			case SPEND:
				return SPEND;
			default:
			case EARN:
				return EARN;
		}
	}

	private void createOrderObserver() {
		orderObserver = new Observer<Order>() {
			@Override
			public void onChanged(Order order) {
				if (order.getOrigin() == Origin.MARKETPLACE) {
					switch (order.getStatus()) {
						case PENDING:
							incrementPendingCount();
							currentPendingOrder = order;
							status = getStatus(order);
							updateSubTitle(order.getAmount(), status, getType(order.getOfferType()));
							break;
						case COMPLETED:
						case FAILED:
							decrementPendingCount();
						case DELAYED:
							if (isCurrentOrder(order)) {
								status = getStatus(order);
								updateSubTitle(order.getAmount(), status, getType(order.getOfferType()));
							}
							break;
					}
				}

			}
		};
	}

	private void incrementPendingCount() {
		pendingOrderCount++;
	}

	private void decrementPendingCount() {
		if(pendingOrderCount > 0) {
			pendingOrderCount--;
		}
	}

	private boolean isCurrentOrder(Order order) {
		return order != null && currentPendingOrder != null && currentPendingOrder.equals(order);
	}

	private @OrderStatus int getStatus(Order order) {
		switch (order.getStatus()) {
			case COMPLETED:
				return COMPLETED;
			case FAILED:
				return FAILED;
			case DELAYED:
				return DELAYED;
			default:
			case PENDING:
				return PENDING;
		}
	}


	private void updateSubTitle(int amount, @OrderStatus int status, @OrderType int offerType) {
		if (view != null) {
			view.updateSubTitle(amount, status, offerType);
		}
	}

	@Override
	public void onAttach(IBalanceView view) {
		super.onAttach(view);
		showWelcomeToKin();
		addObservers();
	}

	private void showWelcomeToKin() {
		if (view != null) {
			view.setWelcomeSubtitle();
		}
	}

	private void addObservers() {
		orderRepository.addOrderObserver(orderObserver);
		blockchainSource.addBalanceObserver(balanceObserver);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		removeObservers();
	}

	private void removeObservers() {
		orderRepository.removeOrderObserver(orderObserver);
		blockchainSource.removeBalanceObserver(balanceObserver);
	}

	@Override
	public void balanceClicked() {
		eventLogger.send(BalanceTapped.create());
		if (this.balanceClickListener != null) {
			this.balanceClickListener.onClick();
		}
	}

	@Override
	public void setClickListener(BalanceClickListener balanceClickListener) {
		this.balanceClickListener = balanceClickListener;
	}

	@Override
	public void visibleScreen(@ScreenId int id) {
		if (currentScreen != id) {
			currentScreen = id;
			switch (id) {
				case ORDER_HISTORY:
					animateArrow(false);
					if (currentPendingOrder != null && status == COMPLETED) {
						clearSubTitle();
					}
					break;
				case MARKETPLACE:
					animateArrow(true);
					if (pendingOrderCount == 0 && currentPendingOrder != null && status == COMPLETED) {
						currentPendingOrder = null;
						showWelcomeToKin();
					}
					break;
			}
		}
	}

	private void animateArrow(boolean showArrow) {
		if (view != null) {
			view.animateArrow(showArrow);
		}
	}

	private void clearSubTitle() {
		if (view != null) {
			view.clearSubTitle();
		}
	}
}
