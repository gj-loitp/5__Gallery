package com.mckimquyen.gallery.act

import android.content.Context
import android.content.res.Configuration
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.provider.MediaStore.Images
import android.provider.MediaStore.Video
import android.view.Display
import android.view.WindowManager
import org.fossify.commons.activities.BaseSimpleActivity
import org.fossify.commons.dialogs.FilePickerDialog
import org.fossify.commons.extensions.getParentPath
import org.fossify.commons.extensions.getRealPathFromURI
import org.fossify.commons.extensions.scanPathRecursively
import org.fossify.commons.helpers.ensureBackgroundThread
import org.fossify.commons.helpers.isPiePlus
import com.mckimquyen.gallery.R
import com.mckimquyen.gallery.ext.addPathToDB
import com.mckimquyen.gallery.ext.config
import com.mckimquyen.gallery.ext.updateDirectoryPath

open class SimpleAct : BaseSimpleActivity() {
    private val observer = object : ContentObserver(null) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            super.onChange(selfChange, uri)
            if (uri != null) {
                val path = getRealPathFromURI(uri)
                if (path != null) {
                    updateDirectoryPath(path.getParentPath())
                    addPathToDB(path)
                }
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val override = Configuration(newBase.resources.configuration)
        override.fontScale = 1.0f
        applyOverrideConfiguration(override)
        super.attachBaseContext(newBase)
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            enableAdaptiveRefreshRate()
        }
    }

    private fun enableAdaptiveRefreshRate() {
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val display: Display? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display // Sử dụng API mới
        } else {
            @Suppress("DEPRECATION")
            wm.defaultDisplay // Fallback cho API thấp hơn
        }

        if (display != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val supportedModes = display.supportedModes
                val highestRefreshRateMode = supportedModes.maxByOrNull { it.refreshRate }
                if (highestRefreshRateMode != null) {
                    window.attributes = window.attributes.apply {
                        preferredDisplayModeId = highestRefreshRateMode.modeId
                    }
                    println("Adaptive refresh rate applied: ${highestRefreshRateMode.refreshRate} Hz")
                }
            }
        }
    }

    override fun getAppIconIDs() = arrayListOf(
//        R.mipmap.ic_launcher_red,
//        R.mipmap.ic_launcher_pink,
//        R.mipmap.ic_launcher_purple,
//        R.mipmap.ic_launcher_deep_purple,
//        R.mipmap.ic_launcher_indigo,
//        R.mipmap.ic_launcher_blue,
//        R.mipmap.ic_launcher_light_blue,
//        R.mipmap.ic_launcher_cyan,
//        R.mipmap.ic_launcher_teal,
        R.mipmap.ic_launcher,
//        R.mipmap.ic_launcher_light_green,
//        R.mipmap.ic_launcher_lime,
//        R.mipmap.ic_launcher_yellow,
//        R.mipmap.ic_launcher_amber,
//        R.mipmap.ic_launcher_orange,
//        R.mipmap.ic_launcher_deep_orange,
//        R.mipmap.ic_launcher_brown,
//        R.mipmap.ic_launcher_blue_grey,
//        R.mipmap.ic_launcher_grey_black
    )

    override fun getAppLauncherName() = getString(R.string.app_launcher_name)

    protected fun checkNotchSupport() {
        if (isPiePlus()) {
            val cutoutMode = when {
                config.showNotch -> WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                else -> WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
            }

            window.attributes.layoutInDisplayCutoutMode = cutoutMode
            if (config.showNotch) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            }
        }
    }

    protected fun registerFileUpdateListener() {
        try {
            contentResolver.registerContentObserver(Images.Media.EXTERNAL_CONTENT_URI, true, observer)
            contentResolver.registerContentObserver(Video.Media.EXTERNAL_CONTENT_URI, true, observer)
        } catch (ignored: Exception) {
        }
    }

    protected fun unregisterFileUpdateListener() {
        try {
            contentResolver.unregisterContentObserver(observer)
        } catch (ignored: Exception) {
        }
    }

    protected fun showAddIncludedFolderDialog(callback: () -> Unit) {
        FilePickerDialog(this, config.lastFilepickerPath, false, config.shouldShowHidden, false, true) {
            config.lastFilepickerPath = it
            config.addIncludedFolder(it)
            callback()
            ensureBackgroundThread {
                scanPathRecursively(it)
            }
        }
    }
}
