package com.example.rentall.data.firebase

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import com.example.rentall.data.entity.ChatEntity
import com.example.rentall.data.entity.ProductEntity
import com.example.rentall.data.entity.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class RemoteDataSource {

//    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
//    private val userRef = FirebaseDatabase.getInstance().reference.child("Users")
//        .child(userId)

    companion object {
        @Volatile
        private var instance: RemoteDataSource? = null
        fun getInstance(): RemoteDataSource =
            instance ?: synchronized(this) {
                instance ?: RemoteDataSource()
            }
    }

    fun registerUser(
        userEntity: UserEntity?,
        password: String,
        activity: Activity
    ) {
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(userEntity?.email.toString(), password)
            .addOnCompleteListener(activity) { task ->
                if (!task.isSuccessful || userEntity?.fullname.toString().isEmpty()) {
                    Toast.makeText(activity, "sign up error", Toast.LENGTH_SHORT).show()
                } else {
                    val userId = FirebaseAuth.getInstance().currentUser!!.uid
                    val userRef = FirebaseDatabase.getInstance().reference.child("Users")
                        .child(userId)
                    val userInfo: MutableMap<String, Any> = HashMap()
                    userInfo["id"] = userId
                    userInfo["email"] = userEntity?.email.toString()
                    userInfo["fullname"] = userEntity?.fullname.toString()
                    userInfo["phone"] = userEntity?.phone.toString()
                    userInfo["address"] = userEntity?.address.toString()
                    userRef.updateChildren(userInfo)
                }
            }
    }

    fun loginUser(email: String?, password: String?, activity: Activity) {
        if (email != null && password != null) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity) { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(activity, "Login Failed", Toast.LENGTH_SHORT).show()
                    }
                }
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

    fun editAccount(userEntity: UserEntity?) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
            .child(userId)
        val userInfo: MutableMap<String, Any> = HashMap()
        userInfo["fullname"] = userEntity?.fullname.toString()
        userInfo["phone"] = userEntity?.phone.toString()
        userInfo["address"] = userEntity?.address.toString()
        userRef.updateChildren(userInfo)
    }

    fun getProductList(searchQuery: String?, callback: LoadProductsCallback) {
        val listProduct: MutableList<ProductEntity?> = mutableListOf()
        var userProduct: ProductEntity?
        val productRef = FirebaseDatabase.getInstance().reference.child("Products")
            .orderByChild("name")
            .startAt(searchQuery)
            .endAt(searchQuery + "\uf8ff")
        productRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                        listProduct.clear()
                        for (dataSnapshot in dataSnapshot.children) {
                            userProduct =
                                dataSnapshot.getValue(ProductEntity::class.java)
                            listProduct.add(userProduct)
                        }
                        callback.onAllProductsReceived(listProduct)
                    }
                } catch (e: Exception) {
                    throw Exception(e.message.toString())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun getUserProductList(callback: LoadProductsCallback) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
            .child(userId)
        val listProduct: MutableList<ProductEntity?> = mutableListOf()
        var productEntity: ProductEntity?
        var productRef: Query
        var userProductIdRef: DatabaseReference
        val userProductRef = userRef.child("Products")
        userProductRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                        listProduct.clear()
                        for (dataSnapshot1 in dataSnapshot.children) {
                            productEntity =
                                dataSnapshot1.getValue(ProductEntity::class.java)
                            productRef =
                                FirebaseDatabase.getInstance().reference.child("Products")
                                    .orderByChild("id")
                                    .equalTo(productEntity?.id)
                            productRef.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (dataSnapshot2 in dataSnapshot.children) {
                                            productEntity =
                                                dataSnapshot2.getValue(ProductEntity::class.java)
                                            listProduct.add(productEntity)
                                        }
                                        callback.onAllProductsReceived(listProduct)
                                    } else {
                                        userProductIdRef = userProductRef.child(productEntity?.id!!)
                                        userProductIdRef.removeValue()
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {}
                            })
                        }
                    }
                } catch (e: Exception) {
                    throw Exception(e.message.toString())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun getUserChatList(callback: LoadProductsCallback) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
            .child(userId)
        val listUserChats: MutableList<ProductEntity?> = mutableListOf()
        var productEntity: ProductEntity?
        var productRef: Query
        var userChatIdRef: DatabaseReference
        val userChatRef = userRef.child("Chats")
        userChatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                        listUserChats.clear()
                        for (dataSnapshot1 in dataSnapshot.children) {
                            productEntity =
                                dataSnapshot1.getValue(ProductEntity::class.java)
                            productRef =
                                FirebaseDatabase.getInstance().reference.child("Products")
                                    .orderByChild("id")
                                    .equalTo(productEntity?.id)
                            productRef.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (dataSnapshot2 in dataSnapshot.children) {
                                            productEntity =
                                                dataSnapshot2.getValue(ProductEntity::class.java)
                                            listUserChats.add(productEntity)
                                        }
                                        callback.onAllProductsReceived(listUserChats)
                                    } else {
                                        userChatIdRef = userChatRef.child(productEntity?.id!!)
                                        userChatIdRef.removeValue()
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {}
                            })
                        }
                    }
                } catch (e: Exception) {
                    throw Exception(e.message.toString())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun getUserRentingHistoryList(callback: LoadProductsCallback) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
            .child(userId)
        val listUserRents = ArrayList<ProductEntity?>()
        val userRentRef =
            userRef.child("Rents")
        userRentRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                        listUserRents.clear()
                        for (dataSnapshot1 in dataSnapshot.children) {
                            val userRent: ProductEntity? =
                                dataSnapshot1.getValue(ProductEntity::class.java)
                            val productRef: Query =
                                FirebaseDatabase.getInstance().reference.child("Products")
                                    .orderByChild("id")
                                    .equalTo(userRent?.id)
                            productRef.addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (dataSnapshot2 in dataSnapshot.children) {
                                            val productEntity: ProductEntity? =
                                                dataSnapshot2.getValue(ProductEntity::class.java)
                                            productEntity?.time = userRent?.time
                                            listUserRents.add(productEntity)
                                        }
                                        callback.onAllProductsReceived(listUserRents)
                                    } else {
                                        val userRentIdRef = userRentRef.child(userRent?.id!!)
                                        userRentIdRef.removeValue()
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {}
                            })
                        }
                    }
                } catch (e: Exception) {
                    throw Exception(e.message.toString())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    fun rentProduct(productEntity: ProductEntity?) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
            .child(userId)
        val rentId = userRef.child("Rents").push().key
        val userRentRef = userRef.child("Rents").child(rentId!!)
        val productInfo: MutableMap<String, Any> = HashMap()
        productInfo["id"] = productEntity?.id as String
        productInfo["time"] =
            SimpleDateFormat("MMM dd, yyyy 'at' hh:mm:ss a").format(Calendar.getInstance().time)
        userRentRef.updateChildren(productInfo)
    }

    fun chatOwner(productEntity: ProductEntity?) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
            .child(userId)
        val productInfo: MutableMap<String, Any> = HashMap()
        productInfo["id"] = productEntity?.id as String
        val userChatRef = userRef.child(userId).child("Chats").child(productEntity.id as String)
        userChatRef.updateChildren(productInfo)
    }

    fun addProduct(productEntity: ProductEntity?, filePath: Uri?) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef = FirebaseDatabase.getInstance().reference.child("Users")
            .child(userId)
        val productId = FirebaseDatabase.getInstance().reference.child("Products").push().key
        val productRef =
            FirebaseDatabase.getInstance().reference.child("Products").child(productId!!)
        val userProductRef = userRef.child("Products").child(productId)
        val userChatRef = userRef.child("Chats").child(productId)
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
        var chatEntity: ChatEntity?
        val chatRef =
            FirebaseDatabase.getInstance().reference.child("Chats").child(productEntity?.id!!)
                .orderByChild("time")
        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    listChat.clear()
                    for (dataSnapshot in dataSnapshot.children) {
                        chatEntity = dataSnapshot.getValue(ChatEntity::class.java)
                        listChat.add(chatEntity)
                    }
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


