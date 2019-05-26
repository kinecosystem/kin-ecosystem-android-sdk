package com.kin.ecosystem.recovery.backup.presenter

import android.net.Uri
import android.os.Bundle
import com.kin.ecosystem.recovery.backup.presenter.SaveAndSharePresenterImpl.IS_SEND_EMAIL_CLICKED
import com.kin.ecosystem.recovery.backup.view.BackupNavigator
import com.kin.ecosystem.recovery.backup.view.SaveAndShareView
import com.kin.ecosystem.recovery.events.BackupEventCode.*
import com.kin.ecosystem.recovery.events.CallbackManager
import com.kin.ecosystem.recovery.qr.QRBarcodeGenerator
import com.nhaarman.mockitokotlin2.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class SaveAndSharePresenterImplTest {

    private val uri: Uri = mock()

    private val callbackManager: CallbackManager = mock()
    private val backupNavigator: BackupNavigator = mock()
    private val qrBarcodeGenerator: QRBarcodeGenerator = mock {
        on { generate(any()) } doReturn uri
    }
    private val saveInstanceState: Bundle = mock()

    private val saveAndShareView: SaveAndShareView = mock()


    private val KEY_TEST_DATA = ("{\n"
            + "  \"pkey\": \"GCJS54LFY5H5UXSAKLWP3GXCNKAZZLRAPO45B6PLAAINRVKJSWZGZAF4\",\n"
            + "  \"seed\": \"cb60a6afa2427194f4fbdc19969dd2b34677e2cae5108d34f51970a43f47eacf36520ebe26c34064ab6d1cd29e9e8c362685651a81f0ce0525dd728028b7956e037545ec223b72d8\",\n"
            + "  \"salt\": \"f16fa85a112efdd00eb0134239f53c37\"\n"
            + "}")

    private lateinit var presenter: SaveAndSharePresenterImpl

    @Test
    fun `send qr code page view event on create`() {
        createPresenter()
        verify(callbackManager).sendBackupEvent(BACKUP_QR_CODE_PAGE_VIEWED)
    }

    @Test
    fun `send qr code is clicked, show send intent and saved checkbox`() {
        createPresenter()
        presenter.actionButtonClicked()
        verify(callbackManager).sendBackupEvent(BACKUP_QR_PAGE_SEND_QR_TAPPED)
        verify(saveAndShareView).showSendIntent(any())
        verify(saveAndShareView).showIHaveSavedQRState()
    }

    @Test
    fun `could not load qr, show error message`() {
        createPresenter()
        presenter.couldNotLoadQRImage()
        verify(saveAndShareView).showErrorTryAgainLater()
    }

    @Test
    fun `i have saved is checked, enable done button`() {
        createPresenter()
        presenter.iHaveSavedChecked(true)
        verify(callbackManager).sendBackupEvent(BACKUP_QR_PAGE_QR_SAVED_TAPPED)
        verify(saveAndShareView).setActionButtonEnabled(true)
    }

    @Test
    fun `i have saved is checked and done was pressed, navigate to well done page`() {
        createPresenter()
        presenter.actionButtonClicked() // send the qr
        presenter.iHaveSavedChecked(true) // saved
        presenter.actionButtonClicked() // done
        verify(backupNavigator).navigateToWellDonePage()
    }

    @Test
    fun `i have saved is not checked and done was pressed, do nothing`() {
        createPresenter()
        presenter.actionButtonClicked() // send the qr
        presenter.actionButtonClicked() // done
        verifyNoMoreInteractions(backupNavigator)
    }

    @Test
    fun `i have saved is unchecked , disable done button`() {
        createPresenter()
        presenter.iHaveSavedChecked(false)
        verify(saveAndShareView).setQRImage(uri)
        verify(saveAndShareView).setActionButtonEnabled(false)
    }

    @Test
    fun `save instance state send qr has been clicked, show i saved checkbox`() {
        whenever(saveInstanceState.getBoolean(IS_SEND_EMAIL_CLICKED)) doReturn (true)
        createPresenter()
        verify(saveAndShareView).showIHaveSavedQRState()
    }

    @Test
    fun `could not generate the QR, show error`() {
        whenever(qrBarcodeGenerator.generate(KEY_TEST_DATA)) doThrow (QRBarcodeGenerator.QRBarcodeGeneratorException::class)
        createPresenter()
        verify(saveAndShareView).showErrorTryAgainLater()
    }

    @Test
    fun `save instance state is being updated, show i have saved checkbox`() {
        createPresenter()
        presenter.apply {
            actionButtonClicked()
            onSaveInstanceState(saveInstanceState)
            onAttach(view)
        }
        verify(saveAndShareView, times(2)).showIHaveSavedQRState()
    }

    private fun createPresenter() {
        presenter = SaveAndSharePresenterImpl(callbackManager, backupNavigator, qrBarcodeGenerator, KEY_TEST_DATA, saveInstanceState)
        presenter.onAttach(saveAndShareView)
    }
}