package com.mckimquyen.gallery.ext

import android.annotation.TargetApi
import android.app.Activity
import android.content.ContentProviderOperation
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Files
import android.provider.MediaStore.Images
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.exifinterface.media.ExifInterface
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.squareup.picasso.Picasso
import org.fossify.commons.activities.BaseSimpleActivity
import org.fossify.commons.dialogs.ConfirmationDialog
import org.fossify.commons.dialogs.SecurityDialog
import org.fossify.commons.extensions.*
import org.fossify.commons.helpers.*
import org.fossify.commons.models.FAQItem
import org.fossify.commons.models.FileDirItem
import com.mckimquyen.gallery.BuildConfig
import com.mckimquyen.gallery.R
import com.mckimquyen.gallery.act.MediaActMediaOperations
import com.mckimquyen.gallery.act.SettingsAct
import com.mckimquyen.gallery.act.SimpleAct
import com.mckimquyen.gallery.dlg.AllFilesPermissionDlg
import com.mckimquyen.gallery.dlg.PickDirectoryDlg
import com.mckimquyen.gallery.dlg.ResizeMultipleImagesDlg
import com.mckimquyen.gallery.dlg.ResizeWithPathDlg
import com.mckimquyen.gallery.helper.DIRECTORY
import com.mckimquyen.gallery.helper.RECYCLE_BIN
import com.mckimquyen.gallery.model.DateTaken
import java.io.*
import java.text.SimpleDateFormat
import java.util.Locale

fun Activity.sharePath(path: String) {
    sharePathIntent(path = path, applicationId = BuildConfig.APPLICATION_ID)
}

fun Activity.sharePaths(paths: ArrayList<String>) {
    sharePathsIntent(paths = paths, applicationId = BuildConfig.APPLICATION_ID)
}

fun Activity.shareMediumPath(path: String) {
    sharePath(path)
}

fun Activity.shareMediaPaths(paths: ArrayList<String>) {
    sharePaths(paths)
}

fun Activity.setAs(path: String) {
    setAsIntent(path = path, applicationId = BuildConfig.APPLICATION_ID)
}

fun Activity.openPath(
    path: String,
    forceChooser: Boolean,
    extras: HashMap<String, Boolean> = HashMap(),
) {
    openPathIntent(
        path = path,
        forceChooser = forceChooser,
        applicationId = BuildConfig.APPLICATION_ID,
        extras = extras
    )
}

fun Activity.openEditor(
    path: String,
    forceChooser: Boolean = false,
) {
    val newPath = path.removePrefix("file://")
    openEditorIntent(
        path = newPath,
        forceChooser = forceChooser,
        applicationId = BuildConfig.APPLICATION_ID
    )
}

fun Activity.launchCamera() {
    val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
    launchActivityIntent(intent)
}

fun SimpleAct.launchSettings() {
    hideKeyboard()
    startActivity(Intent(applicationContext, SettingsAct::class.java))
}

fun SimpleAct.launchAbout() {
    val licenses = LICENSE_GLIDE or LICENSE_CROPPER or LICENSE_RTL or LICENSE_SUBSAMPLING or LICENSE_PATTERN or LICENSE_REPRINT or LICENSE_GIF_DRAWABLE or
        LICENSE_PICASSO or LICENSE_EXOPLAYER or LICENSE_SANSELAN or LICENSE_FILTERS or LICENSE_GESTURE_VIEWS or LICENSE_APNG

    val faqItems = arrayListOf(
        FAQItem(R.string.faq_3_title, R.string.faq_3_text),
        FAQItem(R.string.faq_12_title, R.string.faq_12_text),
        FAQItem(R.string.faq_7_title, R.string.faq_7_text),
        FAQItem(R.string.faq_14_title, R.string.faq_14_text),
        FAQItem(R.string.faq_1_title, R.string.faq_1_text),
        FAQItem(org.fossify.commons.R.string.faq_5_title_commons, org.fossify.commons.R.string.faq_5_text_commons),
        FAQItem(R.string.faq_5_title, R.string.faq_5_text),
        FAQItem(R.string.faq_4_title, R.string.faq_4_text),
        FAQItem(R.string.faq_6_title, R.string.faq_6_text),
        FAQItem(R.string.faq_8_title, R.string.faq_8_text),
        FAQItem(R.string.faq_10_title, R.string.faq_10_text),
        FAQItem(R.string.faq_11_title, R.string.faq_11_text),
        FAQItem(R.string.faq_13_title, R.string.faq_13_text),
        FAQItem(R.string.faq_15_title, R.string.faq_15_text),
        FAQItem(R.string.faq_2_title, R.string.faq_2_text),
        FAQItem(R.string.faq_18_title, R.string.faq_18_text),
        FAQItem(org.fossify.commons.R.string.faq_9_title_commons, org.fossify.commons.R.string.faq_9_text_commons),
    )

    if (!resources.getBoolean(org.fossify.commons.R.bool.hide_google_relations)) {
        faqItems.add(FAQItem(org.fossify.commons.R.string.faq_2_title_commons, org.fossify.commons.R.string.faq_2_text_commons))
        faqItems.add(FAQItem(org.fossify.commons.R.string.faq_6_title_commons, org.fossify.commons.R.string.faq_6_text_commons))
        faqItems.add(FAQItem(org.fossify.commons.R.string.faq_7_title_commons, org.fossify.commons.R.string.faq_7_text_commons))
        faqItems.add(FAQItem(org.fossify.commons.R.string.faq_10_title_commons, org.fossify.commons.R.string.faq_10_text_commons))
    }

    if (isRPlus() && !isExternalStorageManager()) {
        faqItems.add(0, FAQItem(R.string.faq_16_title, "${getString(R.string.faq_16_text)} ${getString(R.string.faq_16_text_extra)}"))
        faqItems.add(1, FAQItem(R.string.faq_17_title, R.string.faq_17_text))
        faqItems.removeIf { it.text == R.string.faq_7_text }
        faqItems.removeIf { it.text == R.string.faq_14_text }
        faqItems.removeIf { it.text == R.string.faq_8_text }
    }

    startAboutActivity(R.string.app_name, licenses, BuildConfig.VERSION_NAME, faqItems, true)
}

fun BaseSimpleActivity.handleMediaManagementPrompt(callback: () -> Unit) {
    if (canManageMedia() || isExternalStorageManager()) {
        callback()
    } else if (isRPlus() && resources.getBoolean(R.bool.require_all_files_access) && !config.avoidShowingAllFilesPrompt) {
        if (Environment.isExternalStorageManager()) {
            callback()
        } else {
            var messagePrompt = getString(org.fossify.commons.R.string.access_storage_prompt)
            messagePrompt += if (isSPlus()) {
                "\n\n${getString(R.string.media_management_alternative)}"
            } else {
                "\n\n${getString(R.string.alternative_media_access)}"
            }

            AllFilesPermissionDlg(
                activity = this,
                message = messagePrompt,
                callback = { success ->
                    if (success) {
                        launchGrantAllFilesIntent()
                    }
                },
                neutralPressed = {
                    if (isSPlus()) {
                        launchMediaManagementIntent(callback)
                    } else {
                        config.avoidShowingAllFilesPrompt = true
                    }
                }
            )
        }
    } else {
        callback()
    }
}

fun BaseSimpleActivity.launchGrantAllFilesIntent() {
    try {
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
        intent.addCategory("android.intent.category.DEFAULT")
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    } catch (e: Exception) {
        val intent = Intent()
        intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
        try {
            startActivity(intent)
        } catch (e: Exception) {
            showErrorToast(e)
        }
    }
}

fun AppCompatActivity.showSystemUI(toggleActionBarVisibility: Boolean) {
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
}

fun AppCompatActivity.hideSystemUI(toggleActionBarVisibility: Boolean) {
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
        View.SYSTEM_UI_FLAG_LOW_PROFILE or
        View.SYSTEM_UI_FLAG_FULLSCREEN or
        View.SYSTEM_UI_FLAG_IMMERSIVE
}

fun BaseSimpleActivity.addNoMedia(path: String, callback: () -> Unit) {
    val file = File(path, NOMEDIA)
    if (getDoesFilePathExist(file.absolutePath)) {
        callback()
        return
    }

    if (needsStupidWritePermissions(path)) {
        handleSAFDialog(file.absolutePath) {
            if (!it) {
                return@handleSAFDialog
            }

            val fileDocument = getDocumentFile(path)
            if (fileDocument?.exists() == true && fileDocument.isDirectory) {
                fileDocument.createFile("", NOMEDIA)
                addNoMediaIntoMediaStore(file.absolutePath)
                callback()
            } else {
                toast(org.fossify.commons.R.string.unknown_error_occurred)
                callback()
            }
        }
    } else {
        try {
            if (file.createNewFile()) {
                ensureBackgroundThread {
                    addNoMediaIntoMediaStore(file.absolutePath)
                }
            } else {
                toast(org.fossify.commons.R.string.unknown_error_occurred)
            }
        } catch (e: Exception) {
            showErrorToast(e)
        }
        callback()
    }
}

fun BaseSimpleActivity.addNoMediaIntoMediaStore(path: String) {
    try {
        val content = ContentValues().apply {
            put(Files.FileColumns.TITLE, NOMEDIA)
            put(Files.FileColumns.DATA, path)
            put(Files.FileColumns.MEDIA_TYPE, Files.FileColumns.MEDIA_TYPE_NONE)
        }
        contentResolver.insert(Files.getContentUri("external"), content)
    } catch (e: Exception) {
        showErrorToast(e)
    }
}

fun BaseSimpleActivity.removeNoMedia(
    path: String,
    callback: (() -> Unit)? = null,
) {
    val file = File(path, NOMEDIA)
    if (!getDoesFilePathExist(file.absolutePath)) {
        callback?.invoke()
        return
    }

    tryDeleteFileDirItem(
        fileDirItem = file.toFileDirItem(applicationContext),
        allowDeleteFolder = false,
        deleteFromDatabase = false
    ) {
        callback?.invoke()
        deleteFromMediaStore(file.absolutePath) { needsRescan ->
            if (needsRescan) {
                rescanAndDeletePath(path) {
                    rescanFolderMedia(path)
                }
            } else {
                rescanFolderMedia(path)
            }
        }
    }
}

fun BaseSimpleActivity.toggleFileVisibility(
    oldPath: String,
    hide: Boolean,
    callback: ((newPath: String) -> Unit)? = null,
) {
    val path = oldPath.getParentPath()
    var filename = oldPath.getFilenameFromPath()
    if ((hide && filename.startsWith('.')) || (!hide && !filename.startsWith('.'))) {
        callback?.invoke(oldPath)
        return
    }

    filename = if (hide) {
        ".${filename.trimStart('.')}"
    } else {
        filename.substring(1, filename.length)
    }

    val newPath = "$path/$filename"
    renameFile(oldPath, newPath, false) { _, _ ->
        runOnUiThread {
            callback?.invoke(newPath)
        }

        ensureBackgroundThread {
            updateDBMediaPath(oldPath = oldPath, newPath = newPath)
        }
    }
}

fun BaseSimpleActivity.tryCopyMoveFilesTo(
    fileDirItems: ArrayList<FileDirItem>,
    isCopyOperation: Boolean,
    callback: (destinationPath: String) -> Unit,
) {
    if (fileDirItems.isEmpty()) {
        toast(org.fossify.commons.R.string.unknown_error_occurred)
        return
    }

    val source = fileDirItems[0].getParentPath()
    PickDirectoryDlg(
        activity = this,
        sourcePath = source,
        showOtherFolderButton = true,
        showFavoritesBin = false,
        isPickingCopyMoveDestination = true,
        isPickingFolderForWidget = false
    ) {
        val destination = it
        handleSAFDialog(source) {
            if (it) {
                copyMoveFilesTo(
                    fileDirItems = fileDirItems,
                    source = source.trimEnd('/'),
                    destination = destination,
                    isCopyOperation = isCopyOperation,
                    copyPhotoVideoOnly = true,
                    copyHidden = config.shouldShowHidden,
                    callback = callback
                )
            }
        }
    }
}

fun BaseSimpleActivity.tryDeleteFileDirItem(
    fileDirItem: FileDirItem,
    allowDeleteFolder: Boolean = false,
    deleteFromDatabase: Boolean,
    callback: ((wasSuccess: Boolean) -> Unit)? = null,
) {
    deleteFile(
        fileDirItem = fileDirItem,
        allowDeleteFolder = allowDeleteFolder,
        isDeletingMultipleFiles = false
    ) {
        if (deleteFromDatabase) {
            ensureBackgroundThread {
                deleteDBPath(fileDirItem.path)
                runOnUiThread {
                    callback?.invoke(it)
                }
            }
        } else {
            callback?.invoke(it)
        }
    }
}

fun BaseSimpleActivity.movePathsInRecycleBin(
    paths: ArrayList<String>,
    callback: ((wasSuccess: Boolean) -> Unit)?,
) {
    ensureBackgroundThread {
        var pathsCnt = paths.size
        val OTGPath = config.OTGPath

        for (source in paths) {
            if (OTGPath.isNotEmpty() && source.startsWith(OTGPath)) {
                var inputStream: InputStream? = null
                var out: OutputStream? = null
                try {
                    val destination = "$recycleBinPath/$source"
                    val fileDocument = getSomeDocumentFile(source)
                    inputStream = applicationContext.contentResolver.openInputStream(fileDocument?.uri!!)
                    out = getFileOutputStreamSync(destination, source.getMimeType())

                    var copiedSize = 0L
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var bytes = inputStream!!.read(buffer)
                    while (bytes >= 0) {
                        out!!.write(buffer, 0, bytes)
                        copiedSize += bytes
                        bytes = inputStream.read(buffer)
                    }

                    out?.flush()

                    if (fileDocument.getItemSize(true) == copiedSize && getDoesFilePathExist(destination)) {
                        mediaDB.updateDeleted("$RECYCLE_BIN$source", System.currentTimeMillis(), source)
                        pathsCnt--
                    }
                } catch (e: Exception) {
                    showErrorToast(e)
                    return@ensureBackgroundThread
                } finally {
                    inputStream?.close()
                    out?.close()
                }
            } else {
                val file = File(source)
                val internalFile = File(recycleBinPath, source)
                val lastModified = file.lastModified()
                try {
                    if (file.copyRecursively(internalFile, true)) {
                        mediaDB.updateDeleted("$RECYCLE_BIN$source", System.currentTimeMillis(), source)
                        pathsCnt--

                        if (config.keepLastModified && lastModified != 0L) {
                            internalFile.setLastModified(lastModified)
                        }
                    }
                } catch (e: Exception) {
                    showErrorToast(e)
                    return@ensureBackgroundThread
                }
            }
        }
        callback?.invoke(pathsCnt == 0)
    }
}

fun BaseSimpleActivity.restoreRecycleBinPath(path: String, callback: () -> Unit) {
    restoreRecycleBinPaths(arrayListOf(path), callback)
}

fun BaseSimpleActivity.restoreRecycleBinPaths(paths: ArrayList<String>, callback: () -> Unit) {
    ensureBackgroundThread {
        val newPaths = ArrayList<String>()
        var shownRestoringToPictures = false
        for (source in paths) {
            var destination = source.removePrefix(recycleBinPath)

            val destinationParent = destination.getParentPath()
            if (isRestrictedWithSAFSdk30(destinationParent) && !isInDownloadDir(destinationParent)) {
                // if the file is not writeable on SDK30+, change it to Pictures
                val picturesDirectory = getPicturesDirectoryPath(destination)
                destination = File(picturesDirectory, destination.getFilenameFromPath()).path
                if (!shownRestoringToPictures) {
                    toast(getString(R.string.restore_to_path, humanizePath(picturesDirectory)))
                    shownRestoringToPictures = true
                }
            }

            val lastModified = File(source).lastModified()

            val isShowingSAF = handleSAFDialog(destination) {}
            if (isShowingSAF) {
                return@ensureBackgroundThread
            }

            val isShowingSAFSdk30 = handleSAFDialogSdk30(destination) {}
            if (isShowingSAFSdk30) {
                return@ensureBackgroundThread
            }

            if (getDoesFilePathExist(destination)) {
                val newFile = getAlternativeFile(File(destination))
                destination = newFile.path
            }

            var inputStream: InputStream? = null
            var out: OutputStream? = null
            try {
                out = getFileOutputStreamSync(destination, source.getMimeType())
                inputStream = getFileInputStreamSync(source)

                var copiedSize = 0L
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                var bytes = inputStream!!.read(buffer)
                while (bytes >= 0) {
                    out!!.write(buffer, 0, bytes)
                    copiedSize += bytes
                    bytes = inputStream.read(buffer)
                }

                out?.flush()

                if (File(source).length() == copiedSize) {
                    mediaDB.updateDeleted(
                        newPath = destination.removePrefix(recycleBinPath),
                        deletedTS = 0,
                        oldPath = "$RECYCLE_BIN${source.removePrefix(recycleBinPath)}"
                    )
                }
                newPaths.add(destination)

                if (config.keepLastModified && lastModified != 0L) {
                    File(destination).setLastModified(lastModified)
                }
            } catch (e: Exception) {
                showErrorToast(e)
            } finally {
                inputStream?.close()
                out?.close()
            }
        }

        runOnUiThread {
            callback()
        }

        rescanPaths(newPaths) {
            fixDateTaken(paths = newPaths, showToasts = false)
        }
    }
}

fun BaseSimpleActivity.emptyTheRecycleBin(callback: (() -> Unit)? = null) {
    ensureBackgroundThread {
        try {
            recycleBin.deleteRecursively()
            mediaDB.clearRecycleBin()
            directoryDB.deleteRecycleBin()
            toast(org.fossify.commons.R.string.recycle_bin_emptied)
            callback?.invoke()
        } catch (e: Exception) {
            toast(org.fossify.commons.R.string.unknown_error_occurred)
        }
    }
}

fun BaseSimpleActivity.emptyAndDisableTheRecycleBin(callback: () -> Unit) {
    ensureBackgroundThread {
        emptyTheRecycleBin {
            config.useRecycleBin = false
            callback()
        }
    }
}

fun BaseSimpleActivity.showRecycleBinEmptyingDialog(callback: () -> Unit) {
    ConfirmationDialog(
        activity = this,
        message = "",
        messageId = org.fossify.commons.R.string.empty_recycle_bin_confirmation,
        positive = org.fossify.commons.R.string.yes,
        negative = org.fossify.commons.R.string.no
    ) {
        callback()
    }
}

fun BaseSimpleActivity.updateFavoritePaths(
    fileDirItems: ArrayList<FileDirItem>,
    destination: String,
) {
    ensureBackgroundThread {
        fileDirItems.forEach {
            val newPath = "$destination/${it.name}"
            updateDBMediaPath(it.path, newPath)
        }
    }
}

fun Activity.hasNavBar(): Boolean {
    val display = windowManager.defaultDisplay

    val realDisplayMetrics = DisplayMetrics()
    display.getRealMetrics(realDisplayMetrics)

    val displayMetrics = DisplayMetrics()
    display.getMetrics(displayMetrics)

    return (realDisplayMetrics.widthPixels - displayMetrics.widthPixels > 0) || (realDisplayMetrics.heightPixels - displayMetrics.heightPixels > 0)
}

fun AppCompatActivity.fixDateTaken(
    paths: ArrayList<String>,
    showToasts: Boolean,
    hasRescanned: Boolean = false,
    callback: (() -> Unit)? = null,
) {
    val BATCH_SIZE = 50
    if (showToasts && !hasRescanned) {
        toast(R.string.fixing)
    }

    val pathsToRescan = ArrayList<String>()
    try {
        var didUpdateFile = false
        val operations = ArrayList<ContentProviderOperation>()

        ensureBackgroundThread {
            val dateTakens = ArrayList<DateTaken>()

            for (path in paths) {
                try {
                    val dateTime: String = ExifInterface(path).getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)
                        ?: ExifInterface(path).getAttribute(ExifInterface.TAG_DATETIME) ?: continue

                    // some formats contain a "T" in the middle, some don't
                    // sample dates: 2015-07-26T14:55:23, 2018:09:05 15:09:05
                    val t = if (dateTime.substring(10, 11) == "T") "\'T\'" else " "
                    val separator = dateTime.substring(4, 5)
                    val format = "yyyy${separator}MM${separator}dd${t}kk:mm:ss"
                    val formatter = SimpleDateFormat(format, Locale.getDefault())
                    val timestamp = formatter.parse(dateTime).time

                    val uri = getFileUri(path)
                    ContentProviderOperation.newUpdate(uri).apply {
                        val selection = "${Images.Media.DATA} = ?"
                        val selectionArgs = arrayOf(path)
                        withSelection(selection, selectionArgs)
                        withValue(Images.Media.DATE_TAKEN, timestamp)
                        operations.add(build())
                    }

                    if (operations.size % BATCH_SIZE == 0) {
                        contentResolver.applyBatch(MediaStore.AUTHORITY, operations)
                        operations.clear()
                    }

                    mediaDB.updateFavoriteDateTaken(path, timestamp)
                    didUpdateFile = true

                    val dateTaken = DateTaken(
                        id = null,
                        fullPath = path,
                        filename = path.getFilenameFromPath(),
                        parentPath = path.getParentPath(),
                        taken = timestamp,
                        lastFixed = (System.currentTimeMillis() / 1000).toInt(),
                        lastModified = File(path).lastModified()
                    )
                    dateTakens.add(dateTaken)
                    if (!hasRescanned && getFileDateTaken(path) == 0L) {
                        pathsToRescan.add(path)
                    }
                } catch (e: Exception) {
                    continue
                }
            }

            if (!didUpdateFile) {
                if (showToasts) {
                    toast(R.string.no_date_takens_found)
                }

                runOnUiThread {
                    callback?.invoke()
                }
                return@ensureBackgroundThread
            }

            val resultSize = contentResolver.applyBatch(MediaStore.AUTHORITY, operations).size
            if (resultSize == 0) {
                didUpdateFile = false
            }

            if (hasRescanned || pathsToRescan.isEmpty()) {
                if (dateTakens.isNotEmpty()) {
                    dateTakensDB.insertAll(dateTakens)
                }

                runOnUiThread {
                    if (showToasts) {
                        toast(if (didUpdateFile) R.string.dates_fixed_successfully else org.fossify.commons.R.string.unknown_error_occurred)
                    }

                    callback?.invoke()
                }
            } else {
                rescanPaths(pathsToRescan) {
                    fixDateTaken(paths = paths, showToasts = showToasts, hasRescanned = true, callback = callback)
                }
            }
        }
    } catch (e: Exception) {
        if (showToasts) {
            showErrorToast(e)
        }
    }
}

fun BaseSimpleActivity.saveRotatedImageToFile(
    oldPath: String,
    newPath: String,
    degrees: Int,
    showToasts: Boolean,
    callback: () -> Unit,
) {
    var newDegrees = degrees
    if (newDegrees < 0) {
        newDegrees += 360
    }

    if (oldPath == newPath && oldPath.isJpg()) {
        if (tryRotateByExif(
                path = oldPath,
                degrees = newDegrees,
                showToasts = showToasts,
                callback = callback
            )
        ) {
            return
        }
    }

    val tmpPath = "$recycleBinPath/.tmp_${newPath.getFilenameFromPath()}"
    val tmpFileDirItem = FileDirItem(tmpPath, tmpPath.getFilenameFromPath())
    try {
        getFileOutputStream(tmpFileDirItem) {
            if (it == null) {
                if (showToasts) {
                    toast(org.fossify.commons.R.string.unknown_error_occurred)
                }
                return@getFileOutputStream
            }

            val oldLastModified = File(oldPath).lastModified()
            if (oldPath.isJpg()) {
                copyFile(oldPath, tmpPath)
                saveExifRotation(ExifInterface(tmpPath), newDegrees)
            } else {
                val inputstream = getFileInputStreamSync(oldPath)
                val bitmap = BitmapFactory.decodeStream(inputstream)
                saveFile(tmpPath, bitmap, it as FileOutputStream, newDegrees)
            }

            copyFile(tmpPath, newPath)
            rescanPaths(arrayListOf(newPath))
            fileRotatedSuccessfully(newPath, oldLastModified)

            it.flush()
            it.close()
            callback.invoke()
        }
    } catch (e: OutOfMemoryError) {
        if (showToasts) {
            toast(org.fossify.commons.R.string.out_of_memory_error)
        }
    } catch (e: Exception) {
        if (showToasts) {
            showErrorToast(e)
        }
    } finally {
        tryDeleteFileDirItem(
            fileDirItem = tmpFileDirItem,
            allowDeleteFolder = false,
            deleteFromDatabase = true
        )
    }
}

@TargetApi(Build.VERSION_CODES.N)
fun Activity.tryRotateByExif(
    path: String,
    degrees: Int,
    showToasts: Boolean,
    callback: () -> Unit,
): Boolean {
    return try {
        val file = File(path)
        val oldLastModified = file.lastModified()
        if (saveImageRotation(path, degrees)) {
            fileRotatedSuccessfully(path, oldLastModified)
            callback.invoke()
            if (showToasts) {
                toast(org.fossify.commons.R.string.file_saved)
            }
            true
        } else {
            false
        }
    } catch (e: Exception) {
        // lets not show IOExceptions, rotating is saved just fine even with them
        if (showToasts && e !is IOException) {
            showErrorToast(e)
        }
        false
    }
}

fun Activity.fileRotatedSuccessfully(path: String, lastModified: Long) {
    if (config.keepLastModified && lastModified != 0L) {
        File(path).setLastModified(lastModified)
        updateLastModified(path, lastModified)
    }

    Picasso.get().invalidate(path.getFileKey(lastModified))
    // we cannot refresh a specific image in Glide Cache, so just clear it all
    val glide = Glide.get(applicationContext)
    glide.clearDiskCache()
    runOnUiThread {
        glide.clearMemory()
    }
}

fun BaseSimpleActivity.copyFile(source: String, destination: String) {
    var inputStream: InputStream? = null
    var out: OutputStream? = null
    try {
        out = getFileOutputStreamSync(path = destination, mimeType = source.getMimeType())
        inputStream = getFileInputStreamSync(source)
        inputStream!!.copyTo(out!!)
    } catch (e: Exception) {
        showErrorToast(e)
    } finally {
        inputStream?.close()
        out?.close()
    }
}

fun BaseSimpleActivity.ensureWriteAccess(path: String, callback: () -> Unit) {
    when {
        isRestrictedSAFOnlyRoot(path) -> {
            handleAndroidSAFDialog(path) {
                if (!it) {
                    return@handleAndroidSAFDialog
                }
                callback.invoke()
            }
        }

        needsStupidWritePermissions(path) -> {
            handleSAFDialog(path) {
                if (!it) {
                    return@handleSAFDialog
                }
                callback()
            }
        }

        isAccessibleWithSAFSdk30(path) -> {
            handleSAFDialogSdk30(path) {
                if (!it) {
                    return@handleSAFDialogSdk30
                }
                callback()
            }
        }

        else -> {
            callback()
        }
    }
}

fun BaseSimpleActivity.launchResizeMultipleImagesDialog(
    paths: List<String>,
    callback: (() -> Unit)? = null,
) {
    ensureBackgroundThread {
        val imagePaths = mutableListOf<String>()
        val imageSizes = mutableListOf<Point>()
        for (path in paths) {
            val size = path.getImageResolution(this)
            if (size != null) {
                imagePaths.add(path)
                imageSizes.add(size)
            }
        }

        runOnUiThread {
            ResizeMultipleImagesDlg(
                activity = this,
                imagePaths = imagePaths,
                imageSizes = imageSizes
            ) {
                callback?.invoke()
            }
        }
    }
}

fun BaseSimpleActivity.launchResizeImageDialog(path: String, callback: (() -> Unit)? = null) {
    val originalSize = path.getImageResolution(this) ?: return
    ResizeWithPathDlg(activity = this, size = originalSize, path = path) { newSize, newPath ->
        ensureBackgroundThread {
            val file = File(newPath)
            val pathLastModifiedMap = mapOf(file.absolutePath to file.lastModified())
            try {
                resizeImage(oldPath = path, newPath = newPath, size = newSize) { success ->
                    if (success) {
                        toast(org.fossify.commons.R.string.file_saved)

                        val paths = arrayListOf(file.absolutePath)
                        rescanPathsAndUpdateLastModified(paths, pathLastModifiedMap) {
                            runOnUiThread {
                                callback?.invoke()
                            }
                        }
                    } else {
                        toast(R.string.image_editing_failed)
                    }
                }
            } catch (e: OutOfMemoryError) {
                toast(org.fossify.commons.R.string.out_of_memory_error)
            } catch (e: Exception) {
                showErrorToast(e)
            }
        }
    }
}

fun BaseSimpleActivity.resizeImage(
    oldPath: String,
    newPath: String,
    size: Point,
    callback: (success: Boolean) -> Unit,
) {
    var oldExif: ExifInterface? = null
    if (isNougatPlus()) {
        val inputStream = contentResolver.openInputStream(Uri.fromFile(File(oldPath)))
        oldExif = ExifInterface(inputStream!!)
    }

    val newBitmap = Glide.with(applicationContext).asBitmap().load(oldPath).submit(size.x, size.y).get()

    val newFile = File(newPath)
    val newFileDirItem = FileDirItem(newPath, newPath.getFilenameFromPath())
    getFileOutputStream(newFileDirItem, true) { out ->
        if (out != null) {
            out.use {
                try {
                    newBitmap.compress(newFile.absolutePath.getCompressionFormat(), 90, out)

                    if (isNougatPlus()) {
                        val newExif = ExifInterface(newFile.absolutePath)
                        oldExif?.copyNonDimensionAttributesTo(newExif)
                    }
                } catch (ignored: Exception) {
                }

                callback(true)
            }
        } else {
            callback(false)
        }
    }
}

fun BaseSimpleActivity.rescanPathsAndUpdateLastModified(
    paths: ArrayList<String>,
    pathLastModifiedMap: Map<String, Long>,
    callback: () -> Unit,
) {
    fixDateTaken(paths, false)
    for (path in paths) {
        val file = File(path)
        val lastModified = pathLastModifiedMap[path]
        if (config.keepLastModified && lastModified != null && lastModified != 0L) {
            File(file.absolutePath).setLastModified(lastModified)
            updateLastModified(file.absolutePath, lastModified)
        }
    }
    rescanPaths(paths, callback)
}

fun saveFile(
    path: String,
    bitmap: Bitmap,
    out: FileOutputStream,
    degrees: Int,
) {
    val matrix = Matrix()
    matrix.postRotate(degrees.toFloat())
    val bmp = Bitmap.createBitmap(
        /* source = */ bitmap,
        /* x = */ 0,
        /* y = */ 0,
        /* width = */ bitmap.width,
        /* height = */ bitmap.height,
        /* m = */ matrix,
        /* filter = */ true
    )
    bmp.compress(
        /* format = */ path.getCompressionFormat(),
        /* quality = */ 90,
        /* stream = */ out
    )
}

fun Activity.getShortcutImage(
    tmb: String,
    drawable: Drawable,
    callback: () -> Unit,
) {
    ensureBackgroundThread {
        val options = RequestOptions()
            .format(DecodeFormat.PREFER_ARGB_8888)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .fitCenter()

        val size = resources.getDimension(org.fossify.commons.R.dimen.shortcut_size).toInt()
        val builder = Glide.with(this)
            .asDrawable()
            .load(tmb)
            .apply(options)
            .centerCrop()
            .into(size, size)

        try {
            (drawable as LayerDrawable).setDrawableByLayerId(R.id.shortcut_image, builder.get())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        runOnUiThread {
            callback()
        }
    }
}

@TargetApi(Build.VERSION_CODES.N)
fun Activity.showFileOnMap(path: String) {
    val exif = try {
        if (path.startsWith("content://") && isNougatPlus()) {
            ExifInterface(contentResolver.openInputStream(Uri.parse(path))!!)
        } else {
            ExifInterface(path)
        }
    } catch (e: Exception) {
        showErrorToast(e)
        return
    }

    val latLon = FloatArray(2)
    if (exif.getLatLong(latLon)) {
        showLocationOnMap("${latLon[0]}, ${latLon[1]}")
    } else {
        toast(R.string.unknown_location)
    }
}

fun Activity.handleExcludedFolderPasswordProtection(callback: () -> Unit) {
    if (config.isExcludedPasswordProtectionOn) {
        SecurityDialog(
            activity = this,
            requiredHash = config.excludedPasswordHash,
            showTabIndex = config.excludedProtectionType
        ) { _, _, success ->
            if (success) {
                callback()
            }
        }
    } else {
        callback()
    }
}

fun Activity.openRecycleBin() {
    Intent(this, MediaActMediaOperations::class.java).apply {
        putExtra(DIRECTORY, RECYCLE_BIN)
        startActivity(this)
    }
}

fun Activity.rateApp(
    packageName: String? = null,
) {
    if (packageName.isNullOrEmpty()) {
        return
    }
    try {
        this.startActivity(
            Intent(
                Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")
            )
        )
    } catch (e: android.content.ActivityNotFoundException) {
        e.printStackTrace()
        this.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
            )
        )
    }
}

fun Activity.moreApp(
    nameOfDeveloper: String = "McKimQuyen",
) {
    val uri = "https://play.google.com/store/apps/developer?id=$nameOfDeveloper"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
    this.startActivity(intent)
}

fun Activity.shareApp(
) {
    try {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_SUBJECT, this.getString(R.string.app_name))
        var sAux = "\nỨng dụng này rất bổ ích, thân mời bạn tải về cài đặt để trải nghiệm\n\n"
        sAux = sAux + "https://play.google.com/store/apps/details?id=" + this.packageName
        intent.putExtra(Intent.EXTRA_TEXT, sAux)
        this.startActivity(Intent.createChooser(intent, "Vui lòng chọn"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Context.isDefaultLauncher(): Boolean {
    val intent = Intent(Intent.ACTION_MAIN)
    intent.addCategory(Intent.CATEGORY_HOME)
    val resolveInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager.resolveActivity(
            /* intent = */ intent,
            /* flags = */ PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong())
        )
    } else {
        @Suppress("DEPRECATION")
        packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
    }

    val currentLauncherName = resolveInfo?.activityInfo?.packageName
    if (currentLauncherName == packageName) {
        return true
    }
    return false
}

//mo app setting default cua device
fun Context.launchSystemSetting(
    packageName: String,
) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.data = Uri.parse("package:$packageName")
    this.startActivity(intent)
}

/*
         * send email support
         */
fun Context?.sendEmail(
) {
    val emailIntent = Intent(Intent.ACTION_SENDTO)
    emailIntent.data = Uri.parse("mailto: roy.mobile.dev@gmail.com")
    this?.startActivity(Intent.createChooser(emailIntent, "Send feedback"))
}

const val URL_POLICY_NOTION = "https://loitp.notion.site/loitp/Privacy-Policy-319b1cd8783942fa8923d2a3c9bce60f/"

//const val URL_LICENSE = "https://raw.githubusercontent.com/gj-loitp/PocketPlan/master/LICENSE"

fun Context.openBrowserPolicy(
) {
    this.openUrlInBrowser(url = URL_POLICY_NOTION)
}

fun Context?.openUrlInBrowser(
    url: String?,
) {
    if (this == null || url.isNullOrEmpty()) {
        return
    }
    try {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        this.startActivity(i)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
