package com.mckimquyen.gallery.helper

import android.graphics.Bitmap
import android.graphics.Matrix
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

class RotateTransformation(var degrees: Int) : BitmapTransformation() {
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {}

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int,
    ): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees.toFloat())
        return Bitmap.createBitmap(
            /* source = */ toTransform,
            /* x = */ 0,
            /* y = */ 0,
            /* width = */ toTransform.width,
            /* height = */ toTransform.height,
            /* m = */ matrix,
            /* filter = */ true
        )
    }
}
