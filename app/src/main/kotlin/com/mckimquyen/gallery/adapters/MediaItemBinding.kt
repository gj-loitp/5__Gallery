package com.mckimquyen.gallery.adapters

import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.mckimquyen.gallery.databinding.*
import org.fossify.commons.views.MySquareImageView

interface MediaItemBinding {
    val root: ViewGroup
    val mediaItemHolder: ViewGroup
    val favorite: ImageView
    val playPortraitOutline: ImageView?
    val fileType: TextView?
    val mediumName: TextView
    val videoDuration: TextView?
    val mediumCheck: ImageView
    val mediumThumbnail: MySquareImageView
}

class PhotoListMediaItemBinding(val binding: VPhotoItemListBinding) : MediaItemBinding {
    override val root: ViewGroup = binding.root
    override val mediaItemHolder: ViewGroup = binding.mediaItemHolder
    override val favorite: ImageView = binding.favorite
    override val playPortraitOutline: ImageView? = null
    override val fileType: TextView = binding.tvFileType
    override val mediumName: TextView = binding.tvMediumName
    override val videoDuration: TextView? = null
    override val mediumCheck: ImageView = binding.mediumCheck
    override val mediumThumbnail: MySquareImageView = binding.mediumThumbnail
}

fun VPhotoItemListBinding.toMediaItemBinding() = PhotoListMediaItemBinding(this)

class PhotoGridMediaItemBinding(val binding: VPhotoItemGridBinding) : MediaItemBinding {
    override val root: ViewGroup = binding.root
    override val mediaItemHolder: ViewGroup = binding.mediaItemHolder
    override val favorite: ImageView = binding.favorite
    override val playPortraitOutline: ImageView? = null
    override val fileType: TextView = binding.tvFileType
    override val mediumName: TextView = binding.tvMediumName
    override val videoDuration: TextView? = null
    override val mediumCheck: ImageView = binding.mediumCheck
    override val mediumThumbnail: MySquareImageView = binding.mediumThumbnail
}

fun VPhotoItemGridBinding.toMediaItemBinding() = PhotoGridMediaItemBinding(this)

class VideoListMediaItemBinding(val binding: VVideoItemListBinding) : MediaItemBinding {
    override val root: ViewGroup = binding.root
    override val mediaItemHolder: ViewGroup = binding.mediaItemHolder
    override val favorite: ImageView = binding.favorite
    override val playPortraitOutline: ImageView = binding.ivPlayPortraitOutline
    override val fileType: TextView? = null
    override val mediumName: TextView = binding.tvMediumName
    override val videoDuration: TextView = binding.tvVideoDuration
    override val mediumCheck: ImageView = binding.mediumCheck
    override val mediumThumbnail: MySquareImageView = binding.mediumThumbnail
}

fun VVideoItemListBinding.toMediaItemBinding() = VideoListMediaItemBinding(this)

class VideoGridMediaItemBinding(val binding: VVideoItemGridBinding) : MediaItemBinding {
    override val root: ViewGroup = binding.root
    override val mediaItemHolder: ViewGroup = binding.mediaItemHolder
    override val favorite: ImageView = binding.favorite
    override val playPortraitOutline: ImageView = binding.ivPlayPortraitOutline
    override val fileType: TextView? = null
    override val mediumName: TextView = binding.tvMediumName
    override val videoDuration: TextView = binding.tvVideoDuration
    override val mediumCheck: ImageView = binding.mediumCheck
    override val mediumThumbnail: MySquareImageView = binding.mediumThumbnail
}

fun VVideoItemGridBinding.toMediaItemBinding() = VideoGridMediaItemBinding(this)
