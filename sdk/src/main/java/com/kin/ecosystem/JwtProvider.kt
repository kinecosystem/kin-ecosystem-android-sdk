package com.kin.ecosystem

interface JwtProvider {

	fun getPayToUserJwt(recipientUserID: String, amount: Double) : String?
}