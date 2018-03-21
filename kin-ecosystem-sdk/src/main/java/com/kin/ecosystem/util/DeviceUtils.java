package com.kin.ecosystem.util;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.IntDef;
import android.util.DisplayMetrics;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static android.util.DisplayMetrics.DENSITY_HIGH;
import static android.util.DisplayMetrics.DENSITY_XHIGH;
import static android.util.DisplayMetrics.DENSITY_XXHIGH;
import static com.kin.ecosystem.util.DeviceUtils.DensityDpi.HDPI;
import static com.kin.ecosystem.util.DeviceUtils.DensityDpi.XHDPI;
import static com.kin.ecosystem.util.DeviceUtils.DensityDpi.XXHDPI;

public class DeviceUtils {


    @IntDef({HDPI, XHDPI, XXHDPI})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DensityDpi {

        int HDPI = 0x00000240;
        int XHDPI = 0x00000320;
        int XXHDPI = 0x00000480;
    }

    private static @DensityDpi
    int densityDpi = HDPI;
    private static int screenHeight;
    private static int screenWidth;

    public static void init(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int orientation = context.getResources().getConfiguration().orientation;
        checkDensityDpi(displayMetrics);
        checkScreenSize(orientation, displayMetrics);
    }

    private static void checkDensityDpi(DisplayMetrics displayMetrics) {
        int dpi = displayMetrics.densityDpi;
        if (dpi <= DENSITY_HIGH) {
            densityDpi = HDPI;
        } else if (dpi >= DENSITY_XHIGH && dpi <= DENSITY_XXHIGH) {
            densityDpi = XHDPI;
        } else {
            densityDpi = XXHDPI;
        }
    }

    private static void checkScreenSize(int orientation, DisplayMetrics displayMetrics) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            screenHeight = displayMetrics.widthPixels;
            screenWidth = displayMetrics.heightPixels;
        } else {
            screenHeight = displayMetrics.heightPixels;
            screenWidth = displayMetrics.widthPixels;
        }
    }


    public static boolean isDensity(@DensityDpi int dpi) {
        return densityDpi == dpi;
    }

    public static int getScreenHeight() {
        return screenHeight;
    }

    public static int getScreenWidth() {
        return screenWidth;
    }

}
