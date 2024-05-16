package com.mckimquyen.gallery.dlg

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import org.fossify.commons.activities.BaseSimpleActivity
import org.fossify.commons.extensions.getAlertDialogBuilder
import org.fossify.commons.extensions.setupDialogStuff
import com.mckimquyen.gallery.R

@SuppressLint("InflateParams")
class AllFilesPermissionDlg(
    val activity: BaseSimpleActivity,
    message: String = "",
    val callback: (result: Boolean) -> Unit,
    val neutralPressed: () -> Unit,
) {
    private var dialog: AlertDialog? = null

    init {
        val view = activity.layoutInflater.inflate(org.fossify.commons.R.layout.dialog_message, null)
        view.findViewById<TextView>(R.id.message).text = message

        activity.getAlertDialogBuilder().setPositiveButton(R.string.all_files) { _, _ -> positivePressed() }
            .setNeutralButton(R.string.media_only) { _, _ -> neutralPressed() }
            .apply {
                activity.setupDialogStuff(view, this) { alertDialog ->
                    dialog = alertDialog
                }
            }
    }

    private fun positivePressed() {
        dialog?.dismiss()
        callback(true)
    }
}
