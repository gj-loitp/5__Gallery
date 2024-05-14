package com.mckimquyen.gallery.svg

import android.content.Context
import android.graphics.drawable.PictureDrawable

import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.caverock.androidsvg.SVG

import java.io.InputStream

@GlideModule
class SvgModule : AppGlideModule() {
    override fun registerComponents(
        context: Context,
        glide: Glide,
        registry: Registry,
    ) {
        registry.register(
            /* resourceClass = */ SVG::class.java,
            /* transcodeClass = */ PictureDrawable::class.java,
            /* transcoder = */ SvgDrawableTranscoder()
        ).append(
            /* dataClass = */ InputStream::class.java,
            /* resourceClass = */ SVG::class.java,
            /* decoder = */ SvgDecoder()
        )
    }

    override fun isManifestParsingEnabled() = false
}
