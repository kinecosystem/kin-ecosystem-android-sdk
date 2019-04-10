package com.kin.ecosystem.core.accountmanager

import com.kin.ecosystem.common.KinCallback
import com.kin.ecosystem.common.Observer
import com.kin.ecosystem.core.accountmanager.AccountManager.*
import com.kin.ecosystem.core.bi.EventLogger
import com.kin.ecosystem.core.data.auth.AuthDataSource
import com.kin.ecosystem.core.data.blockchain.BlockchainSource
import com.kin.ecosystem.core.network.model.AccountInfo
import com.nhaarman.mockitokotlin2.*
import kin.ecosystem.test.base.BaseTestClass
import kin.sdk.migration.common.interfaces.IKinAccount
import kin.sdk.migration.common.interfaces.IListenerRegistration
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class AccountManagerImplTest : BaseTestClass() {

    private val local: AccountManager.Local = mock {
        on { accountState } doAnswer { REQUIRE_CREATION }
    }

    private val eventLogger: EventLogger = mock()
    private val authRepository: AuthDataSource = mock {
        val authTokenCaptor = argumentCaptor<KinCallback<AccountInfo>>()
        on { getAccountInfo(authTokenCaptor.capture()) } doAnswer { authTokenCaptor.firstValue.onResponse(any()) }
    }

    private val blockchainSource: BlockchainSource = mock {
        val trustlineCaptor = argumentCaptor<KinCallback<Void>>()
        on { createTrustLine(trustlineCaptor.capture()) } doAnswer { trustlineCaptor.firstValue.onResponse(null) }
        on { kinAccount } doAnswer { kinAccount }
    }

    private val accountCreationRegistration: IListenerRegistration = mock()
    private val kinAccount: IKinAccount = mock {
        on { addAccountCreationListener(any()) } doAnswer { accountCreationRegistration }
    }

    private lateinit var accountManager: AccountManager

    @Before
    override fun setUp() {
        super.setUp()
        resetInstance()
    }

    @Throws(Exception::class)
    private fun resetInstance() {
        val instance = AccountManagerImpl::class.java.getDeclaredField("instance")
        instance.apply {
            isAccessible = true
            set(null, null)
        }
    }

    private fun initWithFirstState(@AccountState state: Int) {
        whenever(local.accountState).thenReturn(state)
        AccountManagerImpl.init(local, eventLogger, authRepository, blockchainSource)
        accountManager = AccountManagerImpl.getInstance()
    }

    @Test
    fun `getAccountState should return the current state`() {
        initWithFirstState(REQUIRE_CREATION)
        assertEquals(REQUIRE_CREATION, accountManager.accountState)

        whenever(local.accountState).doReturn(PENDING_CREATION)
        assertEquals(PENDING_CREATION, accountManager.accountState)

        whenever(local.accountState).doReturn(CREATION_COMPLETED)
        assertEquals(CREATION_COMPLETED, accountManager.accountState)
    }

    @Test
    fun `isAccountCreate should return false`() {
        initWithFirstState(REQUIRE_CREATION)
        whenever(local.accountState).doReturn(REQUIRE_CREATION)
        assertFalse(accountManager.isAccountCreated)

        whenever(local.accountState).doReturn(PENDING_CREATION)
        assertFalse(accountManager.isAccountCreated)

        assertFalse(accountManager.isAccountCreated)
    }

    @Test
    fun `isAccountCreate should return true`() {
        initWithFirstState(REQUIRE_CREATION)
        whenever(local.accountState).doReturn(CREATION_COMPLETED)
        assertTrue(accountManager.isAccountCreated)
    }

    @Test
    fun `start account creation flow and listen for state changes, skip on REQUIRE_TRUSTLINE`() {
        initWithFirstState(REQUIRE_CREATION)
        val stateList = arrayListOf<Int>()
        val stateObserver = object : Observer<Int>() {
            override fun onChanged(value: Int) {
                stateList.add(value)
            }
        }

        accountManager.addAccountStateObserver(stateObserver)
        accountManager.start()
        argumentCaptor<KinCallback<Void>>().apply {
            verify(blockchainSource).isAccountCreated(capture())
            firstValue.onResponse(null)
        }


        assertEquals(listOf(1, 1, 2, 4), stateList)
    }

    @Test
    fun `start with old state REQUIRE_TRUSTLINE state move to COMPLETE`() {
        initWithFirstState(REQUIRE_TRUSTLINE)
        val stateList = arrayListOf<Int>()
        val stateObserver = object : Observer<Int>() {
            override fun onChanged(value: Int) {
                stateList.add(value)
            }
        }

        accountManager.addAccountStateObserver(stateObserver)
        accountManager.start()

        assertEquals(listOf(3, 3, 4), stateList)
    }

    @Test
    fun `retry from the beginning skip on REQUIRE_TRUSTLINE`() {
        initWithFirstState(ERROR)
        val statesArray = arrayListOf<Int>()
        val stateObserver = object : Observer<Int>() {
            override fun onChanged(value: Int) {
                statesArray.add(value)
            }
        }
        whenever(local.accountState).doReturn(REQUIRE_CREATION)
        accountManager.addAccountStateObserver(stateObserver)
        accountManager.retry()
        argumentCaptor<KinCallback<Void>>().apply {
            verify(blockchainSource).isAccountCreated(capture())
            firstValue.onResponse(null)
        }

        assertEquals(listOf(5, 1, 2, 4).toList(), statesArray)
    }
}