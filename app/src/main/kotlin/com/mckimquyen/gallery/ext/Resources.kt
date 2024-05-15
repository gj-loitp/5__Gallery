package com.mckimquyen.gallery.ext

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.util.TypedValue

@SuppressLint("DiscouragedApi", "InternalInsetResource")
fun Resources.getActionBarHeight(context: Context): Int {
    val tv = TypedValue()
    return if (context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
        TypedValue.complexToDimensionPixelSize(tv.data, displayMetrics)
    } else
        0
}

@SuppressLint("DiscouragedApi", "InternalInsetResource")
fun Resources.getStatusBarHeight(): Int {
    val id = getIdentifier(
        /* name = */ "status_bar_height",
        /* defType = */ "dimen",
        /* defPackage = */ "android"
    )
    return if (id > 0) {
        getDimensionPixelSize(id)
    } else
        0
}

@SuppressLint("DiscouragedApi", "InternalInsetResource")
fun Resources.getNavBarHeight(): Int {
    val id = getIdentifier(
        /* name = */ "navigation_bar_height",
        /* defType = */ "dimen",
        /* defPackage = */ "android"
    )
    return if (id > 0) {
        getDimensionPixelSize(id)
    } else
        0
}
