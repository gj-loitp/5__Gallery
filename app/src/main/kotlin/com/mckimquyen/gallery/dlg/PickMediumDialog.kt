package com.mckimquyen.gallery.dlg

import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import org.fossify.commons.activities.BaseSimpleActivity
import org.fossify.commons.extensions.getAlertDialogBuilder
import org.fossify.commons.extensions.getProperPrimaryColor
import org.fossify.commons.extensions.setupDialogStuff
import org.fossify.commons.helpers.VIEW_TYPE_GRID
import org.fossify.commons.views.MyGridLayoutManager
import com.mckimquyen.gallery.R
import com.mckimquyen.gallery.adapters.MediaAdapter
import com.mckimquyen.gallery.asynctasks.GetMediaAsynctask
import com.mckimquyen.gallery.databinding.DlgMediumPickerBinding
import com.mckimquyen.gallery.ext.config
import com.mckimquyen.gallery.ext.getCachedMedia
import com.mckimquyen.gallery.helper.DecorationGridSpacingItem
import com.mckimquyen.gallery.helper.SHOW_ALL
import com.mckimquyen.gallery.model.Medium
import com.mckimquyen.gallery.model.ThumbnailItem
import com.mckimquyen.gallery.model.ThumbnailSection

class PickMediumDialog(val activity: BaseSimpleActivity, val path: String, val callback: (path: String) -> Unit) {
    private var dialog: AlertDialog? = null
    private var shownMedia = ArrayList<ThumbnailItem>()
    private val binding = DlgMediumPickerBinding.inflate(activity.layoutInflater)
    private val config = activity.config
    private val viewType = config.getFolderViewType(if (config.showAll) SHOW_ALL else path)
    private var isGridViewType = viewType == VIEW_TYPE_GRID

    init {
        (binding.mediaGrid.layoutManager as MyGridLayoutManager).apply {
            orientation = if (config.scrollHorizontally && isGridViewType) RecyclerView.HORIZONTAL else RecyclerView.VERTICAL
            spanCount = if (isGridViewType) config.mediaColumnCnt else 1
        }

        binding.mediaFastscroller.updateColors(activity.getProperPrimaryColor())

        activity.getAlertDialogBuilder()
            .setPositiveButton(org.fossify.commons.R.string.ok, null)
            .setNegativeButton(org.fossify.commons.R.string.cancel, null)
            .setNeutralButton(R.string.other_folder) { dialogInterface, i -> showOtherFolder() }
            .apply {
                activity.setupDialogStuff(binding.root, this, org.fossify.commons.R.string.select_photo) { alertDialog ->
                    dialog = alertDialog
                }
            }

        activity.getCachedMedia(path) {
            val media = it.filter { it is Medium } as ArrayList
            if (media.isNotEmpty()) {
                activity.runOnUiThread {
                    gotMedia(media)
                }
            }
        }

        GetMediaAsynctask(activity, path, false, false, false) {
            gotMedia(it)
        }.execute()
    }

    private fun showOtherFolder() {
        PickDirectoryDialog(activity, path, true, true, false, false) {
            callback(it)
            dialog?.dismiss()
        }
    }

    private fun gotMedia(media: ArrayList<ThumbnailItem>) {
        if (media.hashCode() == shownMedia.hashCode())
            return

        shownMedia = media
        val adapter = MediaAdapter(activity, shownMedia.clone() as ArrayList<ThumbnailItem>, null, true, false, path, binding.mediaGrid) {
            if (it is Medium) {
                callback(it.path)
                dialog?.dismiss()
            }
        }

        val scrollHorizontally = config.scrollHorizontally && isGridViewType
        binding.apply {
            mediaGrid.adapter = adapter
            mediaFastscroller.setScrollVertically(!scrollHorizontally)
        }
        handleGridSpacing(media)
    }

    private fun handleGridSpacing(media: ArrayList<ThumbnailItem>) {
        if (isGridViewType) {
            val spanCount = config.mediaColumnCnt
            val spacing = config.thumbnailSpacing
            val useGridPosition = media.firstOrNull() is ThumbnailSection

            var currentGridDecoration: DecorationGridSpacingItem? = null
            if (binding.mediaGrid.itemDecorationCount > 0) {
                currentGridDecoration = binding.mediaGrid.getItemDecorationAt(0) as DecorationGridSpacingItem
                currentGridDecoration.items = media
            }

            val newGridDecoration = DecorationGridSpacingItem(spanCount, spacing, config.scrollHorizontally, config.fileRoundedCorners, media, useGridPosition)
            if (currentGridDecoration.toString() != newGridDecoration.toString()) {
                if (currentGridDecoration != null) {
                    binding.mediaGrid.removeItemDecoration(currentGridDecoration)
                }
                binding.mediaGrid.addItemDecoration(newGridDecoration)
            }
        }
    }
}