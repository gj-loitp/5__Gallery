package com.mckimquyen.gallery.act

import android.os.Bundle

class VideoAct : PhotoVideoAct() {

    override fun onCreate(savedInstanceState: Bundle?) {
        mIsVideo = true
        super.onCreate(savedInstanceState)
    }
}
