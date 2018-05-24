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
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.Kin;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.data.model.OrderConfirmation;
import com.kin.ecosystem.exception.TaskFailedException;
import com.kin.ecosystem.marketplace.model.NativeSpendOffer;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Ecosystem - SampleApp";

    private TextView balanceView;
    private Button nativeSpendButton;
    private Button nativeEarnButton;
    private Button showPublicAddressButton;
    private TextView publicAddressTextArea;

    private Callback<OrderConfirmation> nativeSpendOrderConfirmationCallback;
    private Callback<OrderConfirmation> nativeEarnOrderConfirmationCallback;
    private Observer<NativeSpendOffer> nativeSpendOfferClickedObserver;

    private String publicAddress;

    int randomID = new Random().nextInt((9999 - 1) + 1) + 1;
    NativeSpendOffer nativeSpendOffer =
        new NativeSpendOffer(String.valueOf(randomID))
            .title("Get Themes")
            .description("Personalize your chat")
            .amount(1000)
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

    // Use this method to remove the nativeSpendOffer you added
    private void removeNativeOffer(@NonNull NativeSpendOffer nativeSpendOffer) {
        try {
            if (Kin.removeNativeOffer(nativeSpendOffer)) {
                showToast("Native offer removed");
            }
        } catch (TaskFailedException e) {
            e.printStackTrace();
        }
    }

    private void addNativeOfferClickedObserver() {
        try {
            Kin.addNativeOfferClickedObserver(getNativeOfferClickedObserver());
        } catch (TaskFailedException e) {
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
        } catch (TaskFailedException e) {
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
        } catch (TaskFailedException e) {
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
            Kin.getBalance(new Callback<Integer>() {
                @Override
                public void onResponse(Integer balance) {
                    enableView(balanceView, true);
                    balanceView.setText(getString(R.string.get_balance_d, balance));
                }

                @Override
                public void onFailure(Throwable t) {
                    enableView(balanceView, true);
                    balanceView.setText(R.string.failed_to_get_balance);
                }
            });
        } catch (TaskFailedException e) {
            balanceView.setText(R.string.failed_to_get_balance);
            e.printStackTrace();
        }
    }

    private void openKinMarketplace() {
        try {
            Kin.launchMarketplace(MainActivity.this);
        } catch (TaskFailedException e) {
            e.printStackTrace();
        }
    }

    private void createNativeSpendOffer() {
        String userID = SignInRepo.getUserId(getApplicationContext());
        String offerJwt = JwtUtil.generateSpendOfferExampleJWT(BuildConfig.SAMPLE_APP_ID, userID);
        Log.d(TAG, "createNativeSpendOffer: " + offerJwt);
        try {
            Kin.purchase(offerJwt, getNativeSpendOrderConfirmationCallback());
        } catch (TaskFailedException e) {
            e.printStackTrace();
        }
    }

    private void createNativeEarnOffer() {
        String userID = SignInRepo.getUserId(getApplicationContext());
        String offerJwt = JwtUtil.generateEarnOfferExampleJWT(BuildConfig.SAMPLE_APP_ID, userID);
        try {
            Kin.requestPayment(offerJwt, getNativeEarnOrderConfirmationCallback());
        } catch (TaskFailedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Use this method with the offerID you created, to get {@link OrderConfirmation}
     */
    private void getOrderConfirmation(@NonNull final String offerID) {
        if (!TextUtils.isEmpty(offerID)) {
            try {
                Kin.getOrderConfirmation(offerID, new Callback<OrderConfirmation>() {
                    @Override
                    public void onResponse(OrderConfirmation orderConfirmation) {
                        showToast("Offer: " + offerID + " Status is: " + orderConfirmation.getStatus());
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        showToast("Failed to get OfferId: " + offerID + " status");
                    }
                });
            } catch (TaskFailedException e) {
                e.printStackTrace();
            }
        }
    }

    private Callback<OrderConfirmation> getNativeSpendOrderConfirmationCallback() {
        if (nativeSpendOrderConfirmationCallback == null) {
            nativeSpendOrderConfirmationCallback = new Callback<OrderConfirmation>() {
                @Override
                public void onResponse(OrderConfirmation orderConfirmation) {
                    getBalance();
                    showToast("Succeed to create native spend");
                    Log.d(TAG, "Jwt confirmation: \n" + orderConfirmation.getJwtConfirmation());
                    enableView(nativeSpendButton, true);
                }

                @Override
                public void onFailure(Throwable t) {
                    showToast("Failed - " + t.getMessage());
                    enableView(nativeSpendButton, true);
                }
            };
        }
        return nativeSpendOrderConfirmationCallback;
    }

    private Callback<OrderConfirmation> getNativeEarnOrderConfirmationCallback() {
        if (nativeEarnOrderConfirmationCallback == null) {
            nativeEarnOrderConfirmationCallback = new Callback<OrderConfirmation>() {
                @Override
                public void onResponse(OrderConfirmation orderConfirmation) {
                    getBalance();
                    showToast("Succeed to create native earn");
                    Log.d(TAG, "Jwt confirmation: \n" + orderConfirmation.getJwtConfirmation());
                    enableView(nativeEarnButton, true);
                }

                @Override
                public void onFailure(Throwable t) {
                    showToast("Failed - " + t.getMessage());
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
        } catch (TaskFailedException e) {
            Log.d(TAG, "onDestroy: Failed to remove native offer clicked observer");
        }
    }
}
