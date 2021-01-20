package com.example.rentall.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rentall.R
import com.example.rentall.data.entity.RentEntity
import com.example.rentall.util.Helper.currencyFormatter
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.item_product_landscape.view.*

class RentHistoryAdapter : RecyclerView.Adapter<RentHistoryAdapter.ListViewHolder>() {
    private val listProducts = ArrayList<RentEntity?>()
    private lateinit var storageReference: StorageReference

    fun setData(entities: Collection<RentEntity?>) {
        listProducts.clear()
        listProducts.addAll(entities)
        notifyDataSetChanged()
        storageReference = FirebaseStorage.getInstance().reference
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder =
        ListViewHolder(
            LayoutInflater.from(viewGroup.context).inflate(
                R.layout.item_product_landscape,
                viewGroup,
                false
            )
        )

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bindView(listProducts[position])
    }

    override fun getItemCount(): Int = listProducts.size

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(rentEntity: RentEntity?) {
            rentEntity?.let {
                with(itemView) {
                    it.productEntity?.let {
                        storageReference.child("images/products/${it.id}").downloadUrl.addOnSuccessListener { uri ->
                            Glide.with(context)
                                .load(uri)
                                .into(item_product_landscape_iv_product)
                        }.addOnFailureListener {}
                        item_product_landscape_tv_name.text = it.name
                        item_product_landscape_tv_price.text = currencyFormatter(it.price)
                        item_product_landscape_tv_owner.text = it.owner
                    }
                    item_product_landscape_tv_time.text = it.time
                }
            }
        }
    }
}