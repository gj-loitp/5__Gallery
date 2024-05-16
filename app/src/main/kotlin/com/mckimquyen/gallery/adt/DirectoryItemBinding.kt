package com.mckimquyen.gallery.adt

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import org.fossify.commons.views.MySquareImageView
import com.mckimquyen.gallery.databinding.VDirectoryItemGridRoundedCornersBinding
import com.mckimquyen.gallery.databinding.VDirectoryItemGridSquareBinding
import com.mckimquyen.gallery.databinding.VDirectoryItemListBinding

interface DirectoryItemBinding {
    val root: ViewGroup
    val dirThumbnail: MySquareImageView
    val dirPath: TextView?
    val dirCheck: ImageView
    val dirHolder: ViewGroup
    val photoCnt: TextView
    val dirName: TextView
    val dirLock: ImageView
    val dirPin: ImageView
    val dirLocation: ImageView
    val dirDragHandle: ImageView
    val dirDragHandleWrapper: ViewGroup?
}

class ListDirectoryItemBinding(val binding: VDirectoryItemListBinding) : DirectoryItemBinding {
    override val root: ViewGroup = binding.root
    override val dirThumbnail: MySquareImageView = binding.dirThumbnail
    override val dirPath: TextView = binding.dirPath
    override val dirCheck: ImageView = binding.dirCheck
    override val dirHolder: ViewGroup = binding.dirHolder
    override val photoCnt: TextView = binding.photoCnt
    override val dirName: TextView = binding.dirName
    override val dirLock: ImageView = binding.dirLock
    override val dirPin: ImageView = binding.dirPin
    override val dirLocation: ImageView = binding.dirLocation
    override val dirDragHandle: ImageView = binding.dirDragHandle
    override val dirDragHandleWrapper: ViewGroup? = null
}

fun VDirectoryItemListBinding.toItemBinding() = ListDirectoryItemBinding(this)

class GridDirectoryItemSquareBinding(val binding: VDirectoryItemGridSquareBinding) : DirectoryItemBinding {
    override val root: ViewGroup = binding.root
    override val dirThumbnail: MySquareImageView = binding.dirThumbnail
    override val dirPath: TextView? = null
    override val dirCheck: ImageView = binding.dirCheck
    override val dirHolder: ViewGroup = binding.dirHolder
    override val photoCnt: TextView = binding.photoCnt
    override val dirName: TextView = binding.dirName
    override val dirLock: ImageView = binding.dirLock
    override val dirPin: ImageView = binding.dirPin
    override val dirLocation: ImageView = binding.dirLocation
    override val dirDragHandle: ImageView = binding.dirDragHandle
    override val dirDragHandleWrapper: ViewGroup = binding.dirDragHandleWrapper
}

fun VDirectoryItemGridSquareBinding.toItemBinding() = GridDirectoryItemSquareBinding(this)

class GridDirectoryItemRoundedCornersBinding(val binding: VDirectoryItemGridRoundedCornersBinding) : DirectoryItemBinding {
    override val root: ViewGroup = binding.root
    override val dirThumbnail: MySquareImageView = binding.dirThumbnail
    override val dirPath: TextView? = null
    override val dirCheck: ImageView = binding.dirCheck
    override val dirHolder: ViewGroup = binding.dirHolder
    override val photoCnt: TextView = binding.photoCnt
    override val dirName: TextView = binding.dirName
    override val dirLock: ImageView = binding.dirLock
    override val dirPin: ImageView = binding.dirPin
    override val dirLocation: ImageView = binding.dirLocation
    override val dirDragHandle: ImageView = binding.dirDragHandle
    override val dirDragHandleWrapper: ViewGroup = binding.dirDragHandleWrapper
}

fun VDirectoryItemGridRoundedCornersBinding.toItemBinding() = GridDirectoryItemRoundedCornersBinding(this)
