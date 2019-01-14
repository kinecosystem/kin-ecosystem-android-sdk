package com.kin.ecosystem.core.data.auth

import android.text.format.DateUtils
import com.kin.ecosystem.common.Callback
import com.kin.ecosystem.common.KinCallback
import com.kin.ecosystem.common.exception.ClientException
import com.kin.ecosystem.common.exception.KinEcosystemException
import com.kin.ecosystem.core.network.ApiException
import com.kin.ecosystem.core.network.model.AccountInfo
import com.kin.ecosystem.core.network.model.AuthToken
import com.kin.ecosystem.core.network.model.UserProperties
import com.nhaarman.mockitokotlin2.*
import kin.ecosystem.test.base.BaseTestClass
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Instant


@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class AuthRepositoryTest : BaseTestClass() {

    companion object {
        const val APP_ID = "test"
        const val USER_ID_A = "alice"
        const val DEVICE_ID = "some_device_id"

        const val ECOSYSTEM_USER_ID_A = "ecosystem_alice"

        const val JWT_A = "eyJraWQiOiJyczUxMl8xIiwidHlwIjoiand0IiwiYWxnIjoiUlM1MTIifQ.eyJpYXQiOjE1NDczNzYyMjUsImlzcyI6InRlc3QiLCJleHAiOjE1NDc0NjI2MjUsInN1YiI6InJlZ2lzdGVyIiwidXNlcl9pZCI6ImFsaWNlIiwiZGV2aWNlX2lkIjoic29tZV9kZXZpY2VfaWQifQ.Qc7RmnF3bnWWAnLrZxzboroJeimPA1iYxPr3-rsHsLPboo5QI2zY-YpOsOzI1ULtEvODTKf6gfTTdRQokRkDtob4P9QRiWTQB4uDwmG1ReQ6IiswwMdka8B3rPQzhc-c2dYs6M3bFjUkawY9vHmSlLDaF_VmVrOxH7QfxSLQ9Gk"
        const val JWT_B = "eyJraWQiOiJyczUxMl8xIiwidHlwIjoiand0IiwiYWxnIjoiUlM1MTIifQ.eyJpYXQiOjE1NDczNzYzMTksImlzcyI6InRlc3QiLCJleHAiOjE1NDc0NjI3MTksInN1YiI6InJlZ2lzdGVyIiwidXNlcl9pZCI6ImJvYiIsImRldmljZV9pZCI6InNvbWVfZGV2aWNlX2lkIn0.QWrrdWUYKWY-XXoR30toIf0BLG_7bA8SLQbnHTUgZtfFdOMF-UHMe1CmOlQkxJbv6IJY2pZZKVFgjl_QxcR7klpOkR1DBjwFQnAAwnNdWlpNsRR_yQIeWULiq7Ic3633mhodxFE9xMFzAUSHuPvK9lY3aTPKTNCBECkKLnWsRLI"
        const val JWT_WRONG = "eyJhbGciOiJSUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.MejLezWY6hjGgbIXkq6Qbvx_-q5vWaTR6qPiNHphvla-XaZD3up1DN6Ib5AEOVtuB3fC9l-0L36noK4qQA79lhpSK3gozXO6XPIcCp4C8MU_ACzGtYe7IwGnnK3Emr6IHQE0bpGinHX1Ak1pAuwJNawaQ6Nvmz2ozZPsyxmiwoo"
    }

    private val local: AuthDataSource.Local = mock()
    private val remote: AuthDataSource.Remote = mock()


    private lateinit var authRepository: AuthDataSource


    @Before
    override fun setUp() {
        super.setUp()
        resetInstance()
    }

    @Throws(Exception::class)
    private fun resetInstance() {
        val instance = AuthRepository::class.java.getDeclaredField("instance")
        instance.isAccessible = true
        instance.set(null, null)
        AuthRepository.init(local, remote)
        authRepository = AuthRepository.getInstance()
    }

    @Test
    fun `set jwt on first time, local saved`() {
        whenever(local.userID) doAnswer { USER_ID_A }
        authRepository.setJWT(JWT_A)
        verify(local).setJWT(any())
    }

    @Test(expected = ClientException::class)
    fun `wrong jwt should throw exception`() {
        authRepository.setJWT(JWT_WRONG)
    }

    @Test
    fun `current userID is A, set jwt with different userId - B`() {
        whenever(local.userID) doAnswer { USER_ID_A }
        authRepository.setJWT(JWT_B)
        inOrder(local, remote) {
            verify(remote).logout(null)
            verify(local).setJWT(any())
        }
    }

    @Test
    fun `update wallet address correctly`() {
        val myAddress = "my_address_12"
        val updateWalletCaptor = argumentCaptor<UserProperties>()

        authRepository.updateWalletAddress(myAddress, object : KinCallback<Boolean> {
            override fun onResponse(response: Boolean?) {
                // no-op
            }

            override fun onFailure(exception: KinEcosystemException?) {
                // no-op
            }
        })

        verify(remote).updateWalletAddress(updateWalletCaptor.capture(), any())
        updateWalletCaptor.firstValue.apply {
            assertEquals(myAddress, this.walletAddress)
        }
    }

    @Test
    fun `get correct appID`() {
        whenever(local.appId) doAnswer { APP_ID }
        assertEquals(APP_ID, authRepository.appID)
        verify(local).appId
    }

    @Test
    fun `get correct deviceID`() {
        whenever(local.deviceID) doAnswer { DEVICE_ID }
        assertEquals(DEVICE_ID, authRepository.deviceID)
        verify(local).deviceID
    }

    @Test
    fun `get correct userID`() {
        whenever(local.userID) doAnswer { USER_ID_A }
        assertEquals(USER_ID_A, authRepository.userID)
        verify(local).userID
    }

    @Test
    fun `get correct ecosystemUserID`() {
        whenever(local.ecosystemUserID) doAnswer { ECOSYSTEM_USER_ID_A }
        assertEquals(ECOSYSTEM_USER_ID_A, authRepository.ecosystemUserID)
        verify(local).ecosystemUserID
    }

    @Test
    fun `get correct ecosystemUserID from cachedAuthToken`() {
        val token = getValidToken()
        whenever(local.authTokenSync) doAnswer { token }
        authRepository.authTokenSync

        assertEquals(ECOSYSTEM_USER_ID_A, authRepository.ecosystemUserID)
        verify(local, never()).ecosystemUserID
    }

    @Test
    fun `logout onResponse clears user info`() {
        val logoutCaptor = argumentCaptor<Callback<Void, ApiException>>()
        authRepository.logout()
        verify(remote).logout(logoutCaptor.capture())
        logoutCaptor.firstValue.onResponse(null)
        verify(local).logout()
    }

    @Test
    fun `logout onFailure clears user info`() {
        val logoutCaptor = argumentCaptor<Callback<Void, ApiException>>()
        authRepository.logout()
        verify(remote).logout(logoutCaptor.capture())
        logoutCaptor.firstValue.onFailure(null)
        verify(local).logout()
    }

    @Test
    fun `get auth token, jwt is empty fail callback with exception`() {
        val callbackCaptor: KinCallback<AuthToken> = mock()
        authRepository.getAuthToken(callbackCaptor)
        verify(callbackCaptor).onFailure(any<ClientException>())
    }

    @Test
    fun `get auth token, onResponse ok`() {
        val token = getValidToken()
        val accountInfo = AccountInfo()
        accountInfo.authToken = token;
        val callbackCaptor = argumentCaptor<Callback<AccountInfo, ApiException>>()
        val callback: KinCallback<AuthToken> = mock()

        authRepository.setJWT(JWT_A)
        authRepository.getAuthToken(callback)
        verify(remote).getAccountInfo(any(), callbackCaptor.capture())
        callbackCaptor.firstValue.apply {
            onResponse(accountInfo)
            verify(callback).onResponse(any())
        }
    }

    private fun getValidToken() : AuthToken {
        val tomorrow = Instant.now().plusMillis(DateUtils.DAY_IN_MILLIS).toString()
        return AuthToken("token", tomorrow , APP_ID, USER_ID_A, ECOSYSTEM_USER_ID_A)
    }
}