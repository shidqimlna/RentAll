package com.example.rentall.data.firebase

import com.example.rentall.data.entity.ProductEntity
import com.example.rentall.data.entity.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RemoteDataSource {

    companion object {
        @Volatile
        private var instance: RemoteDataSource? = null
        fun getInstance(): RemoteDataSource =
            instance ?: synchronized(this) {
                instance ?: RemoteDataSource()
            }
    }

    fun getUserDetail(callback: LoadUserDetailCallback) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
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

    interface LoadProductsCallback {
        fun onAllProductsReceived(productResponse: List<ProductEntity?>)
    }

    interface LoadUserDetailCallback {
        fun onUserDetailReceived(userResponse: UserEntity?)
    }
}


