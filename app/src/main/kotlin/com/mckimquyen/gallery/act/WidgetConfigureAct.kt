package com.mckimquyen.gallery.act

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.RemoteViews
import com.bumptech.glide.signature.ObjectKey
import org.fossify.commons.dialogs.ColorPickerDialog
import org.fossify.commons.extensions.*
import org.fossify.commons.helpers.ensureBackgroundThread
import com.mckimquyen.gallery.R
import com.mckimquyen.gallery.databinding.AWidgetConfigBinding
import com.mckimquyen.gallery.dlg.PickDirectoryDlg
import com.mckimquyen.gallery.ext.*
import com.mckimquyen.gallery.helper.MyWidgetProvider
import com.mckimquyen.gallery.helper.ROUNDED_CORNERS_NONE
import com.mckimquyen.gallery.model.Directory
import com.mckimquyen.gallery.model.Widget

class WidgetConfigureAct : SimpleAct() {
    private var mBgAlpha = 0f
    private var mWidgetId = 0
    private var mBgColor = 0
    private var mBgColorWithoutTransparency = 0
    private var mTextColor = 0
    private var mFolderPath = ""
    private var mDirectories = ArrayList<Directory>()
    private val binding by viewBinding(AWidgetConfigBinding::inflate)

    override fun attachBaseContext(newBase: Context) {
        val override = Configuration(newBase.resources.configuration)
        override.fontScale = 1.0f
        applyOverrideConfiguration(override)
        super.attachBaseContext(newBase)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        useDynamicTheme = false
        super.onCreate(savedInstanceState)
        setResult(RESULT_CANCELED)
        setContentView(binding.root)
        initVariables()

        mWidgetId = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (mWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
        }

        binding.configSave.setOnClickListener {
            saveConfig()
        }
        binding.configBgColor.setOnClickListener {
            pickBackgroundColor()
        }
        binding.configTextColor.setOnClickListener {
            pickTextColor()
        }
        binding.folderPickerValue.setOnClickListener {
            changeSelectedFolder()
        }
        binding.configImageHolder.setOnClickListener {
            changeSelectedFolder()
        }

        updateTextColors(binding.folderPickerHolder)
        val primaryColor = getProperPrimaryColor()
        binding.configBgSeekbar.setColors(mTextColor, primaryColor, primaryColor)
        binding.folderPickerHolder.background = ColorDrawable(getProperBackgroundColor())

        binding.folderPickerShowFolderName.isChecked = config.showWidgetFolderName
        handleFolderNameDisplay()
        binding.folderPickerShowFolderNameHolder.setOnClickListener {
            binding.folderPickerShowFolderName.toggle()
            handleFolderNameDisplay()
        }

        getCachedDirectories(getVideosOnly = false, getImagesOnly = false) {
            mDirectories = it
            val path = it.firstOrNull()?.path
            if (path != null) {
                updateFolderImage(path)
            }
        }
    }

    private fun initVariables() {
        mBgColor = config.widgetBgColor
        mBgAlpha = Color.alpha(mBgColor) / 255f

        mBgColorWithoutTransparency = Color.rgb(
            /* red = */ Color.red(mBgColor),
            /* green = */ Color.green(mBgColor),
            /* blue = */ Color.blue(mBgColor)
        )
        binding.configBgSeekbar.apply {
            progress = (mBgAlpha * 100).toInt()

            onSeekBarChangeListener { progress ->
                mBgAlpha = progress / 100f
                updateBackgroundColor()
            }
        }
        updateBackgroundColor()

        mTextColor = config.widgetTextColor
        if (mTextColor == resources.getColor(org.fossify.commons.R.color.default_widget_text_color)
            && config.isUsingSystemTheme
        ) {
            mTextColor = resources.getColor(org.fossify.commons.R.color.you_primary_color, theme)
        }

        updateTextColor()
    }

    private fun saveConfig() {
        val views = RemoteViews(
            /* packageName = */ packageName,
            /* layoutId = */ R.layout.widget
        )
        views.setBackgroundColor(R.id.widgetHolder, mBgColor)
        AppWidgetManager.getInstance(this)?.updateAppWidget(mWidgetId, views) ?: return
        config.showWidgetFolderName = binding.folderPickerShowFolderName.isChecked
        val widget = Widget(
            id = null,
            widgetId = mWidgetId,
            folderPath = mFolderPath
        )
        ensureBackgroundThread {
            widgetsDB.insertOrUpdate(widget)
        }

        storeWidgetColors()
        requestWidgetUpdate()

        Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId)
            setResult(Activity.RESULT_OK, this)
        }
        finish()
    }

    private fun storeWidgetColors() {
        config.apply {
            widgetBgColor = mBgColor
            widgetTextColor = mTextColor
        }
    }

    private fun requestWidgetUpdate() {
        Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, this, MyWidgetProvider::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(mWidgetId))
            sendBroadcast(this)
        }
    }

    private fun updateTextColor() {
        binding.configFolderName.setTextColor(mTextColor)
        binding.configTextColor.setFillWithStroke(mTextColor, mTextColor)
        binding.configSave.setTextColor(getProperPrimaryColor().getContrastColor())
    }

    private fun updateBackgroundColor() {
        mBgColor = mBgColorWithoutTransparency.adjustAlpha(mBgAlpha)
        binding.configImageHolder.background.applyColorFilter(mBgColor)
        binding.configBgColor.setFillWithStroke(mBgColor, mBgColor)
        binding.configSave.backgroundTintList = ColorStateList.valueOf(getProperPrimaryColor())
    }

    private fun pickBackgroundColor() {
        ColorPickerDialog(this, mBgColorWithoutTransparency) { wasPositivePressed, color ->
            if (wasPositivePressed) {
                mBgColorWithoutTransparency = color
                updateBackgroundColor()
            }
        }
    }

    private fun pickTextColor() {
        ColorPickerDialog(this, mTextColor) { wasPositivePressed, color ->
            if (wasPositivePressed) {
                mTextColor = color
                updateTextColor()
            }
        }
    }

    private fun changeSelectedFolder() {
        PickDirectoryDlg(
            activity = this,
            sourcePath = "",
            showOtherFolderButton = false,
            showFavoritesBin = true,
            isPickingCopyMoveDestination = false,
            isPickingFolderForWidget = true
        ) {
            updateFolderImage(it)
        }
    }

    private fun updateFolderImage(folderPath: String) {
        mFolderPath = folderPath
        runOnUiThread {
            binding.folderPickerValue.text = getFolderNameFromPath(folderPath)
            binding.configFolderName.text = getFolderNameFromPath(folderPath)
        }

        ensureBackgroundThread {
            val path = directoryDB.getDirectoryThumbnail(folderPath)
            if (path != null) {
                runOnUiThread {
                    val signature = ObjectKey(System.currentTimeMillis().toString())
                    loadImageBase(
                        path = path,
                        target = binding.configImage,
                        cropThumbnails = config.cropThumbnails,
                        roundCorners = ROUNDED_CORNERS_NONE,
                        signature = signature
                    )
                }
            }
        }
    }

    private fun handleFolderNameDisplay() {
        val showFolderName = binding.folderPickerShowFolderName.isChecked
        binding.configFolderName.beVisibleIf(showFolderName)
    }
}
