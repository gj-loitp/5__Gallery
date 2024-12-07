package com.mckimquyen.gallery

import android.app.Application
import com.github.ajalt.reprint.core.Reprint
import com.mckimquyen.gallery.ext.setupApplovinAd
import com.squareup.picasso.Downloader
import com.squareup.picasso.Picasso
import okhttp3.Request
import okhttp3.Response
import org.fossify.commons.extensions.checkUseEnglish

//TODO firebase analytic
//TODO why you see ad
//TODO UI ios switch
//TODO in app review

//120hz
//vung bi mat de show applovin config
//ad applovin
//font scale
//done mckimquyen
//rename app
//proguard
//leak canary
//gen ic_launcher https://easyappicon.com/
//license
//rate, more app, share app
//github
//keystore
//20 tester https://github.com/gj-loitp/20-TESTER-FOR-CLOSED-TESTING

class RApp : Application() {
    override fun onCreate() {
        super.onCreate()
        setupApp()
    }

    private fun setupApp() {
        checkUseEnglish()
        Reprint.initialize(this)
        Picasso.setSingletonInstance(Picasso.Builder(this).downloader(object : Downloader {
            override fun load(request: Request) = Response.Builder().build()

            override fun shutdown() {}
        }).build())
        this.setupApplovinAd()
    }
}
