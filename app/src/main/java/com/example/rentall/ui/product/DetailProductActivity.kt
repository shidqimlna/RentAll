package com.example.rentall.ui.product

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.rentall.R
import com.example.rentall.data.entity.ProductEntity
import com.example.rentall.di.Injection
import com.example.rentall.ui.chat.ChatActivity
import com.example.rentall.ui.main.MainActivity
import com.example.rentall.viewmodel.MainViewModel
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_detail_product.*
import java.util.*

class DetailProductActivity : AppCompatActivity() {

    private lateinit var username: String
    private var productEntity: ProductEntity? = ProductEntity()
    private lateinit var viewModel: MainViewModel

    companion object {
        const val EXTRA_PRODUCT = "extra_product"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_product)

        productEntity = intent.extras?.getParcelable(EXTRA_PRODUCT)

        viewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory()
        )[MainViewModel::class.java]

        viewModel.getUserDetail().observe(this, { user ->
            username = user?.fullname!!
        })

        activity_detail_product_btn_chat.setOnClickListener {
            viewModel.chatOwner(productEntity)
            val intent = Intent(this@DetailProductActivity, ChatActivity::class.java)
            intent.putExtra(ChatActivity.EXTRA_PRODUCT, productEntity?.id)
            startActivity(intent)
            finish()
        }

        activity_detail_product_btn_rent.setOnClickListener {
            if (username != productEntity?.owner) {
                viewModel.rentProduct(productEntity)
                val intent = Intent(this@DetailProductActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        loadData()
    }

    private fun loadData() {
        FirebaseStorage.getInstance().reference.child("images/products/${productEntity?.id}").downloadUrl.addOnSuccessListener { uri ->
            Glide.with(applicationContext)
                .load(uri)
                .into(activity_detail_product_iv_productimg)
        }.addOnFailureListener {}
        activity_detail_product_tv_name.text = productEntity?.name
        activity_detail_product_tv_price.text = productEntity?.price
        activity_detail_product_tv_owner.text = productEntity?.owner
        activity_detail_product_tv_desciption.text = productEntity?.desc
    }
}