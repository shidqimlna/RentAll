package com.example.rentall.data.firebase

import android.net.Uri
import android.util.Log
import com.example.rentall.data.entity.ChatEntity
import com.example.rentall.data.entity.ProductEntity
import com.example.rentall.data.entity.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class RemoteDataSource {

    private val userId = FirebaseAuth.getInstance().currentUser!!.uid

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
                    listProduct.clear()
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

    fun rentProduct(productEntity: ProductEntity?) {
        val rentId =
            FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Rents")
                .push().key
        val userRentRef =
            FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Rents")
                .child(rentId!!)
        val productInfo: MutableMap<String, Any> = HashMap()
        productInfo["id"] = productEntity?.id as String
        productInfo["time"] =
            SimpleDateFormat("MMM dd, yyyy 'at' hh:mm:ss a").format(Calendar.getInstance().time)
        userRentRef.updateChildren(productInfo)
    }

    fun chatOwner(productEntity: ProductEntity?) {
        val productInfo: MutableMap<String, Any> = HashMap()
        productInfo["id"] = productEntity?.id as String
        val userChatRef =
            FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("Chats")
                .child(productEntity.id as String)
        userChatRef.updateChildren(productInfo)
    }

    fun addProduct(productEntity: ProductEntity?, filePath: Uri?) {
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

        val productInfo: MutableMap<String, Any> = HashMap()
        productInfo["id"] = productId
        productInfo["name"] = productEntity?.name.toString()
        productInfo["price"] = productEntity?.price.toString()
        productInfo["desc"] = productEntity?.desc.toString()
        productInfo["owner"] = productEntity?.owner.toString()
        productRef.updateChildren(productInfo)

        val productInfo2: MutableMap<String, Any> = HashMap()
        productInfo2["id"] = productId
        userProductRef.updateChildren(productInfo2)

        val productInfo3: MutableMap<String, Any> = HashMap()
        productInfo3["id"] = productId
        userChatRef.updateChildren(productInfo3)

        if (filePath != null) productImageRef.putFile(filePath)
    }

    fun editProduct(productEntity: ProductEntity?, filePath: Uri?) {
        val productRef =
            FirebaseDatabase.getInstance().reference.child("Products").child(productEntity?.id!!)
        val productInfo: MutableMap<String, Any> = HashMap()
        productInfo["name"] = productEntity.name.toString()
        productInfo["price"] = productEntity.price.toString()
        productInfo["desc"] = productEntity.desc.toString()
        productRef.updateChildren(productInfo)

        val productImageRef =
            FirebaseStorage.getInstance().reference.child("images/products/${productEntity.id}")
        if (filePath != null) productImageRef.putFile(filePath)
    }

    fun deleteProduct(productEntity: ProductEntity?) {
        val productRef =
            FirebaseDatabase.getInstance().reference.child("Products").child(productEntity?.id!!)
        productRef.removeValue()
        val productImageRef =
            FirebaseStorage.getInstance().reference.child("images/products/${productEntity.id}")
        productImageRef.delete()
    }

    fun sendMessage(chatEntity: ChatEntity, productEntity: ProductEntity?) {
        val chatId =
            FirebaseDatabase.getInstance().reference.child("Chats").child(productEntity?.id!!)
                .push().key
        val chatRef =
            FirebaseDatabase.getInstance().reference.child("Chats").child(productEntity.id!!)
                .child(chatId!!)
        val messageInfoMap: HashMap<String, Any> = HashMap()
        messageInfoMap["name"] = chatEntity.name.toString()
        messageInfoMap["message"] = chatEntity.message.toString()
        messageInfoMap["time"] = chatEntity.time.toString()
        chatRef.updateChildren(messageInfoMap)
    }

    fun getMessages(productEntity: ProductEntity?, callback: LoadChatMessagesCallback) {
        val listChat: MutableList<ChatEntity?> = mutableListOf()
        val chatRef =
            FirebaseDatabase.getInstance().reference.child("Chats").child(productEntity?.id!!)
                .orderByChild("time")
        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    listChat.clear()
                    for (dataSnapshot in dataSnapshot.children) {
                        val chatEntity: ChatEntity? = dataSnapshot.getValue(ChatEntity::class.java)
                        listChat.add(chatEntity)
                    }
                    Log.e("RDS", listChat.size.toString())
                    callback.onChatMessagesReceived(listChat)
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

    interface LoadChatMessagesCallback {
        fun onChatMessagesReceived(chatResponse: List<ChatEntity?>)
    }

}


