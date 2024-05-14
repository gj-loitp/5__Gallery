package com.mckimquyen.gallery.helper

import android.content.Context
import android.graphics.*
import android.net.Uri
import com.davemorrissey.labs.subscaleview.ImageRegionDecoder

class DecoderPicassoRegion(
    private val showHighestQuality: Boolean,
    private val screenWidth: Int,
    private val screenHeight: Int,
    private val minTileDpi: Int,
) : ImageRegionDecoder {
    private var decoder: BitmapRegionDecoder? = null
    private val decoderLock = Any()

    override fun init(context: Context, uri: Uri): Point {
        val newUri = Uri.parse(uri.toString().replace("%", "%25").replace("#", "%23"))
        val inputStream = context.contentResolver.openInputStream(newUri)
        decoder = BitmapRegionDecoder.newInstance(
            /* is = */ inputStream!!,
            /* isShareable = */ false
        )
        return Point(
            /* x = */ decoder!!.width,
            /* y = */ decoder!!.height
        )
    }

    override fun decodeRegion(rect: Rect, sampleSize: Int): Bitmap {
        synchronized(decoderLock) {
            var newSampleSize = sampleSize
            if (!showHighestQuality && minTileDpi == LOW_TILE_DPI) {
                if ((rect.width() > rect.height() && screenWidth > screenHeight) || (rect.height() > rect.width() && screenHeight > screenWidth)) {
                    if ((rect.width() / sampleSize > screenWidth || rect.height() / sampleSize > screenHeight)) {
                        newSampleSize *= 2
                    }
                }
            }

            val options = BitmapFactory.Options()
            options.inSampleSize = newSampleSize
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            val bitmap = decoder!!.decodeRegion(/* rect = */ rect, /* options = */ options)
            return bitmap ?: throw RuntimeException("Region decoder returned null bitmap - image format may not be supported")
        }
    }

    override fun isReady() = decoder != null && !decoder!!.isRecycled

    override fun recycle() {
        decoder?.recycle()
    }
}
