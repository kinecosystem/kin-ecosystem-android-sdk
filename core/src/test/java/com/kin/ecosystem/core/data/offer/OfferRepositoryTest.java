package com.kin.ecosystem.core.data.offer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kin.ecosystem.common.KinCallback;
import com.kin.ecosystem.common.NativeOfferClickEvent;
import com.kin.ecosystem.common.Observer;
import com.kin.ecosystem.common.Callback;
import com.kin.ecosystem.common.model.NativeEarnOffer;
import com.kin.ecosystem.common.model.NativeEarnOfferBuilder;
import com.kin.ecosystem.common.model.NativeOffer;
import com.kin.ecosystem.common.model.NativeOffer.OfferType;
import com.kin.ecosystem.common.model.NativeOfferBuilder;
import com.kin.ecosystem.common.model.NativeSpendOffer;
import com.kin.ecosystem.common.model.NativeSpendOfferBuilder;
import com.kin.ecosystem.core.data.order.OrderDataSource;
import com.kin.ecosystem.common.exception.KinEcosystemException;
import com.kin.ecosystem.core.network.model.Offer;
import com.kin.ecosystem.core.network.model.OfferList;
import com.kin.ecosystem.core.network.model.Paging;
import com.kin.ecosystem.core.network.model.PagingCursors;
import com.kin.ecosystem.core.util.OfferConverter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import com.kin.ecosystem.core.network.ApiException;
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
	private OrderDataSource orderRepository;

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
		OfferRepository.init(remote, orderRepository);
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
	public void addNativeOfferCallback() throws Exception {
		Observer<NativeOfferClickEvent> callback = new Observer<NativeOfferClickEvent>() {
			@Override
			public void onChanged(NativeOfferClickEvent nativeOffer) {
				assertEquals("5", nativeOffer.getNativeOffer().getId());
				assertFalse(nativeOffer.isDismissOnTap());
			}
		};

		offerRepository.addNativeOfferClickedObserver(callback);
		offerRepository.getNativeSpendOfferObservable().postValue(new NativeOfferClickEvent.Builder()
			.nativeOffer(new NativeEarnOffer("5"))
			.isDismissed(false)
			.build());
	}

	@Test
	public void addNativeSpendOffer() throws Exception {

		List<Offer> newEarnOffers = new ArrayList<>();
		List<Offer> newSpendOffers = new ArrayList<>();
		NativeOffer nativeOffer =
			new NativeSpendOfferBuilder("1")
				.title("Native offer title")
				.description("Native offer desc")
				.amount(1000)
				.image("Native offer image").build();

		offerRepository.addNativeOffer(nativeOffer, true);
		assertEquals(1, offerRepository.getCachedOfferList().getOffers().size());
		assertEquals(nativeOffer.getId(), offerRepository.getCachedOfferList().getOffers().get(0).getId());

		Offer offer = OfferConverter.toOffer(nativeOffer);
		assertTrue(offerRepository.shouldDismissOnTap(offer.getId()));

		// Update on second time, still the size is 1 with same offer
		offerRepository.addNativeOffer(nativeOffer, false);
		offer = OfferConverter.toOffer(nativeOffer);
		assertFalse(offerRepository.shouldDismissOnTap(offer.getId()));
		assertEquals(1, offerRepository.getCachedOfferList().getOffers().size());
		assertEquals(nativeOffer.getId(), offerRepository.getCachedOfferList().getOffers().get(0).getId());
		OfferListUtil.splitOffersByType(offerRepository.getCachedOfferList().getOffers(), newEarnOffers, newSpendOffers);
		assertEquals(1, newSpendOffers.size());
		assertEquals(0, newEarnOffers.size());
	}


	@Test
	public void addNativeEarnOffer() throws Exception {

		List<Offer> newEarnOffers = new ArrayList<>();
		List<Offer> newSpendOffers = new ArrayList<>();
		NativeOffer nativeOffer =
			new NativeEarnOfferBuilder("2")
				.title("Native offer title")
				.description("Native offer desc")
				.amount(1000)
				.image("Native offer image").build();

		offerRepository.addNativeOffer(nativeOffer, true);
		assertEquals(1, offerRepository.getCachedOfferList().getOffers().size());
		assertEquals(nativeOffer.getId(), offerRepository.getCachedOfferList().getOffers().get(0).getId());

		Offer offer = OfferConverter.toOffer(nativeOffer);
		assertTrue(offerRepository.shouldDismissOnTap(offer.getId()));

		// Update on second time, still the size is 1 with same offer
		offerRepository.addNativeOffer(nativeOffer, false);
		offer = OfferConverter.toOffer(nativeOffer);
		assertFalse(offerRepository.shouldDismissOnTap(offer.getId()));
		assertEquals(1, offerRepository.getCachedOfferList().getOffers().size());
		assertEquals(nativeOffer.getId(), offerRepository.getCachedOfferList().getOffers().get(0).getId());
		OfferListUtil.splitOffersByType(offerRepository.getCachedOfferList().getOffers(), newEarnOffers, newSpendOffers);
		assertEquals(0, newSpendOffers.size());
		assertEquals(1, newEarnOffers.size());
	}



	@Test
	public void removeNativeSpendOffer() throws Exception {
		NativeOffer nativeOffer =
			new NativeSpendOfferBuilder("1")
				.title("Native offer title")
				.description("Native offer desc")
				.amount(1000)
				.image("Native offer image")
				.build();

		offerRepository.addNativeOffer(nativeOffer, true);
		assertEquals(1, offerRepository.getCachedOfferList().getOffers().size());
		assertEquals(nativeOffer.getId(), offerRepository.getCachedOfferList().getOffers().get(0).getId());

		Offer offer = OfferConverter.toOffer(nativeOffer);
		assertTrue(offerRepository.shouldDismissOnTap(offer.getId()));

		offerRepository.removeNativeOffer(nativeOffer);
		assertEquals(0, offerRepository.getCachedOfferList().getOffers().size());
	}

	@Test
	public void shouldDismissOnTap_WithNoExistingOfferId() {
		assertFalse(offerRepository.shouldDismissOnTap("404"));
	}

	private OfferList getOfferList() {
		OfferList offerList = new OfferList();
		offerList.add(offer);
		offerList.setPaging(new Paging().next("1").previous("0").cursors(new PagingCursors().after("1").before("0")));
		return offerList;
	}

	private ApiException getApiException() {
		Exception exception = new IllegalArgumentException();
		ApiException apiException = new ApiException(500, exception);
		return apiException;
	}
}