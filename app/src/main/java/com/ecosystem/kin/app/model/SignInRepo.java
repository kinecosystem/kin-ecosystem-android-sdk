package com.ecosystem.kin.app.model;

import static com.ecosystem.kin.app.JwtUtil.getRandomID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.provider.Settings.Secure;
import android.support.annotation.NonNull;
import com.ecosystem.kin.app.BuildConfig;
import com.ecosystem.kin.app.JwtUtil;
import com.kin.ecosystem.common.KinTheme;
import java.util.Locale;
import java.util.UUID;

public class SignInRepo {

    private final static String USER_PREFERENCE_FILE_KEY = "USER_PREFERENCE_FILE_KEY";
    private final static String USER_UUID_KEY = "USER_UUID_KEY";
    private final static String DEVICE_UUID_KEY = "DEVICE_UUID_KEY";

    private final static String KIN_THEME_KEY = "KIN_THEME_KEY";

    public static String getJWT(Context context) {
        return JwtUtil.generateSignInExampleJWT(getAppId(), getUserId(context), getDeviceId(context));
    }

    @NonNull
    private static String getAppId() {
        return BuildConfig.SAMPLE_APP_ID;
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context
            .getSharedPreferences(USER_PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
    }

    public static String getUserId(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
		return sharedPreferences.getString(USER_UUID_KEY, null);
    }

    public static void setUserId(Context context, String userId) {
        Editor editor = getSharedPreferences(context).edit();
        editor.putString(USER_UUID_KEY, userId).apply();
    }

    @NonNull
    public static String generateUserID() {
        return String.format(Locale.US,"user_%d", getRandomID());
    }

    public static String getDeviceId(Context context) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        String deviceID = sharedPreferences.getString(DEVICE_UUID_KEY, null);
        if (deviceID == null) {
            deviceID = getAndroidDeviceID(context);
            sharedPreferences.edit().putString(DEVICE_UUID_KEY, deviceID).apply();
        }
        return deviceID;
    }

	@SuppressLint("HardwareIds")
	private static String getAndroidDeviceID(Context context) {
    	String deviceId;
    	try {
			deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		} catch (Exception e) {
			deviceId = UUID.randomUUID().toString();
		}
		return deviceId;
	}

	public static void logout(Context context) {
		Editor editor = getSharedPreferences(context).edit();
		editor.remove(USER_UUID_KEY).apply();
	}

	public static void setKinTheme(Context context, String kinThemeName) {
		getSharedPreferences(context).edit().putString(KIN_THEME_KEY, kinThemeName).apply();
	}

	public static KinTheme getKinTheme(Context context) {
		return KinTheme.valueOf(getSharedPreferences(context).getString(KIN_THEME_KEY, KinTheme.LIGHT.name()));
	}
}
