package com.ecosystem.kin.app.main

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.ecosystem.kin.app.R
import kotlinx.android.synthetic.main.pay_to_user_dialog.*

class PayToUserDialog(context: Context) : Dialog(context) {

    var recipientId: String? = null
                private set

    var openGifting = false
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pay_to_user_dialog)
        pay_button.setOnClickListener {
            performAction()
        }
        gift_button.setOnClickListener {
            performAction(openGiftingDialog = true)
        }
    }

    private fun performAction(openGiftingDialog: Boolean = false) {
        disableButtons()
        getRecipientId()
        openGifting = openGiftingDialog
        dismiss()
    }

    private fun getRecipientId() {
        user_id_edit_text.text.let {
            recipientId = if (it.isEmpty()) null else it.toString()
        }
    }

    private fun disableButtons() {
        disable(pay_button)
        disable(gift_button)
    }

    private fun disable(view: View) {
        view.apply {
            isEnabled = false
            isClickable = false
        }
    }
}