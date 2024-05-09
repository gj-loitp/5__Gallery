package com.mckimquyen.gallery.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.fossify.commons.helpers.ensureBackgroundThread
import com.mckimquyen.gallery.extensions.updateDirectoryPath
import com.mckimquyen.gallery.helpers.MediaFetcher

class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        ensureBackgroundThread {
            MediaFetcher(context).getFoldersToScan().forEach {
                context.updateDirectoryPath(it)
            }
        }
    }
}
