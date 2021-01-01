package com.example.rentall.data

import android.net.Uri
import androidx.lifecycle.LiveData
import com.example.rentall.data.entity.ChatEntity
import com.example.rentall.data.entity.ProductEntity
import com.example.rentall.data.entity.UserEntity

interface MainRepositoryInterface {
    fun getUserDetail(): LiveData<UserEntity?>
    fun getProductList(query: String?): LiveData<List<ProductEntity?>>
    fun getUserProductList(): LiveData<List<ProductEntity?>>
    fun editAccount(userEntity: UserEntity?)
    fun addProduct(productEntity: ProductEntity?, filePath: Uri?)
    fun editProduct(productEntity: ProductEntity?, filePath: Uri?)
    fun deleteProduct(productEntity: ProductEntity?)
    fun sendMessage(chatEntity: ChatEntity, productEntity: ProductEntity?)
    fun getMessages(productEntity: ProductEntity?): LiveData<List<ChatEntity?>>
    fun rentProduct(productEntity: ProductEntity?)
    fun chatOwner(productEntity: ProductEntity?)
}