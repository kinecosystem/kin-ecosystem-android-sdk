package com.kin.ecosystem.core.accountmanager

import com.kin.ecosystem.common.KinCallback
import com.kin.ecosystem.common.Observer
import com.kin.ecosystem.core.accountmanager.AccountManager.*
import com.kin.ecosystem.core.bi.EventLogger
import com.kin.ecosystem.core.data.auth.AuthDataSource
import com.kin.ecosystem.core.data.blockchain.BlockchainSource
import com.kin.ecosystem.core.network.model.AuthToken
import com.nhaarman.mockitokotlin2.*
import kin.core.BlockchainEvents
import kin.core.EventListener
import kin.core.KinAccount
import kin.core.ListenerRegistration
import kin.ecosystem.test.base.BaseTestClass
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

    private var eventLogger: EventLogger = mock()
    private var authRepository: AuthDataSource = mock {
        val authTokenCaptor = argumentCaptor<KinCallback<AuthToken>>()
        on { getAuthToken(authTokenCaptor.capture()) } doAnswer { authTokenCaptor.firstValue.onResponse(any()) }
    }

    private var blockchainSource: BlockchainSource = mock {
        val trustlineCaptor = argumentCaptor<KinCallback<Void>>()
        on { createTrustLine(trustlineCaptor.capture()) } doAnswer { trustlineCaptor.firstValue.onResponse(null) }
        on { kinAccount } doAnswer { kinAccount }
    }

    private val accountCreationRegistration: ListenerRegistration = mock()
    private val blockchainEvents: BlockchainEvents = mock {
        on { addAccountCreationListener(any()) } doAnswer { accountCreationRegistration }
    }
    private var kinAccount: KinAccount = mock {
        on { blockchainEvents() } doAnswer { blockchainEvents }
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

        whenever(local.accountState).doReturn(REQUIRE_TRUSTLINE)
        assertEquals(REQUIRE_TRUSTLINE, accountManager.accountState)

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

        whenever(local.accountState).doReturn(REQUIRE_TRUSTLINE)
        assertFalse(accountManager.isAccountCreated)
    }

    @Test
    fun `isAccountCreate should return true`() {
        initWithFirstState(REQUIRE_CREATION)
        whenever(local.accountState).doReturn(CREATION_COMPLETED)
        assertTrue(accountManager.isAccountCreated)
    }

    @Test
    fun `start account creation flow and listen for state changes`() {
        initWithFirstState(REQUIRE_CREATION)
        val stateList = arrayListOf<Int>()
        val stateObserver = object : Observer<Int>() {
            override fun onChanged(value: Int) {
                stateList.add(value)
            }
        }

        accountManager.addAccountStateObserver(stateObserver)
        accountManager.start()
        argumentCaptor<EventListener<Void>>().apply {
            verify(blockchainEvents).addAccountCreationListener(capture())
            firstValue.onEvent(null)
        }



        assertEquals(listOf(1, 1, 2, 3, 4), stateList)
    }


    @Test
    fun `retry from the beginning`() {
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
        argumentCaptor<EventListener<Void>>().apply {
            verify(blockchainEvents).addAccountCreationListener(capture())
            firstValue.onEvent(null)
        }

        assertEquals(listOf(5, 1, 2, 3, 4).toList(), statesArray)
    }
}