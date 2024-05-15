package com.mckimquyen.gallery.dlg

import android.view.ViewGroup
import android.widget.RadioGroup
import org.fossify.commons.activities.BaseSimpleActivity
import org.fossify.commons.databinding.RadioButtonBinding
import org.fossify.commons.extensions.beVisibleIf
import org.fossify.commons.extensions.getAlertDialogBuilder
import org.fossify.commons.extensions.getBasePath
import org.fossify.commons.extensions.setupDialogStuff
import com.mckimquyen.gallery.databinding.DlgExcludeFolderBinding
import com.mckimquyen.gallery.ext.config

class ExcludeFolderDlg(
    val activity: BaseSimpleActivity,
    private val selectedPaths: List<String>,
    val callback: () -> Unit,
) {
    private val alternativePaths = getAlternativePathsList()
    private var radioGroup: RadioGroup? = null

    init {
        val binding = DlgExcludeFolderBinding.inflate(activity.layoutInflater).apply {
            excludeFolderParent.beVisibleIf(alternativePaths.size > 1)

            radioGroup = excludeFolderRadioGroup
            excludeFolderRadioGroup.beVisibleIf(alternativePaths.size > 1)
        }

        alternativePaths.forEachIndexed { index, _ ->
            val radioButton = RadioButtonBinding.inflate(activity.layoutInflater).root.apply {
                text = alternativePaths[index]
                isChecked = index == 0
                id = index
            }
            radioGroup?.addView(
                /* child = */ radioButton,
                /* params = */ RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            )
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(org.fossify.commons.R.string.ok) { _, _ -> dialogConfirmed() }
            .setNegativeButton(org.fossify.commons.R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this)
            }
    }

    private fun dialogConfirmed() {
        val path = if (alternativePaths.isEmpty()) selectedPaths[0] else alternativePaths[radioGroup!!.checkedRadioButtonId]
        activity.config.addExcludedFolder(path)
        callback()
    }

    private fun getAlternativePathsList(): List<String> {
        val pathsList = ArrayList<String>()
        if (selectedPaths.size > 1)
            return pathsList

        val path = selectedPaths[0]
        var basePath = path.getBasePath(activity)
        val relativePath = path.substring(basePath.length)
        val parts = relativePath.split("/").filter(String::isNotEmpty)
        if (parts.isEmpty())
            return pathsList

        pathsList.add(basePath)
        if (basePath == "/")
            basePath = ""

        for (part in parts) {
            basePath += "/$part"
            pathsList.add(basePath)
        }

        return pathsList.reversed()
    }
}
