package com.mckimquyen.gallery

import android.app.Application
import com.github.ajalt.reprint.core.Reprint
import com.squareup.picasso.Downloader
import com.squareup.picasso.Picasso
import okhttp3.Request
import okhttp3.Response
import org.fossify.commons.extensions.checkUseEnglish

//TODO firebase analytic
//TODO why you see ad
//TODO ad applovin
//TODO vung bi mat de show applovin config

//TODO license
//TODO rate, more app, share app
//TODO github
//TODO gen ic_launcher https://easyappicon.com/
//TODO UI ios switch
//TODO check xem touch point 16 days ko work
//TODO keystore

//done mckimquyen
//rename app
//proguard
//leak canary

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
    }
}
