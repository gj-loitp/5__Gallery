package com.mckimquyen.gallery.dlg

import android.content.DialogInterface
import org.fossify.commons.activities.BaseSimpleActivity
import org.fossify.commons.extensions.*
import org.fossify.commons.helpers.*
import com.mckimquyen.gallery.R
import com.mckimquyen.gallery.databinding.DlgChangeSortingBinding
import com.mckimquyen.gallery.ext.config
import com.mckimquyen.gallery.helper.SHOW_ALL

class ChangeSortingDialog(
    val activity: BaseSimpleActivity, val isDirectorySorting: Boolean, val showFolderCheckbox: Boolean,
    val path: String = "", val callback: () -> Unit
) :
    DialogInterface.OnClickListener {
    private var currSorting = 0
    private var config = activity.config
    private var pathToUse = if (!isDirectorySorting && path.isEmpty()) SHOW_ALL else path
    private val binding: DlgChangeSortingBinding

    init {
        currSorting = if (isDirectorySorting) config.directorySorting else config.getFolderSorting(pathToUse)
        binding = DlgChangeSortingBinding.inflate(activity.layoutInflater).apply {
            sortingDialogOrderDivider.beVisibleIf(showFolderCheckbox || (currSorting and SORT_BY_NAME != 0 || currSorting and SORT_BY_PATH != 0))

            sortingDialogNumericSorting.beVisibleIf(showFolderCheckbox && (currSorting and SORT_BY_NAME != 0 || currSorting and SORT_BY_PATH != 0))
            sortingDialogNumericSorting.isChecked = currSorting and SORT_USE_NUMERIC_VALUE != 0

            sortingDialogUseForThisFolder.beVisibleIf(showFolderCheckbox)
            sortingDialogUseForThisFolder.isChecked = config.hasCustomSorting(pathToUse)
            sortingDialogBottomNote.beVisibleIf(!isDirectorySorting)
            sortingDialogRadioCustom.beVisibleIf(isDirectorySorting)
        }

        activity.getAlertDialogBuilder()
            .setPositiveButton(org.fossify.commons.R.string.ok, this)
            .setNegativeButton(org.fossify.commons.R.string.cancel, null)
            .apply {
                activity.setupDialogStuff(binding.root, this, org.fossify.commons.R.string.sort_by)
            }

        setupSortRadio()
        setupOrderRadio()
    }

    private fun setupSortRadio() {
        val sortingRadio = binding.sortingDialogRadioSorting
        sortingRadio.setOnCheckedChangeListener { _, checkedId ->
            val isSortingByNameOrPath = checkedId == binding.sortingDialogRadioName.id || checkedId == binding.sortingDialogRadioPath.id
            binding.sortingDialogNumericSorting.beVisibleIf(isSortingByNameOrPath)
            binding.sortingDialogOrderDivider.beVisibleIf(binding.sortingDialogNumericSorting.isVisible() || binding.sortingDialogUseForThisFolder.isVisible())

            val hideSortOrder = checkedId == binding.sortingDialogRadioCustom.id || checkedId == binding.sortingDialogRadioRandom.id
            binding.sortingDialogRadioOrder.beGoneIf(hideSortOrder)
            binding.sortingDialogSortingDivider.beGoneIf(hideSortOrder)
        }

        val sortBtn = when {
            currSorting and SORT_BY_PATH != 0 -> binding.sortingDialogRadioPath
            currSorting and SORT_BY_SIZE != 0 -> binding.sortingDialogRadioSize
            currSorting and SORT_BY_DATE_MODIFIED != 0 -> binding.sortingDialogRadioLastModified
            currSorting and SORT_BY_DATE_TAKEN != 0 -> binding.sortingDialogRadioDateTaken
            currSorting and SORT_BY_RANDOM != 0 -> binding.sortingDialogRadioRandom
            currSorting and SORT_BY_CUSTOM != 0 -> binding.sortingDialogRadioCustom
            else -> binding.sortingDialogRadioName
        }
        sortBtn.isChecked = true
    }

    private fun setupOrderRadio() {
        var orderBtn = binding.sortingDialogRadioAscending

        if (currSorting and SORT_DESCENDING != 0) {
            orderBtn = binding.sortingDialogRadioDescending
        }
        orderBtn.isChecked = true
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        val sortingRadio = binding.sortingDialogRadioSorting
        var sorting = when (sortingRadio.checkedRadioButtonId) {
            R.id.sortingDialogRadioName -> SORT_BY_NAME
            R.id.sortingDialogRadioPath -> SORT_BY_PATH
            R.id.sortingDialogRadioSize -> SORT_BY_SIZE
            R.id.sortingDialogRadioLastModified -> SORT_BY_DATE_MODIFIED
            R.id.sortingDialogRadioRandom -> SORT_BY_RANDOM
            R.id.sortingDialogRadioCustom -> SORT_BY_CUSTOM
            else -> SORT_BY_DATE_TAKEN
        }

        if (binding.sortingDialogRadioOrder.checkedRadioButtonId == R.id.sortingDialogRadioDescending) {
            sorting = sorting or SORT_DESCENDING
        }

        if (binding.sortingDialogNumericSorting.isChecked) {
            sorting = sorting or SORT_USE_NUMERIC_VALUE
        }

        if (isDirectorySorting) {
            config.directorySorting = sorting
        } else {
            if (binding.sortingDialogUseForThisFolder.isChecked) {
                config.saveCustomSorting(pathToUse, sorting)
            } else {
                config.removeCustomSorting(pathToUse)
                config.sorting = sorting
            }
        }

        if (currSorting != sorting) {
            callback()
        }
    }
}