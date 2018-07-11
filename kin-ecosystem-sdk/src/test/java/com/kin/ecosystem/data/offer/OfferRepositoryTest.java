package com.kin.ecosystem.data.offer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kin.ecosystem.KinCallback;
import com.kin.ecosystem.base.Observer;
import com.kin.ecosystem.bi.EventLogger;
import com.kin.ecosystem.data.Callback;
import com.kin.ecosystem.exception.KinEcosystemException;
import com.kin.ecosystem.marketplace.model.NativeSpendOffer;

import com.kin.ecosystem.network.model.Offer;
import com.kin.ecosystem.network.model.OfferList;
import com.kin.ecosystem.network.model.Paging;
import com.kin.ecosystem.network.model.PagingCursors;
import java.lang.reflect.Field;
import java.util.List;
import kin.ecosystem.core.network.ApiException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class OfferRepositoryTest {


    @Mock
    private OfferDataSource.Remote remote;

    @Mock
    private Offer offer;

    private OfferRepository offerRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        resetInstance();
    }

    private void resetInstance() throws Exception {
        Field instance = OfferRepository.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
        OfferRepository.init(remote);
        offerRepository = OfferRepository.getInstance();

        when(offer.getId()).thenReturn("1");
        when(offer.getAmount()).thenReturn(10);
    }

    @Test
    public void getCachedOfferList_Empty() {
        List<Offer> offers = offerRepository.getCachedOfferList().getOffers();
        assertEquals(0, offers.size());
    }

    @Test
    public void getOffers_Succeed_SavedToCachedList() {
        KinCallback<OfferList> offerListCallback = mock(KinCallback.class);
        ArgumentCaptor<Callback<OfferList, ApiException>> getOfferCapture = ArgumentCaptor.forClass(Callback.class);

        OfferList offerList = getOfferList();

        offerRepository.getOffers(offerListCallback);
        verify(remote).getOffers(getOfferCapture.capture());

        getOfferCapture.getValue().onResponse(offerList);
        assertEquals(1, offerRepository.getCachedOfferList().getOffers().size());
        verify(offerListCallback).onResponse(offerList);
    }

    @Test
    public void getOffers_Failed() {
        KinCallback<OfferList> offerListCallback = mock(KinCallback.class);
        ArgumentCaptor<Callback<OfferList, ApiException>> getOfferCapture = ArgumentCaptor.forClass(Callback.class);

        offerRepository.getOffers(offerListCallback);
        verify(remote).getOffers(getOfferCapture.capture());

        getOfferCapture.getValue().onFailure(getApiException());
        assertEquals(0, offerRepository.getCachedOfferList().getOffers().size());
        verify(offerListCallback).onFailure(any(KinEcosystemException.class));
    }

    @Test
    public void setPendingOfferByID_OfferInTheList_PendingOfferUpdated() {
        KinCallback<OfferList> offerListCallback = mock(KinCallback.class);
        ArgumentCaptor<Callback<OfferList, ApiException>> getOfferCapture = ArgumentCaptor.forClass(Callback.class);

        // Update cachedOfferList to work with
        OfferList offerList = getOfferList();
        offerRepository.getOffers(offerListCallback);
        verify(remote).getOffers(getOfferCapture.capture());
        getOfferCapture.getValue().onResponse(offerList);

        offerRepository.setPendingOfferByID(offer.getId());
        assertEquals(offer, offerRepository.getPendingOffer().getValue()); // Updated
        assertEquals(0, offerRepository.getCachedOfferList().getOffers().size()); // Removed from cachedList
    }

    @Test
    public void addNativeOfferCallback() throws Exception {
        Observer<NativeSpendOffer> callback = new Observer<NativeSpendOffer>() {
            @Override
            public void onChanged(NativeSpendOffer nativeSpendOffer) {
                assertEquals("5", nativeSpendOffer.getId());
            }
        };

        offerRepository.addNativeOfferClickedObserver(callback);
        offerRepository.getNativeSpendOfferObservable().postValue(new NativeSpendOffer("5"));
    }

    @Test
    public void addNativeOffer() throws Exception {
        NativeSpendOffer nativeOffer =
            new NativeSpendOffer("1")
                .title("Native offer title")
                .description("Native offer desc")
                .amount(1000)
                .image("Native offer image");

        assertTrue(offerRepository.addNativeOffer(nativeOffer));
        assertEquals(1, offerRepository.getCachedOfferList().getOffers().size());
        assertEquals(nativeOffer, offerRepository.getCachedOfferList().getOffers().get(0));

        // Can't add twice same offer
        assertFalse(offerRepository.addNativeOffer(nativeOffer));
    }

    @Test
    public void removeNativeOffer() throws Exception {
        NativeSpendOffer nativeOffer =
            new NativeSpendOffer("1")
                .title("Native offer title")
                .description("Native offer desc")
                .amount(1000)
                .image("Native offer image");

        assertTrue(offerRepository.addNativeOffer(nativeOffer));
        assertEquals(1, offerRepository.getCachedOfferList().getOffers().size());
        assertEquals(nativeOffer, offerRepository.getCachedOfferList().getOffers().get(0));

        assertTrue(offerRepository.removeNativeOffer(nativeOffer));
        assertEquals(0, offerRepository.getCachedOfferList().getOffers().size());

        // Offer already removed
        assertFalse(offerRepository.removeNativeOffer(nativeOffer));
    }

    private OfferList getOfferList() {
        OfferList offerList = new OfferList();
        offerList.addAtIndex(0, offer);
        offerList.setPaging(new Paging().next("1").previous("0").cursors(new PagingCursors().after("1").before("0")));
        return offerList;
    }

    private ApiException getApiException() {
        Exception exception = new IllegalArgumentException();
        ApiException apiException = new ApiException(500,exception);
        return apiException;
    }
}