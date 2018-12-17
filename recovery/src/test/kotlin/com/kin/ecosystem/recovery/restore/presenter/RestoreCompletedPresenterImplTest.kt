package com.kin.ecosystem.recovery.restore.presenter

import android.os.Bundle
import com.kin.ecosystem.recovery.restore.presenter.RestorePresenterImpl.KEY_ACCOUNT_INDEX
import com.kin.ecosystem.recovery.restore.view.RestoreCompletedView
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class RestoreCompletedPresenterImplTest {

    companion object {
        const val accountIndex = 1
    }

    private val view: RestoreCompletedView = mock()
    private val parentPresenter: RestorePresenter = mock()

    private lateinit var presenter: RestoreCompletedPresenterImpl

    @Before
    fun setUp() {
        createPresenter()
    }

    @Test
    fun `back clicked go to previous step`() {
        presenter.onBackClicked()
        verify(parentPresenter).previousStep()
    }

    @Test
    fun `close flow with the correect account index`() {
        presenter.close()
        verify(parentPresenter).closeFlow(accountIndex)
    }

    @Test
    fun `onSaveInstanceState save the correct values`() {
        val bundle = Bundle()
        presenter.onSaveInstanceState(bundle)
        assertEquals(accountIndex, bundle.getInt(KEY_ACCOUNT_INDEX))
    }

    private fun createPresenter() {
        presenter = RestoreCompletedPresenterImpl(accountIndex)
        presenter.onAttach(view, parentPresenter)
    }
}