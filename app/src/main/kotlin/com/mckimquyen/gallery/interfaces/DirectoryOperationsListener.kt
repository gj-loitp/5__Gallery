package com.mckimquyen.gallery.interfaces

import com.mckimquyen.gallery.models.Directory
import java.io.File

interface DirectoryOperationsListener {
    fun refreshItems()

    fun deleteFolders(folders: ArrayList<File>)

    fun recheckPinnedFolders()

    fun updateDirectories(directories: ArrayList<Directory>)
}
