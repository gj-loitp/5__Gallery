package com.mckimquyen.gallery.ext

import android.os.SystemClock
import android.view.MotionEvent
import android.view.View

fun View.sendFakeClick(
    x: Float,
    y: Float,
) {
    val uptime = SystemClock.uptimeMillis()
    val event = MotionEvent.obtain(
        /* downTime = */ uptime,
        /* eventTime = */ uptime,
        /* action = */ MotionEvent.ACTION_DOWN,
        /* x = */ x,
        /* y = */ y,
        /* metaState = */ 0
    )
    dispatchTouchEvent(event)
    event.action = MotionEvent.ACTION_UP
    dispatchTouchEvent(event)
}
