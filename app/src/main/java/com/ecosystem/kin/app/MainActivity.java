package com.ecosystem.kin.app;

import android.content.Context;
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
import com.kin.ecosystem.Callback;
import com.kin.ecosystem.Kin;
import com.kin.ecosystem.exception.TaskFailedException;
import com.kin.ecosystem.marketplace.model.NativeSpendOffer;
import com.kin.ecosystem.network.model.Order.Status;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Ecosystem - SampleApp";

    private TextView balanceView;
    private Button nativeSpendButton;
    private Button showPublicAddressButton;
    private TextView publicAddressTextArea;

    private Callback<String> nativeSpendCallback;

    private String publicAddress;

    int randomID = new Random().nextInt((9999 - 1) + 1) + 1;
    NativeSpendOffer nativeOffer =
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
        findViewById(R.id.launch_marketplace).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openKinMarketplace();
            }
        });

        addNativeSpendOffer(nativeOffer);
    }

    // Use this method to remove the nativeOffer you added
    private void removeNativeOffer(@NonNull NativeSpendOffer nativeSpendOffer) {
        try {
            if (Kin.removeNativeOffer(nativeSpendOffer)) {
                showToast("Native offer removed");
            }
        } catch (TaskFailedException e) {
            e.printStackTrace();
        }
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
            showPublicAddressButton.setText("Copy Public Address");
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
        String offerJwt = JwtUtil.generateSpendOfferExampleJWT(BuildConfig.SAMPLE_APP_ID);
        Log.d(TAG, "createNativeSpendOffer: " + offerJwt);
        try {
            Kin.purchase(offerJwt, getNativeSpendCallback());
        } catch (TaskFailedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Use this method with the offerID you created, to get the order {@link Status}
     * @param offerID
     */
    private void getOrderStatus(@NonNull final String offerID) {
        if (!TextUtils.isEmpty(offerID)) {
            try {
                Kin.getOrderStatus(offerID, new Callback<Status>() {
                    @Override
                    public void onResponse(Status status) {
                        showToast("Offer: " + offerID + " Status is: " + status.getValue());
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

    private Callback<String> getNativeSpendCallback() {
        if (nativeSpendCallback == null) {
            nativeSpendCallback = new Callback<String>() {
                @Override
                public void onResponse(String jwtConfirmation) {
                    getBalance();
                    showToast("Succeed to create native spend");
                    Log.d(TAG, "Jwt confirmation: \n" + jwtConfirmation);
                    enableView(nativeSpendButton, true);
                }

                @Override
                public void onFailure(Throwable t) {
                    showToast("Failed - " + t.getMessage());
                    enableView(nativeSpendButton, true);
                }
            };
        }
        return nativeSpendCallback;
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
        nativeSpendCallback = null;
    }
}
