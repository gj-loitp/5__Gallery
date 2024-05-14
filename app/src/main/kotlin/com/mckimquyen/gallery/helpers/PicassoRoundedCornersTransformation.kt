package com.mckimquyen.gallery.helpers

import android.graphics.*
import com.squareup.picasso.Transformation

// taken from https://stackoverflow.com/a/35241525/1967672
class PicassoRoundedCornersTransformation(private val radius: Float) : Transformation {

    override fun transform(source: Bitmap): Bitmap {
        val size = Math.min(source.width, source.height)
        val x = (source.width - size) / 2
        val y = (source.height - size) / 2
        val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)
        if (squaredBitmap != source) {
            source.recycle()
        }

        val bitmap = Bitmap.createBitmap(
            /* width = */ size,
            /* height = */ size,
            /* config = */ source.config
        )
        val canvas = Canvas(bitmap)
        val paint = Paint()
        val shader = BitmapShader(
            /* bitmap = */ squaredBitmap,
            /* tileX = */ Shader.TileMode.CLAMP,
            /* tileY = */ Shader.TileMode.CLAMP
        )
        paint.shader = shader
        paint.isAntiAlias = true

        val rect = RectF(
            /* left = */ 0f,
            /* top = */ 0f,
            /* right = */ source.width.toFloat(),
            /* bottom = */ source.height.toFloat()
        )
        canvas.drawRoundRect(
            /* rect = */ rect,
            /* rx = */ radius,
            /* ry = */ radius,
            /* paint = */ paint
        )
        squaredBitmap.recycle()
        return bitmap
    }

    override fun key() = "rounded_corners"
}
