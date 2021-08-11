package com.dbtest.android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dbtest.android.databinding.ItemsUsersBinding
import com.dbtest.android.dataresponse.UserDetails
import com.dbtest.android.listener.OnItemClickListener
import com.dbtest.android.utils.imageGlideLoad


class UsersAdapter(private val itemClickListener: OnItemClickListener) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mItems = mutableListOf<UserDetails>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return UserVH(ItemsUsersBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as UserVH).onBindData(getItem(position))
    }

    override fun getItemCount() = mItems.size

    fun getItem(index: Int): UserDetails {
        return mItems[index]
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setData(items: List<UserDetails>) {
        this.mItems.clear()
        this.mItems.addAll(items)
        notifyDataSetChanged()
    }

    inner class UserVH(val binding: ItemsUsersBinding) :
            RecyclerView.ViewHolder(binding.root) {
        fun onBindData(item: UserDetails) {
            binding.userName.text = item.first_name.plus(" ").plus(item.last_name)
            binding.userEmail.text = item.email
            imageGlideLoad(binding.userImage, item.avatar)
            itemView.setOnClickListener {
                val userDetails = getItem(adapterPosition)
                itemClickListener.itemClick(userDetails)
            }
        }
    }
}