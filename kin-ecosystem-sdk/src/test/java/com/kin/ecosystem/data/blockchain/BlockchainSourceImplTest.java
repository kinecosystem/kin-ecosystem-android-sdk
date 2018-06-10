package com.kin.ecosystem.data.blockchain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.data.model.BalanceUpdate;
import com.kin.ecosystem.data.model.Payment;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;
import kin.core.Balance;
import kin.core.BlockchainEvents;
import kin.core.EventListener;
import kin.core.KinAccount;
import kin.core.KinClient;
import kin.core.ListenerRegistration;
import kin.core.Request;
import kin.core.ResultCallback;
import kin.core.TransactionId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class BlockchainSourceImplTest {

    private static final String PUBLIC_ADDRESS = "public_address";
    private static final String APP_ID = "appID";
    private static final String ORDER_ID = "orderID";
    private static final String MEMO_EXAMPLE = "1-" + APP_ID + "-" + ORDER_ID;


    @Mock
    private KinClient kinClient;

    @Mock
    private BlockchainSource.Local local;

    @Mock
    private KinAccount kinAccount;

    @Mock
    private BlockchainEvents blockchainEvents;


    @Captor
    private ArgumentCaptor<EventListener<Void>> accountCreationCaptor;

    @Mock
    private ListenerRegistration accountRegistration;


    @Mock
    private Request<Void> activateAccountReq;

    @Captor
    private ArgumentCaptor<ResultCallback<Void>> accountActivateCaptor;


    @Mock
    private Request<Balance> getBalanceReq;

    @Captor
    private ArgumentCaptor<ResultCallback<Balance>> getBalanceCaptor;

    @Mock
    private Balance balanceObj;


    private BlockchainSourceImpl blockchainSource;
    private BalanceUpdate balanceUpdate;
    private int x = 0;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(kinClient.addAccount()).thenReturn(kinAccount);
        when(kinAccount.blockchainEvents()).thenReturn(blockchainEvents);
        when(kinAccount.getBalance()).thenReturn(getBalanceReq);
        when(kinAccount.activate()).thenReturn(activateAccountReq);
        when(blockchainEvents.addAccountCreationListener(any(EventListener.class))).thenReturn(accountRegistration);
        when(balanceObj.value()).thenReturn(new BigDecimal(20));
        when(kinAccount.getPublicAddress()).thenReturn(PUBLIC_ADDRESS);

        resetInstance();
        resetPaymentObserverCount();
        resetBalanceObserverCount();

        // Account Creation
        verify(kinClient).addAccount();
        verify(blockchainEvents).addAccountCreationListener(accountCreationCaptor.capture());
        accountCreationCaptor.getValue().onEvent(null);

        verify(activateAccountReq).run(accountActivateCaptor.capture());
        accountActivateCaptor.getValue().onResult(null);

        // init Balance
        verify(getBalanceReq).run(getBalanceCaptor.capture());
        getBalanceCaptor.getValue().onResult(balanceObj);
        verify(local).setBalance(balanceObj.value().intValue());

        when(kinClient.getAccount(0)).thenReturn(kinAccount);
        balanceUpdate = new BalanceUpdate();
    }


    private void resetInstance() throws Exception {
        Field instance = BlockchainSourceImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
        BlockchainSourceImpl.init(kinClient, local);
        blockchainSource = BlockchainSourceImpl.getInstance();
    }

    private void resetPaymentObserverCount() throws Exception {
        Field paymentObserversCount = BlockchainSourceImpl.class.getDeclaredField("paymentObserversCount");
        paymentObserversCount.setAccessible(true);
        paymentObserversCount.set(paymentObserversCount, new AtomicInteger(0));
    }

    private void resetBalanceObserverCount() throws Exception {
        Field balanceObserversCount = BlockchainSourceImpl.class.getDeclaredField("balanceObserversCount");
        balanceObserversCount.setAccessible(true);
        balanceObserversCount.set(balanceObserversCount, new AtomicInteger(0));
    }


    @Test
    public void init_once_and_one_account() throws Exception {
        BlockchainSourceImpl.init(kinClient, local);
        BlockchainSourceImpl.init(kinClient, local);
        assertEquals(blockchainSource, BlockchainSourceImpl.getInstance());
        verify(kinClient).addAccount();
    }

    @Test
    public void set_app_id_memo_generated_correctly() {
        blockchainSource.setAppID(APP_ID);
        assertEquals(MEMO_EXAMPLE, blockchainSource.generateMemo(ORDER_ID));
    }

    @Test
    public void get_public_address() {
        assertEquals(PUBLIC_ADDRESS, blockchainSource.getPublicAddress());
    }

    @Test
    public void extract_order_id() {
        // without app id set
        assertNull(blockchainSource.extractOrderId("123"));
        assertNull(blockchainSource.extractOrderId(MEMO_EXAMPLE));

        // with app id
        blockchainSource.setAppID(APP_ID);
        assertEquals(ORDER_ID, blockchainSource.extractOrderId(MEMO_EXAMPLE));
    }

    @Test
    public void send_transaction_failed() {
        String toAddress = "some_pub_address";
        BigDecimal amount = new BigDecimal(10);
        final String orderID = "someID";

        Request<TransactionId> transactionRequest = mock(Request.class);
        ArgumentCaptor<ResultCallback<TransactionId>> resultCallbackArgumentCaptor =
            forClass(ResultCallback.class);
        when(kinAccount.sendTransaction(any(String.class), any(BigDecimal.class), any(String.class)))
            .thenReturn(transactionRequest);

        blockchainSource.setAppID(APP_ID);
        blockchainSource.sendTransaction(toAddress, amount, orderID);
        verify(transactionRequest).run(resultCallbackArgumentCaptor.capture());

        blockchainSource.addPaymentObservable(new Observer<Payment>() {
            @Override
            public void onChanged(Payment value) {
                assertFalse(value.isSucceed());
                assertEquals(orderID, value.getOrderID());
                assertEquals("failed", value.getResultMessage());
            }
        });

        resultCallbackArgumentCaptor.getValue().onError(new Exception("failed"));
    }

    @Test
    public void add_balance_observer_get_onChanged() {
        Balance innerBalance = mock(Balance.class);
        blockchainSource.addBalanceObserver(new Observer<BalanceUpdate>() {
            @Override
            public void onChanged(BalanceUpdate value) {
                x++;
                balanceUpdate = value;
            }
        });
        assertEquals(new BigDecimal(20), balanceUpdate.getAmount());

        InOrder inOrder = Mockito.inOrder(local);
        BigDecimal value = new BigDecimal(25);
        when(innerBalance.value()).thenReturn(value);
        blockchainSource.setBalance(innerBalance);
        assertEquals(value, balanceUpdate.getAmount());

        value = new BigDecimal(50);
        when(innerBalance.value()).thenReturn(value);
        blockchainSource.setBalance(innerBalance);
        assertEquals(value, balanceUpdate.getAmount());

        value = new BigDecimal(50);
        when(innerBalance.value()).thenReturn(value);
        blockchainSource.setBalance(innerBalance);
        assertEquals(value, balanceUpdate.getAmount());

        inOrder.verify(local).setBalance(25);
        inOrder.verify(local).setBalance(50);
        inOrder.verify(local, never()).setBalance(any(Integer.class));
    }

    @Test
    public void add_balance_observer_and_start_listen() throws Exception {
        ArgumentCaptor<EventListener<Balance>> balanceEventListener = forClass(EventListener.class);

        blockchainSource.addBalanceObserverAndStartListen(new Observer<BalanceUpdate>() {
            @Override
            public void onChanged(BalanceUpdate value) {
                balanceUpdate = value;
            }
        });

        verify(blockchainEvents).addBalanceListener(balanceEventListener.capture());
        BigDecimal value = new BigDecimal(123);

        when(balanceObj.value()).thenReturn(value);
        balanceEventListener.getValue().onEvent(balanceObj);

        assertEquals(value, balanceUpdate.getAmount());
        verify(local).setBalance(value.intValue());

    }
}