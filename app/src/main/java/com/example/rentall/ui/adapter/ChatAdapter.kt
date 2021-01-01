package com.example.rentall.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rentall.R
import com.example.rentall.data.entity.ChatEntity
import kotlinx.android.synthetic.main.item_chat.view.*


class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ListViewHolder>() {
    private val listChats = ArrayList<ChatEntity?>()

    fun setChat(entities: Collection<ChatEntity?>) {
        listChats.clear()
        listChats.addAll(entities)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder =
        ListViewHolder(
            LayoutInflater.from(viewGroup.context).inflate(
                R.layout.item_chat,
                viewGroup,
                false
            )
        )

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bindView(listChats[position])
    }

    override fun getItemCount(): Int = listChats.size

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(chatEntity: ChatEntity?) {
            chatEntity?.let {
                with(itemView) {
                        item_chat_tv_name_mine.text = it.name
                        item_chat_tv_message_mine.text = it.message
                        item_chat_tv_time_mine.text = it.time
                }
            }
        }
    }
}