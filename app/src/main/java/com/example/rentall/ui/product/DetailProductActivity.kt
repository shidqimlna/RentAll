package com.example.rentall.ui.product

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.rentall.R
import com.example.rentall.data.entity.ProductEntity
import com.example.rentall.ui.chat.ChatActivity
import com.example.rentall.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_detail_product.*
import kotlinx.android.synthetic.main.item_rent_history.view.*
import java.text.SimpleDateFormat
import java.util.*

class DetailProductActivity : AppCompatActivity() {

    private lateinit var userRentRef: DatabaseReference
    private lateinit var userId: String
    private var productEntity: ProductEntity? = ProductEntity()

    companion object {
        const val EXTRA_PRODUCT = "extra_product"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_product)

        userId = FirebaseAuth.getInstance().currentUser!!.uid
        val productId = intent.extras?.getString(EXTRA_PRODUCT) ?: ""
        val productRef = FirebaseDatabase.getInstance().reference.child("Products").child(productId)
        val rentId =
            FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Rents")
                .push().key
        userRentRef =
            FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Rents")
                .child(rentId!!)
        val storageReference = FirebaseStorage.getInstance().reference

        productRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                    productEntity = dataSnapshot.getValue(ProductEntity::class.java)
                    storageReference.child("images/products/${productEntity?.id}").downloadUrl.addOnSuccessListener { uri ->
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

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        activity_detail_product_btn_chat.setOnClickListener {
            chatOwner()
            val intent = Intent(this@DetailProductActivity, ChatActivity::class.java)
            intent.putExtra(ChatActivity.EXTRA_PRODUCT, productEntity?.id)
            startActivity(intent)
            finish()
        }

        activity_detail_product_btn_rent.setOnClickListener {
            if (userId != productEntity?.ownerId) {
                rentProduct()
                val intent = Intent(this@DetailProductActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun rentProduct() {
        val productInfo: MutableMap<String, Any> = HashMap()
        productInfo["id"] = productEntity?.id as String
        productInfo["time"] =
            SimpleDateFormat("MMM dd, yyyy 'at' hh:mm:ss a").format(Calendar.getInstance().time)
        userRentRef.updateChildren(productInfo)
    }

    private fun chatOwner() {
        val productInfo: MutableMap<String, Any> = HashMap()
        productInfo["id"] = productEntity?.id as String
        val userChatRef =
            FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Chats")
                .child(productEntity?.id as String)
        userChatRef.updateChildren(productInfo)
    }

}