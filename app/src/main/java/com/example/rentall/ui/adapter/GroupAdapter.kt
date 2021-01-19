package com.example.rentall.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rentall.R
import com.example.rentall.data.entity.ProductEntity
import com.example.rentall.ui.activity.chat.ChatActivity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.item_product.view.*

class GroupAdapter : RecyclerView.Adapter<GroupAdapter.ListViewHolder>() {
    private val listProducts = ArrayList<ProductEntity?>()
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference

    fun setData(entities: Collection<ProductEntity?>) {
        listProducts.clear()
        listProducts.addAll(entities)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder =
        ListViewHolder(
            LayoutInflater.from(viewGroup.context).inflate(
                R.layout.item_product,
                viewGroup,
                false
            )
        )

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bindView(listProducts[position])
    }

    override fun getItemCount(): Int = listProducts.size

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(productEntity: ProductEntity?) {
            productEntity?.let {
                with(itemView) {
                    storageReference.child("images/products/${it.id}").downloadUrl.addOnSuccessListener { uri ->
                        Glide.with(context)
                            .load(uri)
                            .into(item_user_product_iv_product)
                    }.addOnFailureListener {}
                    item_user_product_tv_name.text = it.name
                    item_user_product_tv_price.text = it.price
                    item_user_product_tv_owner.text = it.owner
                    item_user_product_cardView.setOnClickListener {
                        val intent = Intent(context, ChatActivity::class.java)
                        intent.putExtra(ChatActivity.EXTRA_PRODUCT, productEntity)
                        context.startActivity(intent)
                    }
                }
            }
        }
    }
}