package com.kin.ecosystem.recovery.backup.presenter

import com.kin.ecosystem.recovery.backup.view.BackupInfoView
import com.kin.ecosystem.recovery.backup.view.BackupNavigator
import com.kin.ecosystem.recovery.events.BackupEventCode.BACKUP_WELCOME_PAGE_START_TAPPED
import com.kin.ecosystem.recovery.events.BackupEventCode.BACKUP_WELCOME_PAGE_VIEWED
import com.kin.ecosystem.recovery.events.CallbackManager
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test

class BackupInfoPresenterImplTest {

    private val callbackManager: CallbackManager = mock()
    private val backupNavigator: BackupNavigator = mock()

    private val view: BackupInfoView = mock()

    private lateinit var presenter: BackupInfoPresenterImpl

    @Before
    fun setUp() {
        presenter = BackupInfoPresenterImpl(callbackManager, backupNavigator)
        presenter.onAttach(view)
    }

    @Test
    fun `send welcome page view event`() {
        verify(callbackManager).sendBackupEvent(BACKUP_WELCOME_PAGE_VIEWED)
    }

    @Test
    fun `lets go clicked, navigate to create password page`() {
        presenter.letsGoButtonClicked()
        verify(callbackManager).sendBackupEvent(BACKUP_WELCOME_PAGE_START_TAPPED)
        verify(backupNavigator).navigateToCreatePasswordPage()
    }

    @Test
    fun `onBackClicked, close flow`() {
        presenter.onBackClicked()
        verify(backupNavigator).closeFlow()
    }
}