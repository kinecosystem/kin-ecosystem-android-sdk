package com.kin.ecosystem.settings.presenter

import android.content.Intent
import com.kin.ecosystem.base.BasePresenter
import com.kin.ecosystem.common.Observer
import com.kin.ecosystem.common.exception.ClientException
import com.kin.ecosystem.common.model.Balance
import com.kin.ecosystem.core.Log
import com.kin.ecosystem.core.Logger
import com.kin.ecosystem.core.bi.EventLogger
import com.kin.ecosystem.core.bi.RecoveryBackupEvents
import com.kin.ecosystem.core.bi.RecoveryRestoreEvents
import com.kin.ecosystem.core.bi.events.BackupWalletFailed
import com.kin.ecosystem.core.bi.events.SettingsPageViewed
import com.kin.ecosystem.core.data.blockchain.BlockchainSource
import com.kin.ecosystem.core.data.settings.SettingsDataSource
import com.kin.ecosystem.main.INavigator
import com.kin.ecosystem.recovery.BackupAndRestoreCallback
import com.kin.ecosystem.recovery.exception.BackupAndRestoreException
import com.kin.ecosystem.settings.BackupManager
import com.kin.ecosystem.settings.view.ISettingsView
import com.kin.ecosystem.settings.view.ISettingsView.IconColor
import com.kin.ecosystem.settings.view.ISettingsView.Item
import java.math.BigDecimal

class SettingsPresenter(private val settingsDataSource: SettingsDataSource,
                        private val blockchainSource: BlockchainSource,
                        private var backupManager: BackupManager,
                        private val navigator: INavigator?,
                        private val eventLogger: EventLogger) : BasePresenter<ISettingsView>(), ISettingsPresenter {

    private var balanceObserver: Observer<Balance>? = null
    private var currentBalance = blockchainSource.balance
    private var publicAddress = blockchainSource.publicAddress

    init {
        registerToEvents()
        registerToCallbacks()
    }

    override fun onAttach(view: ISettingsView) {
        super.onAttach(view)
        eventLogger.send(SettingsPageViewed.create())
    }

    override fun onResume() {
        updateSettingsIcon()
    }

    override fun onPause() {
        removeBalanceObserver()
    }

    override fun onDetach() {
        super.onDetach()
        backupManager.release()
    }

    private fun addBalanceObserver() {
        balanceObserver = object : Observer<Balance>() {
            override fun onChanged(value: Balance) {
                currentBalance = value
                if (isGreaterThenZero(value)) {
                    updateSettingsIcon()
                }
            }
        }.also {
            blockchainSource.addBalanceObserver(it, false)
        }
    }

    private fun isGreaterThenZero(value: Balance): Boolean {
        return value.amount.compareTo(BigDecimal.ZERO) == 1
    }

    private fun updateSettingsIcon() {
        if (!publicAddress.isEmpty()) {
            if (!settingsDataSource.isBackedUp(publicAddress)) {
                if (isGreaterThenZero(currentBalance)) {
                    changeTouchIndicator(Item.ITEM_BACKUP, true)
                    removeBalanceObserver()
                } else {
                    addBalanceObserver()
                    changeTouchIndicator(Item.ITEM_BACKUP, false)
                }
            } else {
                changeIconColor(Item.ITEM_BACKUP, IconColor.PRIMARY)
                changeTouchIndicator(Item.ITEM_BACKUP, false)
            }
        }
    }

    private fun removeBalanceObserver() {
        balanceObserver?.let {
            blockchainSource.removeBalanceObserver(it, false)
            balanceObserver = null
        }
    }

    override fun backupClicked() {
        try {
            backupManager.backupFlow()
        } catch (e: ClientException) {
            // Could not happen in this case.
        }
    }

    override fun restoreClicked() {
        try {
            backupManager.restoreFlow()
        } catch (e: ClientException) {
            // Could not happen in this case.
        }
    }

    override fun backClicked() {
        navigator?.navigateBack()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        backupManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun registerToEvents() {
        backupManager.let {
            it.registerBackupEvents(RecoveryBackupEvents(eventLogger))
            it.registerRestoreEvents(RecoveryRestoreEvents(eventLogger))
        }
    }

    private fun registerToCallbacks() {
        backupManager.registerBackupCallback(object : BackupAndRestoreCallback {
            override fun onSuccess() {
                onBackupSuccess()
            }

            override fun onCancel() {
                Logger.log(Log().withTag(TAG).put("BackupCallback", "onCancel"))
            }

            override fun onFailure(exception: BackupAndRestoreException) {
                eventLogger.send(BackupWalletFailed.create(getErrorMessage(exception, BACKUP_DEFAULT_ERROR_MSG)))
            }
        })

        backupManager.registerRestoreCallback(object : BackupAndRestoreCallback {
            override fun onSuccess() {
                Logger.log(Log().withTag(TAG).put("RestoreCallback", "onSuccess"))
            }

            override fun onCancel() {
                Logger.log(Log().withTag(TAG).put("RestoreCallback", "onCancel"))
            }

            override fun onFailure(throwable: BackupAndRestoreException) {
                showCouldNotImportAccountError()
            }
        })
    }

    private fun getErrorMessage(exception: BackupAndRestoreException, defaultMsg: String): String {
        return exception.cause?.let { cause -> cause.message ?:  exception.message ?: defaultMsg }
                ?: exception.message ?: defaultMsg
    }


    private fun showCouldNotImportAccountError() {
        view?.showCouldNotImportAccount()
    }

    private fun onBackupSuccess() {
        updateSettingsIcon()
    }

    private fun changeTouchIndicator(item: Item, isVisible: Boolean) {
        view?.changeTouchIndicatorVisibility(item, isVisible)
    }

    private fun changeIconColor(item: Item, color: IconColor) {
        view?.setIconColor(item, color)
    }

    companion object {

        private val TAG = SettingsPresenter::class.java.simpleName
        private const val BACKUP_DEFAULT_ERROR_MSG = "Backup failed - with unknown reason"
    }
}
