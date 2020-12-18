package com.example.rentall

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.item_user_product.view.*


class UserProductAdapter : RecyclerView.Adapter<UserProductAdapter.ListViewHolder>() {
    private val listProducts = ArrayList<ProductEntity?>()
    private lateinit var storageReference: StorageReference

    fun userProductAdapter(entities: ArrayList<ProductEntity?>) {
        listProducts.clear()
        listProducts.addAll(entities)
        notifyDataSetChanged()
        storageReference = FirebaseStorage.getInstance().reference
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder =
        ListViewHolder(
            LayoutInflater.from(viewGroup.context).inflate(
                R.layout.item_user_product,
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
                    item_user_product_tv_linkimg.text = it.image
                    item_user_product_cardView.setOnClickListener {
                        val intent = Intent(context, EditProductActivity::class.java)
                        intent.putExtra(EditProductActivity.EXTRA_PRODUCT, productEntity.id)
                        context.startActivity(intent)
                    }
                }
            }
        }
    }
}