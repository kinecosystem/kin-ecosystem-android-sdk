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
import com.kin.ecosystem.EcosystemExperience;
import com.kin.ecosystem.Kin;
import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.NativeOfferClickEvent;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.exception.ClientException;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.common.exception.ServiceException;
import com.kin.ecosystem.common.model.Balance;
import com.kin.ecosystem.common.model.NativeEarnOfferBuilder;
import com.kin.ecosystem.common.model.NativeOffer;
import com.kin.ecosystem.common.model.NativeSpendOfferBuilder;
import com.kin.ecosystem.common.model.OrderConfirmation;
import com.kin.ecosystem.common.model.UserStats;
import com.kin.ecosystem.common.model.WhitelistData;
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
	private TextView getUserStatsTextView;


	private KinCallback<OrderConfirmation> nativeSpendOrderConfirmationCallback;
	private KinCallback<OrderConfirmation> nativeEarnOrderConfirmationCallback;
	private KinCallback<OrderConfirmation> payToUserOrderConfirmationCallback;
	private Observer<NativeOfferClickEvent> nativeOfferClickedObserver;
	private Observer<Balance> balanceObserver;

	private String userID;
	private String publicAddress;
	private String spendOfferID = "";


	private int getRandomID() {
		return new Random().nextInt((999999 - 1) + 1) + 1;
	}

	private NativeOffer nativeOffer;

	private NativeOffer getNativeSpendOffer() {
		return new NativeSpendOfferBuilder(String.valueOf(getRandomID()))
			.title("Spacial one time offer")
			.description("More details on native spend")
			.amount(100)
			.image("https://cdn.kinecosystem.com/thumbnails/offers/spend_offer_smplapp.png")
			.build();
	}


	private NativeOffer getNativeEarnOffer() {
		return new NativeEarnOfferBuilder(String.valueOf(getRandomID()))
			.title("Get your free Kin")
			.description("Upgrade your profile")
			.amount(100)
			.image("https://cdn.kinecosystem.com/thumbnails/offers/spend_offer_smplapp.png")
			.build();
	}

	private boolean addNativeSpendOrder = true;

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
		getUserStatsTextView = findViewById(R.id.get_user_stats);

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

		getUserStatsTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				enableView(v, false);
				showUserStats(v);
			}
		});

		login();
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
				launchExperience(EcosystemExperience.MARKETPLACE);
			}
		});
		findViewById(R.id.launch_orderHistory).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				launchExperience(EcosystemExperience.ORDER_HISTORY);
			}
		});
		((TextView) findViewById(R.id.sample_app_version))
			.setText(getString(R.string.version_name, BuildConfig.VERSION_NAME));
		addNativeOffer();
		addNativeOfferClickedObserver();
	}


	private void login() {
		if (BuildConfig.IS_JWT_REGISTRATION) {
			/**
			 * SignInData should be created with registration JWT {see https://jwt.io/} created securely by server side
			 * In the the this example {@link SignInRepo#getJWT} generate the JWT locally.
			 * DO NOT!!!! use this approach in your real app.
			 * */
			String jwt = SignInRepo.getJWT(this);

			Kin.login(jwt, new KinCallback<Void>() {
				@Override
				public void onResponse(Void response) {
					showSnackbar("login succeed jwt", false);
					Log.d(TAG, "JWT onResponse: login");
				}

				@Override
				public void onFailure(KinEcosystemException exception) {
					showSnackbar("login failed jwt", true);
					Log.e(TAG, "JWT onFailure: " + exception.getMessage());
				}
			});
		} else {
			/** Use {@link WhitelistData} for small scale testing */
			WhitelistData whitelistData = SignInRepo.getWhitelistSignInData(this, getAppId(), getApiKey());
			Kin.login(whitelistData, new KinCallback<Void>() {
				@Override
				public void onResponse(Void response) {
					showSnackbar("login succeed whitelist", false);
					Log.d(TAG, "WhiteList onResponse: login");
				}

				@Override
				public void onFailure(KinEcosystemException exception) {
					showSnackbar("login failed whitelist", true);
					Log.e(TAG, "WhiteList onFailure: " + exception.getMessage());
				}
			});
		}
	}

	private void launchExperience(@EcosystemExperience final int experience) {
		try {
			Kin.launchEcosystem(MainActivity.this, experience);
		} catch (ClientException e) {
			e.printStackTrace();
		}
	}

	private void addNativeOffer() {
		if (nativeOffer != null) {
			removeNativeOffer(nativeOffer);
		}
		if (addNativeSpendOrder) {
			nativeOffer = getNativeSpendOffer();
			addNativeOffer(nativeOffer, true);
		} else {
			nativeOffer = getNativeEarnOffer();
			addNativeOffer(nativeOffer, false);

		}
		addNativeSpendOrder = !addNativeSpendOrder;
	}

	private void showUserStats(final View v) {
		try {
			Kin.userStats(new KinCallback<UserStats>() {
				@Override
				public void onResponse(UserStats response) {
					StringBuilder userStats = new StringBuilder();
					userStats.append("Earns: ").append(response.getEarnCount()).append(", ").append("last Earn date: ")
						.append(response.getLastEarnDate()).append(", ").append("Spends: ")
						.append(response.getSpendCount()).append(", ").append("last spend date: ")
						.append(response.getLastSpendDate());
					showToast(userStats.toString());
					enableView(v, true);
				}

				@Override
				public void onFailure(KinEcosystemException exception) {
					showSnackbar("onFailure on Kin.userStats: " + exception.getMessage(), true);
					enableView(v, true);
				}
			});
		} catch (ClientException e) {
			showSnackbar("ClientException on Kin.userStats: " + e.getMessage(), true);
			enableView(v, true);
		}
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
				showSnackbar("ClientException  " + e.getMessage(), true);
				e.printStackTrace();
			}
		}
	}

	private void removeBalanceObserver() {
		try {
			Kin.removeBalanceObserver(balanceObserver);
		} catch (ClientException e) {
			showSnackbar("ClientException  " + e.getMessage(), true);
			e.printStackTrace();
		}
		balanceObserver = null;
	}

	// Use this method to remove the nativeSpendOffer you added
	private void removeNativeOffer(@NonNull NativeOffer nativeOffer) {
		try {
			if (Kin.removeNativeOffer(nativeOffer)) {
				showSnackbar("Native offer removed", false);
			} else {
				showSnackbar("Could not removed native offer", true);
			}
		} catch (ClientException e) {
			showSnackbar("ClientException  " + e.getMessage(), true);
			e.printStackTrace();
		}
	}

	private void addNativeOfferClickedObserver() {
		try {
			Kin.addNativeOfferClickedObserver(getNativeOfferClickedObserver());
		} catch (ClientException e) {
			showSnackbar("ClientException  " + e.getMessage(), true);
			e.printStackTrace();
		}
	}

	private Observer<NativeOfferClickEvent> getNativeOfferClickedObserver() {
		if (nativeOfferClickedObserver == null) {
			nativeOfferClickedObserver = new Observer<NativeOfferClickEvent>() {
				@Override
				public void onChanged(NativeOfferClickEvent nativeOfferClickEvent) {

					NativeOffer nativeOffer = nativeOfferClickEvent.getNativeOffer();
					removeNativeOffer(nativeOffer);
					addNativeOffer();
					if (nativeOfferClickEvent.isDismissOnTap()) {
						new AlertDialog.Builder(MainActivity.this)
							.setTitle("Native Offer (" + nativeOffer.getTitle() + ")")
							.setMessage("You tapped on a native " + nativeOffer.getOfferType()
								+ " offer and the observer was notified.")
							.show();
					} else {
						Intent nativeOfferIntent = NativeOfferActivity
							.createIntent(MainActivity.this, nativeOffer.getTitle(),
								nativeOffer.getOfferType().toString());
						startActivity(nativeOfferIntent);
					}
				}
			};
		}
		return nativeOfferClickedObserver;
	}

	private void addNativeOffer(@NonNull NativeOffer nativeOffer, boolean dismissMarketPlaceOnTap) {
		try {
			if (Kin.addNativeOffer(nativeOffer, dismissMarketPlaceOnTap)) {
				showToast("Native offer added");
			} else {
				showToast("Could not add native offer");
			}
		} catch (ClientException e) {
			showSnackbar("ClientException  " + e.getMessage(), true);
			e.printStackTrace();
		}
	}

	private void getPublicAddress() {

		try {
			publicAddress = Kin.getPublicAddress();
		} catch (ClientException e) {
			showSnackbar("ClientException  " + e.getMessage(), true);
			//Account could not be found
			publicAddressTextArea.setText(e.getMessage());
			e.printStackTrace();
		}
		int blueColor = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
		publicAddressTextArea.getBackground().setColorFilter(blueColor, Mode.SRC_ATOP);
		showPublicAddressTextView.setText(R.string.copy_public_address);
		publicAddressTextArea.setText(publicAddress);
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
				showSnackbar("ClientException  " + e.getMessage(), true);
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
			showSnackbar("ClientException  " + e.getMessage(), true);
		}
	}

	private void setBalanceFailed() {
		balanceView.setText(R.string.failed_to_get_balance);
	}

	private void setBalanceWithAmount(Balance balance) {
		int balanceValue = balance.getAmount().intValue();
		balanceView.setText(getString(R.string.get_balance_d, balanceValue));
	}

	private void createNativeSpendOffer() {
		spendOfferID = String.valueOf(getRandomID());
		String offerJwt = JwtUtil.generateSpendOfferExampleJWT(BuildConfig.SAMPLE_APP_ID, userID, spendOfferID);
		Log.d(TAG, "createNativeSpendOffer: " + offerJwt);
		try {
			Kin.purchase(offerJwt, getNativeSpendOrderConfirmationCallback());
		} catch (ClientException e) {
			showSnackbar("ClientException  " + e.getMessage(), true);
			e.printStackTrace();
		}
	}

	private void createNativeEarnOffer() {
		String offerJwt = JwtUtil.generateEarnOfferExampleJWT(BuildConfig.SAMPLE_APP_ID, userID);
		try {
			Kin.requestPayment(offerJwt, getNativeEarnOrderConfirmationCallback());
		} catch (ClientException e) {
			showSnackbar("ClientException  " + e.getMessage(), true);
			e.printStackTrace();
		}
	}

	private void createPayToUserOffer(String recipientUserID) {
		String offerJwt = JwtUtil.generatePayToUserOfferExampleJWT(BuildConfig.SAMPLE_APP_ID, userID, recipientUserID);
		try {
			Kin.payToUser(offerJwt, getNativePayToUserOrderConfirmationCallback());
		} catch (ClientException e) {
			showSnackbar("ClientException  " + e.getMessage(), true);
			e.printStackTrace();
		}
	}

	private void showPayToUserDialog(final View v) {
		final PayToUserDialog payToUserDialog = new PayToUserDialog(v.getContext());
		payToUserDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (payToUserDialog.getUserId() != null) {
					showSnackbar("Pay to user flow started", false);
					final String userId = payToUserDialog.getUserId();
					try {
						Kin.hasAccount(userId, new KinCallback<Boolean>() {
							@Override
							public void onResponse(Boolean hasAccount) {
								if (hasAccount != null && hasAccount) {
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
						showSnackbar("ClientException  " + e.getMessage(), true);
						e.printStackTrace();
					}

				} else {
					enableView(v, true);
				}
			}
		});
		payToUserDialog.show();
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
				showSnackbar("ClientException  " + e.getMessage(), true);
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
					getOrderConfirmation(spendOfferID);
					spendOfferID = "";
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

	@NonNull
	public static String getAppId() {
		return BuildConfig.SAMPLE_APP_ID;
	}

	@NonNull
	public static String getApiKey() {
		return BuildConfig.SAMPLE_API_KEY;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		nativeSpendOrderConfirmationCallback = null;
		nativeEarnOrderConfirmationCallback = null;
		payToUserOrderConfirmationCallback = null;
		try {
			Kin.removeNativeOffer(nativeOffer);
			Kin.removeNativeOfferClickedObserver(nativeOfferClickedObserver);
		} catch (ClientException e) {
			showSnackbar("ClientException  " + e.getMessage(), true);
			e.printStackTrace();
		}
	}
}
