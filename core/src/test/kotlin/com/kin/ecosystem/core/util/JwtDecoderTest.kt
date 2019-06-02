package com.kin.ecosystem.core.util

import com.kin.ecosystem.core.network.model.Offer
import org.json.JSONException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class JwtDecoderTest {

    companion object {
        const val TEST_JWT_LOGIN = "eyJraWQiOiJyczUxMl8wIiwidHlwIjoiand0IiwiYWxnIjoiUlM1MTIifQ.eyJpYXQiOjE1NDY1MTQ2ODEsImlzcyI6InRlc3QiLCJleHAiOjE1NDY2MDEwODEsInN1YiI6InJlZ2lzdGVyIiwidXNlcl9pZCI6IjY0MTkwZjA0LTdhNGUtNDMxYy1hZGE3LWJiZGJmZmRjMzM0MyIsImRldmljZV9pZCI6ImY3YWY2ODU5LTBiYzEtNDg1NS1iN2ViLWE2MDU4NDdiN2I5NSJ9.OyiOrkHpYiDy1B13uV26VDol2jQyTuIni3QfZxsXMtEQghV76CR17WxgpwF6rrbHOtrugV5eugqH9ZeDIvtGRhD13UY7koLXUghuI52Xn_WlNEcqfC2eULowc3SYVs1yYXprk0Mt1sW7BAuksnJDFYbcQmZQqWX1AFRtOrAla0Y"
        const val WRONG_JWT_LOGIN = "eyJhbGciOiJSUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.MejLezWY6hjGgbIXkq6Qbvx_-q5vWaTR6qPiNHphvla-XaZD3up1DN6Ib5AEOVtuB3fC9l-0L36noK4qQA79lhpSK3gozXO6XPIcCp4C8MU_ACzGtYe7IwGnnK3Emr6IHQE0bpGinHX1Ak1pAuwJNawaQ6Nvmz2ozZPsyxmiwoo"
        const val TEST_JWT_ORDER = "eyJraWQiOiJyczUxMl8xIiwidHlwIjoiand0IiwiYWxnIjoiUlM1MTIifQ.eyJpYXQiOjE1NTkxMzIzNzYsImlzcyI6InNtcGwiLCJleHAiOjE1NTkyMjIzNzYsInN1YiI6ImVhcm4iLCJvZmZlciI6eyJhbW91bnQiOjEwLCJpZCI6IjE4MTkwMCJ9LCJyZWNpcGllbnQiOnsiZGVzY3JpcHRpb24iOiJVcGxvYWQgcHJvZmlsZSBwaWN0dXJlIiwiZGV2aWNlX2lkIjoiNTNmNmM2Zjc5NjEzMWY0NiIsInRpdGxlIjoiUmVjZWl2ZWQgS2luIGZyb20gQXZpc2h5IiwidXNlcl9pZCI6InVzZXJfMTQxNzQ5In19.bY_c-mpDzUPNKOajfnAH4To1-9-tl_IekWHVeCSKMByF3dTUbywu4bOe2drlbX4Grd29fb538TlBCaZSMU9ask7LhVIf7YPoiU-PfkAoX4OWByyKTRp_gVRtsee560O26G-7Bh-5l524mGAJgYsEkvMsojVLcSYwZ9HMZTlVurM"
    }

    @Test
    fun `decode login jwt get correct attributes`() {
        val body = JwtDecoder.getJwtBody(TEST_JWT_LOGIN)
        assertEquals("test", body?.appId)
        assertEquals("64190f04-7a4e-431c-ada7-bbdbffdc3343", body?.userId)
        assertEquals("f7af6859-0bc1-4855-b7eb-a605847b7b95", body?.deviceId)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `The string is not a jwt encoded, throw Exception`() {
        JwtDecoder.getJwtBody("some.wrong.jwt")
    }

    @Test(expected = JSONException::class)
    fun `jwt missing attrs, throw JSONException`() {
        JwtDecoder.getJwtBody(WRONG_JWT_LOGIN)
    }

    @Test
    fun `jwt too short not the jwt format, return null`() {
        assertNull(JwtDecoder.getJwtBody("jib"))
    }

    @Test
    fun `decode order jwt get correct attributes`() {
        val body = JwtDecoder.getOfferJwtBody(TEST_JWT_ORDER)
        assertEquals("181900", body?.offerId)
        assertEquals(Offer.OfferType.EARN, body?.type)
    }
}