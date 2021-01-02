package com.example.rentall.viewmodel

import android.app.Activity
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.rentall.data.MainRepository
import com.example.rentall.data.entity.ChatEntity
import com.example.rentall.data.entity.ProductEntity
import com.example.rentall.data.entity.UserEntity

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {
    fun registerUser(userEntity: UserEntity?, password: String, activity: Activity) {
        return mainRepository.registerUser(userEntity, password, activity)
    }

    fun loginUser(email: String?, password: String?, activity: Activity) {
        return mainRepository.loginUser(email, password, activity)
    }

    fun getProductList(query: String?): LiveData<List<ProductEntity?>> {
        return mainRepository.getProductList(query)
    }

    fun getUserProductList(): LiveData<List<ProductEntity?>> {
        return mainRepository.getUserProductList()
    }

    fun getUserChatList(): LiveData<List<ProductEntity?>> {
        return mainRepository.getUserChatList()
    }

    fun getUserRentingHistoryList(): LiveData<List<ProductEntity?>> {
        return mainRepository.getUserRentingHistoryList()
    }

    fun getUserDetail(): LiveData<UserEntity?> {
        return mainRepository.getUserDetail()
    }

    fun editAccount(userEntity: UserEntity?) {
        return mainRepository.editAccount(userEntity)
    }

    fun addProduct(productEntity: ProductEntity?, filePath: Uri?) {
        return mainRepository.addProduct(productEntity, filePath)
    }

    fun editProduct(productEntity: ProductEntity?, filePath: Uri?) {
        return mainRepository.editProduct(productEntity, filePath)
    }

    fun deleteProduct(productEntity: ProductEntity?) {
        return mainRepository.deleteProduct(productEntity)
    }

    fun rentProduct(productEntity: ProductEntity?) {
        mainRepository.rentProduct(productEntity)
    }

    fun chatOwner(productEntity: ProductEntity?) {
        mainRepository.chatOwner(productEntity)
    }

    fun sendMessage(chatEntity: ChatEntity, productEntity: ProductEntity?) {
        mainRepository.sendMessage(chatEntity, productEntity)
    }

    fun getMessages(productEntity: ProductEntity?): LiveData<List<ChatEntity?>> {
        return mainRepository.getMessages(productEntity)
    }

}