package com.kin.ecosystem.core.data.blockchain

import com.kin.ecosystem.common.Observer
import com.kin.ecosystem.common.exception.BlockchainException
import com.kin.ecosystem.common.model.Balance
import com.kin.ecosystem.core.bi.EventLogger
import com.kin.ecosystem.core.bi.events.SpendTransactionBroadcastToBlockchainFailed
import com.kin.ecosystem.core.bi.events.SpendTransactionBroadcastToBlockchainSucceeded
import com.kin.ecosystem.core.data.auth.AuthDataSource
import com.nhaarman.mockitokotlin2.*
import kin.sdk.migration.common.interfaces.I*
import kin.ecosystem.test.base.BaseTestClass
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.math.BigDecimal

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class BlockchainSourceImplTest() : BaseTestClass() {

    companion object {
        const val KIN_USER_ID_A = "kin_user_id_A"
        const val PUBLIC_ADDRESS_A = "public_address_A"

        const val KIN_USER_ID_B = "kin_user_id_B"
        const val PUBLIC_ADDRESS_B = "public_address_B"

        const val APP_ID = "appID"
        const val ORDER_ID = "orderID"
        const val MEMO_EXAMPLE = "1-$APP_ID-$ORDER_ID"
    }

    private val eventLogger: EventLogger = mock()
    private val local: BlockchainSource.Local = mock {
        on { accountIndex } doAnswer { BlockchainSourceLocal.NOT_EXIST }
    }
    private val authRepository: AuthDataSource = mock {
        on { ecosystemUserID } doAnswer { KIN_USER_ID_A }
    }

    private val blockchainEvents: BlockchainEvents = mock()
    private val getBalanceReq: Request<kin.sdk.migration.common.interfaces.IBalance> = mock()
    private val kinAccountA: IKinAccount = mock {
        on { blockchainEvents() } doAnswer { blockchainEvents }
        on { balance } doAnswer { getBalanceReq }
        on { publicAddress } doAnswer { PUBLIC_ADDRESS_A }
        on { activateSync() } doAnswer {}
    }

    private val kinAccountB: IKinAccount = mock {
        on { blockchainEvents() } doAnswer { blockchainEvents }
        on { balance } doAnswer { getBalanceReq }
        on { publicAddress } doAnswer { PUBLIC_ADDRESS_B }
        on { activateSync() } doAnswer {}
    }


    private val kinClient: IKinClient = mock {
        on { addAccount() } doAnswer { kinAccountA }
    }

    private val balanceObj: kin.sdk.migration.common.interfaces.IBalance = mock()

    private lateinit var blockchainSource: BlockchainSourceImpl
    private var balance: Balance? = null

    @Before
    override fun setUp() {
        super.setUp()
        resetInstance()
    }

    @Throws(Exception::class)
    private fun resetInstance() {
        val instance = BlockchainSourceImpl::class.java.getDeclaredField("instance")
        instance.isAccessible = true
        instance.set(null, null)
        BlockchainSourceImpl.init(eventLogger, kinClient, local, authRepository)
        blockchainSource = BlockchainSourceImpl.getInstance()
    }

    private fun loadAccount(kinAccount: IKinAccount, publicAddress: String, kinUserId: String) {
        whenever(local.getLastWalletAddress(kinUserId)) doAnswer { publicAddress }

        whenever(kinClient.getAccount(0)) doAnswer { kinAccount }
        whenever(kinClient.accountCount) doAnswer { 1 }
        whenever(kinClient.hasAccount()).doReturn(true)

        blockchainSource.loadAccount(kinUserId)
    }

    @Test
    fun `kinClient has no account do not init account`() {
        kinClient.let {
            verify(it, never()).hasAccount()
            verify(it, never()).getAccount(any())
        }
    }


    @Test
    fun `accountIndex not exists, skip migrate, kinClient don't have wallets, create new account`() {
        whenever(local.getLastWalletAddress(KIN_USER_ID_A)) doAnswer { null }
        whenever(kinClient.hasAccount()).doReturn(false)

        blockchainSource.loadAccount(KIN_USER_ID_A)

        verify(kinClient).addAccount()
        verify(local).setActiveUserWallet(KIN_USER_ID_A, PUBLIC_ADDRESS_A)
    }

    @Test
    fun `accountIndex exists, migrateToMultipleUsers, found account, load relevant account`() {
        whenever(local.accountIndex) doAnswer { 0 }
        whenever(local.getLastWalletAddress(KIN_USER_ID_A)) doAnswer { PUBLIC_ADDRESS_A }

        whenever(kinClient.getAccount(0)) doAnswer { kinAccountA }
        whenever(kinClient.accountCount) doAnswer { 1 }
        whenever(kinClient.hasAccount()).doReturn(true)

        blockchainSource.loadAccount(KIN_USER_ID_A)

        verify(kinClient, never()).addAccount()
        verify(local, times(2)).setActiveUserWallet(KIN_USER_ID_A, PUBLIC_ADDRESS_A)
    }

    @Test
    fun `accountIndex not exists, switching user with no lastWalletAddress, create new account`() {
        whenever(local.getLastWalletAddress(KIN_USER_ID_B)) doAnswer { PUBLIC_ADDRESS_B }

        whenever(kinClient.getAccount(0)) doAnswer { kinAccountA }
        whenever(kinClient.accountCount) doAnswer { 1 }
        whenever(kinClient.hasAccount()).doReturn(true)
        whenever(kinClient.addAccount()) doAnswer { kinAccountB }

        blockchainSource.loadAccount(KIN_USER_ID_B)

        verify(kinClient).addAccount()
        verify(local).setActiveUserWallet(KIN_USER_ID_B, PUBLIC_ADDRESS_B)
    }

    @Test
    fun `load last active account`() {
        val kinAccountC: IKinAccount = mock {
            on { blockchainEvents() } doAnswer { blockchainEvents }
            on { balance } doAnswer { getBalanceReq }
            on { publicAddress } doAnswer { "some_other_address" }
            on { activateSync() } doAnswer {}
        }

        whenever(local.getLastWalletAddress(KIN_USER_ID_A)) doAnswer { PUBLIC_ADDRESS_A }

        whenever(kinClient.getAccount(0)) doAnswer { kinAccountB }
        whenever(kinClient.getAccount(1)) doAnswer { kinAccountC }
        whenever(kinClient.getAccount(2)) doAnswer { kinAccountA }
        whenever(kinClient.accountCount) doAnswer { 3 }
        whenever(kinClient.hasAccount()).doReturn(true)

        blockchainSource.loadAccount(KIN_USER_ID_A)

        verify(kinClient, never()).addAccount()
        verify(local).setActiveUserWallet(KIN_USER_ID_A, PUBLIC_ADDRESS_A)
    }


    @Test
    fun `set appId, meme is generated correctly`() {
        whenever(authRepository.appID).thenReturn(APP_ID);
        assertEquals(MEMO_EXAMPLE, blockchainSource.generateMemo(ORDER_ID));
    }

    @Test
    @Throws(BlockchainException::class)
    fun `get public address`() {
        loadAccount(kinAccountA, PUBLIC_ADDRESS_A, KIN_USER_ID_A)
        val address = blockchainSource.publicAddress
        assertEquals(PUBLIC_ADDRESS_A, address)
    }

    @Test
    fun `extract order id`() {
        // without app id set
        assertNull(blockchainSource.extractOrderId("123"))
        assertNull(blockchainSource.extractOrderId(MEMO_EXAMPLE))

       // with app id
        whenever(authRepository.appID).thenReturn(APP_ID);
        assertEquals(ORDER_ID, blockchainSource.extractOrderId(MEMO_EXAMPLE))
    }

    @Test
    fun `send transaction succeeded`() {
        whenever(authRepository.appID).thenReturn(APP_ID);
        loadAccount(kinAccountA, PUBLIC_ADDRESS_A, KIN_USER_ID_A)
        val toAddress = "some_pub_address"
        val amount = BigDecimal(10)
        val orderID = "someID"
        val transactionID = "transactionID"

        val transactionRequest: Request<ITransactionId> = mock()
        val resultCallbackArgumentCaptor = argumentCaptor<ResultCallback<ITransactionId>>()
        whenever(kinAccountA.sendTransaction(any(), any(), any())).thenReturn(transactionRequest)

        blockchainSource.sendTransaction(toAddress, amount, orderID, "offerID")
        verify(transactionRequest).run(resultCallbackArgumentCaptor.capture())
        resultCallbackArgumentCaptor.firstValue.onResult(ITransactionId { transactionID })
        verify(eventLogger).send(any<SpendTransactionBroadcastToBlockchainSucceeded>())
    }

    @Test
    fun `send transaction failed`() {
        whenever(authRepository.appID).thenReturn(APP_ID);
        loadAccount(kinAccountA, PUBLIC_ADDRESS_A, KIN_USER_ID_A)
        val toAddress = "some_pub_address"
        val amount = BigDecimal(10)
        val orderID = "someID"

        val transactionRequest: Request<ITransactionId> = mock()
        val resultCallbackArgumentCaptor = argumentCaptor<ResultCallback<ITransactionId>>()
        whenever(kinAccountA.sendTransaction(any(), any(), any())).thenReturn(transactionRequest)

        blockchainSource.sendTransaction(toAddress, amount, orderID, "offerID")
        verify(transactionRequest).run(resultCallbackArgumentCaptor.capture())

        val exception = Exception("failed")

        blockchainSource.addPaymentObservable(object : Observer<Payment>() {
            override fun onChanged(value: Payment) {
                assertFalse(value.isSucceed)
                assertEquals(orderID, value.orderID)
                assertEquals(exception, value.exception)
                assertEquals(Payment.UNKNOWN.toLong(), value.type.toLong())
            }
        })

        resultCallbackArgumentCaptor.firstValue.onError(exception)
        verify(eventLogger).send(any<SpendTransactionBroadcastToBlockchainFailed>())
    }

    @Test
    fun `add balance observer, get onChanged value`() {
        whenever(local.balance).thenReturn(20)
        loadAccount(kinAccountA, PUBLIC_ADDRESS_A, KIN_USER_ID_A)
        val innerBalance: kin.sdk.migration.common.interfaces.IBalance = mock()
        blockchainSource.addBalanceObserver(object : Observer<Balance>() {
            override fun onChanged(value: Balance) {
                balance = value
            }
        }, false)
        assertEquals(BigDecimal(20), balance?.amount)

        var value = BigDecimal(25)
        whenever(innerBalance.value()).thenReturn(value)
        blockchainSource.setBalance(innerBalance)
        assertEquals(value, balance?.amount)

        value = BigDecimal(50)
        whenever(innerBalance.value()).thenReturn(value)
        blockchainSource.setBalance(innerBalance)
        assertEquals(value, balance?.amount)

        value = BigDecimal(50)
        whenever(innerBalance.value()).thenReturn(value)
        blockchainSource.setBalance(innerBalance)
        assertEquals(value, balance?.amount)

        inOrder(local).apply {
            verify(local).balance = 25
            verify(local).balance = 50
            verify(local, never()).balance
        }
    }

    @Test
    fun `add balance observer and start listen`() {
        loadAccount(kinAccountA, PUBLIC_ADDRESS_A, KIN_USER_ID_A)
        val balanceEventListener = argumentCaptor<EventListener<kin.sdk.migration.common.interfaces.IBalance>>()

        blockchainSource.addBalanceObserver(object : Observer<Balance>() {
            override fun onChanged(value: Balance) {
                balance = value
            }
        }, true)

        verify(blockchainEvents).addBalanceListener(balanceEventListener.capture())
        val value = BigDecimal(123)

        whenever(balanceObj.value()).thenReturn(value)
        balanceEventListener.firstValue.onEvent(balanceObj)

        assertEquals(value, balance?.amount)
        verify(local).balance = value.toInt()
    }

    @Test
    fun `get KeyStoreProvider is not null`() {
        assertNotNull(blockchainSource.keyStoreProvider)
    }
}