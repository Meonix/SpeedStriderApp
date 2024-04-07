package com.example.extension.views

import android.app.Activity
import androidx.appcompat.app.AlertDialog

/**
 * Dialog uti
 */
fun Activity.showAlertDialog(block: (AlertDialog.Builder.() -> Unit)? = null) {
    /**
     * --------------------------------------------------------------
     * |    Title                                                   |
     * |    Message                                                 |
     * |                                                            |
     * |    NeutralButton        NegativeButton | PositiveButton    |
     * |                                                            |
     * --------------------------------------------------------------
     */
    val dialog = AlertDialog.Builder(this)
    block?.invoke(dialog)
    dialog.create().show()
}