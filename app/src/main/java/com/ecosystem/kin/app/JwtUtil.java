package com.ecosystem.kin.app;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Base64;
import com.fasterxml.jackson.annotation.JsonProperty;
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

	private static final String JWT_CLAIM_OBJECT_OFFER_PART = "offer";
	private static final String JWT_CLAIM_OBJECT_SENDER_PART = "sender"; // Should be part of native SPEND jwt
	private static final String JWT_CLAIM_OBJECT_RECIPIENT_PART = "recipient"; // Should be part of native EARN jwt

	private static final String JWT_SUBJECT_REGISTER = "register";
	private static final String JWT_SUBJECT_SPEND = "spend";
	private static final String JWT_SUBJECT_EARN = "earn";
	private static final String JWT_SUBJECT_PAY_TO_USER = "pay_to_user";

	private static final String JWT_KEY_USER_ID = "user_id";
	private static final String JWT_KEY_DEVICE_ID = "device_id";

	private static final String JWT_HEADER_KID = "kid";
	private static final String JWT_HEADER_TYP = "typ";

	private static final String JWT = "jwt";


	public static String generateSignInExampleJWT(String appID, String userId, String deviceId) {
		return getBasicJWT(appID)
			.setSubject(JWT_SUBJECT_REGISTER)
			.claim(JWT_KEY_USER_ID, userId)
			.claim(JWT_KEY_DEVICE_ID, deviceId)
			.signWith(SignatureAlgorithm.RS512, getRS512PrivateKey()).compact();
	}

	public static String generateSpendOfferExampleJWT(String appID, String userID, String deviceId, String offerID) {
		return getBasicJWT(appID)
			.setSubject(JWT_SUBJECT_SPEND)
			.claim(JWT_CLAIM_OBJECT_OFFER_PART, createOfferPartExampleObject(offerID))
			.claim(JWT_CLAIM_OBJECT_SENDER_PART,
				new JWTSenderPart(userID, deviceId, "Bought a sticker", "Lion sticker"))
			.signWith(SignatureAlgorithm.RS512, getRS512PrivateKey()).compact();
	}

	public static String generateEarnOfferExampleJWT(String appID, String userID, String deviceId) {
		return getBasicJWT(appID)
			.setSubject(JWT_SUBJECT_EARN)
			.claim(JWT_CLAIM_OBJECT_OFFER_PART, createOfferPartExampleObject())
			.claim(JWT_CLAIM_OBJECT_RECIPIENT_PART,
				new JWTRecipientPart(userID, deviceId, "Received Kin", "upload profile picture"))
			.signWith(SignatureAlgorithm.RS512, getRS512PrivateKey()).compact();
	}

	public static String generatePayToUserOfferExampleJWT(String appID, String userID, String deviceId,
		String recipientUserID) {
		return getBasicJWT(appID)
			.setSubject(JWT_SUBJECT_PAY_TO_USER)
			.claim(JWT_CLAIM_OBJECT_OFFER_PART, createOfferPartExampleObject())
			.claim(JWT_CLAIM_OBJECT_SENDER_PART, new JWTSenderPart(userID, deviceId, "Tip to someone", "Code review"))
			.claim(JWT_CLAIM_OBJECT_RECIPIENT_PART,
				new JWTRecipientPart(recipientUserID, "Tip from someone", "Code review"))
			.signWith(SignatureAlgorithm.RS512, getRS512PrivateKey()).compact();
	}

	@NonNull
	private static String getPrivateKeyForJWT() {
		return BuildConfig.RS512_PRIVATE_KEY;
	}

	private static JwtBuilder getBasicJWT(String appID) {
		return Jwts.builder().setHeaderParam(JWT_HEADER_KID, BuildConfig.RS512_PRIVATE_KEY_ID)
			.setHeaderParam(JWT_HEADER_TYP, JWT)
			.setIssuedAt(new Date(new Date().getTime() - (DateUtils.HOUR_IN_MILLIS)))
			.setIssuer(appID)
			.setExpiration(new Date(System.currentTimeMillis() + DateUtils.DAY_IN_MILLIS));
	}

	@Nullable
	private static PrivateKey getRS512PrivateKey() {
		PrivateKey privateKey = null;
		KeyFactory keyFactory;

		byte[] bytes = Base64.decode(getPrivateKeyForJWT(), Base64.NO_WRAP);
		try {
			keyFactory = KeyFactory.getInstance(ALGORITHM_RSA, SECURITY_PROVIDER_BC);
			privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(bytes));

		} catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException e) {
			e.printStackTrace();
		}
		return privateKey;
	}

	private static JWTOfferPart createOfferPartExampleObject() {
		int randomID = getRandomID();
		return new JWTOfferPart(String.valueOf(randomID), 10);
	}

	private static JWTOfferPart createOfferPartExampleObject(String offerId) {
		return new JWTOfferPart(offerId, 10);
	}

	public static int getRandomID() {
		return new Random().nextInt((999999 - 1) + 1) + 1;
	}

	private static class JWTOfferPart {


		@JsonProperty("id")
		private String id;
		@JsonProperty("amount")
		private int amount;

		/**
		 * These fields are REQUIRED in order to succeed.
		 *
		 * @param id decided by you (internal)
		 * @param amount of KIN for this offer / (price)
		 */
		public JWTOfferPart(String id, int amount) {
			this.id = id;
			this.amount = amount;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public int getAmount() {
			return amount;
		}

		public void setAmount(int amount) {
			this.amount = amount;
		}
	}

	private static class JWTOrderPart {

		@JsonProperty("user_id")
		private String user_id;
		@JsonProperty("device_id")
		private String device_id;
		@JsonProperty("title")
		private String title;
		@JsonProperty("description")
		private String description;

		JWTOrderPart(String user_id, String device_id, String title, String description) {
			this.user_id = user_id;
			this.device_id = device_id;
			this.title = title;
			this.description = description;
		}

		JWTOrderPart(String user_id, String title, String description) {
			this.user_id = user_id;
			this.title = title;
			this.description = description;
		}

		public String getUser_id() {
			return user_id;
		}

		public void setUser_id(String user_id) {
			this.user_id = user_id;
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

		public String getDevice_id() {
			return device_id;
		}

		public void setDevice_id(String device_id) {
			this.device_id = device_id;
		}
	}

	private static class JWTSenderPart extends JWTOrderPart {

		JWTSenderPart(String user_id, String device_id, String title, String description) {
			super(user_id, device_id, title, description);
		}
	}

	private static class JWTRecipientPart extends JWTOrderPart {

		JWTRecipientPart(String user_id, String title, String description) {
			super(user_id, title, description);
		}

		JWTRecipientPart(String user_id, String device_id, String title, String description) {
			super(user_id, device_id, title, description);
		}
	}
}
