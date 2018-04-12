package com.ecosystem.kin.app.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.ecosystem.kin.app.R;
import com.kin.ecosystem.network.model.SignInData;
import com.kin.ecosystem.network.model.SignInData.SignInTypeEnum;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.EllipticCurveProvider;
import io.jsonwebtoken.impl.crypto.MacProvider;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.UUID;
import android.util.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class SignInRepo {
    static {
        Security.removeProvider("BC");
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

    private final static String USER_PREFERENCE_FILE_KEY = "USER_PREFERENCE_FILE_KEY";
    private final static String USER_UUID_KEY = "USER_UUID_KEY";
    private static final String DEVICE_UUID_KEY = "DEVICE_UUID_KEY";
    private static final long MILLISECONDS_IN_DAY = 1000*60*60*24;
    private static final String API_KEY = "A28hNcn2wp77QyaM8kB2C";
    private static final String ES256_PRIVATE_KEY = "MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgcok+N5GrcPumrW3hLb9pouIp0S4K\n"
        + "                                                            NWNRFUQk0dxMxISgCgYIKoZIzj0DAQehRANCAAQkfU8r3FLuA2d1a9lhNHr/xM7xArUTpO7Gffbl\n"
        + "                                                            XOoRXfbhJ+LVVyENo2S10oGvSxjfGpxrRfw4EPBlo+EnzTHk";
//    private static final String ES256_PRIVATE_KEY = "MIICWwIBAAKBgQDdlatRjRjogo3WojgGHFHYLugdUWAY9iR3fy4arWNA1KoS8kVw\n"
//        +"33cJibXr8bvwUAUparCwlvdbH6dvEOfou0/gCFQsHUfQrSDv+MuSUMAe8jzKE4qW\n"
//        +"+jK+xQU9a03GUnKHkkle+Q0pX/g6jXZ7r1/xAK5Do2kQ+X5xK9cipRgEKwIDAQAB\n"
//        +"AoGAD+onAtVye4ic7VR7V50DF9bOnwRwNXrARcDhq9LWNRrRGElESYYTQ6EbatXS\n"
//        +"3MCyjjX2eMhu/aF5YhXBwkppwxg+EOmXeh+MzL7Zh284OuPbkglAaGhV9bb6/5Cp\n"
//        +"uGb1esyPbYW+Ty2PC0GSZfIXkXs76jXAu9TOBvD0ybc2YlkCQQDywg2R/7t3Q2OE\n"
//        +"2+yo382CLJdrlSLVROWKwb4tb2PjhY4XAwV8d1vy0RenxTB+K5Mu57uVSTHtrMK0\n"
//        +"GAtFr833AkEA6avx20OHo61Yela/4k5kQDtjEf1N0LfI+BcWZtxsS3jDM3i1Hp0K\n"
//        +"Su5rsCPb8acJo5RO26gGVrfAsDcIXKC+bQJAZZ2XIpsitLyPpuiMOvBbzPavd4gY\n"
//        +"6Z8KWrfYzJoI/Q9FuBo6rKwl4BFoToD7WIUS+hpkagwWiz+6zLoX1dbOZwJACmH5\n"
//        +"fSSjAkLRi54PKJ8TFUeOP15h9sQzydI8zJU+upvDEKZsZc/UhT/SySDOxQ4G/523\n"
//        +"Y0sz/OZtSWcol/UMgQJALesy++GdvoIDLfJX5GBQpuFgFenRiRDabxrE9MNUZ2aP\n"
//        +"FaFp+DyAe+b4nDwuJaW2LURbr8AEZga7oQj0uYxcYw==";

    private static final String ES256_PUBLIC_KEY = "";




    public static SignInData getSignInData(Context context) {
        SignInData signInData;
        SharedPreferences sharedPreferences = context
            .getSharedPreferences(USER_PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString(USER_UUID_KEY, null);
        String deviceUUID = sharedPreferences.getString(DEVICE_UUID_KEY, null);
        if (deviceUUID == null) {
            deviceUUID = UUID.randomUUID().toString();
            sharedPreferences.edit().putString(DEVICE_UUID_KEY, deviceUUID).apply();
        }
        if (userID == null) {
            userID = UUID.randomUUID().toString();
            sharedPreferences.edit().putString(USER_UUID_KEY, userID).apply();
        }
        String apiKey = context.getResources().getString(R.string.sample_kin_ecosystem_api_key);
        String appID = context.getResources().getString(R.string.sample_app_id);

    /*    signInData = new SignInData()
            .signInType(SignInTypeEnum.WHITELIST)
            .appId(appID)
            .deviceId(deviceUUID)
            .userId(userID)
            .apiKey(apiKey);*/
        signInData = new SignInData()
            .signInType(SignInTypeEnum.JWT)
            .jwt(getJWT(userID, API_KEY))
            .appId(appID)
            .deviceId(deviceUUID)
            .userId(userID)
            .apiKey(API_KEY);

        return signInData;
    }

    private static String getJWT(String userID, String apiKey){

        PrivateKey privateKey = null;
        KeyFactory keyFactory = null;


        byte[] bytes = Base64.decode(ES256_PRIVATE_KEY, Base64.NO_WRAP);
        try {
            keyFactory = KeyFactory.getInstance("ECDSA", "BC");
            privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(bytes));

        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();

        }  catch (InvalidKeySpecException e){
            e.printStackTrace();

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }


//        KeyPair keyPair = EllipticCurveProvider.generateKeyPair(SignatureAlgorithm.ES256);
//        String privateKey =  Base64.encodeToString(keyPair.getPrivate().getEncoded(), Base64.DEFAULT);
//        String publicKey =  Base64.encodeToString(keyPair.getPublic().getEncoded(), Base64.DEFAULT);
//
//        Log.d("JWT", "private key : " + privateKey);
//        Log.d("JWT", "public key : " + publicKey);

        String jwt = Jwts.builder()
            .setHeaderParam("key_id", "1")
            .setIssuer("smpl")
            .setSubject("register")
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + MILLISECONDS_IN_DAY))
            .claim("user_id", userID)
            .claim("api_key",  apiKey)
            .signWith(SignatureAlgorithm.RS512, privateKey).compact();
        Log.d("JWT", "getJWT: jwt = " + jwt);
        return jwt;
    }




}
