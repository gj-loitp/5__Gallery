package com.mckimquyen.gallery.itf

import org.fossify.commons.models.FileDirItem
import com.mckimquyen.gallery.model.ThumbnailItem

interface MediaOperationsListener {
    fun refreshItems()

    fun tryDeleteFiles(
        fileDirItems: ArrayList<FileDirItem>,
        skipRecycleBin: Boolean,
    )

    fun selectedPaths(paths: ArrayList<String>)

    fun updateMediaGridDecoration(media: ArrayList<ThumbnailItem>)
}
