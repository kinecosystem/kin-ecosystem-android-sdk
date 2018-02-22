package com.ecosystem.kin.app.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.security.PrivateKey;
import java.util.UUID;

/**
 * Created by yohaybarski on 20/02/2018.
 */

public class UserRepo {

    private final static String USER_PREFERENCE_FILE_KEY = "USER_PREFERENCE_FILE_KEY";
    private final static String USER_UUID_KEY = "USER_UUID_KEY";
    private final static String TAG = UserRepo.class.getCanonicalName();


    public static User getUser(Context context) {
        User user;
        SharedPreferences sharedPreferences = context
            .getSharedPreferences(USER_PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString(USER_UUID_KEY, "");
        if (userID != "") {
            user = new User(userID);

        } else {
            UUID userUUID = UUID.randomUUID();
            sharedPreferences.edit().putString(USER_UUID_KEY, userUUID.toString()).commit();
            user = new User(userUUID);
        }
        return user;
    }


}
