package com.ecosystem.kin.app

import com.kin.ecosystem.JwtProvider

class KinJwtProvider(private val appId: String,
                     private val userId: String,
                     private val deviceId: String) : JwtProvider {

	override fun getPayToUserJwt(recipientUserID: String, amount: Double): String? = JwtUtil.generateGiftOfferExampleJWT(appId, userId, deviceId, recipientUserID, amount.toInt())
}