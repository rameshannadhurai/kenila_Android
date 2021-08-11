package com.dbtest.android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dbtest.android.data.roomdata.ImageVideo
import com.dbtest.android.databinding.ItemFileViewBinding
import com.dbtest.android.listener.OnItemFileClickListener
import com.dbtest.android.utils.imageGlideLoad

class FileAdapter(private val itemClickListener: OnItemFileClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mItems = mutableListOf<ImageVideo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return FileVH(
            ItemFileViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as FileVH).onBindData(getItem(position))
    }

    override fun getItemCount() = mItems.size

    fun getItem(index: Int): ImageVideo {
        return mItems[index]
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setData(items: List<ImageVideo>) {
        this.mItems.clear()
        this.mItems.addAll(items)
        notifyDataSetChanged()
    }

    inner class FileVH(val binding: ItemFileViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBindData(item: ImageVideo) {
            imageGlideLoad(binding.fileImage, item.file)
            itemView.setOnClickListener {
                val item = getItem(adapterPosition)
                itemClickListener.itemClick(item)
            }
        }
    }
}