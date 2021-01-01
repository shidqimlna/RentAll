package com.example.rentall.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rentall.R
import com.example.rentall.data.entity.ProductEntity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.item_rent_history.view.*


class RentHistoryAdapter : RecyclerView.Adapter<RentHistoryAdapter.ListViewHolder>() {
    private val listProducts = ArrayList<ProductEntity?>()
    private lateinit var storageReference: StorageReference

    fun setData(entities: Collection<ProductEntity?>) {
        listProducts.clear()
        listProducts.addAll(entities)
        notifyDataSetChanged()
        storageReference = FirebaseStorage.getInstance().reference
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder =
        ListViewHolder(
            LayoutInflater.from(viewGroup.context).inflate(
                R.layout.item_rent_history,
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
                            .into(item_rent_history_iv_product)
                    }.addOnFailureListener {}
                    item_rent_history_tv_name.text = it.name
                    item_rent_history_tv_price.text = it.price
                    item_rent_history_tv_owner.text = it.owner
                    item_rent_history_tv_time.text = it.time
//                    item_rent_history_cardView.setOnClickListener {
//                        val intent = Intent(context, DetailProductActivity::class.java)
//                        intent.putExtra(DetailProductActivity.EXTRA_PRODUCT, productEntity.id)
//                        context.startActivity(intent)
//                    }
                }
            }
        }
    }
}