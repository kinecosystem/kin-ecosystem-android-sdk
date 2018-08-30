package com.ecosystem.kin.app

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import kotlinx.android.synthetic.main.pay_to_user_dialog.*

class PayToUserDialog(context: Context) : Dialog(context) {

    var userId: String? = null
                private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pay_to_user_dialog)
        pay_button.setOnClickListener {
            it.isEnabled = false
            it.isClickable = false
            user_id_edit_text.text.let {
                userId = if (it.isEmpty()) null else it.toString()
            }
            dismiss()
        }
    }
}