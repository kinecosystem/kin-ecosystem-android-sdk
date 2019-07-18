package com.kin.ecosystem

interface JwtProvider {

	/**
	 * Create a p2p JWT offer
	 *
	 * @param recipientUserID the recipient account user id
	 * @param amount the actual amount to send
	 *
	 * @return a signed JWT representing a p2p offer with the above values.
	 */
	fun getPayToUserJwt(recipientUserID: String, amount: Double) : String?
}