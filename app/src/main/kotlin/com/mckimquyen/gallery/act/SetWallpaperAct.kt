package com.mckimquyen.gallery.act

import android.app.Activity
import android.app.WallpaperManager
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import com.canhub.cropper.CropImageView
import org.fossify.commons.dialogs.RadioGroupDialog
import org.fossify.commons.extensions.checkAppSideloading
import org.fossify.commons.extensions.toast
import org.fossify.commons.extensions.viewBinding
import org.fossify.commons.helpers.NavigationIcon
import org.fossify.commons.helpers.ensureBackgroundThread
import org.fossify.commons.helpers.isNougatPlus
import org.fossify.commons.models.RadioItem
import com.mckimquyen.gallery.R
import com.mckimquyen.gallery.databinding.ASetWallpaperBinding

class SetWallpaperAct : SimpleAct(), CropImageView.OnCropImageCompleteListener {
    private val ratioPortrait = 0
    private val ratioLandscape = 1
    private val ratioSquare = 2
    private val pickImage = 1
    private var aspectRatio = ratioPortrait
    private var wallpaperFlag = -1
    lateinit var uri: Uri
    private lateinit var wallpaperManager: WallpaperManager
    private val binding by viewBinding(ASetWallpaperBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupBottomActions()

        if (checkAppSideloading()) {
            return
        }

        setupOptionsMenu()
        if (intent.data == null) {
            val pickIntent = Intent(applicationContext, MainAct::class.java)
            pickIntent.action = Intent.ACTION_PICK
            pickIntent.type = "image/*"
            startActivityForResult(pickIntent, pickImage)
            return
        }

        handleImage(intent)
    }

    override fun onResume() {
        super.onResume()
        setupToolbar(
            toolbar = binding.setWallpaperToolbar,
            toolbarNavigationIcon = NavigationIcon.Arrow
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        resultData: Intent?,
    ) {
        if (requestCode == pickImage) {
            if (resultCode == Activity.RESULT_OK && resultData != null) {
                handleImage(resultData)
            } else {
                finish()
            }
        }
        super.onActivityResult(requestCode, resultCode, resultData)
    }

    private fun setupOptionsMenu() {
        binding.setWallpaperToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.save -> confirmWallpaper()
                R.id.allowChangingAspectRatio -> binding.cropImageView.clearAspectRatio()
                else -> return@setOnMenuItemClickListener false
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun handleImage(intent: Intent) {
        uri = intent.data!!
        if (uri.scheme != "file" && uri.scheme != "content") {
            toast(R.string.unknown_file_location)
            finish()
            return
        }

        wallpaperManager = WallpaperManager.getInstance(applicationContext)
        binding.cropImageView.apply {
            setOnCropImageCompleteListener(this@SetWallpaperAct)
            setImageUriAsync(uri)
        }

        setupAspectRatio()
    }

    private fun setupBottomActions() {
        binding.bottomSetWallpaperActions.bottomSetWallpaperAspectRatio.setOnClickListener {
            changeAspectRatio()
        }

        binding.bottomSetWallpaperActions.bottomSetWallpaperRotate.setOnClickListener {
            binding.cropImageView.rotateImage(90)
        }
    }

    private fun setupAspectRatio() {
        var widthToUse = wallpaperManager.desiredMinimumWidth
        val heightToUse = wallpaperManager.desiredMinimumHeight
        if (widthToUse == heightToUse) {
            widthToUse /= 2
        }

        when (aspectRatio) {
            ratioPortrait -> binding.cropImageView.setAspectRatio(aspectRatioX = heightToUse, aspectRatioY = widthToUse)
            ratioLandscape -> binding.cropImageView.setAspectRatio(aspectRatioX = widthToUse, aspectRatioY = heightToUse)
            else -> binding.cropImageView.setAspectRatio(aspectRatioX = widthToUse, aspectRatioY = widthToUse)
        }
    }

    private fun changeAspectRatio() {
        aspectRatio = ++aspectRatio % (ratioSquare + 1)
        setupAspectRatio()
    }

    private fun confirmWallpaper() {
        if (isNougatPlus()) {
            val items = arrayListOf(
                RadioItem(WallpaperManager.FLAG_SYSTEM, getString(R.string.home_screen)),
                RadioItem(WallpaperManager.FLAG_LOCK, getString(R.string.lock_screen)),
                RadioItem(WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK, getString(R.string.home_and_lock_screen))
            )

            RadioGroupDialog(this, items) {
                wallpaperFlag = it as Int
                binding.cropImageView.croppedImageAsync()
            }
        } else {
            binding.cropImageView.croppedImageAsync()
        }
    }

    override fun onCropImageComplete(
        view: CropImageView,
        result: CropImageView.CropResult,
    ) {
        if (isDestroyed)
            return

        if (result.error == null && result.bitmap != null) {
            toast(R.string.setting_wallpaper)
            ensureBackgroundThread {
                val bitmap = result.bitmap!!
                val wantedHeight = wallpaperManager.desiredMinimumHeight
                val ratio = wantedHeight / bitmap.height.toFloat()
                val wantedWidth = (bitmap.width * ratio).toInt()
                try {
                    val scaledBitmap = Bitmap.createScaledBitmap(
                        /* src = */ bitmap,
                        /* dstWidth = */ wantedWidth,
                        /* dstHeight = */ wantedHeight,
                        /* filter = */ true
                    )
                    if (isNougatPlus()) {
                        wallpaperManager.setBitmap(
                            /* fullImage = */ scaledBitmap,
                            /* visibleCropHint = */ null,
                            /* allowBackup = */ true,
                            /* which = */ wallpaperFlag
                        )
                    } else {
                        wallpaperManager.setBitmap(scaledBitmap)
                    }
                    setResult(Activity.RESULT_OK)
                } catch (e: OutOfMemoryError) {
                    toast(org.fossify.commons.R.string.out_of_memory_error)
                    setResult(Activity.RESULT_CANCELED)
                }
                finish()
            }
        } else {
            toast("${getString(R.string.image_editing_failed)}: ${result.error?.message}")
        }
    }
}
