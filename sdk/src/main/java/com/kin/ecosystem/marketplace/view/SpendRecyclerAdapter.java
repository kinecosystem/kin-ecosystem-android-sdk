package com.kin.ecosystem.marketplace.view;

import static com.kin.ecosystem.core.util.DeviceUtils.DensityDpi.XXHDPI;

import com.kin.ecosystem.R;
import com.kin.ecosystem.core.util.DeviceUtils;

class SpendRecyclerAdapter extends OfferRecyclerAdapter {

    private static final float IMAGE_WIDTH_TO_SCREEN_RATIO = 0.833f;
    private static final float IMAGE_HEIGHT_RATIO = 0.333f;

	private static final float IMAGE_WIDTH_TO_SCREEN_XX_RES_RATIO = 0.73f;
	private static final float IMAGE_HEIGHT_XX_RES_RATIO = 0.3f;

    SpendRecyclerAdapter() {
        super(R.layout.kinecosystem_spend_recycler_item);
    }

    @Override
    protected float getImageWidthToScreenRatio() {
        return DeviceUtils.isDensity(XXHDPI) ? IMAGE_WIDTH_TO_SCREEN_XX_RES_RATIO : IMAGE_WIDTH_TO_SCREEN_RATIO;
    }

    @Override
    protected float getImageHeightRatio() {
        return DeviceUtils.isDensity(XXHDPI) ? IMAGE_HEIGHT_XX_RES_RATIO : IMAGE_HEIGHT_RATIO;
    }
}
