package com.mckimquyen.gallery.dlg

import org.fossify.commons.activities.BaseSimpleActivity
import org.fossify.commons.extensions.applyColorFilter
import org.fossify.commons.extensions.getAlertDialogBuilder
import org.fossify.commons.extensions.getProperTextColor
import org.fossify.commons.extensions.setupDialogStuff
import com.mckimquyen.gallery.databinding.DlgGrantAllFilesBinding
import com.mckimquyen.gallery.ext.launchGrantAllFilesIntent

class GrantAllFilesDlg(val activity: BaseSimpleActivity) {
    init {
        val binding = DlgGrantAllFilesBinding.inflate(activity.layoutInflater)
        binding.grantAllFilesImage.applyColorFilter(activity.getProperTextColor())

        activity.getAlertDialogBuilder()
            .setPositiveButton(org.fossify.commons.R.string.ok) { _, _ -> activity.launchGrantAllFilesIntent() }
            .setNegativeButton(org.fossify.commons.R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this) { }
            }
    }
}
