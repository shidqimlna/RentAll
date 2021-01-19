package com.example.rentall.viewmodel

import android.app.Activity
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.rentall.data.MainRepository
import com.example.rentall.data.entity.ChatEntity
import com.example.rentall.data.entity.ProductEntity
import com.example.rentall.data.entity.RentEntity
import com.example.rentall.data.entity.UserEntity

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {

    fun registerUser(userEntity: UserEntity?, password: String, activity: Activity) =
        mainRepository.registerUser(userEntity, password, activity)

    fun loginUser(email: String?, password: String?, activity: Activity) =
        mainRepository.loginUser(email, password, activity)

    fun getProductList(query: String?): LiveData<List<ProductEntity?>> =
        mainRepository.getProductList(query)

    fun getUserProductList(): LiveData<List<ProductEntity?>> = mainRepository.getUserProductList()

    fun getUserChatList(): LiveData<List<ProductEntity?>> = mainRepository.getUserChatList()

    fun getUserRentingHistoryList(): LiveData<List<RentEntity?>> =
        mainRepository.getUserRentingHistoryList()

    fun getUserDetail(): LiveData<UserEntity?> = mainRepository.getUserDetail()

    fun editAccount(userEntity: UserEntity?) = mainRepository.editAccount(userEntity)

    fun addProduct(productEntity: ProductEntity?, filePath: Uri?) =
        mainRepository.addProduct(productEntity, filePath)

    fun editProduct(productEntity: ProductEntity?, filePath: Uri?) =
        mainRepository.editProduct(productEntity, filePath)

    fun deleteProduct(productEntity: ProductEntity?) = mainRepository.deleteProduct(productEntity)

    fun rentProduct(productEntity: ProductEntity?) = mainRepository.rentProduct(productEntity)

    fun chatOwner(productEntity: ProductEntity?) = mainRepository.chatOwner(productEntity)

    fun sendMessage(chatEntity: ChatEntity, productEntity: ProductEntity?) =
        mainRepository.sendMessage(chatEntity, productEntity)

    fun getMessages(productEntity: ProductEntity?): LiveData<List<ChatEntity?>> =
        mainRepository.getMessages(productEntity)

}