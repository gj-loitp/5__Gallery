package com.mckimquyen.gallery.adt

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mckimquyen.gallery.R
import com.mckimquyen.gallery.databinding.VEditorFilterItemBinding
import com.mckimquyen.gallery.model.FilterItem

class FiltersAdt(
    val context: Context,
    private val filterItems: ArrayList<FilterItem>,
    private val itemClick: (Int) -> Unit,
) :
    RecyclerView.Adapter<FiltersAdt.ViewHolder>() {

    private var currentSelection = filterItems.first()
//    private var strokeBackground = context.resources.getDrawable(R.drawable.selector_stroke_background)
    private var strokeBackground = ContextCompat.getDrawable(context, R.drawable.selector_stroke_background)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(filterItems[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = VEditorFilterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = filterItems.size

    fun getCurrentFilter() = currentSelection

    private fun setCurrentFilter(position: Int) {
        val filterItem = filterItems.getOrNull(position) ?: return
        if (currentSelection != filterItem) {
            currentSelection = filterItem
            notifyDataSetChanged()
            itemClick.invoke(position)
        }
    }

    inner class ViewHolder(private val binding: VEditorFilterItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindView(filterItem: FilterItem): View {
            binding.apply {
                editorFilterItemLabel.text = filterItem.filter.name
                editorFilterItemThumbnail.setImageBitmap(filterItem.bitmap)
                editorFilterItemThumbnail.background = if (getCurrentFilter() == filterItem) {
                    strokeBackground
                } else {
                    null
                }

                root.setOnClickListener {
                    setCurrentFilter(adapterPosition)
                }
            }
            return itemView
        }
    }
}