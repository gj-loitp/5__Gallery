package com.mckimquyen.gallery.act

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle

class PhotoAct : PhotoVideoAct() {
    override fun onCreate(savedInstanceState: Bundle?) {
        mIsVideo = false
        super.onCreate(savedInstanceState)
    }
}
