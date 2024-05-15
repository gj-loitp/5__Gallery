package com.mckimquyen.gallery.itf

import com.mckimquyen.gallery.model.Directory
import java.io.File

interface ListenerDirectoryOperations {
    fun refreshItems()

    fun deleteFolders(folders: ArrayList<File>)

    fun recheckPinnedFolders()

    fun updateDirectories(directories: ArrayList<Directory>)
}