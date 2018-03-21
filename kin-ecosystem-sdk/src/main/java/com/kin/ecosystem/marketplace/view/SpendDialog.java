package com.kin.ecosystem.marketplace.view;

import static com.kin.ecosystem.util.StringUtil.getAmountFormatted;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.kin.ecosystem.R;
import com.kin.ecosystem.marketplace.presenter.ISpendDialogPresenter;
import com.kin.ecosystem.util.DeviceUtils;
import com.squareup.picasso.Picasso;


public class SpendDialog extends Dialog implements ISpendDialog, OnClickListener {

    private ISpendDialogPresenter spendDialogPresenter;

    private TextView title;
    private TextView description;
    private Button confirmButton;
    private ImageView confirmationImage;
    private ImageView closeButton;
    private ImageView brandImage;

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private static final float WIDTH_RATIO = 0.422f;
    private static final float HEIGHT_RATIO = 0.733f;

    private static final int NOT_INITIALIZED = -1;
    private static int imageWidth = NOT_INITIALIZED;
    private static int imageHeight = NOT_INITIALIZED;
    private static int colorBlue = NOT_INITIALIZED;

    public SpendDialog(@NonNull Context context, @NonNull ISpendDialogPresenter presenter) {
        super(context, R.style.FullScreenDialogStyle);
        setUpWindowLayout();
        updateSizes();
        spendDialogPresenter = presenter;
    }

    private void setUpWindowLayout() {
        LayoutParams newParams = new LayoutParams();
        newParams.copyFrom(getWindow().getAttributes());
        newParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        newParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        newParams.gravity = Gravity.BOTTOM;
        newParams.windowAnimations = R.style.BottomAnimationDialog;
        getWindow().setAttributes(newParams);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_spend);
        initViews();
        attachPresenter(spendDialogPresenter);
    }

    private void initViews() {
        confirmButton = findViewById(R.id.confirm_button);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        confirmationImage = findViewById(R.id.confirmation_image);
        closeButton = findViewById(R.id.close_button);
        brandImage = findViewById(R.id.brand_image);

        ViewGroup.LayoutParams imageParams = brandImage.getLayoutParams();
        imageParams.width = imageWidth;
        imageParams.height = imageHeight;
        brandImage.setLayoutParams(imageParams);

        findViewById(R.id.close_button).setOnClickListener(this);
        confirmButton.setOnClickListener(this);
    }

    @Override
    public void attachPresenter(ISpendDialogPresenter presenter) {
        spendDialogPresenter = presenter;
        spendDialogPresenter.onAttach(this);
    }

    @Override
    public void closeDialog() {
        dismiss();
    }

    public void setupImage(String image) {
        Picasso.with(getContext())
            .load(image)
            .placeholder(R.drawable.placeholder)
            .fit()
            .into(brandImage);
    }

    public void setupTitle(String titleText, int amount) {
        if (colorBlue == NOT_INITIALIZED) {
            colorBlue = ContextCompat.getColor(getContext(), R.color.bluePrimary);
        }
        final String delimiter = " - ";
        final String amountKin = getAmountFormatted(amount) + " Kin";
        SpannableStringBuilder spannableTitleBuilder = new SpannableStringBuilder(titleText);
        spannableTitleBuilder.append(delimiter).append(amountKin);
        spannableTitleBuilder.setSpan(new ForegroundColorSpan(colorBlue), titleText.length() + delimiter.length(),
            spannableTitleBuilder.length(),
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setText(spannableTitleBuilder);
    }

    public void setupDescription(String descriptionText) {
        description.setText(descriptionText);
    }

    private void updateSizes() {
        if (imageWidth == -1) {
            imageWidth = (int) (DeviceUtils.getScreenWidth() * WIDTH_RATIO);
        }
        if (imageHeight == -1) {
            imageHeight = (int) (imageWidth * HEIGHT_RATIO);
        }
    }

    @Override
    public void showThankYouLayout(@NonNull final String title, @NonNull final String description) {
        this.title.setText(title);
        this.description.setText(description);
        this.confirmButton.setVisibility(View.INVISIBLE);
        this.confirmationImage.setVisibility(View.VISIBLE);
        this.closeButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showToast(final String msg) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.close_button) {
            spendDialogPresenter.closeClicked();
        } else if (id == R.id.confirm_button) {
            spendDialogPresenter.confirmClicked();
        }
    }
}
