package com.mckimquyen.gallery.dlg

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import org.fossify.commons.extensions.beGoneIf
import org.fossify.commons.extensions.getAlertDialogBuilder
import org.fossify.commons.extensions.setupDialogStuff
import com.mckimquyen.gallery.databinding.DlgDeleteWithRememberBinding

class DeleteWithRememberDlg(
    private val activity: Activity,
    message: String,
    showSkipRecycleBinOption: Boolean,
    private val callback: (
        remember: Boolean,
        skipRecycleBin: Boolean,
    ) -> Unit,
) {

    private var dialog: AlertDialog? = null
    private val binding = DlgDeleteWithRememberBinding.inflate(activity.layoutInflater)

    init {
        binding.deleteRememberTitle.text = message
        binding.skipTheRecycleBinCheckbox.beGoneIf(!showSkipRecycleBinOption)
        activity.getAlertDialogBuilder()
            .setPositiveButton(org.fossify.commons.R.string.yes) { _, _ ->
                dialogConfirmed()
            }
            .setNegativeButton(org.fossify.commons.R.string.no, null)
            .apply {
                activity.setupDialogStuff(binding.root, this) { alertDialog ->
                    dialog = alertDialog
                }
            }
    }

    private fun dialogConfirmed() {
        dialog?.dismiss()
        callback(
            binding.deleteRememberCheckbox.isChecked,
            binding.skipTheRecycleBinCheckbox.isChecked
        )
    }
}
