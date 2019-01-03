package com.kin.ecosystem.core.util

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
        const val TEST_JWT = "eyJraWQiOiJyczUxMl8wIiwidHlwIjoiand0IiwiYWxnIjoiUlM1MTIifQ.eyJpYXQiOjE1NDY1MTQ2ODEsImlzcyI6InRlc3QiLCJleHAiOjE1NDY2MDEwODEsInN1YiI6InJlZ2lzdGVyIiwidXNlcl9pZCI6IjY0MTkwZjA0LTdhNGUtNDMxYy1hZGE3LWJiZGJmZmRjMzM0MyIsImRldmljZV9pZCI6ImY3YWY2ODU5LTBiYzEtNDg1NS1iN2ViLWE2MDU4NDdiN2I5NSJ9.OyiOrkHpYiDy1B13uV26VDol2jQyTuIni3QfZxsXMtEQghV76CR17WxgpwF6rrbHOtrugV5eugqH9ZeDIvtGRhD13UY7koLXUghuI52Xn_WlNEcqfC2eULowc3SYVs1yYXprk0Mt1sW7BAuksnJDFYbcQmZQqWX1AFRtOrAla0Y"
        const val WRONG_JWT = "eyJhbGciOiJSUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.MejLezWY6hjGgbIXkq6Qbvx_-q5vWaTR6qPiNHphvla-XaZD3up1DN6Ib5AEOVtuB3fC9l-0L36noK4qQA79lhpSK3gozXO6XPIcCp4C8MU_ACzGtYe7IwGnnK3Emr6IHQE0bpGinHX1Ak1pAuwJNawaQ6Nvmz2ozZPsyxmiwoo"
    }

    @Test
    fun `decode jwt get correct attributes`() {
        val body = JwtDecoder.getJwtBody(TEST_JWT)
        assertEquals("test", body?.appId)
        assertEquals("64190f04-7a4e-431c-ada7-bbdbffdc3343", body?.userId)
        assertEquals("f7af6859-0bc1-4855-b7eb-a605847b7b95", body?.deviceId)
    }

    @Test(expected = JSONException::class)
    fun `jwt missing attrs, throw JSONException`() {
        JwtDecoder.getJwtBody(WRONG_JWT)
    }

    @Test
    fun `jwt too short not the jwt format, return null`() {
        assertNull(JwtDecoder.getJwtBody("jib"))
    }
}