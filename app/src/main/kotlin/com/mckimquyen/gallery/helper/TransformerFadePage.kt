package com.mckimquyen.gallery.helper

import android.view.View
import androidx.viewpager.widget.ViewPager

class TransformerFadePage : ViewPager.PageTransformer {
    override fun transformPage(view: View, position: Float) {
        view.translationX = view.width * -position

        view.alpha = if (position <= -1f || position >= 1f) {
            0f
        } else if (position == 0f) {
            1f
        } else {
            1f - Math.abs(position)
        }
    }
}
