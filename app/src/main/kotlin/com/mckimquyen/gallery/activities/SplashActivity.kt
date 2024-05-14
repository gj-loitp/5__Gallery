package com.mckimquyen.gallery.activities

import android.content.Intent
import org.fossify.commons.activities.BaseSplashActivity
import org.fossify.commons.helpers.ensureBackgroundThread
import com.mckimquyen.gallery.extensions.config
import com.mckimquyen.gallery.extensions.favoritesDB
import com.mckimquyen.gallery.extensions.getFavoriteFromPath
import com.mckimquyen.gallery.extensions.mediaDB
import com.mckimquyen.gallery.model.Favorite

class SplashActivity : BaseSplashActivity() {
    override fun initActivity() {
        // check if previously selected favorite items have been properly migrated into the new Favorites table
        if (config.wereFavoritesMigrated) {
            launchActivity()
        } else {
            if (config.appRunCount == 0) {
                config.wereFavoritesMigrated = true
                launchActivity()
            } else {
                config.wereFavoritesMigrated = true
                ensureBackgroundThread {
                    val favorites = ArrayList<Favorite>()
                    val favoritePaths = mediaDB.getFavorites().map { it.path }.toMutableList() as ArrayList<String>
                    favoritePaths.forEach {
                        favorites.add(getFavoriteFromPath(it))
                    }
                    favoritesDB.insertAll(favorites)

                    runOnUiThread {
                        launchActivity()
                    }
                }
            }
        }
    }

    private fun launchActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
