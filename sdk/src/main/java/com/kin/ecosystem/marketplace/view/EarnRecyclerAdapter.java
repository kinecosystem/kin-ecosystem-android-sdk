package com.kin.ecosystem.marketplace.view;

import static com.kin.ecosystem.core.util.DeviceUtils.DensityDpi.XXHDPI;

import com.kin.ecosystem.R;
import com.kin.ecosystem.core.util.DeviceUtils;

class EarnRecyclerAdapter extends OfferRecyclerAdapter {

    private static final float IMAGE_WIDTH_TO_SCREEN_RATIO = 0.388f;
    private static final float IMAGE_HEIGHT_RATIO = 0.714f;

	private static final float IMAGE_WIDTH_XX_RES_TO_SCREEN_RATIO = 0.333f;

    EarnRecyclerAdapter() {
        super(R.layout.kinecosystem_earn_recycler_item);
    }

	@Override
	protected float getImageWidthToScreenRatio() {
		return DeviceUtils.isDensity(XXHDPI) ? IMAGE_WIDTH_XX_RES_TO_SCREEN_RATIO : IMAGE_WIDTH_TO_SCREEN_RATIO;
	}

	@Override
	protected float getImageHeightRatio() {
		return IMAGE_HEIGHT_RATIO;
	}
}
