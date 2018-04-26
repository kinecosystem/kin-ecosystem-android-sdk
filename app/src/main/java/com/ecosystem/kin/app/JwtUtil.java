package com.ecosystem.kin.app;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.Random;

public class JwtUtil {

    private static final String ALGORITHM_RSA = "RSA";
    private static final String SECURITY_PROVIDER_BC = "BC";

    private static final long DAY_IN_MILLISECONDS = 1000 * 60 * 60 * 24;

    private static final String JWT_CLAIM_OBJECT_OFFER = "offer";

    private static final String JWT_SUBJECT_REGISTER = "register";
    private static final String JWT_SUBJECT_SPEND = "spend";

    private static final String JWT_KEY_USER_ID = "user_id";

    private static final String JWT_HEADER_KID = "kid";
    private static final String JWT_HEADER_TYP = "typ";

    private static final String JWT = "jwt";


    public static String generateSignInExampleJWT(String appID, String userId) {
        String jwt = getBasicJWT(appID)
            .setSubject(JWT_SUBJECT_REGISTER)
            .claim(JWT_KEY_USER_ID, userId)
            .signWith(SignatureAlgorithm.RS512, getRS512PrivateKey()).compact();
        return jwt;
    }

    public static String generateSpendOfferExampleJWT(String appID) {
        String jwt = getBasicJWT(appID)
            .setSubject(JWT_SUBJECT_SPEND)
            .claim(JWT_CLAIM_OBJECT_OFFER, createSpendOfferExampleObject())
            .signWith(SignatureAlgorithm.RS512, getRS512PrivateKey()).compact();
        return jwt;
    }

    @NonNull
    private static String getPrivateKeyForJWT() {
        return BuildConfig.RS512_PRIVATE_KEY;
    }

    private static JwtBuilder getBasicJWT(String appID) {
        return Jwts.builder().setHeaderParam(JWT_HEADER_KID, "1")
            .setHeaderParam(JWT_HEADER_TYP, JWT)
            .setIssuedAt(new Date())
            .setIssuer(appID)
            .setExpiration(new Date(System.currentTimeMillis() + DAY_IN_MILLISECONDS));
    }

    @Nullable
    private static PrivateKey getRS512PrivateKey() {
        PrivateKey privateKey = null;
        KeyFactory keyFactory;

        byte[] bytes = Base64.decode(getPrivateKeyForJWT(), Base64.NO_WRAP);
        try {
            keyFactory = KeyFactory.getInstance(ALGORITHM_RSA, SECURITY_PROVIDER_BC);
            privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(bytes));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();

        } catch (InvalidKeySpecException e) {
            e.printStackTrace();

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    private static JWTSpendOffer createSpendOfferExampleObject() {
        int randomID = new Random().nextInt((9999 - 1) + 1) + 1;
        return new JWTSpendOffer(String.valueOf(randomID),
            "Set profile picture",
            "Get superpowers in real life",
            10,
            "GDSXU3DVE3M3CTWXGN3DOLJHRDQLII3WV3GUGK5ZDQGWWDHKJFHRVGNB");
    }

    private static class JWTSpendOffer {

        private String id;
        private String title;
        private String description;
        private int amount;
        private String wallet_address;

        /**
         * All of these fields are REQUIRED in order to succeed.
         *
         * @param id decided by you (internal)
         * @param title appears in order history page
         * @param description appears in order history page
         * @param amount of KIN for this offer / (price)
         * @param wallet_address address the client should send kin to to acquire this offer
         */
        public JWTSpendOffer(String id, String title, String description, int amount, String wallet_address) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.amount = amount;
            this.wallet_address = wallet_address;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public String getWallet_address() {
            return wallet_address;
        }

        public void setWallet_address(String wallet_address) {
            this.wallet_address = wallet_address;
        }
    }
}
