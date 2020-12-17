package com.example.rentall

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_edit_product.*
import kotlinx.android.synthetic.main.activity_new_product.*


class EditProductActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PRODUCT = "extra_product"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        val productId = intent.extras?.getString(EXTRA_PRODUCT) ?: ""
        val productRef = FirebaseDatabase.getInstance().reference.child("Products").child(productId)

        productRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                    val productEntity: ProductEntity? =
                        dataSnapshot.getValue(ProductEntity::class.java)
                    activity_edit_product_et_name.setText(productEntity?.name)
                    activity_edit_product_et_price.setText(productEntity?.price)
                    activity_edit_product_et_desciption.setText(productEntity?.desc)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

        activity_edit_product_btn_submit.setOnClickListener{
            uploadData(productRef)

            val intent = Intent(this@EditProductActivity, UserAccountActivity::class.java)
            startActivity(intent)
            finish()
        }

        activity_edit_product_btn_delete.setOnClickListener{
                    productRef.removeValue()
                    val move = Intent(this@EditProductActivity, UserAccountActivity::class.java)
                    startActivity(move)
        }
    }

    private fun uploadData(productRef: DatabaseReference) {
        val productName: String = activity_edit_product_et_name.text.toString()
        val price: String = activity_edit_product_et_price.text.toString()
        val description: String = activity_edit_product_et_desciption.text.toString()

        val productInfo: MutableMap<String, Any> = java.util.HashMap()
        productInfo["name"] = productName
        productInfo["price"] = price
        productInfo["desc"] = description
        productRef.updateChildren(productInfo)
    }
}