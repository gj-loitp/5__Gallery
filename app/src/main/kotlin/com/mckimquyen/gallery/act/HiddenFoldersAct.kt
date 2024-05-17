package com.mckimquyen.gallery.act

import android.os.Bundle
import org.fossify.commons.dialogs.FilePickerDialog
import org.fossify.commons.extensions.beVisibleIf
import org.fossify.commons.extensions.getProperTextColor
import org.fossify.commons.extensions.viewBinding
import org.fossify.commons.helpers.NavigationIcon
import org.fossify.commons.helpers.ensureBackgroundThread
import org.fossify.commons.interfaces.RefreshRecyclerViewListener
import com.mckimquyen.gallery.R
import com.mckimquyen.gallery.adt.ManageHiddenFoldersAdt
import com.mckimquyen.gallery.databinding.AManageFoldersBinding
import com.mckimquyen.gallery.ext.addNoMedia
import com.mckimquyen.gallery.ext.config
import com.mckimquyen.gallery.ext.getNoMediaFolders

class HiddenFoldersAct : SimpleAct(), RefreshRecyclerViewListener {

    private val binding by viewBinding(AManageFoldersBinding::inflate)
    override fun onCreate(savedInstanceState: Bundle?) {
        isMaterialActivity = true
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        updateFolders()
        setupOptionsMenu()
        binding.manageFoldersToolbar.title = getString(R.string.hidden_folders)

        updateMaterialActivityViews(
            mainCoordinatorLayout = binding.manageFoldersCoordinator,
            nestedView = binding.manageFoldersList,
            useTransparentNavigation = true,
            useTopSearchMenu = false
        )
        setupMaterialScrollListener(
            scrollingView = binding.manageFoldersList,
            toolbar = binding.manageFoldersToolbar
        )
    }

    override fun onResume() {
        super.onResume()
        setupToolbar(
            toolbar = binding.manageFoldersToolbar,
            toolbarNavigationIcon = NavigationIcon.Arrow
        )
    }

    private fun updateFolders() {
        getNoMediaFolders {
            runOnUiThread {
                binding.manageFoldersPlaceholder.apply {
                    text = getString(R.string.hidden_folders_placeholder)
                    beVisibleIf(it.isEmpty())
                    setTextColor(getProperTextColor())
                }

                val adapter = ManageHiddenFoldersAdt(
                    activity = this,
                    folders = it,
                    listener = this,
                    recyclerView = binding.manageFoldersList
                ) {}
                binding.manageFoldersList.adapter = adapter
            }
        }
    }

    private fun setupOptionsMenu() {
        binding.manageFoldersToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.addFolder -> addFolder()
                else -> return@setOnMenuItemClickListener false
            }
            return@setOnMenuItemClickListener true
        }
    }

    override fun refreshItems() {
        updateFolders()
    }

    private fun addFolder() {
        FilePickerDialog(
            activity = this,
            currPath = config.lastFilepickerPath,
            pickFile = false,
            showHidden = config.shouldShowHidden,
            showFAB = false,
            canAddShowHiddenButton = true
        ) {
            config.lastFilepickerPath = it
            ensureBackgroundThread {
                addNoMedia(it) {
                    updateFolders()
                }
            }
        }
    }
}
