package com.mckimquyen.gallery.act

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import org.fossify.commons.dialogs.FilePickerDialog
import org.fossify.commons.extensions.*
import org.fossify.commons.helpers.NavigationIcon
import org.fossify.commons.helpers.isRPlus
import org.fossify.commons.interfaces.RefreshRecyclerViewListener
import com.mckimquyen.gallery.R
import com.mckimquyen.gallery.adt.ManageFoldersAdt
import com.mckimquyen.gallery.databinding.AManageFoldersBinding
import com.mckimquyen.gallery.ext.config

class ExcludedFoldersAct : SimpleAct(), RefreshRecyclerViewListener {

    private val binding by viewBinding(AManageFoldersBinding::inflate)

    override fun attachBaseContext(newBase: Context) {
        val override = Configuration(newBase.resources.configuration)
        override.fontScale = 1.0f
        applyOverrideConfiguration(override)
        super.attachBaseContext(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        isMaterialActivity = true
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        updateFolders()
        setupOptionsMenu()
        binding.manageFoldersToolbar.title = getString(org.fossify.commons.R.string.excluded_folders)

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
        setupToolbar(binding.manageFoldersToolbar, NavigationIcon.Arrow)
    }

    private fun updateFolders() {
        val folders = ArrayList<String>()
        config.excludedFolders.mapTo(folders) { it }
        var placeholderText = getString(R.string.excluded_activity_placeholder)
        binding.manageFoldersPlaceholder.apply {
            beVisibleIf(folders.isEmpty())
            setTextColor(getProperTextColor())

            if (isRPlus() && !isExternalStorageManager()) {
                placeholderText = placeholderText.substringBefore("\n")
            }

            text = placeholderText
        }

        val adapter = ManageFoldersAdt(
            activity = this,
            folders = folders,
            isShowingExcludedFolders = true,
            listener = this,
            recyclerView = binding.manageFoldersList
        ) {}
        binding.manageFoldersList.adapter = adapter
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
            currPath = internalStoragePath,
            pickFile = false,
            showHidden = config.shouldShowHidden,
            showFAB = false,
            canAddShowHiddenButton = true,
            enforceStorageRestrictions = false,
        ) {
            config.lastFilepickerPath = it
            config.addExcludedFolder(it)
            updateFolders()
        }
    }
}
