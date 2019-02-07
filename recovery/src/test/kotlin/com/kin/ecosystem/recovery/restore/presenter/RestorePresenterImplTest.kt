package com.kin.ecosystem.recovery.restore.presenter

import android.content.Intent
import android.os.Bundle
import com.kin.ecosystem.recovery.events.CallbackManager
import com.kin.ecosystem.recovery.restore.presenter.RestorePresenterImpl.*
import com.kin.ecosystem.recovery.restore.view.RestoreView
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class RestorePresenterImplTest {

    private val callbackManager: CallbackManager = mock()
    private val savedInstanceState: Bundle = mock {
        // a bug does not return the default as it should be, it returns 0 and not -1 as we wrote.
        // so we will force it :)
        on { getInt(KEY_ACCOUNT_INDEX, -1) } doReturn (-1)
    }
    private val view: RestoreView = mock()

    private val accountKey = "some_fake_account_key"
    private val accountIndex = 1

    private lateinit var presenter: RestorePresenterImpl

    @Test
    fun `initial step is null, default navigate to upload qr page`() {
        createPresenter()
        verify(view).navigateToUpload()
    }

    @Test
    fun `initial step is STEP_ENTER_PASSWORD and accountKey is set, navigate to enter password page`() {
        whenever(savedInstanceState.getInt(KEY_RESTORE_STEP, STEP_UPLOAD)) doReturn (STEP_ENTER_PASSWORD)
        whenever(savedInstanceState.getString(KEY_ACCOUNT_KEY)) doReturn (accountKey)
        createPresenter()
        verify(view).navigateToEnterPassword(accountKey)
    }

    @Test
    fun `initial step is STEP_ENTER_PASSWORD and accountKey is null, show error`() {
        whenever(savedInstanceState.getInt(KEY_RESTORE_STEP, STEP_UPLOAD)) doReturn (STEP_ENTER_PASSWORD)
        whenever(savedInstanceState.getString(KEY_ACCOUNT_KEY)).thenReturn(null)
        createPresenter()
        verify(view).showError()
    }

    @Test
    fun `initial step is STEP_RESTORE_COMPLETED and accountIndex is set, navigate to restore completed page`() {
        whenever(savedInstanceState.getInt(KEY_RESTORE_STEP, STEP_UPLOAD)) doReturn (STEP_RESTORE_COMPLETED)
        whenever(savedInstanceState.getInt(KEY_ACCOUNT_INDEX, -1)) doReturn (accountIndex)
        createPresenter()
        verify(view).closeKeyboard()
        verify(view).navigateToRestoreCompleted(accountIndex)
    }

    @Test
    fun `initial step is STEP_RESTORE_COMPLETED and accountIndex is not set, show error`() {
        whenever(savedInstanceState.getInt(KEY_RESTORE_STEP, STEP_UPLOAD)) doReturn (STEP_RESTORE_COMPLETED)
        createPresenter()
        verify(view).closeKeyboard()
        verify(view).showError()
    }

    @Test
    fun `initial step is STEP_FINISH and accountIndex is set, set result success`() {
        whenever(savedInstanceState.getInt(KEY_RESTORE_STEP, STEP_UPLOAD)) doReturn (STEP_FINISH)
        whenever(savedInstanceState.getInt(KEY_ACCOUNT_INDEX, -1)) doReturn (accountIndex)
        createPresenter()
        verify(callbackManager).sendRestoreSuccessResult(accountIndex)
        verify(view).close()
    }

    @Test
    fun `initial step is STEP_FINISH and accountIndex is not set, show error`() {
        whenever(savedInstanceState.getInt(KEY_RESTORE_STEP, STEP_UPLOAD)) doReturn (STEP_FINISH)
        createPresenter()
        verify(view).showError()
    }

    @Test
    fun `navigate to enter password page`() {
        createPresenter()
        presenter.navigateToEnterPasswordPage(accountKey)
        verify(view).navigateToEnterPassword(accountKey)
    }

    @Test
    fun `navigate to restore completed page`() {
        createPresenter()
        presenter.navigateToRestoreCompletedPage(accountIndex)
        verify(view).navigateToRestoreCompleted(accountIndex)
    }

    @Test
    fun `close flow`() {
        createPresenter()
        presenter.closeFlow(accountIndex)
        verify(callbackManager).sendRestoreSuccessResult(accountIndex)
        verify(view).close()
    }

    @Test
    fun `onBackClicked called to previous step to the correct step`() {
        whenever(savedInstanceState.getInt(KEY_RESTORE_STEP, STEP_UPLOAD)) doReturn (STEP_FINISH)
        createPresenter()
        presenter.onBackClicked()
        verify(view).navigateBack()
    }

    @Test
    fun `previous step cancel on back from upload page`() {
        whenever(savedInstanceState.getInt(KEY_RESTORE_STEP, STEP_UPLOAD)) doReturn (STEP_FINISH)
        createPresenter()
        presenter.apply {
            previousStep()
            previousStep()
            previousStep()
            previousStep()
        }

        inOrder(callbackManager, view).apply {
            verify(view, times(3)).navigateBack()
            verify(view).closeKeyboard()
            verify(view).close()
        }
    }

    @Test
    fun `onActivityResult passes the correct data`() {
        createPresenter()
        val data = Intent("some_action")
        presenter.onActivityResult(1, 2, data)
        verify(callbackManager).onActivityResult(1, 2, data)
    }

    @Test
    fun `onSaveInstanceState is updated correctly`() {
        createPresenter()
        val outState = Bundle()
        presenter.apply {
            navigateToEnterPasswordPage(accountKey)
            onSaveInstanceState(outState)
        }

        outState.apply {
            assertEquals(STEP_ENTER_PASSWORD, getInt(KEY_RESTORE_STEP))
            assertEquals(accountKey, getString(KEY_ACCOUNT_KEY))
            assertEquals(-1, getInt(KEY_ACCOUNT_INDEX, -1))
        }

        presenter.apply {
            navigateToRestoreCompletedPage(accountIndex)
            onSaveInstanceState(outState)
        }

        outState.apply {
            assertEquals(STEP_RESTORE_COMPLETED, getInt(KEY_RESTORE_STEP))
            assertEquals(accountKey, getString(KEY_ACCOUNT_KEY))
            assertEquals(accountIndex, getInt(KEY_ACCOUNT_INDEX))
        }
    }

    private fun createPresenter() {
        presenter = RestorePresenterImpl(callbackManager, savedInstanceState)
        presenter.onAttach(view)
    }
}