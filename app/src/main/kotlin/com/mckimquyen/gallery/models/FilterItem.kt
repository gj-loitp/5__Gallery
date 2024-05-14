package com.mckimquyen.gallery.models

import android.graphics.Bitmap
import androidx.annotation.Keep
import com.zomato.photofilters.imageprocessors.Filter

@Keep
data class FilterItem(var bitmap: Bitmap, val filter: Filter)
