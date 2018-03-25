package com.kin.ecosystem.base;

import static com.kin.ecosystem.util.StringUtil.getAmountFormatted;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.LayoutRes;
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
import com.kin.ecosystem.util.DeviceUtils;
import com.squareup.picasso.Picasso;

public abstract class BottomDialog<T extends IBottomDialogPresenter> extends Dialog
    implements IBottomDialog<T>, OnClickListener {

    protected T presenter;

    protected TextView title;
    protected TextView description;
    protected Button bottomButton;
    protected ImageView closeButton;
    protected ImageView brandImage;

    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private static final float WIDTH_RATIO = 0.422f;
    private static final float HEIGHT_RATIO = 0.733f;

    private static final int NOT_INITIALIZED = -1;
    private static int imageWidth = NOT_INITIALIZED;
    private static int imageHeight = NOT_INITIALIZED;
    protected static int colorBlue = NOT_INITIALIZED;

    private int layoutRes = R.layout.dialog_base_bottom_layout;

    protected abstract void initViews();

    public BottomDialog(@NonNull Context context, @NonNull T presenter, @LayoutRes int layoutRes) {
        super(context, R.style.FullScreenDialogStyle);
        this.setUpWindowLayout();
        this.updateSizes();
        this.initColors();
        this.layoutRes = layoutRes;
        this.presenter = presenter;
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

    private void updateSizes() {
        if (imageWidth == NOT_INITIALIZED) {
            imageWidth = (int) (DeviceUtils.getScreenWidth() * WIDTH_RATIO);
        }
        if (imageHeight == NOT_INITIALIZED) {
            imageHeight = (int) (imageWidth * HEIGHT_RATIO);
        }
    }

    private void initColors() {
        if (colorBlue == NOT_INITIALIZED) {
            colorBlue = ContextCompat.getColor(getContext(), R.color.bluePrimary);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutRes);
        initBaseViews();
        initViews();
        attachPresenter(presenter);
    }

    private void initBaseViews() {
        bottomButton = findViewById(R.id.confirm_button);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        closeButton = findViewById(R.id.close_button);
        brandImage = findViewById(R.id.brand_image);

        ViewGroup.LayoutParams imageParams = brandImage.getLayoutParams();
        imageParams.width = imageWidth;
        imageParams.height = imageHeight;
        brandImage.setLayoutParams(imageParams);

        findViewById(R.id.close_button).setOnClickListener(this);
        bottomButton.setOnClickListener(this);
    }

    @Override
    public void attachPresenter(T presenter) {
        this.presenter = presenter;
        this.presenter.onAttach(this);
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

    public void setupTitle(String titleText) {
        title.setText(titleText);
    }

    public void setupTitle(String titleText, int amount) {
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

    @Override
    public void setUpButtonText(int stringRes) {
        bottomButton.setText(stringRes);
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
            presenter.closeClicked();
        } else if (id == R.id.confirm_button) {
            presenter.bottomButtonClicked();
        }
    }
}
