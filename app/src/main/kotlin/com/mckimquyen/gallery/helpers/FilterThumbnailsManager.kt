package com.mckimquyen.gallery.helpers

import android.graphics.Bitmap
import com.mckimquyen.gallery.models.FilterItem

class FilterThumbnailsManager {
    private var filterThumbnails = ArrayList<FilterItem>(10)
    private var processedThumbnails = ArrayList<FilterItem>(10)

    fun addThumb(filterItem: FilterItem) {
        filterThumbnails.add(filterItem)
    }

    fun processThumbs(): ArrayList<FilterItem> {
        for (filterItem in filterThumbnails) {
            filterItem.bitmap = filterItem.filter.processFilter(Bitmap.createBitmap(filterItem.bitmap))
            processedThumbnails.add(filterItem)
        }
        return processedThumbnails
    }

    fun clearThumbs() {
        filterThumbnails = ArrayList()
        processedThumbnails = ArrayList()
    }
}
