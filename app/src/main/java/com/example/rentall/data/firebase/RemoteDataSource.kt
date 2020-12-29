package com.example.rentall.data.firebase

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.example.rentall.data.entity.ProductEntity
import com.example.rentall.data.entity.UserEntity
import com.example.rentall.ui.account.UserAccountActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class RemoteDataSource {

    val userId = FirebaseAuth.getInstance().currentUser!!.uid

    companion object {
        @Volatile
        private var instance: RemoteDataSource? = null
        fun getInstance(): RemoteDataSource =
            instance ?: synchronized(this) {
                instance ?: RemoteDataSource()
            }
    }

    fun getUserDetail(callback: LoadUserDetailCallback) {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
            .child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                        val userDetail: UserEntity? = dataSnapshot.getValue(UserEntity::class.java)
                        callback.onUserDetailReceived(userDetail)
                    }
                } catch (e: Exception) {
                    throw Exception(e.message.toString())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun getProductList(searchQuery: String?, callback: LoadProductsCallback) {
        val listProduct: MutableList<ProductEntity?> = mutableListOf()
        val productRef = FirebaseDatabase.getInstance().reference.child("Products")
            .orderByChild("name")
            .startAt(searchQuery)
            .endAt(searchQuery + "\uf8ff")
        productRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    for (dataSnapshot in dataSnapshot.children) {
                        val userProduct: ProductEntity? =
                            dataSnapshot.getValue(ProductEntity::class.java)
                        listProduct.add(userProduct)
                    }
                    callback.onAllProductsReceived(listProduct)
                } catch (e: Exception) {
                    throw Exception(e.message.toString())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun addProduct(productEntity: ProductEntity?, filePath: Uri?, context: Context) {
        val productId = FirebaseDatabase.getInstance().reference.child("Products").push().key
        val productRef =
            FirebaseDatabase.getInstance().reference.child("Products").child(productId!!)
        val userProductRef =
            FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Products")
                .child(productId)
        val userChatRef =
            FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Chats")
                .child(productId)
        val productImageRef =
            FirebaseStorage.getInstance().reference.child("images/products/$productId")

        Log.e("RDS", productEntity?.name.toString())

        val productInfo: MutableMap<String, Any> = HashMap()
        productInfo["id"] = productId
        productInfo["name"] = productEntity?.name.toString()
        productInfo["price"] = productEntity?.price.toString()
        productInfo["desc"] = productEntity?.desc.toString()
        productInfo["owner"] = productEntity?.owner.toString()
        productInfo["ownerId"] = userId
        productRef.updateChildren(productInfo)

        val productInfo2: MutableMap<String, Any> = HashMap()
        productInfo2["id"] = productId
        userProductRef.updateChildren(productInfo2)

        val productInfo3: MutableMap<String, Any> = HashMap()
        productInfo3["id"] = productId
        userChatRef.updateChildren(productInfo3)

        if (filePath != null) {
            val progressDialog = ProgressDialog(context)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()
            productImageRef.putFile(filePath).addOnSuccessListener {
                try {
                    progressDialog.dismiss()
                    Toast.makeText(context, "Data Uploaded!!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, UserAccountActivity::class.java)
                    startActivity(context, intent, null)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
                .addOnFailureListener { e ->
                    try {
                        progressDialog.dismiss()
                        Toast.makeText(context, "Failed " + e.message, Toast.LENGTH_SHORT).show()
                        val intent = Intent(context, UserAccountActivity::class.java)
                        startActivity(context, intent, null)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                .addOnProgressListener { taskSnapshot ->
                    try {
                        val progress =
                            (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                        progressDialog.setMessage("Uploaded " + progress.toInt() + "%")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        }
    }

    interface LoadProductsCallback {
        fun onAllProductsReceived(productResponse: List<ProductEntity?>)
    }

    interface LoadUserDetailCallback {
        fun onUserDetailReceived(userResponse: UserEntity?)
    }
}


