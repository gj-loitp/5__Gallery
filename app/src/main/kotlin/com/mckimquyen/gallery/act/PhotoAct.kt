package com.mckimquyen.gallery.act

import android.os.Bundle

class PhotoAct : PhotoVideoAct() {
    override fun onCreate(savedInstanceState: Bundle?) {
        mIsVideo = false
        super.onCreate(savedInstanceState)
    }
}
