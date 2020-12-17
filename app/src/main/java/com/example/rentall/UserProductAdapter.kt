package com.example.rentall

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_user_product.view.*

class UserProductAdapter : RecyclerView.Adapter<UserProductAdapter.ListViewHolder>() {

    private val listProducts = ArrayList<ProductEntity?>()

    fun setData(entities: ArrayList<ProductEntity?>) {
        listProducts.clear()
        listProducts.addAll(entities)
        notifyDataSetChanged()
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
//                    Glide.with(this /* context */)
//                        .using(FirebaseImageLoader())
//                        .load(storageReference)
//                        .into(imageView)
                    Picasso.get().load(it.image).fit()
//                        .placeholder(R.drawable.loading_decor).error(R.drawable.ic_imageerror)
                        .into(item_user_product_iv_product)
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