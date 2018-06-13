package com.ecosystem.kin.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.ecosystem.kin.app.model.SignInRepo;
import com.kin.ecosystem.KinCallback;
import com.kin.ecosystem.data.Callback;
import com.kin.ecosystem.Kin;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.data.model.Balance;
import com.kin.ecosystem.data.model.OrderConfirmation;
import com.kin.ecosystem.exception.ClientException;
import com.kin.ecosystem.exception.KinEcosystemException;
import com.kin.ecosystem.marketplace.model.NativeSpendOffer;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Ecosystem - SampleApp";

    private TextView balanceView;
    private Button nativeSpendButton;
    private Button nativeEarnButton;
    private Button showPublicAddressButton;
    private TextView publicAddressTextArea;

    private KinCallback<OrderConfirmation> nativeSpendOrderConfirmationCallback;
    private KinCallback<OrderConfirmation> nativeEarnOrderConfirmationCallback;
    private Observer<NativeSpendOffer> nativeSpendOfferClickedObserver;
    private Observer<Balance> balanceObserver;

    private String publicAddress;

    int randomID = new Random().nextInt((9999 - 1) + 1) + 1;
    NativeSpendOffer nativeSpendOffer =
        new NativeSpendOffer(String.valueOf(randomID))
            .title("Get Themes")
            .description("Personalize your chat")
            .amount(100)
            .image("https://s3.amazonaws.com/kinmarketplace-assets/version1/kik_theme_offer+2.png");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        balanceView = findViewById(R.id.get_balance);
        nativeSpendButton = findViewById(R.id.native_spend_button);
        nativeEarnButton = findViewById(R.id.native_earn_button);
        showPublicAddressButton = findViewById(R.id.show_public_address);
        publicAddressTextArea = findViewById(R.id.public_text_area);
        showPublicAddressButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (publicAddress == null) {
                    getPublicAddress();
                } else {
                    copyToClipboard(publicAddress);
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
        nativeSpendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Native spend flow started");
                enableView(v, false);
                createNativeSpendOffer();
            }
        });
        nativeEarnButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Native earn flow started");
                enableView(v, false);
                createNativeEarnOffer();
            }
        });
        findViewById(R.id.launch_marketplace).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openKinMarketplace();
            }
        });

        addNativeSpendOffer(nativeSpendOffer);
        addNativeOfferClickedObserver();
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
                    showToast("Balance - " + value.getAmount().intValue());
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
                showToast("Native offer removed");
            }
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    private void addNativeOfferClickedObserver() {
        try {
            Kin.addNativeOfferClickedObserver(getNativeOfferClickedObserver());
        } catch (ClientException e) {
            showToast("Could not add native offer callback");
        }
    }

    private Observer<NativeSpendOffer> getNativeOfferClickedObserver() {
        if (nativeSpendOfferClickedObserver == null) {
            nativeSpendOfferClickedObserver = new Observer<NativeSpendOffer>() {
                @Override
                public void onChanged(NativeSpendOffer value) {
                    Intent nativeOfferIntent = NativeOfferActivity.createIntent(MainActivity.this, value.getTitle());
                    startActivity(nativeOfferIntent);
                }
            };
        }
        return nativeSpendOfferClickedObserver;
    }

    private void addNativeSpendOffer(@NonNull NativeSpendOffer nativeSpendOffer) {
        try {
            if (Kin.addNativeOffer(nativeSpendOffer)) {
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
            int blueColor = ContextCompat.getColor(getApplicationContext(), R.color.sample_app_blue);
            publicAddressTextArea.getBackground().setColorFilter(blueColor, Mode.SRC_ATOP);
            showPublicAddressButton.setText(R.string.copy_public_address);
            publicAddressTextArea.setText(publicAddress);
        } catch (ClientException e) {
            e.printStackTrace();
        }

    }

    private void copyToClipboard(CharSequence textToCopy) {
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
        Toast.makeText(this, "Copied to your clipboard", Toast.LENGTH_SHORT).show();
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
                public void onFailure(KinEcosystemException error) {
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
        String userID = SignInRepo.getUserId(getApplicationContext());
        String offerJwt = JwtUtil.generateSpendOfferExampleJWT(BuildConfig.SAMPLE_APP_ID, userID);
        Log.d(TAG, "createNativeSpendOffer: " + offerJwt);
        try {
            Kin.purchase(offerJwt, getNativeSpendOrderConfirmationCallback());
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    private void createNativeEarnOffer() {
        String userID = SignInRepo.getUserId(getApplicationContext());
        String offerJwt = JwtUtil.generateEarnOfferExampleJWT(BuildConfig.SAMPLE_APP_ID, userID);
        try {
            Kin.requestPayment(offerJwt, getNativeEarnOrderConfirmationCallback());
        } catch (ClientException e) {
            e.printStackTrace();
        }
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
                        showToast("Offer: " + offerID + " Status is: " + orderConfirmation.getStatus());
                    }

                    @Override
                    public void onFailure(KinEcosystemException error) {
                        showToast("Failed to get OfferId: " + offerID + " status");
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
                    showToast("Succeed to create native spend");
                    Log.d(TAG, "Jwt confirmation: \n" + orderConfirmation.getJwtConfirmation());
                    enableView(nativeSpendButton, true);
                }

                @Override
                public void onFailure(KinEcosystemException error) {
                    showToast("Failed - " + error.getMessage());
                    enableView(nativeSpendButton, true);
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
                    showToast("Succeed to create native earn");
                    Log.d(TAG, "Jwt confirmation: \n" + orderConfirmation.getJwtConfirmation());
                    enableView(nativeEarnButton, true);
                }

                @Override
                public void onFailure(KinEcosystemException error) {
                    showToast("Failed - " + error.getMessage());
                    enableView(nativeEarnButton, true);
                }
            };
        }
        return nativeEarnOrderConfirmationCallback;
    }

    private void enableView(View v, boolean enable) {
        v.setEnabled(enable);
        v.setClickable(enable);
        v.setAlpha(enable ? 1f : 0.5f);
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        nativeSpendOrderConfirmationCallback = null;
        nativeEarnOrderConfirmationCallback = null;
        try {
            Kin.removeNativeOffer(nativeSpendOffer);
            Kin.removeNativeOfferClickedObserver(nativeSpendOfferClickedObserver);
        } catch (ClientException e) {
            Log.d(TAG, "onDestroy: Failed to remove native offer clicked observer");
        }
    }
}
