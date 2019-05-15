package com.kin.ecosystem.core.data.offer

import com.kin.ecosystem.common.Callback
import com.kin.ecosystem.common.KinCallback
import com.kin.ecosystem.common.NativeOfferClickEvent
import com.kin.ecosystem.common.Observer
import com.kin.ecosystem.common.exception.KinEcosystemException
import com.kin.ecosystem.common.model.NativeEarnOffer
import com.kin.ecosystem.common.model.NativeEarnOfferBuilder
import com.kin.ecosystem.common.model.NativeSpendOfferBuilder
import com.kin.ecosystem.core.data.order.OrderDataSource
import com.kin.ecosystem.core.network.ApiException
import com.kin.ecosystem.core.network.model.Offer
import com.kin.ecosystem.core.network.model.OfferList
import com.kin.ecosystem.core.network.model.Paging
import com.kin.ecosystem.core.network.model.PagingCursors
import com.kin.ecosystem.core.util.OfferConverter
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class OfferRepositoryTest {

    private val orderRepository: OrderDataSource = mock()
    private val remote: OfferDataSource.Remote = mock()
    private val offer: Offer = mock()

    private lateinit var offerRepository: OfferRepository

    private val offerList: OfferList
        get() {
            val offerList = OfferList()
            offerList.add(offer)
            offerList.paging = Paging().next("1").previous("0").cursors(PagingCursors().after("1").before("0"))
            return offerList
        }

    private val apiException: ApiException
        get() {
            val exception = IllegalArgumentException()
            return ApiException(500, exception)
        }

    @Before
    @Throws(Exception::class)
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        resetInstance()
    }

    @Throws(Exception::class)
    private fun resetInstance() {
        val instance = OfferRepository::class.java.getDeclaredField("instance")
        instance.isAccessible = true
        instance.set(null, null)
        OfferRepository.init(remote, orderRepository)
        offerRepository = OfferRepository.getInstance()

        with(offer) {
            whenever(id).thenReturn("1")
            whenever(amount).thenReturn(10)
        }
    }

    @Test
    fun `first time cached offer list is empty`() {
        val offers = offerRepository.cachedOfferList.offers
        assertEquals(0, offers.size)
    }

    @Test
    fun `get offers from remote and update cached offers on success`() {
        val offerListCallback : KinCallback<OfferList> = mock()
        val getOfferCapture = argumentCaptor<Callback<OfferList, ApiException>>()

        val offerList = offerList

        offerRepository.getOffers(offerListCallback)
        verify(remote).getOffers(getOfferCapture.capture())
        getOfferCapture.firstValue.onResponse(offerList)

        assertEquals(1, offerRepository.cachedOfferList.offers.size)
        verify<KinCallback<OfferList>>(offerListCallback).onResponse(offerList)
    }

    @Test
    fun `get offers from remote failde, cached list is empty`() {
        val offerListCallback : KinCallback<OfferList> = mock()
        val getOfferCapture = argumentCaptor<Callback<OfferList, ApiException>>()

        offerRepository.getOffers(offerListCallback)
        verify(remote).getOffers(getOfferCapture.capture())

        getOfferCapture.firstValue.onFailure(apiException)
        assertEquals(0, offerRepository.cachedOfferList.offers.size)
        verify(offerListCallback).onFailure(any(KinEcosystemException::class.java))
    }

    @Test
    fun `add native offer callback and listen to post values`() {
        val callback: Observer<NativeOfferClickEvent> = mock()
        val offer = NativeOfferClickEvent.Builder()
                .nativeOffer(NativeEarnOffer("5"))
                .isDismissed(false)
                .build()

        offerRepository.addNativeOfferClickedObserver(callback)
        offerRepository.nativeSpendOfferObservable.postValue(offer)

        verify(callback).onChanged(offer)
    }

    @Test
    fun `add native spend offer to list`() {
        val nativeOffer = NativeSpendOfferBuilder("1")
                .title("Native offer title")
                .description("Native offer desc")
                .amount(1000)
                .dismissOnTap(true)
                .image("Native offer image").build()

        offerRepository.addNativeOffer(nativeOffer)
        with(offerRepository.cachedOfferList.offers) {
            assertEquals(1, size.toLong())
            assertEquals(OfferConverter.toOffer(nativeOffer), this[0])
        }
    }

    @Test
    fun `add native earn offer to list`() {
        val nativeOffer = NativeEarnOfferBuilder("2")
                .title("Native offer title")
                .description("Native offer desc")
                .amount(1000)
                .dismissOnTap(true)
                .image("Native offer image").build()

        offerRepository.addNativeOffer(nativeOffer)
        with(offerRepository.cachedOfferList.offers) {
            assertEquals(1, size.toLong())
            assertEquals(OfferConverter.toOffer(nativeOffer), this[0])
        }
    }

    @Test
    fun `remove native offer from list`() {
        val nativeOffer = NativeSpendOfferBuilder("1")
                .title("Native offer title")
                .description("Native offer desc")
                .amount(1000)
                .image("Native offer image")
                .dismissOnTap(true)
                .build()

        offerRepository.addNativeOffer(nativeOffer)
        with(offerRepository.cachedOfferList.offers) {
            assertEquals(1, size.toLong())
            assertEquals(OfferConverter.toOffer(nativeOffer), this[0])
        }


        offerRepository.removeNativeOffer(nativeOffer)
        assertEquals(0, offerRepository.cachedOfferList.offers.size)
    }

    @Test
    fun `add all offers to the top of the list and keep the order`() {
        val firstOrder = NativeSpendOfferBuilder("1")
                .title("First Native offer title")
                .description("Native offer desc")
                .amount(1)
                .image("Native offer image")
                .dismissOnTap(true)
                .build()

        val secondOrder = NativeSpendOfferBuilder("2")
                .title("Second Native offer title")
                .description("Native offer desc")
                .amount(2)
                .image("Native offer image")
                .dismissOnTap(false)
                .build()

        val thirdOrder = NativeSpendOfferBuilder("3")
                .title("Third Native offer title")
                .description("Native offer desc")
                .amount(3)
                .image("Native offer image")
                .dismissOnTap(true)
                .build()

        offerRepository.addNativeOffer(thirdOrder)
        offerRepository.addAllNativeOffers(arrayListOf(firstOrder, secondOrder))
        with(offerRepository.cachedOfferList.offers) {
            assertEquals(3, size.toLong())
            assertEquals(OfferConverter.toOffer(firstOrder), this[0])
            assertEquals(OfferConverter.toOffer(secondOrder), this[1])
            assertEquals(OfferConverter.toOffer(thirdOrder), this[2])
        }
    }

    @Test
    fun `update native offer if exists`() {
        val nativeOffer = NativeEarnOfferBuilder("2")
                .title("Native offer title")
                .description("Native offer desc")
                .amount(1000)
                .dismissOnTap(true)
                .image("Native offer image").build()

        offerRepository.addNativeOffer(nativeOffer)
        with(offerRepository.cachedOfferList.offers) {
            assertEquals(1, size.toLong())
            assertEquals(OfferConverter.toOffer(nativeOffer), this[0])
        }

        nativeOffer.amount = 5
        nativeOffer.isDismissOnTap = false
        offerRepository.addNativeOffer(nativeOffer)
        with(offerRepository.cachedOfferList.offers) {
            assertEquals(1, size.toLong())
            assertEquals(OfferConverter.toOffer(nativeOffer), this[0])
        }

        nativeOffer.amount = 22
        nativeOffer.isDismissOnTap = true
        offerRepository.addAllNativeOffers(arrayListOf(nativeOffer))
        with(offerRepository.cachedOfferList.offers) {
            assertEquals(1, size.toLong())
            assertEquals(OfferConverter.toOffer(nativeOffer), this[0])
        }
    }

    @Test
    fun `always add offer to top of the list`() {
        val firstOffer = NativeEarnOfferBuilder("1")
                .title("First Native offer title")
                .description("Native offer desc")
                .amount(1)
                .dismissOnTap(true)
                .image("Native offer image").build()

        val secondOffer = NativeEarnOfferBuilder("2")
                .title("Second Native offer title")
                .description("Native offer desc")
                .amount(2)
                .dismissOnTap(false)
                .image("Native offer image").build()

        offerRepository.addNativeOffer(secondOffer)
        offerRepository.addNativeOffer(firstOffer)

        //The last order added should be on the top of the list
        with(offerRepository.cachedOfferList.offers) {
            assertEquals(2, size.toLong())
            assertEquals(OfferConverter.toOffer(firstOffer), this[0])
            assertEquals(OfferConverter.toOffer(secondOffer), this[1])
        }
    }
}