package com.ecosystem.kin.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import com.ecosystem.kin.app.model.SignInRepo;
import com.kin.ecosystem.Kin;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.NativeOfferClickEvent;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.exception.ClientException;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.common.exception.ServiceException;
import com.kin.ecosystem.common.model.Balance;
import com.kin.ecosystem.common.model.NativeSpendOffer;
import com.kin.ecosystem.common.model.OrderConfirmation;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = "Ecosystem - SampleApp";

	private ConstraintLayout containerLayout;
	private TextView balanceView;
	private TextView nativeSpendTextView;
	private TextView nativeEarnTextView;
	private TextView showPublicAddressTextView;
	private TextView publicAddressTextArea;
	private TextView payToUserTextView;

	private KinCallback<OrderConfirmation> nativeSpendOrderConfirmationCallback;
	private KinCallback<OrderConfirmation> nativeEarnOrderConfirmationCallback;
	private KinCallback<OrderConfirmation> payToUserOrderConfirmationCallback;
	private Observer<NativeOfferClickEvent> nativeSpendOfferClickedObserver;
	private Observer<Balance> balanceObserver;

	private String userID;
	private String publicAddress;

	private int randomID = new Random().nextInt((9999 - 1) + 1) + 1;
	private NativeSpendOffer nativeSpendOffer =
		new NativeSpendOffer(String.valueOf(randomID))
			.title("Native Spend")
			.description("Upgrade your profile")
			.amount(100)
			.image("https://cdn.kinecosystem.com/thumbnails/offers/spend_offer_smplapp.png");
	private boolean dismissOnTap = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		userID = SignInRepo.getUserId(getApplicationContext());
		containerLayout = findViewById(R.id.container);
		balanceView = findViewById(R.id.get_balance);
		nativeSpendTextView = findViewById(R.id.native_spend_button);
		nativeEarnTextView = findViewById(R.id.native_earn_button);
		showPublicAddressTextView = findViewById(R.id.show_public_address);
		publicAddressTextArea = findViewById(R.id.public_text_area);
		payToUserTextView = findViewById(R.id.pay_to_user_button);
		showPublicAddressTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (publicAddress == null) {
					getPublicAddress();
				} else {
					copyToClipboard(publicAddress, "Public address");
				}
			}
		});
		balanceView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				enableView(v, false);
				getBalance();
			}
		});
		nativeSpendTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showSnackbar("Native spend flow started", false);
				enableView(v, false);
				createNativeSpendOffer();
			}
		});
		nativeEarnTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showSnackbar("Native earn flow started", false);
				enableView(v, false);
				createNativeEarnOffer();
			}
		});
		payToUserTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				enableView(v, false);
				showPayToUserDialog(v);
			}
		});

		final TextView userIdTextView = findViewById(R.id.user_id_textview);
		userIdTextView.setText(getString(R.string.user_id, userID));
		userIdTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				copyToClipboard(userID, "User ID");
			}
		});
		findViewById(R.id.launch_marketplace).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addNativeSpendOffer(nativeSpendOffer, getDismissOnTap());
				openKinMarketplace();
			}
		});
		((TextView) findViewById(R.id.sample_app_version))
			.setText(getString(R.string.version_name, BuildConfig.VERSION_NAME));
		addNativeSpendOffer(nativeSpendOffer, getDismissOnTap());
		addNativeOfferClickedObserver();
	}

	private boolean getDismissOnTap() {
		return dismissOnTap = !dismissOnTap;
	}

	@Override
	protected void onStart() {
		super.onStart();
		addBalanceObserver();
	}

	@Override
	protected void onStop() {
		super.onStop();
		removeBalanceObserver();
	}

	private void addBalanceObserver() {
		if (balanceObserver == null) {
			balanceObserver = new Observer<Balance>() {
				@Override
				public void onChanged(Balance value) {
					Log.d(TAG, "Balance - " + value.getAmount().intValue());
				}
			};

			try {
				Kin.addBalanceObserver(balanceObserver);
			} catch (ClientException e) {
				e.printStackTrace();
			}
		}

	}

	private void removeBalanceObserver() {
		try {
			Kin.removeBalanceObserver(balanceObserver);
			balanceObserver = null;
		} catch (ClientException e) {
			e.printStackTrace();
		}
	}

	// Use this method to remove the nativeSpendOffer you added
	private void removeNativeOffer(@NonNull NativeSpendOffer nativeSpendOffer) {
		try {
			if (Kin.removeNativeOffer(nativeSpendOffer)) {
				showSnackbar("Native offer removed", false);
			}
		} catch (ClientException e) {
			e.printStackTrace();
		}
	}

	private void addNativeOfferClickedObserver() {
		try {
			Kin.addNativeOfferClickedObserver(getNativeOfferClickedObserver());
		} catch (ClientException e) {
			showSnackbar("Could not add native offer callback", true);
		}
	}

	private Observer<NativeOfferClickEvent> getNativeOfferClickedObserver() {
		if (nativeSpendOfferClickedObserver == null) {
			nativeSpendOfferClickedObserver = new Observer<NativeOfferClickEvent>() {
				@Override
				public void onChanged(NativeOfferClickEvent nativeOfferClickEvent) {
					NativeSpendOffer nativeSpendOffer = (NativeSpendOffer) nativeOfferClickEvent.getNativeOffer();
					if (nativeOfferClickEvent.isDismissOnTap()) {
						new AlertDialog.Builder(MainActivity.this)
							.setTitle("Native Offer (" + nativeSpendOffer.getTitle() + ")")
							.setMessage("You tapped a native offer and the observer was notified.")
							.show();
					} else {
						Intent nativeOfferIntent = NativeOfferActivity
							.createIntent(MainActivity.this, nativeSpendOffer.getTitle());
						startActivity(nativeOfferIntent);
					}
				}
			};
		}
		return nativeSpendOfferClickedObserver;
	}

	private void addNativeSpendOffer(@NonNull NativeSpendOffer nativeSpendOffer, boolean dismissMarketPlaceOnTap) {
		try {
			if (Kin.addNativeOffer(nativeSpendOffer, dismissMarketPlaceOnTap)) {
				showToast("Native offer added");
			}
		} catch (ClientException e) {
			e.printStackTrace();
			showToast("Could not add native offer");
		}
	}

	private void getPublicAddress() {
		try {
			publicAddress = Kin.getPublicAddress();
			int blueColor = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
			publicAddressTextArea.getBackground().setColorFilter(blueColor, Mode.SRC_ATOP);
			showPublicAddressTextView.setText(R.string.copy_public_address);
			publicAddressTextArea.setText(publicAddress);
		} catch (ClientException e) {
			e.printStackTrace();
		}

	}

	private void copyToClipboard(CharSequence textToCopy, String paramName) {
		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(
				Context.CLIPBOARD_SERVICE);
			clipboard.setText(textToCopy);
		} else {
			android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(
				Context.CLIPBOARD_SERVICE);
			android.content.ClipData clip = android.content.ClipData.newPlainText("copied text", textToCopy);
			clipboard.setPrimaryClip(clip);
		}
		Toast.makeText(this, paramName + " copied to your clipboard", Toast.LENGTH_SHORT).show();
	}

	private void getBalance() {
		try {
			//Get Cached Balance
			try {
				Balance cachedBalance = Kin.getCachedBalance();
				setBalanceWithAmount(cachedBalance);
			} catch (ClientException e) {
				e.printStackTrace();
			}

			Kin.getBalance(new KinCallback<Balance>() {
				@Override
				public void onResponse(Balance balance) {
					enableView(balanceView, true);
					setBalanceWithAmount(balance);
				}

				@Override
				public void onFailure(KinEcosystemException exception) {
					enableView(balanceView, true);
					setBalanceFailed();
				}
			});
		} catch (ClientException e) {
			setBalanceFailed();
			e.printStackTrace();
		}
	}

	private void setBalanceFailed() {
		balanceView.setText(R.string.failed_to_get_balance);
	}

	private void setBalanceWithAmount(Balance balance) {
		int balanceValue = balance.getAmount().intValue();
		balanceView.setText(getString(R.string.get_balance_d, balanceValue));
	}

	private void openKinMarketplace() {
		try {
			Kin.launchMarketplace(MainActivity.this);
		} catch (ClientException e) {
			e.printStackTrace();
		}
	}

	private void createNativeSpendOffer() {
		String offerJwt = JwtUtil.generateSpendOfferExampleJWT(BuildConfig.SAMPLE_APP_ID, userID);
		Log.d(TAG, "createNativeSpendOffer: " + offerJwt);
		try {
			Kin.purchase(offerJwt, getNativeSpendOrderConfirmationCallback());
		} catch (ClientException e) {
			e.printStackTrace();
		}
	}

	private void createNativeEarnOffer() {
		String offerJwt = JwtUtil.generateEarnOfferExampleJWT(BuildConfig.SAMPLE_APP_ID, userID);
		try {
			Kin.requestPayment(offerJwt, getNativeEarnOrderConfirmationCallback());
		} catch (ClientException e) {
			e.printStackTrace();
		}
	}

	private void createPayToUserOffer(String recipientUserID) {
		String offerJwt = JwtUtil.generatePayToUserOfferExampleJWT(BuildConfig.SAMPLE_APP_ID, userID, recipientUserID);
		try {
			Kin.payToUser(offerJwt, getNativePayToUserOrderConfirmationCallback());
		} catch (ClientException e) {
			e.printStackTrace();
		}
	}

	private void showPayToUserDialog(final View v) {
		PayToUserDialog dialog = new PayToUserDialog(v.getContext());
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				PayToUserDialog myDialog = (PayToUserDialog) dialog;
				if (myDialog.getUserId() != null) {
					showSnackbar("Pay to user flow started", false);
					final String userId = myDialog.getUserId();
					try {
						Kin.hasAccount(userId, new KinCallback<Boolean>() {
							@Override
							public void onResponse(Boolean hasAccount) {
								if(hasAccount != null && hasAccount){
									createPayToUserOffer(userId);
								} else {
									showSnackbar("Account not found", true);
									enableView(v, true);
								}
							}

							@Override
							public void onFailure(KinEcosystemException exception) {
								showSnackbar("Failed - " + exception.getMessage(), true);
								enableView(v, true);
							}
						});
					} catch (ClientException e) {
						e.printStackTrace();
						enableView(v, true);
					}

				} else {
					enableView(v, true);
				}
			}
		});
		dialog.show();
	}

	/**
	 * Use this method with the offerID you created, to get {@link OrderConfirmation}
	 */
	private void getOrderConfirmation(@NonNull final String offerID) {
		if (!TextUtils.isEmpty(offerID)) {
			try {
				Kin.getOrderConfirmation(offerID, new KinCallback<OrderConfirmation>() {
					@Override
					public void onResponse(OrderConfirmation orderConfirmation) {
						showSnackbar("Offer: " + offerID + " Status is: " + orderConfirmation.getStatus(), false);
					}

					@Override
					public void onFailure(KinEcosystemException exception) {
						showSnackbar("Failed to get OfferId: " + offerID + " status", true);
					}
				});
			} catch (ClientException e) {
				e.printStackTrace();
			}
		}
	}

	private KinCallback<OrderConfirmation> getNativeSpendOrderConfirmationCallback() {
		if (nativeSpendOrderConfirmationCallback == null) {
			nativeSpendOrderConfirmationCallback = new KinCallback<OrderConfirmation>() {
				@Override
				public void onResponse(OrderConfirmation orderConfirmation) {
					getBalance();
					showSnackbar("Succeed to create native spend", false);
					Log.d(TAG, "Jwt confirmation: \n" + orderConfirmation.getJwtConfirmation());
					enableView(nativeSpendTextView, true);
				}

				@Override
				public void onFailure(KinEcosystemException exception) {
					showSnackbar("Failed - " + exception.getMessage(), true);
					enableView(nativeSpendTextView, true);
				}
			};
		}
		return nativeSpendOrderConfirmationCallback;
	}

	private KinCallback<OrderConfirmation> getNativeEarnOrderConfirmationCallback() {
		if (nativeEarnOrderConfirmationCallback == null) {
			nativeEarnOrderConfirmationCallback = new KinCallback<OrderConfirmation>() {
				@Override
				public void onResponse(OrderConfirmation orderConfirmation) {
					getBalance();
					showSnackbar("Succeed to create native earn", false);
					Log.d(TAG, "Jwt confirmation: \n" + orderConfirmation.getJwtConfirmation());
					enableView(nativeEarnTextView, true);
				}

				@Override
				public void onFailure(KinEcosystemException exception) {
					showSnackbar("Failed - " + exception.getMessage(), true);
					enableView(nativeEarnTextView, true);
				}
			};
		}
		return nativeEarnOrderConfirmationCallback;
	}

	private KinCallback<OrderConfirmation> getNativePayToUserOrderConfirmationCallback() {
		if (payToUserOrderConfirmationCallback == null) {
			payToUserOrderConfirmationCallback = new KinCallback<OrderConfirmation>() {
				@Override
				public void onResponse(OrderConfirmation orderConfirmation) {
					getBalance();
					showSnackbar("Succeed to pay to user", false);
					Log.d(TAG, "Jwt confirmation: \n" + orderConfirmation.getJwtConfirmation());
					enableView(payToUserTextView, true);
				}

				@Override
				public void onFailure(KinEcosystemException exception) {
					if (exception.getCode() == ServiceException.USER_NOT_FOUND) {
						showSnackbar("Account not found", true);
					} else {
						showSnackbar("Failed - " + exception.getMessage(), true);
					}
					enableView(payToUserTextView, true);
				}
			};
		}
		return payToUserOrderConfirmationCallback;
	}

	private void enableView(View v, boolean enable) {
		v.setEnabled(enable);
		v.setClickable(enable);
		v.setAlpha(enable ? 1f : 0.5f);
	}

	private void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	private void showSnackbar(String msg, boolean isError) {
		Snackbar snackbar = Snackbar.make(containerLayout, msg, isError ? Snackbar.LENGTH_LONG : Snackbar.LENGTH_SHORT);
		if (isError) {
			((TextView) snackbar.getView()
				.findViewById(android.support.design.R.id.snackbar_text))
				.setTextColor(Color.RED);
		}
		snackbar.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		nativeSpendOrderConfirmationCallback = null;
		nativeEarnOrderConfirmationCallback = null;
		payToUserOrderConfirmationCallback = null;
		try {
			Kin.removeNativeOffer(nativeSpendOffer);
			Kin.removeNativeOfferClickedObserver(nativeSpendOfferClickedObserver);
		} catch (ClientException e) {
			Log.d(TAG, "onDestroy: Failed to remove native offer clicked observer");
		}
	}
}
