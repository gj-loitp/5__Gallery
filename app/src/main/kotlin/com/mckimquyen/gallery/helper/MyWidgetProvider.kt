package com.mckimquyen.gallery.helper

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import org.fossify.commons.extensions.applyColorFilter
import org.fossify.commons.extensions.getFileSignature
import org.fossify.commons.extensions.setText
import org.fossify.commons.extensions.setVisibleIf
import org.fossify.commons.helpers.ensureBackgroundThread
import com.mckimquyen.gallery.R
import com.mckimquyen.gallery.activities.MediaActivityMediaOperations
import com.mckimquyen.gallery.ext.config
import com.mckimquyen.gallery.ext.directoryDB
import com.mckimquyen.gallery.ext.getFolderNameFromPath
import com.mckimquyen.gallery.ext.widgetsDB
import com.mckimquyen.gallery.model.Widget
import kotlin.math.max

class MyWidgetProvider : AppWidgetProvider() {
    private fun setupAppOpenIntent(
        context: Context,
        views: RemoteViews,
        id: Int,
        widget: Widget,
    ) {
        val intent = Intent(context, MediaActivityMediaOperations::class.java).apply {
            putExtra(
                /* name = */ DIRECTORY,
                /* value = */ widget.folderPath
            )
        }

        val pendingIntent = PendingIntent.getActivity(
            /* context = */ context,
            /* requestCode = */ widget.widgetId,
            /* intent = */ intent,
            /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(id, pendingIntent)
    }

    @SuppressLint("RemoteViewLayout", "CheckResult")
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        ensureBackgroundThread {
            val config = context.config
            context.widgetsDB.getWidgets().filter {
                appWidgetIds.contains(it.widgetId)
            }.forEach {
                val views = RemoteViews(context.packageName, R.layout.widget).apply {
                    applyColorFilter(R.id.widgetBackground, config.widgetBgColor)
                    setVisibleIf(R.id.widgetFolderName, config.showWidgetFolderName)
                    setTextColor(R.id.widgetFolderName, config.widgetTextColor)
                    setText(R.id.widgetFolderName, context.getFolderNameFromPath(it.folderPath))
                }

                val path = context.directoryDB.getDirectoryThumbnail(it.folderPath) ?: return@forEach
                val options = RequestOptions()
                    .signature(path.getFileSignature())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)

                if (context.config.cropThumbnails) {
                    options.centerCrop()
                } else {
                    options.fitCenter()
                }

                val density = context.resources.displayMetrics.density
                val appWidgetOptions = appWidgetManager.getAppWidgetOptions(appWidgetIds.first())
                val width = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
                val height = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)

                val widgetSize = (max(width, height) * density).toInt()
                try {
                    val image = Glide.with(context)
                        .asBitmap()
                        .load(path)
                        .apply(options)
                        .submit(widgetSize, widgetSize)
                        .get()
                    views.setImageViewBitmap(R.id.widgetImageview, image)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                setupAppOpenIntent(
                    context = context,
                    views = views,
                    id = R.id.widgetHolder,
                    widget = it
                )

                try {
                    appWidgetManager.updateAppWidget(/* appWidgetId = */ it.widgetId, /* views = */ views)
                } catch (ignored: Exception) {
                    ignored.printStackTrace()
                }
            }
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle,
    ) {
        super.onAppWidgetOptionsChanged(
            /* context = */ context,
            /* appWidgetManager = */ appWidgetManager,
            /* appWidgetId = */ appWidgetId,
            /* newOptions = */ newOptions
        )
        onUpdate(
            context = context,
            appWidgetManager = appWidgetManager,
            appWidgetIds = intArrayOf(appWidgetId)
        )
    }

    override fun onDeleted(
        context: Context,
        appWidgetIds: IntArray,
    ) {
        super.onDeleted(context, appWidgetIds)
        ensureBackgroundThread {
            appWidgetIds.forEach {
                context.widgetsDB.deleteWidgetId(it)
            }
        }
    }
}
